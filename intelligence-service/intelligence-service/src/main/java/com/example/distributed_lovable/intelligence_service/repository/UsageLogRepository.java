package com.example.distributed_lovable.intelligence_service.repository;

import java.time.LocalDate;
import java.util.Optional;

import com.example.distributed_lovable.intelligence_service.entity.UsageLog;
import org.springframework.data.jpa.repository.JpaRepository;


public interface UsageLogRepository extends JpaRepository<UsageLog, Long>{

	Optional<UsageLog> findByUserIdAndDate(Long userId, LocalDate today);

}
