package com.prowler.alertmgmt.repository;

import com.prowler.alertmgmt.model.Alert;

import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.CrudRepository;

import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;

public interface AlertsRepository extends CrudRepository<Alert, UUID> {

    @Query(value = "SELECT * from alert a join suspected_log sl on a.log_id=sl.id " +
            "WHERE sl.application = ?1 AND sl.host_name = ?2 AND" +
            " sl.logged_at > ?3 AND sl.logged_at < ?4 ORDER BY sl.logged_at DESC LIMIT ?6 OFFSET ?5",
           nativeQuery = true)
    List<Alert> search(String application, String host, LocalDateTime startTime,
                             LocalDateTime endTime, int offset, int limit);

    @Query(value = "SELECT * from alert a join suspected_log sl on a.log_id=sl.id WHERE sl.application = ?1 AND" +
            " sl.logged_at > ?2 AND sl.logged_at < ?3 ORDER BY sl.logged_at DESC LIMIT ?5 OFFSET ?4",
           nativeQuery = true)
    List<Alert> search(String application, LocalDateTime startTime,
                       LocalDateTime endTime, Integer offset, Integer limit);

    @Query(value = "SELECT * from alert a join suspected_log sl on a.log_id=sl.id WHERE" +
            " a.status= ?1 ORDER BY sl.logged_at DESC LIMIT ?2",
           nativeQuery = true)
    List<Alert> findAlertsByStatus(String status, int batchSize);

    Alert findAlertByLogId(UUID logId);
}
