package se.jonatandahl.productionflow.controller;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.patch;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import java.util.List;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.webmvc.test.autoconfigure.WebMvcTest;
import org.springframework.http.MediaType;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;

import se.jonatandahl.productionflow.dto.response.ProductionJobResponse;
import se.jonatandahl.productionflow.entity.JobStatus;
import se.jonatandahl.productionflow.exception.ResourceNotFoundException;
import se.jonatandahl.productionflow.service.ProductionJobService;

@WebMvcTest(ProductionJobController.class)
class ProductionJobControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockitoBean
    private ProductionJobService productionJobService;

    @Test
    void shouldCreateProductionJob() throws Exception {
        when(productionJobService.create(any())).thenReturn(response(JobStatus.CREATED));

        mockMvc.perform(post("/api/production-jobs")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("""
                                {
                                  "jobNumber": "JOB-001",
                                  "productName": "Coffee label",
                                  "orderedQuantity": 30000
                                }
                                """))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.jobNumber").value("JOB-001"))
                .andExpect(jsonPath("$.status").value("CREATED"));
    }

    @Test
    void shouldReturnValidationErrorsForInvalidCreateRequest() throws Exception {
        mockMvc.perform(post("/api/production-jobs")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("""
                                {
                                  "jobNumber": "",
                                  "productName": "",
                                  "orderedQuantity": 0
                                }
                                """))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.title").value("Validation failed"))
                .andExpect(jsonPath("$.validationErrors.jobNumber").exists())
                .andExpect(jsonPath("$.validationErrors.productName").exists())
                .andExpect(jsonPath("$.validationErrors.orderedQuantity").exists());
    }

    @Test
    void shouldReturnAllProductionJobs() throws Exception {
        when(productionJobService.findAll())
                .thenReturn(List.of(response(JobStatus.CREATED)));

        mockMvc.perform(get("/api/production-jobs"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[0].jobNumber").value("JOB-001"))
                .andExpect(jsonPath("$[0].status").value("CREATED"));
    }

    @Test
    void shouldReturnProductionJobById() throws Exception {
        when(productionJobService.findById(1L))
                .thenReturn(response(JobStatus.CREATED));

        mockMvc.perform(get("/api/production-jobs/1"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.jobNumber").value("JOB-001"));
    }

    @Test
    void shouldReturnNotFoundWhenProductionJobDoesNotExist() throws Exception {
        when(productionJobService.findById(99L))
                .thenThrow(new ResourceNotFoundException(
                        "Production job not found: 99"
                ));

        mockMvc.perform(get("/api/production-jobs/99"))
                .andExpect(status().isNotFound())
                .andExpect(jsonPath("$.title").value("Resource not found"))
                .andExpect(jsonPath("$.detail")
                        .value("Production job not found: 99"));
    }

    @Test
    void shouldMarkProductionJobAsReady() throws Exception {
        when(productionJobService.markAsProductionReady(1L))
                .thenReturn(response(JobStatus.PRODUCTION_READY));

        mockMvc.perform(patch("/api/production-jobs/1/production-ready"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.status").value("PRODUCTION_READY"));
    }

    @Test
    void shouldStartPrinting() throws Exception {
        when(productionJobService.startPrinting(1L))
                .thenReturn(response(JobStatus.PRINTING));

        mockMvc.perform(patch("/api/production-jobs/1/start-printing"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.status").value("PRINTING"));
    }

    @Test
    void shouldFinishPrinting() throws Exception {
        when(productionJobService.finishPrinting(any(), any()))
                .thenReturn(response(JobStatus.PRINTED));

        mockMvc.perform(patch("/api/production-jobs/1/finish-printing")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("""
                                {
                                  "producedQuantity": 30400,
                                  "wasteQuantity": 400
                                }
                                """))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.status").value("PRINTED"));
    }

    @Test
    void shouldRejectMissingFinishPrintingValues() throws Exception {
        mockMvc.perform(patch("/api/production-jobs/1/finish-printing")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("{}"))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.validationErrors.producedQuantity")
                        .value("Produced quantity is required"))
                .andExpect(jsonPath("$.validationErrors.wasteQuantity")
                        .value("Waste quantity is required"));
    }

    @Test
    void shouldStartRewinding() throws Exception {
        when(productionJobService.startRewinding(1L))
                .thenReturn(response(JobStatus.REWINDING));

        mockMvc.perform(patch("/api/production-jobs/1/start-rewinding"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.status").value("REWINDING"));
    }

    @Test
    void shouldCompleteProductionJob() throws Exception {
        when(productionJobService.complete(1L))
                .thenReturn(response(JobStatus.COMPLETED));

        mockMvc.perform(patch("/api/production-jobs/1/complete"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.status").value("COMPLETED"));
    }

    @Test
    void shouldReturnConflictForInvalidProductionState() throws Exception {
        when(productionJobService.complete(1L))
                .thenThrow(new IllegalStateException(
                        "Expected status REWINDING, but was PRINTED"
                ));

        mockMvc.perform(patch("/api/production-jobs/1/complete"))
                .andExpect(status().isConflict())
                .andExpect(jsonPath("$.title").value("Invalid production state"));
    }

    private ProductionJobResponse response(JobStatus status) {
        return new ProductionJobResponse(
                1L,
                "JOB-001",
                "Coffee label",
                30_000,
                status == JobStatus.PRINTED ? 30_400 : 0,
                status == JobStatus.PRINTED ? 400 : 0,
                status,
                null,
                null,
                0
        );
    }
}
