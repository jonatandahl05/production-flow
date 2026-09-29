package se.jonatandahl.productionflow.service;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import java.util.List;
import java.util.Optional;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import se.jonatandahl.productionflow.dto.request.CreateProductionJobRequest;
import se.jonatandahl.productionflow.dto.request.FinishPrintingRequest;
import se.jonatandahl.productionflow.dto.response.ProductionJobResponse;
import se.jonatandahl.productionflow.entity.JobStatus;
import se.jonatandahl.productionflow.entity.ProductionJob;
import se.jonatandahl.productionflow.exception.DuplicateJobNumberException;
import se.jonatandahl.productionflow.exception.ResourceNotFoundException;
import se.jonatandahl.productionflow.repository.ProductionJobRepository;

@ExtendWith(MockitoExtension.class)
class ProductionJobServiceTest {

    @Mock
    private ProductionJobRepository productionJobRepository;

    private ProductionJobService productionJobService;

    @BeforeEach
    void setUp() {
        productionJobService = new ProductionJobService(productionJobRepository);
    }

    @Test
    void shouldCreateProductionJob() {
        CreateProductionJobRequest request = new CreateProductionJobRequest(
                "JOB-001",
                "Coffee label",
                30_000
        );
        when(productionJobRepository.existsByJobNumber("JOB-001"))
                .thenReturn(false);
        when(productionJobRepository.save(any(ProductionJob.class)))
                .thenAnswer(invocation -> invocation.getArgument(0));

        ProductionJobResponse response = productionJobService.create(request);

        assertEquals("JOB-001", response.jobNumber());
        assertEquals("Coffee label", response.productName());
        assertEquals(30_000, response.orderedQuantity());
        assertEquals(JobStatus.CREATED, response.status());
        verify(productionJobRepository).save(any(ProductionJob.class));
    }

    @Test
    void shouldRejectDuplicateJobNumber() {
        CreateProductionJobRequest request = new CreateProductionJobRequest(
                "JOB-001",
                "Coffee label",
                30_000
        );
        when(productionJobRepository.existsByJobNumber("JOB-001"))
                .thenReturn(true);

        assertThrows(
                DuplicateJobNumberException.class,
                () -> productionJobService.create(request)
        );

        verify(productionJobRepository, never()).save(any());
    }

    @Test
    void shouldReturnJobById() {
        ProductionJob job = createJob();
        when(productionJobRepository.findById(1L))
                .thenReturn(Optional.of(job));

        ProductionJobResponse response = productionJobService.findById(1L);

        assertEquals("JOB-001", response.jobNumber());
        assertEquals(JobStatus.CREATED, response.status());
    }

    @Test
    void shouldThrowWhenJobDoesNotExist() {
        when(productionJobRepository.findById(99L))
                .thenReturn(Optional.empty());

        ResourceNotFoundException exception = assertThrows(
                ResourceNotFoundException.class,
                () -> productionJobService.findById(99L)
        );

        assertEquals("Production job not found: 99", exception.getMessage());
    }

    @Test
    void shouldReturnAllJobs() {
        when(productionJobRepository.findAll()).thenReturn(List.of(
                createJob(),
                new ProductionJob("JOB-002", "Tea label", 20_000)
        ));

        List<ProductionJobResponse> responses = productionJobService.findAll();

        assertEquals(2, responses.size());
        assertEquals("JOB-001", responses.get(0).jobNumber());
        assertEquals("JOB-002", responses.get(1).jobNumber());
    }

    @Test
    void shouldProgressJobThroughCompleteServiceFlow() {
        ProductionJob job = createJob();
        when(productionJobRepository.findById(1L))
                .thenReturn(Optional.of(job));

        assertEquals(
                JobStatus.PRODUCTION_READY,
                productionJobService.markAsProductionReady(1L).status()
        );
        assertEquals(
                JobStatus.PRINTING,
                productionJobService.startPrinting(1L).status()
        );
        assertEquals(
                JobStatus.PRINTED,
                productionJobService.finishPrinting(
                        1L,
                        new FinishPrintingRequest(30_400, 400)
                ).status()
        );
        assertEquals(
                JobStatus.REWINDING,
                productionJobService.startRewinding(1L).status()
        );
        assertEquals(
                JobStatus.COMPLETED,
                productionJobService.complete(1L).status()
        );
    }

    private ProductionJob createJob() {
        return new ProductionJob("JOB-001", "Coffee label", 30_000);
    }
}
