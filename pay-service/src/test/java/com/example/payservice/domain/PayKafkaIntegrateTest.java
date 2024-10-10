//package com.example.payservice.domain;
//
//import com.example.concertticketing.domain.concert.model.Concert;
//import com.example.concertticketing.domain.concert.model.ConcertDetail;
//import com.example.concertticketing.domain.concert.model.Seat;
//import com.example.concertticketing.domain.concert.repository.ConcertRepository;
//import com.example.concertticketing.domain.concert.repository.SeatRepository;
//import com.example.concertticketing.domain.member.model.Member;
//import com.example.concertticketing.domain.member.repository.MemberRepository;
//import com.example.concertticketing.domain.message.model.OutboxStatus;
//import com.example.concertticketing.domain.message.repository.OutboxRepository;
//import com.example.concertticketing.domain.pay.event.PayMessageEvent;
//import com.example.concertticketing.domain.pay.infrastructure.PayOutboxJpaRepository;
//import com.example.concertticketing.domain.pay.model.Pay;
//import com.example.concertticketing.domain.pay.model.PayOutbox;
//import com.example.concertticketing.domain.pay.repository.PayRepository;
//import com.example.concertticketing.domain.queue.model.ActiveQueue;
//import com.example.concertticketing.domain.queue.repository.QueueRepository;
//import com.example.concertticketing.domain.reservation.model.Reservation;
//import com.example.concertticketing.domain.reservation.repository.ReservationRepository;
//import com.example.concertticketing.interfaces.api.pay.dto.PayRequest;
//import com.example.concertticketing.util.JsonConverter;
//import com.example.concertticketing.util.SlackClient;
//import jakarta.persistence.EntityManager;
//import org.junit.jupiter.api.AfterEach;
//import org.junit.jupiter.api.DisplayName;
//import org.junit.jupiter.api.Test;
//import org.springframework.beans.factory.annotation.Autowired;
//import org.springframework.beans.factory.annotation.Qualifier;
//import org.springframework.boot.test.context.SpringBootTest;
//import org.springframework.boot.test.mock.mockito.MockBean;
//import org.springframework.kafka.test.context.EmbeddedKafka;
//import org.springframework.test.context.ActiveProfiles;
//
//import java.time.LocalDateTime;
//import java.time.ZoneOffset;
//import java.util.List;
//import java.util.Set;
//import java.util.concurrent.CompletableFuture;
//import java.util.concurrent.CountDownLatch;
//import java.util.concurrent.TimeUnit;
//import java.util.stream.Collectors;
//
//import static org.assertj.core.api.Assertions.assertThat;
//import static org.mockito.Mockito.anyString;
//import static org.mockito.Mockito.doNothing;
//
//@ActiveProfiles("test")
//@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
//@EmbeddedKafka(partitions = 1,
//        brokerProperties = {"listeners=PLAINTEXT://localhost:9092"},
//        ports = {9092}
//)
//public class PayKafkaIntegrateTest {
//
//    @Autowired
//    private JsonConverter jsonConverter;
//
//    @Autowired
//    private PayService payService;
//
//    @Autowired
//    private QueueRepository queueRepository;
//
//    @Autowired
//    private MemberRepository memberRepository;
//
//    @Autowired
//    private ConcertRepository concertRepository;
//
//    @Autowired
//    private SeatRepository seatRepository;
//
//    @Autowired
//    private ReservationRepository reservationRepository;
//
//    @Autowired
//    private PayRepository payRepository;
//
//    @Autowired
//    @Qualifier("PayOutboxRepository")
//    private OutboxRepository outboxRepository;
//
//    @Autowired
//    private EntityManager entityManager;
//
//    @Autowired
//    private PayOutboxJpaRepository outboxJpaRepository;
//
//    @MockBean
//    private SlackClient slackClient;
//
//    private CountDownLatch latch;
//
//    void delete() {
//        queueRepository.flushAll();
//        concertRepository.deleteAllInBatch();
//        memberRepository.deleteAllInBatch();
//        reservationRepository.deleteAllInBatch();
//        payRepository.deleteAllInBatch();
//        outboxRepository.deleteAllInBatch();
//    }
//
//    @AfterEach
//    void tearDown() {
//        CompletableFuture.allOf(
//                CompletableFuture.runAsync(() -> delete())
//        ).join();
//    }
//
//    private PayRequest createPayRequest(Long reservationId, Long seatId, Long memberId) {
//        return new PayRequest(reservationId, seatId, memberId);
//    }
//
//    private Member createMember(String loginId, Long balance) {
//        return Member.builder()
//                .memberLoginId(loginId)
//                .balance(balance)
//                .build();
//    }
//
//    private Concert createConcert(String singer) {
//        return Concert.builder()
//                .singer(singer)
//                .build();
//    }
//
//    private ConcertDetail createConcertDetail(Concert concert, String name, LocalDateTime date) {
//        return ConcertDetail.builder()
//                .concert(concert)
//                .name(name)
//                .date(date)
//                .build();
//    }
//
//    private Seat createSeat(ConcertDetail concertDetail, Member member, int seatNo, Long price, LocalDateTime reservedAt) {
//        return Seat.builder()
//                .concert(concertDetail)
//                .member(member)
//                .seatNo(seatNo)
//                .price(price)
//                .reservedAt(reservedAt)
//                .build();
//    }
//
//    private Reservation createReservation(Member member, Seat seat, Long price) {
//        return Reservation.builder()
//                .memberId(member.getId())
//                .seat(seat)
//                .seatNo(seat.getSeatNo())
//                .price(price)
//                .build();
//    }
//
//    private PayOutbox createOutbox(Long eventId, LocalDateTime time, OutboxStatus status) {
//        return PayOutbox.builder()
//                .eventId(eventId)
//                .payload("{\n" +
//                        "\t\"payId\": 1,\n" +
//                        "\t\"reservationId\": 1,\n" +
//                        "\t\"amount\": 5000,\n" +
//                        "\t\"status\": \"PAYED\"\n" +
//                        "}")
//                .createdAt(time)
//                .status(status)
//                .build();
//    }
//
//    private Long findFirstMemberId() {
//        return (Long) entityManager.createNativeQuery("SELECT id FROM MEMBER LIMIT 1")
//                .getSingleResult();
//    }
//
//    private Long findFirstSeatId() {
//        return (Long) entityManager.createNativeQuery("SELECT id FROM SEAT LIMIT 1")
//                .getSingleResult();
//    }
//
//    private Long findFirstReservationId() {
//        return (Long) entityManager.createNativeQuery("SELECT id FROM RESERVATION LIMIT 1")
//                .getSingleResult();
//    }
//
//    private PayOutbox findFirstOutbox() {
//        return (PayOutbox) entityManager.createNativeQuery("SELECT * FROM PAY_OUTBOX LIMIT 1", PayOutbox.class)
//                .getSingleResult();
//    }
//
//    private ConcertDetail findFirstConcertDetail() {
//        return (ConcertDetail) entityManager.createNativeQuery("SELECT * FROM CONCERT_DETAIL LIMIT 1", ConcertDetail.class)
//                .getSingleResult();
//    }
//
//    private Pay findFirstPay() {
//        return (Pay) entityManager.createNativeQuery("SELECT * FROM PAY LIMIT 1", Pay.class)
//                .getSingleResult();
//    }
//
//    private void setUpOutbox(LocalDateTime time) {
//        for (int i = 0; i < 6; i++) {
//            if (i < 3) {
//                outboxJpaRepository.save(createOutbox(Long.valueOf(i), time, OutboxStatus.INIT));
//            } else {
//                outboxJpaRepository.save(createOutbox(Long.valueOf(i), time, OutboxStatus.PUBLISHED));
//            }
//        }
//    }
//
//    private void setUpPay() {
//        latch = new CountDownLatch(1);
//
//        Member member = createMember("A1", 5000L);
//        memberRepository.save(member);
//
//        Long memberId = findFirstMemberId();
//        String value = memberId + ":" + LocalDateTime.now().plusMinutes(5).toEpochSecond(ZoneOffset.UTC);
//        queueRepository.addActiveQueues(Set.of(value));
//
//        Concert concert = createConcert("박효신");
//        concertRepository.saveConcert(concert);
//
//        List<ConcertDetail> detailList = List.of(
//                createConcertDetail(concert, concert.getSinger(), LocalDateTime.of(2024, 6, 12, 0, 0, 0))
//        );
//
//        concertRepository.saveConcertDetailAll(detailList);
//
//        ConcertDetail concertDetail = findFirstConcertDetail();
//        List<Seat> seatList = List.of(
//                createSeat(concertDetail, null, 1, 4000L, null),
//                createSeat(concertDetail, null, 1, 6000L, null)
//        );
//        seatRepository.saveAll(seatList);
//
//        List<Reservation> reservationList = List.of(
//                createReservation(member, seatList.get(0), 4000L),
//                createReservation(member, seatList.get(1), 6000L)
//        );
//        reservationRepository.saveAll(reservationList);
//    }
//
//
//    @DisplayName("결제 시 이벤트가 성공적으로 발행되고 로직이 수행된다")
//    @Test
//    void pay_event_published() throws InterruptedException {
//        // given
//        CompletableFuture.allOf(
//                CompletableFuture.runAsync(() -> setUpPay())
//        ).join();
//
//        Long memberId = findFirstMemberId();
//        Long seatId = findFirstSeatId();
//        Long reservationId = findFirstReservationId();
//        PayRequest request = createPayRequest(reservationId, seatId, memberId);
//
//        // when
//        doNothing().when(slackClient).sendMessage(anyString());
//
//        Set<ActiveQueue> beforeTokens = queueRepository.getActiveTokens();
//
//        payService.pay(request);
//        PayOutbox beforeOutbox = findFirstOutbox();
//
//        latch.await(5, TimeUnit.SECONDS);
//
//        Pay pay = findFirstPay();
//        PayOutbox outbox = findFirstOutbox();
//        Set<ActiveQueue> afterTokens = queueRepository.getActiveTokens();
//
//        // then
//        assertThat(outbox.getPayload()).isEqualTo(jsonConverter.toJson(PayMessageEvent.from(pay)));
//        assertThat(beforeOutbox.getStatus()).isEqualTo(OutboxStatus.INIT);
//        assertThat(outbox.getStatus()).isEqualTo(OutboxStatus.PUBLISHED);
//        assertThat(beforeTokens.size()).isEqualTo(1);
//        assertThat(afterTokens.size()).isEqualTo(0);
//    }
//
//    @DisplayName("결제 시 이벤트가 발행되지 않았지만 재발행 조건에 맞아 스케줄러가 이를 발행시킨다")
//    @Test
//    void pay_scheduler_publish() throws InterruptedException {
//        // given
//        setUpOutbox(LocalDateTime.now().minusDays(1));
//
//        // when
//        doNothing().when(slackClient).sendMessage(anyString());
//
//        List<PayOutbox> beforeAll = outboxRepository.findAll();
//        List<PayOutbox> before = beforeAll.stream()
//                .filter(o -> o.getStatus() == OutboxStatus.PUBLISHED)
//                .collect(Collectors.toList());
//
//        payService.republish();
//
//        List<PayOutbox> afterAll = outboxRepository.findAll();
//        List<PayOutbox> after = afterAll.stream()
//                .filter(o -> o.getStatus() == OutboxStatus.PUBLISHED)
//                .collect(Collectors.toList());
//
//        // then
//        assertThat(before).hasSize(3);
//        assertThat(after).hasSize(6);
//    }
//
//    @DisplayName("결제 시 이벤트가 발행되지 않고 재발행 조건에 맞지않아 스케줄러 작업을 실행하지 않는다")
//    @Test
//    void pay_scheduler_no_publish() throws InterruptedException {
//        // given
//        setUpOutbox(LocalDateTime.now());
//
//        // when
//        doNothing().when(slackClient).sendMessage(anyString());
//
//        List<PayOutbox> beforeAll = outboxRepository.findAll();
//        List<PayOutbox> before = beforeAll.stream()
//                .filter(o -> o.getStatus() == OutboxStatus.PUBLISHED)
//                .collect(Collectors.toList());
//
//        payService.republish();
//
//        List<PayOutbox> afterAll = outboxRepository.findAll();
//        List<PayOutbox> after = afterAll.stream()
//                .filter(o -> o.getStatus() == OutboxStatus.PUBLISHED)
//                .collect(Collectors.toList());
//
//        // then
//        assertThat(before).hasSize(3);
//        assertThat(after).hasSize(3);
//    }
//}
