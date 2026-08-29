package se.jonatandahl.productionflow.service;

import java.util.List;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import lombok.RequiredArgsConstructor;
import se.jonatandahl.productionflow.dto.request.CreateProductionJobRequest;
import se.jonatandahl.productionflow.dto.request.response.ProductionJobResponse;
import se.jonatandahl.productionflow.entity.ProductionJob;
import se.jonatandahl.productionflow.exception.DuplicateJobNumberException;
import se.jonatandahl.productionflow.repository.ProductionJobRepository;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true) 
public class ProductionJobService {

    private final ProductionJobRepository productionJobRepository;

    public ProductionJobResponse create(CreateProductionJobRequest request) {

        String jobNumber = request.jobNumber().trim();

        if (productionJobRepository.existsByJobNumber(jobNumber)) {
            throw new DuplicateJobNumberException("A production job with job number '" + jobNumber + "' already exists.");
        }

        ProductionJob job = new ProductionJob(
            jobNumber,
            request.productName().trim(),
            request.orderedQuantity()
        );

        ProductionJob savedJob = productionJobRepository.save(job);

        return ProductionJobResponse.from(savedJob);
    }

    public List<ProductionJobResponse> findAll() {
        return productionJobRepository.findAll()
                .stream()
                .map(ProductionJobResponse::from)
                .toList();
    }
    
}
