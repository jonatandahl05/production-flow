package se.jonatandahl.productionflow.service;

import java.util.List;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import lombok.RequiredArgsConstructor;
import se.jonatandahl.productionflow.dto.request.CreateProductionJobRequest;
import se.jonatandahl.productionflow.dto.response.ProductionJobResponse;
import se.jonatandahl.productionflow.entity.ProductionJob;
import se.jonatandahl.productionflow.exception.DuplicateJobNumberException;
import se.jonatandahl.productionflow.exception.ResourceNotFoundException;
import se.jonatandahl.productionflow.repository.ProductionJobRepository;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true) 
public class ProductionJobService {

    private final ProductionJobRepository productionJobRepository;
    
    @Transactional
    public ProductionJobResponse create(CreateProductionJobRequest request) {
        ProductionJob job = new ProductionJob(
            request.jobNumber(),
            request.productName(),
            request.orderedQuantity()
        );

        if (productionJobRepository.existsByJobNumber(job.getJobNumber())) {
            throw new DuplicateJobNumberException(
                "A production job with job number '" + job.getJobNumber() + "' already exists."
            );
        }

        ProductionJob savedJob = productionJobRepository.save(job);

        return ProductionJobResponse.from(savedJob);
    }

    public ProductionJobResponse findById(Long id) {
        return ProductionJobResponse.from(findEntityById(id));
    }

    public List<ProductionJobResponse> findAll() {
        return productionJobRepository.findAll()
                .stream()
                .map(ProductionJobResponse::from)
                .toList();
    }

    private ProductionJob findEntityById(Long id) {
        return productionJobRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException(
                    "Production job not found: " + id
                ));
    }

    @Transactional
    public ProductionJobResponse markAsProductionReady (Long id) {
        ProductionJob job = findEntityById(id);
        job.markAsProductionReady();
        return ProductionJobResponse.from(job);
    }

    @Transactional
    public ProductionJobResponse startPrinting(Long id) {
        ProductionJob job = findEntityById(id);
        job.startPrinting();
        return ProductionJobResponse.from(job);
    }

    

    
}
