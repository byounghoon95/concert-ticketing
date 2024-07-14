package com.example.concertticketing.domain.queue.model;

import com.example.concertticketing.domain.common.entity.BaseEntity;
import com.example.concertticketing.domain.member.model.Member;
import jakarta.persistence.*;
import lombok.AccessLevel;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import org.hibernate.annotations.Where;

import java.time.LocalDateTime;
import java.util.UUID;

@Getter
@Where(clause = "DELETED_AT IS NULL")
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@Table(name = "QUEUE")
@Entity
public class Queue extends BaseEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    /**
     * UUID로 지정하면 DB 생성 시 자동으로 BINARY(16) 컬럼 생성
     * */
    @Column(name = "TOKEN_ID")
    private UUID token;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "MEMBER_ID", nullable = false, foreignKey = @ForeignKey(ConstraintMode.NO_CONSTRAINT))
    private Member member;

    @Enumerated(EnumType.STRING) // DB 저장 시 문자열로 저장
    @Column(name = "STATUS")
    private QueueStatus status;

    @Column(name = "EXPIRED_AT")
    private LocalDateTime expiredAt;

    @Transient
    private int position;

    @Builder
    public Queue(Long id, UUID token, Member member, QueueStatus status, LocalDateTime expiredAt, int position) {
        this.id = id;
        this.token = token;
        this.status = status;
        this.member = member;
        this.expiredAt = expiredAt;
        this.position = position;
    }

    public void updateStatus(QueueStatus status) {
        this.status = status;
    }

    public void updateExpiredAt(LocalDateTime expiredAt) {
        this.expiredAt = expiredAt;
    }
}
