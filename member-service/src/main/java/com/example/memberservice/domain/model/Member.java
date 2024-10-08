package com.example.memberservice.domain.model;

import com.example.memberservice.domain.common.entity.BaseEntity;
import com.example.memberservice.exception.CustomException;
import com.example.memberservice.exception.ErrorEnum;
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
        @Index(name = "idx_memberLogin_id", columnList = "memberLoginId")
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

    @Version
    @Column(name = "VERSION")
    private Long version;

    @Builder
    public Member(Long id, String memberLoginId, Long balance) {
        this.id = id;
        this.memberLoginId = memberLoginId;
        this.balance = balance;
    }

    public void chargeBalance(Long amount) {
        if (balance + amount < 0) {
            throw new CustomException(ErrorEnum.NOT_ENOUGH_BALANCE);
        }

        this.balance += amount;
    }
}
