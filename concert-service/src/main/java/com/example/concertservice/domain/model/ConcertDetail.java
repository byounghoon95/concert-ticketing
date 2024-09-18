package com.example.concertservice.domain.model;

import com.example.concertservice.domain.common.entity.BaseEntity;
import jakarta.persistence.*;
import lombok.AccessLevel;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import org.hibernate.annotations.Where;

import java.time.LocalDateTime;

@Getter
@Where(clause = "DELETED_AT IS NULL")
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@Table(name = "CONCERT_DETAIL", indexes = {
        @Index(name = "idx_concertId", columnList = "concertId")
})
@Entity
public class ConcertDetail extends BaseEntity {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "CONCERT_DETAIL_ID")
    private Long id;

    @ManyToOne
    @JoinColumn(name = "CONCERT_ID", referencedColumnName = "CONCERT_ID", nullable = false, foreignKey = @ForeignKey(ConstraintMode.NO_CONSTRAINT))
    private Concert concert;

    @Column(name = "NAME")
    private String name;

    @Column(name = "DATE")
    private LocalDateTime date;

    @Column(name = "OPEN_DATE")
    private LocalDateTime openDate;

    @Builder
    public ConcertDetail(Long id, Concert concert, LocalDateTime date, String name, LocalDateTime openDate) {
        this.id = id;
        this.concert = concert;
        this.date = date;
        this.name = name;
        this.openDate = openDate;
    }
}