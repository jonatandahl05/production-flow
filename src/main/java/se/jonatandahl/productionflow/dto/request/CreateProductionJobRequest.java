package se.jonatandahl.productionflow.dto.request;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Positive;
import jakarta.validation.constraints.Size;

public record CreateProductionJobRequest(
    @NotBlank(message = "Job number is required")
    @Size(max = 50, message = "Job number must be at most 50 characters")
    String jobNumber,

    @NotBlank(message = "Product name is required")
    @Size(max = 200, message = "Product name must be at most 200 characters")
    String productName,

    @Positive(message = "Ordered quantity must be a positive number")
    int orderedQuantity
) { 
}
