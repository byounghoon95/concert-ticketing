package com.example.concertticketing;

import com.example.concertticketing.domain.concert.model.Concert;
import com.example.concertticketing.domain.concert.model.ConcertDetail;
import com.example.concertticketing.domain.concert.model.Seat;
import com.example.concertticketing.domain.concert.repository.ConcertRepository;
import com.example.concertticketing.domain.concert.repository.SeatRepository;
import com.example.concertticketing.domain.member.model.Member;
import com.example.concertticketing.domain.member.repository.MemberRepository;
import com.example.concertticketing.domain.member.service.MemberService;
import com.example.concertticketing.domain.pay.repository.PayRepository;
import com.example.concertticketing.domain.queue.model.Queue;
import com.example.concertticketing.domain.queue.model.QueueStatus;
import com.example.concertticketing.domain.queue.repository.QueueRepository;
import com.example.concertticketing.domain.queue.service.QueueService;
import com.example.concertticketing.domain.reservation.model.Reservation;
import com.example.concertticketing.domain.reservation.repository.ReservationRepository;
import jakarta.persistence.EntityManager;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.web.client.TestRestTemplate;
import org.springframework.boot.test.web.server.LocalServerPort;
import org.springframework.cglib.core.Local;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;

import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;

/**
 * TestRestTemplate 사용 시 RANDOM_PORT 아니면 Bean 주입 안됨
 * */
@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
public abstract class CommonControllerIntegrateTest {
    @LocalServerPort
    protected int port;

    @Autowired
    protected TestRestTemplate restTemplate;

    @Autowired
    protected QueueService queueService;

    @Autowired
    protected MemberService memberService;

    @Autowired
    protected QueueRepository queueRepository;

    @Autowired
    protected MemberRepository memberRepository;

    @Autowired
    protected ConcertRepository concertRepository;

    @Autowired
    protected SeatRepository seatRepository;

    @Autowired
    protected ReservationRepository reservationRepository;

    @Autowired
    protected PayRepository payRepository;

    @Autowired
    protected EntityManager entityManager;

    public Member createMember(String loginId, Long balance) {
        return Member.builder()
                .memberLoginId(loginId)
                .balance(balance)
                .build();
    }

    public Queue createQueue(Member member, QueueStatus status, LocalDateTime expiredAt) {
        return Queue.builder()
                .token(UUID.randomUUID())
                .member(member)
                .status(status)
                .expiredAt(expiredAt)
                .build();
    }

    public Concert createConcert(String singer) {
        return Concert.builder()
                .singer(singer)
                .build();
    }

    public ConcertDetail createConcertDetail(Concert concert, String name, LocalDateTime date) {
        return ConcertDetail.builder()
                .concert(concert)
                .name(name)
                .date(date)
                .build();
    }

    public Seat createSeat(ConcertDetail concertDetail, Member member, int seatNo, Long price, LocalDateTime reservedAt) {
        return Seat.builder()
                .concert(concertDetail)
                .member(member)
                .seatNo(seatNo)
                .price(price)
                .reservedAt(reservedAt)
                .build();
    }

    public Reservation createReservation(Member member, Seat seat, Long price) {
        return Reservation.builder()
                .memberId(member.getId())
                .seat(seat)
                .seatNo(seat.getSeatNo())
                .price(price)
                .build();
    }

    protected void setUpQueue() {
        List<Member> memberList = List.of(
                createMember("A1", 5000L),
                createMember("A2", 4000L),
                createMember("A3", 4000L),
                createMember("A4", 4000L),
                createMember("A5", 4000L),
                createMember("A6", 3000L)
        );

        List<Queue> queueList = List.of(
                createQueue(memberList.get(0), QueueStatus.EXPIRED, LocalDateTime.of(2024, 6, 12, 0, 0, 0)),
                createQueue(memberList.get(1), QueueStatus.EXPIRED, LocalDateTime.of(2024, 6, 12, 0, 0, 0)),
                createQueue(memberList.get(2), QueueStatus.ACTIVE, LocalDateTime.of(2024, 6, 12, 0, 5, 0)),
                createQueue(memberList.get(3), QueueStatus.ACTIVE, LocalDateTime.of(2024, 6, 12, 0, 5, 0)),
                createQueue(memberList.get(4), QueueStatus.WAIT, null),
                createQueue(memberList.get(5), QueueStatus.WAIT, null)
        );

        memberRepository.saveAll(memberList);
        queueRepository.saveAll(queueList);
    }

    protected void setUpConcert() {
        Member member = createMember("A1", 5000L);
        memberRepository.save(member);

        Queue queue = createQueue(member, QueueStatus.WAIT, null);
        queueRepository.save(queue);

        Concert concert = createConcert("박효신");
        concertRepository.saveConcert(concert);

        List<ConcertDetail> detailList = List.of(
                createConcertDetail(concert, concert.getSinger(), LocalDateTime.of(2024, 6, 12, 0, 0, 0)),
                createConcertDetail(concert, concert.getSinger(), LocalDateTime.of(2024, 6, 12, 0, 0, 0).plusDays(1)),
                createConcertDetail(concert, concert.getSinger(), LocalDateTime.of(2024, 6, 12, 0, 0, 0).plusDays(2))
        );

        concertRepository.saveConcertDetailAll(detailList);

        ConcertDetail concertDetail = findFirstConcertDetail();
        List<Seat> seatList = List.of(
                createSeat(concertDetail, null, 1, 5000L, null),
                createSeat(concertDetail, null, 2, 5000L, null),
                createSeat(concertDetail, null, 3, 5000L, null),
                createSeat(concertDetail, null, 4, 5000L, null),
                createSeat(concertDetail, null, 5, 5000L, null)
        );
        seatRepository.saveAll(seatList);
    }

    protected void setUpMember() {
        Member member = createMember("A1", 5000L);
        memberRepository.save(member);
    }

    protected void setUpReservation() {
        Member member = createMember("A1", 5000L);
        memberRepository.save(member);

        Queue queue = createQueue(member, QueueStatus.WAIT, null);
        queueRepository.save(queue);

        Concert concert = createConcert("박효신");
        concertRepository.saveConcert(concert);

        List<ConcertDetail> detailList = List.of(
                createConcertDetail(concert, concert.getSinger(), LocalDateTime.of(2024, 6, 12, 0, 0, 0)),
                createConcertDetail(concert, concert.getSinger(), LocalDateTime.of(2024, 6, 12, 0, 0, 0).plusDays(1)),
                createConcertDetail(concert, concert.getSinger(), LocalDateTime.of(2024, 6, 12, 0, 0, 0).plusDays(2))
        );

        concertRepository.saveConcertDetailAll(detailList);

        ConcertDetail concertDetail = findFirstConcertDetail();
        List<Seat> seatList = List.of(
                createSeat(concertDetail, null, 1, 5000L, null),
                createSeat(concertDetail, null, 2, 5000L, LocalDateTime.now().plusMinutes(6))
        );
        seatRepository.saveAll(seatList);
    }

    protected void setUpPay() {
        Member member = createMember("A1", 5000L);
        memberRepository.save(member);

        Queue queue = createQueue(member, QueueStatus.ACTIVE, LocalDateTime.now().plusMinutes(1));
        queueRepository.save(queue);

        Concert concert = createConcert("박효신");
        concertRepository.saveConcert(concert);

        List<ConcertDetail> detailList = List.of(
                createConcertDetail(concert, concert.getSinger(), LocalDateTime.of(2024, 6, 12, 0, 0, 0)),
                createConcertDetail(concert, concert.getSinger(), LocalDateTime.of(2024, 6, 12, 0, 0, 0).plusDays(1)),
                createConcertDetail(concert, concert.getSinger(), LocalDateTime.of(2024, 6, 12, 0, 0, 0).plusDays(2))
        );

        concertRepository.saveConcertDetailAll(detailList);

        ConcertDetail concertDetail = findFirstConcertDetail();
        List<Seat> seatList = List.of(
                createSeat(concertDetail, null, 1, 4000L, null),
                createSeat(concertDetail, null, 2, 5000L, LocalDateTime.now().plusMinutes(6))
        );
        seatRepository.saveAll(seatList);

        Reservation reservation = createReservation(member, seatList.get(1), 5000L);
        reservationRepository.save(reservation);
    }

    protected Long findFirstMemberId() {
        return (Long) entityManager.createNativeQuery("SELECT id FROM MEMBER LIMIT 1")
                .getSingleResult();
    }

    protected Long findFirstConcertId() {
        return (Long) entityManager.createNativeQuery("SELECT concert_id FROM CONCERT LIMIT 1")
                .getSingleResult();
    }

    protected Long findFirstConcertDetailId() {
        return (Long) entityManager.createNativeQuery("SELECT concert_detail_id FROM CONCERT_DETAIL LIMIT 1")
                .getSingleResult();
    }

    protected ConcertDetail findFirstConcertDetail() {
        return (ConcertDetail) entityManager.createNativeQuery("SELECT * FROM CONCERT_DETAIL LIMIT 1", ConcertDetail.class)
                .getSingleResult();
    }

    protected Seat findFirstSeat() {
        return (Seat) entityManager.createNativeQuery("SELECT * FROM SEAT LIMIT 1", Seat.class)
                .getSingleResult();
    }

    protected Long findFirstReservationId() {
        return (Long) entityManager.createNativeQuery("SELECT id FROM RESERVATION LIMIT 1")
                .getSingleResult();
    }

    protected Long findFirstQueueId() {
        return (Long) entityManager.createNativeQuery("SELECT id FROM QUEUE LIMIT 1")
                .getSingleResult();
    }

    protected HttpEntity<String> setHeader(Long memberId) {
        // 헤더 설정
        HttpHeaders headers = new HttpHeaders();
        headers.set("memberId", String.valueOf(memberId));

        return new HttpEntity<>(headers);
    }

    protected <T> HttpEntity<T> setHeader(Long memberId, T body) {
        HttpHeaders headers = new HttpHeaders();
        headers.set("memberId", String.valueOf(memberId));
        headers.setContentType(MediaType.APPLICATION_JSON);

        return new HttpEntity<>(body, headers);
    }

    protected <T> HttpEntity<T> setHeaderNoCheck(T body) {
        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_JSON);

        return new HttpEntity<>(body, headers);
    }
}
