package se.jonatandahl.productionflow.entity;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;

import org.junit.jupiter.api.Test;

class ProductionJobTest {

    @Test

    void newJobShouldStartWithCreated(){
        ProductionJob job = createJob();

        assertEquals(JobStatus.CREATED, job.getStatus());
        assertEquals(0, job.getProducedQuantity());
        assertEquals(0, job.getWasteQuantity());

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

    @Test
    void shouldNotStartPrintingBeforeProductionIsReady() {
        ProductionJob job = createJob();

        assertThrows(IllegalStateException.class, job::startPrinting);

        assertEquals(JobStatus.CREATED, job.getStatus());
    }

    @Test
    void shouldNotFinishPrintingBeforePrintingHasStarted() {
        ProductionJob job = createJob();
        job.markAsProductionReady();

        assertThrows(
                IllegalStateException.class,
                () -> job.finishPrinting(30_000, 500)
        );

        assertEquals(JobStatus.PRODUCTION_READY, job.getStatus());
    }

    @Test
    void shouldRejectNonPositiveProducedQuantity() {
        ProductionJob job = createPrintingJob();

        assertThrows(
                IllegalArgumentException.class,
                () -> job.finishPrinting(0, 500)
        );

        assertEquals(JobStatus.PRINTING, job.getStatus());
        assertEquals(0, job.getProducedQuantity());
    }

    @Test
    void shouldRejectNegativeWasteQuantity() {
        ProductionJob job = createPrintingJob();

        assertThrows(
                IllegalArgumentException.class,
                () -> job.finishPrinting(30_000, -1)
        );

        assertEquals(JobStatus.PRINTING, job.getStatus());
        assertEquals(0, job.getWasteQuantity());
    }

    @Test
    void shouldNotStartRewindingBeforePrintingIsFinished() {
        ProductionJob job = createJob();

        assertThrows(IllegalStateException.class, job::startRewinding);

        assertEquals(JobStatus.CREATED, job.getStatus());
    }

    @Test
    void shouldNotCompleteBeforeRewindingHasStarted() {
        ProductionJob job = createPrintingJob();
        job.finishPrinting(30_000, 500);

        assertThrows(IllegalStateException.class, job::complete);

        assertEquals(JobStatus.PRINTED, job.getStatus());
    }

    @Test
    void shouldRejectBlankJobNumber() {
        assertThrows(
                IllegalArgumentException.class,
                () -> new ProductionJob(" ", "Coffee label", 30_000)
        );
    }

    @Test
    void shouldRejectBlankProductName() {
        assertThrows(
                IllegalArgumentException.class,
                () -> new ProductionJob("JOB-001", " ", 30_000)
        );
    }

    @Test
    void shouldRejectNonPositiveOrderedQuantity() {
        assertThrows(
                IllegalArgumentException.class,
                () -> new ProductionJob("JOB-001", "Coffee label", 0)
        );
    }

    private ProductionJob createJob() {
        return new ProductionJob("JOB-001", "Coffee label", 30_000);
    }

    private ProductionJob createPrintingJob() {
        ProductionJob job = createJob();
        job.markAsProductionReady();
        job.startPrinting();
        return job;
    }
    
}
