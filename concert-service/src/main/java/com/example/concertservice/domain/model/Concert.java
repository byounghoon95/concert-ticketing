package com.example.concertservice.domain.model;

import com.example.concertservice.domain.common.entity.BaseEntity;
import jakarta.persistence.*;
import lombok.AccessLevel;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import org.hibernate.annotations.Where;


@Getter
@Where(clause = "DELETED_AT IS NULL")
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@Table(name = "CONCERT")
@Entity
public class Concert extends BaseEntity {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "CONCERT_ID")
    private Long id;

    @Column(name = "SINGER")
    private String singer;

    @Builder
    public Concert(String singer) {
        this.singer = singer;
    }
}