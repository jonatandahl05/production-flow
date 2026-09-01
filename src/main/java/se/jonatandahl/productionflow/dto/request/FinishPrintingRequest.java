package se.jonatandahl.productionflow.dto.request;

import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;
import jakarta.validation.constraints.PositiveOrZero;

public record FinishPrintingRequest(
    @NotNull(message = "Produced quantity is required")
    @Positive(message = "Printed quantity must be a positive number") 
    Integer producedQuantity,

    @NotNull(message = "Waste quantity is required")
    @PositiveOrZero(message = "Waste quantity must be a non-negative number") 
    Integer wasteQuantity
) {}
