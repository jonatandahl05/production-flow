package se.jonatandahl.productionflow.repository;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.util.List;
import java.util.Optional;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.data.jpa.test.autoconfigure.DataJpaTest;
import org.springframework.dao.DataIntegrityViolationException;

import se.jonatandahl.productionflow.entity.JobStatus;
import se.jonatandahl.productionflow.entity.ProductionJob;

@DataJpaTest(properties = "spring.jpa.hibernate.ddl-auto=create-drop")
class ProductionJobRepositoryTest {

    @Autowired
    private ProductionJobRepository productionJobRepository;

    @Test
    void shouldFindJobByJobNumber() {
        productionJobRepository.saveAndFlush(createJob("JOB-001"));

        Optional<ProductionJob> result =
                productionJobRepository.findByJobNumber("JOB-001");

        assertTrue(result.isPresent());
        assertEquals("Coffee label", result.orElseThrow().getProductName());
    }

    @Test
    void shouldCheckWhetherJobNumberExists() {
        productionJobRepository.saveAndFlush(createJob("JOB-001"));

        assertTrue(productionJobRepository.existsByJobNumber("JOB-001"));
        assertFalse(productionJobRepository.existsByJobNumber("JOB-999"));
    }

    @Test
    void shouldFindJobsByStatus() {
        ProductionJob createdJob = createJob("JOB-001");
        ProductionJob readyJob = createJob("JOB-002");
        readyJob.markAsProductionReady();
        productionJobRepository.saveAllAndFlush(List.of(createdJob, readyJob));

        List<ProductionJob> result =
                productionJobRepository.findByStatus(JobStatus.PRODUCTION_READY);

        assertEquals(1, result.size());
        assertEquals("JOB-002", result.getFirst().getJobNumber());
    }

    @Test
    void shouldRejectDuplicateJobNumberInDatabase() {
        productionJobRepository.saveAndFlush(createJob("JOB-001"));

        assertThrows(
                DataIntegrityViolationException.class,
                () -> productionJobRepository.saveAndFlush(createJob("JOB-001"))
        );
    }

    private ProductionJob createJob(String jobNumber) {
        return new ProductionJob(jobNumber, "Coffee label", 30_000);
    }
}
