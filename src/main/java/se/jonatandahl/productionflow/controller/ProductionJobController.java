package se.jonatandahl.productionflow.controller;

import java.util.List;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import se.jonatandahl.productionflow.dto.request.CreateProductionJobRequest;
import se.jonatandahl.productionflow.dto.response.ProductionJobResponse;
import se.jonatandahl.productionflow.service.ProductionJobService;

@RestController
@RequestMapping("/api/production-jobs")
@RequiredArgsConstructor
public class ProductionJobController {

    private final ProductionJobService productionJobService;

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public ProductionJobResponse create (
             @Valid @RequestBody CreateProductionJobRequest request
    ) {
        return productionJobService.create(request);
    }

    @GetMapping
    public List<ProductionJobResponse> findAll() {
        return productionJobService.findAll();
    }

    @GetMapping("/{id}")
    public ProductionJobResponse findById(
            @PathVariable Long id) {
        return productionJobService.findById(id);
    }

    @PatchMapping("/{id}/production-ready")
    public ProductionJobResponse markAsProductionReady(
            @PathVariable Long id) {
        return productionJobService.markAsProductionReady(id);
    }
    
}
