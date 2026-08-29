package se.jonatandahl.productionflow.entity;

import java.time.Instant;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.PrePersist;
import jakarta.persistence.PreUpdate;
import jakarta.persistence.Table;
import jakarta.persistence.UniqueConstraint;
import jakarta.persistence.Version;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;
@Entity
@Getter
@NoArgsConstructor(access=AccessLevel.PROTECTED)
@Table(name = "production_jobs", uniqueConstraints = {
    @UniqueConstraint(name = "uk_production_jobs_job_number", columnNames = "job_number")
})


public class ProductionJob {

    @Id
    @GeneratedValue(strategy= GenerationType.IDENTITY)
    private Long id;
    
    @Column(name = "job_number", nullable = false, length=50)
    private String jobNumber;
    
    
    @Column(name = "product_name", nullable = false, length=200)
    private String productName;

    @Column(name = "ordered_quantity", nullable = false)
    private int orderedQuantity;

    @Column(name = "produced_quantity", nullable = false)
    private int producedQuantity;

    @Column(name = "waste_quantity", nullable = false)
    private int wasteQuantity;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false, length = 30)
    private JobStatus status;

    @Column(name = "created_at", nullable = false, updatable = false)
    private Instant createdAt;

    @Column(name = "updated_at", nullable = false)
    private Instant updatedAt;
    
    @Version
    private long version;


    public ProductionJob(
            String jobNumber,
            String productName,
            int orderedQuantity
    ) {
        if (jobNumber == null || jobNumber.isBlank()) {
            throw new IllegalArgumentException(
                    "Job number cannot be empty"
            );
        }

        if (productName == null || productName.isBlank()) {
            throw new IllegalArgumentException(
                    "Product name cannot be empty"
            );
        }

        if (orderedQuantity <= 0) {
            throw new IllegalArgumentException(
                    "Ordered quantity must be greater than zero"
            );
        }

        this.jobNumber = jobNumber.trim();
        this.productName = productName.trim();
        this.orderedQuantity = orderedQuantity;
        this.producedQuantity = 0;
        this.wasteQuantity = 0;
        this.status = JobStatus.CREATED;
    }

    public void markAsProductionReady() {
        requireStatus(JobStatus.CREATED);
        status = JobStatus.PRODUCTION_READY;
    }

    public void startPrinting() {
        requireStatus(JobStatus.PRODUCTION_READY);
        status = JobStatus.PRINTING;
    }

    public void finishPrinting(
            int producedQuantity,
            int wasteQuantity
    ) {
        requireStatus(JobStatus.PRINTING);

        if (producedQuantity <= 0) {
            throw new IllegalArgumentException(
                    "Produced quantity must be greater than zero"
            );
        }

        if (wasteQuantity < 0) {
            throw new IllegalArgumentException(
                    "Waste quantity cannot be negative"
            );
        }

        this.producedQuantity = producedQuantity;
        this.wasteQuantity = wasteQuantity;
        this.status = JobStatus.PRINTED;
    }

    public void startRewinding() {
        requireStatus(JobStatus.PRINTED);
        status = JobStatus.REWINDING;
    }

    public void complete() {
        requireStatus(JobStatus.REWINDING);
        status = JobStatus.COMPLETED;
    }

    private void requireStatus(JobStatus requiredStatus) {
        if (status != requiredStatus) {
            throw new IllegalStateException(
                    "Expected status " + requiredStatus
                            + ", but was " + status
            );
        }
    }

    @PrePersist
        private void onCreate() {
        Instant now = Instant.now();
        createdAt = now;
        updatedAt = now;
    }

    @PreUpdate
        private void onUpdate() {
        updatedAt = Instant.now();
        }

    
}
