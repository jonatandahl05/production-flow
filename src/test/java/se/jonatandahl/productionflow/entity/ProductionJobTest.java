package se.jonatandahl.productionflow.entity;

import static org.junit.jupiter.api.Assertions.assertEquals;
import org.junit.jupiter.api.Test;

class ProductionJobTest {

    @Test

    void newJobShouldStartWithCreated(){
        ProductionJob job = createJob();

        assertEquals(JobStatus.CREATED, job.getStatus());

    }
    @Test
    void jobShouldFollowCompleteProductionFlow() {
        ProductionJob job = createJob();

        job.markAsProductionReady();
        assertEquals(
                JobStatus.PRODUCTION_READY,
                job.getStatus()
        );

        job.startPrinting();
        assertEquals(JobStatus.PRINTING, job.getStatus());

        job.finishPrinting(50_400, 1_200);
        assertEquals(JobStatus.PRINTED, job.getStatus());
        assertEquals(50_400, job.getProducedQuantity());
        assertEquals(1_200, job.getWasteQuantity());

        job.startRewinding();
        assertEquals(JobStatus.REWINDING, job.getStatus());

        job.complete();
        assertEquals(JobStatus.COMPLETED, job.getStatus());
    }

    private ProductionJob createJob() {
        return new ProductionJob("JOB-001", "Coffee label", 30000);
    }
    
}
