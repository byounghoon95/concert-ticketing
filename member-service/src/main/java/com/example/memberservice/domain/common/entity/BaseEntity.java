package com.example.memberservice.domain.common.entity;

import jakarta.persistence.Column;
import jakarta.persistence.EntityListeners;
import jakarta.persistence.MappedSuperclass;
import org.springframework.data.annotation.CreatedDate;
import org.springframework.data.annotation.LastModifiedDate;
import org.springframework.data.jpa.domain.support.AuditingEntityListener;

import java.time.LocalDateTime;


@MappedSuperclass
@EntityListeners(AuditingEntityListener.class)
public abstract class BaseEntity {
    @CreatedDate
    @Column(name = "CREATED_AT")
    protected LocalDateTime createdAt;

    @LastModifiedDate
    @Column(name = "MODIFIED_AT")
    protected LocalDateTime modifiedAt;

    @Column(name = "DELETED_AT")
    protected LocalDateTime deletedAt;

    protected void delete() {
        this.deletedAt = LocalDateTime.now();
    }
}