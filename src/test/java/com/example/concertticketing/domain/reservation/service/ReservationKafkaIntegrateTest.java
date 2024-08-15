package com.example.concertticketing.domain.reservation.service;

import com.example.concertticketing.domain.concert.model.Concert;
import com.example.concertticketing.domain.concert.model.ConcertDetail;
import com.example.concertticketing.domain.concert.model.Seat;
import com.example.concertticketing.domain.concert.model.SeatCompensation;
import com.example.concertticketing.domain.concert.repository.ConcertRepository;
import com.example.concertticketing.domain.concert.repository.SeatRepository;
import com.example.concertticketing.domain.member.model.Member;
import com.example.concertticketing.domain.member.repository.MemberRepository;
import com.example.concertticketing.domain.message.model.OutboxStatus;
import com.example.concertticketing.domain.message.repository.OutboxRepository;
import com.example.concertticketing.domain.queue.repository.QueueRepository;
import com.example.concertticketing.domain.reservation.application.ReservationFacade;
import com.example.concertticketing.domain.reservation.event.ReservationEvent;
import com.example.concertticketing.domain.reservation.infrastructure.ReservationOutboxJpaRepository;
import com.example.concertticketing.domain.reservation.model.Reservation;
import com.example.concertticketing.domain.reservation.model.ReservationOutbox;
import com.example.concertticketing.domain.reservation.repository.ReservationRepository;
import com.example.concertticketing.util.JsonConverter;
import com.example.concertticketing.util.SlackClient;
import jakarta.persistence.EntityManager;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.kafka.test.context.EmbeddedKafka;
import org.springframework.test.context.ActiveProfiles;

import java.time.LocalDateTime;
import java.time.ZoneOffset;
import java.util.List;
import java.util.Set;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.TimeUnit;
import java.util.stream.Collectors;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.doNothing;

@ActiveProfiles("test")
@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
@EmbeddedKafka(partitions = 1,
        brokerProperties = {"listeners=PLAINTEXT://localhost:9092"},
        ports = { 9092 }
)
public class ReservationKafkaIntegrateTest {

    @Autowired
    private ReservationFacade reservationFacade;

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

    @MockBean
    private SlackClient slackClient;

    @Autowired
    private ReservationService reservationService;

    @Autowired
    @Qualifier("ReservationOutboxRepository")
    private OutboxRepository outboxRepository;

    @Autowired
    private JsonConverter jsonConverter;

    @Autowired
    private EntityManager entityManager;

    @Autowired
    private ReservationOutboxJpaRepository outboxJpaRepository;

    private CountDownLatch latch;

    private ReservationOutbox findFirstOutbox() {
        return (ReservationOutbox) entityManager.createNativeQuery("SELECT * FROM RESERVATION_OUTBOX LIMIT 1", ReservationOutbox.class)
                .getSingleResult();
    }

    void delete() {
        queueRepository.flushAll();
        concertRepository.deleteAllInBatch();
        memberRepository.deleteAllInBatch();
        reservationRepository.deleteAllInBatch();
        outboxRepository.deleteAllInBatch();
    }

    @AfterEach
    void tearDown() {
        CompletableFuture.allOf(
                CompletableFuture.runAsync(() -> delete())
        ).join();
    }

    private Member createMember(String loginId, Long balance) {
        return Member.builder()
                .memberLoginId(loginId)
                .balance(balance)
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

    private Seat findFirstSeat() {
        return (Seat) entityManager.createNativeQuery("SELECT * FROM SEAT LIMIT 1", Seat.class)
                .getSingleResult();
    }

    private Long findFirstMemberId() {
        return (Long) entityManager.createNativeQuery("SELECT id FROM MEMBER LIMIT 1")
                .getSingleResult();
    }

    private ReservationOutbox createOutbox(Long eventId, LocalDateTime time, OutboxStatus status) {
        return ReservationOutbox.builder()
                .eventId(eventId)
                .payload("{\n" +
                        "\t\"reservationId\": 1,\n" +
                        "\t\"seatNo\": 1,\n" +
                        "\t\"memberId\": 1,\n" +
                        "\t\"price\": 5000,\n" +
                        "\t\"seat\": {\n" +
                        "\t\t\"seatId\": 1,\n" +
                        "\t\t\"memberId\": null,\n" +
                        "\t\t\"reservedAt\": null\n" +
                        "\t}\n" +
                        "}")
                .createdAt(time)
                .status(status)
                .build();
    }

    public void setUpOutbox(LocalDateTime time) {
        for (int i = 0; i < 6; i++) {
            if (i < 3) {
                outboxJpaRepository.save(createOutbox(Long.valueOf(i), time, OutboxStatus.INIT));
            } else {
                outboxJpaRepository.save(createOutbox(Long.valueOf(i), time, OutboxStatus.PUBLISHED));
            }
        }
    }

    private void setUpReservation() {
        latch = new CountDownLatch(1);

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

        Long memberId = findFirstMemberId();

        Set<String> activeSet = Set.of(
                (memberId) + ":" + LocalDateTime.now().plusMinutes(1).toEpochSecond(ZoneOffset.UTC),
                (memberId + 1) + ":" + LocalDateTime.now().plusMinutes(1).toEpochSecond(ZoneOffset.UTC),
                (memberId + 2) + ":" + LocalDateTime.now().plusMinutes(1).toEpochSecond(ZoneOffset.UTC),
                (memberId + 3) + ":" + LocalDateTime.now().plusMinutes(1).toEpochSecond(ZoneOffset.UTC),
                (memberId + 4) + ":" + LocalDateTime.now().plusMinutes(1).toEpochSecond(ZoneOffset.UTC),
                (memberId + 5) + ":" + LocalDateTime.now().plusMinutes(1).toEpochSecond(ZoneOffset.UTC),
                (memberId + 6) + ":" + LocalDateTime.now().plusMinutes(1).toEpochSecond(ZoneOffset.UTC),
                (memberId + 7) + ":" + LocalDateTime.now().plusMinutes(1).toEpochSecond(ZoneOffset.UTC),
                (memberId + 8) + ":" + LocalDateTime.now().plusMinutes(1).toEpochSecond(ZoneOffset.UTC),
                (memberId + 9) + ":" + LocalDateTime.now().plusMinutes(1).toEpochSecond(ZoneOffset.UTC)
        );
        queueRepository.addActiveQueues(activeSet);

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

    @DisplayName("예약 시 이벤트가 성공적으로 발행되고 로직이 수행된다")
    @Test
    void reservation_event_published() throws InterruptedException {
        // given
        CompletableFuture.allOf(
                CompletableFuture.runAsync(() -> setUpReservation())
        ).join();

        Long memberId = findFirstMemberId();
        Seat seat = findFirstSeat();
        Long seatId = seat.getId();
        SeatCompensation seatComp = SeatCompensation.from(seat);

        // when
        Reservation reservation = reservationFacade.reserveSeat(seatId, memberId);
        ReservationOutbox beforeOutbox = findFirstOutbox();

        latch.await(5, TimeUnit.SECONDS);

        ReservationOutbox outbox = findFirstOutbox();

        // then
        assertThat(outbox.getPayload()).isEqualTo(jsonConverter.toJson(ReservationEvent.from(reservation, seatComp)));
        assertThat(outbox.getStatus()).isEqualTo(OutboxStatus.PUBLISHED);
        assertThat(beforeOutbox.getStatus()).isEqualTo(OutboxStatus.INIT);
    }

    @DisplayName("예약 시 이벤트가 발행되지 않았지만 재발행 조건에 맞아 스케줄러가 이를 발행시킨다")
    @Test
    void reservation_scheduler_publish() throws InterruptedException {
        // given
        setUpOutbox(LocalDateTime.now().minusDays(1));

        // when
        doNothing().when(slackClient).sendMessage(anyString());

        List<ReservationOutbox> beforeAll = outboxRepository.findAll();
        List<ReservationOutbox> before = beforeAll.stream()
                .filter(o -> o.getStatus() == OutboxStatus.PUBLISHED)
                .collect(Collectors.toList());

        reservationService.republish();

        List<ReservationOutbox> afterAll = outboxRepository.findAll();
        List<ReservationOutbox> after = afterAll.stream()
                .filter(o -> o.getStatus() == OutboxStatus.PUBLISHED)
                .collect(Collectors.toList());

        // then
        assertThat(before).hasSize(3);
        assertThat(after).hasSize(6);
    }

    @DisplayName("예약 시 이벤트가 발행되지 않고 재발행 조건에 맞지않아 스케줄러 작업을 실행하지 않는다")
    @Test
    void reservation_scheduler_no_publish() throws InterruptedException {
        // given
        setUpOutbox(LocalDateTime.now());

        // when
        doNothing().when(slackClient).sendMessage(anyString());

        List<ReservationOutbox> beforeAll = outboxRepository.findAll();
        List<ReservationOutbox> before = beforeAll.stream()
                .filter(o -> o.getStatus() == OutboxStatus.PUBLISHED)
                .collect(Collectors.toList());

        reservationService.republish();

        List<ReservationOutbox> afterAll = outboxRepository.findAll();
        List<ReservationOutbox> after = afterAll.stream()
                .filter(o -> o.getStatus() == OutboxStatus.PUBLISHED)
                .collect(Collectors.toList());

        // then
        assertThat(before).hasSize(3);
        assertThat(after).hasSize(3);
    }
}
