package com.example.concertticketing.domain.reservation.service;

import com.example.concertticketing.domain.concert.model.Concert;
import com.example.concertticketing.domain.concert.model.ConcertDetail;
import com.example.concertticketing.domain.concert.model.Seat;
import com.example.concertticketing.domain.concert.repository.ConcertRepository;
import com.example.concertticketing.domain.concert.repository.SeatRepository;
import com.example.concertticketing.domain.member.model.Member;
import com.example.concertticketing.domain.member.repository.MemberRepository;
import com.example.concertticketing.domain.queue.model.Queue;
import com.example.concertticketing.domain.queue.model.QueueStatus;
import com.example.concertticketing.domain.queue.repository.QueueRepository;
import com.example.concertticketing.domain.reservation.application.ReservationFacade;
import com.example.concertticketing.domain.reservation.model.Reservation;
import com.example.concertticketing.domain.reservation.repository.ReservationRepository;
import jakarta.persistence.EntityManager;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;
import java.util.concurrent.CompletableFuture;
import java.util.stream.Collectors;

import static org.assertj.core.api.Assertions.assertThat;

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
public class ReservationFacadeIntegrateTest {

    private final Logger log = LoggerFactory.getLogger(this.getClass());

    @Autowired
    private ReservationFacade reservationFacade;

    @Autowired
    private ReservationService reservationService;

    @Autowired
    private QueueRepository queueRepository;

    @Autowired
    private MemberRepository memberRepository;

    @Autowired
    private ConcertRepository concertRepository;

    @Autowired
    private SeatRepository seatRepository;

    @Autowired
    private ReservationRepository reservationRepository;

    @Autowired
    private EntityManager entityManager;

    @AfterEach
    void tearDown() {
        queueRepository.deleteAllInBatch();
        memberRepository.deleteAllInBatch();
        concertRepository.deleteAllInBatch();
        seatRepository.deleteAllInBatch();
        reservationRepository.deleteAllInBatch();
    }

    private Member createMember(String loginId, Long balance) {
        return Member.builder()
                .memberLoginId(loginId)
                .balance(balance)
                .build();
    }

    private Queue createQueue(Member member, QueueStatus status, LocalDateTime expiredAt) {
        return Queue.builder()
                .token(UUID.randomUUID())
                .member(member)
                .status(status)
                .expiredAt(expiredAt)
                .build();
    }

    private Concert createConcert(String singer) {
        return Concert.builder()
                .singer(singer)
                .build();
    }

    private ConcertDetail createConcertDetail(Concert concert, String name, LocalDateTime date) {
        return ConcertDetail.builder()
                .concert(concert)
                .name(name)
                .date(date)
                .build();
    }

    private Seat createSeat(ConcertDetail concertDetail, Member member, int seatNo, Long price, LocalDateTime reservedAt) {
        return Seat.builder()
                .concert(concertDetail)
                .member(member)
                .seatNo(seatNo)
                .price(price)
                .reservedAt(reservedAt)
                .build();
    }

    private ConcertDetail findFirstConcertDetail() {
        return (ConcertDetail) entityManager.createNativeQuery("SELECT * FROM CONCERT_DETAIL LIMIT 1", ConcertDetail.class)
                .getSingleResult();
    }

    protected Seat findFirstSeat() {
        return (Seat) entityManager.createNativeQuery("SELECT * FROM SEAT LIMIT 1", Seat.class)
                .getSingleResult();
    }

    private void setUpReservation() {
        List<Member> members = List.of(
                createMember("A1", 5000L),
                createMember("A2", 5000L),
                createMember("A3", 5000L),
                createMember("A4", 5000L),
                createMember("A5", 5000L),
                createMember("A6", 5000L),
                createMember("A7", 5000L),
                createMember("A8", 5000L),
                createMember("A9", 5000L),
                createMember("A10", 5000L)
        );
        memberRepository.saveAll(members);

        List<Queue> queues = List.of(
                createQueue(members.get(0), QueueStatus.ACTIVE, LocalDateTime.now().plusMinutes(1)),
                createQueue(members.get(1), QueueStatus.ACTIVE, LocalDateTime.now().plusMinutes(1)),
                createQueue(members.get(2), QueueStatus.ACTIVE, LocalDateTime.now().plusMinutes(1)),
                createQueue(members.get(3), QueueStatus.ACTIVE, LocalDateTime.now().plusMinutes(1)),
                createQueue(members.get(4), QueueStatus.ACTIVE, LocalDateTime.now().plusMinutes(1)),
                createQueue(members.get(5), QueueStatus.ACTIVE, LocalDateTime.now().plusMinutes(1)),
                createQueue(members.get(6), QueueStatus.ACTIVE, LocalDateTime.now().plusMinutes(1)),
                createQueue(members.get(7), QueueStatus.ACTIVE, LocalDateTime.now().plusMinutes(1)),
                createQueue(members.get(8), QueueStatus.ACTIVE, LocalDateTime.now().plusMinutes(1)),
                createQueue(members.get(9), QueueStatus.ACTIVE, LocalDateTime.now().plusMinutes(1))
        );
        queueRepository.saveAll(queues);

        Concert concert = createConcert("박효신");
        concertRepository.saveConcert(concert);

        List<ConcertDetail> detailList = List.of(
                createConcertDetail(concert, concert.getSinger(), LocalDateTime.of(2024, 6, 12, 0, 0, 0))
        );

        concertRepository.saveConcertDetailAll(detailList);

        ConcertDetail concertDetail = findFirstConcertDetail();
        List<Seat> seatList = List.of(
                createSeat(concertDetail, null, 1, 5000L, null),
                createSeat(concertDetail, null, 2, 5000L, LocalDateTime.now().plusMinutes(6))
        );
        seatRepository.saveAll(seatList);
    }

    @DisplayName("10명의 유저가 동시에 좌석을 예약한다")
    @Test
    void reserveSeat() {
        // given
        CompletableFuture.allOf(
                CompletableFuture.runAsync(() -> setUpReservation())
        ).join();

        Seat seat = findFirstSeat();
        Long seatId = seat.getId();
        List<Member> members = memberRepository.findAll();
        List<Reservation> prev = reservationRepository.findAll();

        long start = System.currentTimeMillis();

        // when
        List<CompletableFuture<Void>> futures = members.stream()
                .map(member -> CompletableFuture.runAsync(() -> {
                    reservationFacade.reserveSeat(seatId, member.getId());
                }))
                .collect(Collectors.toList());

        futures.stream()
                .forEach(future -> {
                    try {
                        future.join();
                    } catch (Exception e) {

                    }
                });

        long end = System.currentTimeMillis();

        List<Reservation> after = reservationRepository.findAll();

        log.info("Elapsed Time: {} ms", (end - start));
        assertThat(prev.size()).isEqualTo(0);
        assertThat(after.size()).isEqualTo(1);

        CompletableFuture.allOf(
                CompletableFuture.runAsync(() -> tearDown())
        ).join();
    }
}
