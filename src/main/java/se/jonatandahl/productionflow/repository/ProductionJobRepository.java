package se.jonatandahl.productionflow.repository;

import java.util.List;
import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;

import se.jonatandahl.productionflow.entity.JobStatus;
import se.jonatandahl.productionflow.entity.ProductionJob;

public interface ProductionJobRepository extends JpaRepository<ProductionJob, Long> {

    Optional<ProductionJob> findByJobNumber(String jobNumber);

    boolean existsByJobNumber(String jobNumber);

    List<ProductionJob> findByStatus(JobStatus status);

    
}
