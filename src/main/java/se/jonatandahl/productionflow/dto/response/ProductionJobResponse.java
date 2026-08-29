package se.jonatandahl.productionflow.dto.response;

import java.time.Instant;

import se.jonatandahl.productionflow.entity.JobStatus;
import se.jonatandahl.productionflow.entity.ProductionJob;

public record ProductionJobResponse(
    Long id,
    String jobNumber,
    String productName,
    int orderedQuantity,
    int producedQuantity,
    int wasteQuantity,
    JobStatus status,
    Instant createdAt,
    Instant updatedAt,
    long version
) {

    public static ProductionJobResponse from(ProductionJob productionJob) {
        return new ProductionJobResponse(
            productionJob.getId(),
            productionJob.getJobNumber(),
            productionJob.getProductName(),
            productionJob.getOrderedQuantity(),
            productionJob.getProducedQuantity(),
            productionJob.getWasteQuantity(),
            productionJob.getStatus(),
            productionJob.getCreatedAt(),
            productionJob.getUpdatedAt(),
            productionJob.getVersion()
        );
    }


    
}
