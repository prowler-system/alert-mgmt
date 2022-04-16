package com.prowler.alertmgmt.repository;


import com.prowler.alertmgmt.model.SuspectedLog;

import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.CrudRepository;

import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;

public interface SuspectedLogRepository extends CrudRepository<SuspectedLog, UUID> {
    @Query(value = "SELECT * FROM suspected_log WHERE application = ?1 AND host_name = ?2 AND" +
            " logged_at > ?3 AND logged_at < ?4 ORDER BY logged_at DESC LIMIT ?6 OFFSET ?5",
           nativeQuery = true)
    List<SuspectedLog> search(String application, String host,
                              LocalDateTime startTime, LocalDateTime endTime, int offset, int limit);
}
