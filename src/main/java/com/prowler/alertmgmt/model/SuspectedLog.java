package com.prowler.alertmgmt.model;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.prowler.alertmgmt.util.JsonUtil;

import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.Type;
import org.hibernate.annotations.UpdateTimestamp;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

import javax.persistence.CascadeType;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.OneToMany;
import javax.persistence.Table;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Entity
@Getter
@Setter
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class SuspectedLog {
    @Id
    @Type(type = "uuid-char")
    private UUID id = UUID.randomUUID();
    //@ManyToOne(cascade = CascadeType.ALL)
    private String application;
    private String hostName;
    private String logFilePath;
    private String logContent;
    private LocalDateTime       loggedAt;

    @OneToMany(cascade = CascadeType.ALL, fetch = FetchType.EAGER)
    //@JoinColumn(name = "log_id")
    private List<LogViolation> violations = new ArrayList<>();

    @CreationTimestamp
    private LocalDateTime createdAt;

    @UpdateTimestamp
    private LocalDateTime updatedAt;

    public String toJson() throws JsonProcessingException {
        return JsonUtil.mapper.writerWithDefaultPrettyPrinter().writeValueAsString(this);
    }
}
