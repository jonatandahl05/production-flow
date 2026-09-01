package se.jonatandahl.productionflow.dto.request;

import jakarta.validation.constraints.Positive;
import jakarta.validation.constraints.PositiveOrZero;

public record FinishPrintingRequest(
    @Positive(message = "Printed quantity must be a positive number") 
    
    int producedQuantity,
    
    @PositiveOrZero(message = "Waste quantity must be a positive number") 
    
    int wasteQuantity
) {}
