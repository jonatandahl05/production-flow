package se.jonatandahl.productionflow.dto.request.response;

import java.time.Instant;

import se.jonatandahl.productionflow.entity.JobStatus;

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
    
    

 


    
}
