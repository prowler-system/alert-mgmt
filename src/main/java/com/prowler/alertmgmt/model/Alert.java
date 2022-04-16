package com.prowler.alertmgmt.model;

import com.prowler.alertmgmt.vo.AlertStatus;

import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.Type;
import org.hibernate.annotations.UpdateTimestamp;

import java.time.LocalDateTime;
import java.util.UUID;

import javax.persistence.CascadeType;
import javax.persistence.Entity;
import javax.persistence.EnumType;
import javax.persistence.Enumerated;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.OneToOne;
import javax.persistence.Transient;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Entity
@Getter @Setter @Builder
@NoArgsConstructor
@AllArgsConstructor
public class Alert {
    @Id
    @Type(type = "uuid-char")
    private final UUID id = UUID.randomUUID();

    @Enumerated(EnumType.STRING)
    private AlertStatus status;

    @CreationTimestamp
    private LocalDateTime createdAt;

    @UpdateTimestamp
    private LocalDateTime updatedAt;

    @OneToOne(cascade = CascadeType.ALL)
    @JoinColumn(name = "log_id", referencedColumnName = "id")
    private SuspectedLog log;
}
