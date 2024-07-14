package com.example.concertticketing.domain.member.model;

import com.example.concertticketing.domain.common.entity.BaseEntity;
import jakarta.persistence.*;
import lombok.AccessLevel;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import org.hibernate.annotations.Where;

@Getter
@Where(clause = "DELETED_AT IS NULL")
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@Table(name = "MEMBER", indexes = {
        @Index(name = "idx_memberLoginId", columnList = "memberLoginId")
})
@Entity
public class Member extends BaseEntity {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "MEMBER_LOGIN_ID", nullable = false, unique = true)
    private String memberLoginId;

    @Column(name = "BALANCE")
    private Long balance;

    @Builder
    public Member(String memberLoginId, Long balance) {
        this.memberLoginId = memberLoginId;
        this.balance = balance;
    }

    public void chargeBalance(Long amount) {
        this.balance += amount;
    }
}
