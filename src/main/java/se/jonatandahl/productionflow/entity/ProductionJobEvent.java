package se.jonatandahl.productionflow.entity;

import java.time.Instant;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.FetchType;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.PrePersist;
import jakarta.persistence.Table;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Entity
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@Table(name = "production_job_events")
public class ProductionJobEvent {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "production_job_id", nullable = false)
    private ProductionJob productionJob;

    @Enumerated(EnumType.STRING)
    @Column(name = "from_status", length = 30)
    private JobStatus fromStatus;

    @Enumerated(EnumType.STRING)
    @Column(name = "to_status", nullable = false, length = 30)
    private JobStatus toStatus;

    @Column(name = "occurred_at", nullable = false, updatable = false)
    private Instant occurredAt;

    public ProductionJobEvent(
            ProductionJob productionJob,
            JobStatus fromStatus,
            JobStatus toStatus
    ) {
        if (productionJob == null) {
            throw new IllegalArgumentException(
                    "Production job is required"
            );
        }

        if (toStatus == null) {
            throw new IllegalArgumentException(
                    "Target status is required"
            );
        }

        if (fromStatus == toStatus) {
            throw new IllegalArgumentException(
                    "Status must change"
            );
        }

        this.productionJob = productionJob;
        this.fromStatus = fromStatus;
        this.toStatus = toStatus;
    }

    @PrePersist
    private void onCreate() {
        occurredAt = Instant.now();
    }
}