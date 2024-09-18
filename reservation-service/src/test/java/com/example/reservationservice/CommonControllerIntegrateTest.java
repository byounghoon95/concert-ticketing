//package com.example.reservationservice;
//
//import com.example.concertticketing.domain.concert.model.Concert;
//import com.example.concertticketing.domain.concert.model.ConcertDetail;
//import com.example.concertticketing.domain.concert.model.Seat;
//import com.example.concertticketing.domain.concert.repository.ConcertRepository;
//import com.example.concertticketing.domain.concert.repository.SeatRepository;
//import com.example.concertticketing.domain.member.model.Member;
//import com.example.concertticketing.domain.member.repository.MemberRepository;
//import com.example.concertticketing.domain.member.service.MemberService;
//import com.example.concertticketing.domain.pay.repository.PayRepository;
//import com.example.concertticketing.domain.queue.model.Queue;
//import com.example.concertticketing.domain.queue.repository.QueueRepository;
//import com.example.concertticketing.domain.queue.service.QueueService;
//import com.example.concertticketing.domain.reservation.model.Reservation;
//import com.example.concertticketing.domain.reservation.repository.ReservationRepository;
//import jakarta.persistence.EntityManager;
//import org.springframework.beans.factory.annotation.Autowired;
//import org.springframework.boot.test.context.SpringBootTest;
//import org.springframework.boot.test.web.client.TestRestTemplate;
//import org.springframework.boot.test.web.server.LocalServerPort;
//import org.springframework.http.HttpEntity;
//import org.springframework.http.HttpHeaders;
//import org.springframework.http.MediaType;
//import org.springframework.test.context.ActiveProfiles;
//
//import java.time.LocalDateTime;
//import java.time.ZoneOffset;
//import java.util.List;
//import java.util.Set;
//
///**
// * TestRestTemplate 사용 시 RANDOM_PORT 아니면 Bean 주입 안됨
// * */
//@ActiveProfiles("test")
//@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
//public abstract class CommonControllerIntegrateTest {
//    @LocalServerPort
//    protected int port;
//
//    @Autowired
//    protected TestRestTemplate restTemplate;
//
//    @Autowired
//    protected QueueService queueService;
//
//    @Autowired
//    protected MemberService memberService;
//
//    @Autowired
//    protected QueueRepository queueRepository;
//
//    @Autowired
//    protected MemberRepository memberRepository;
//
//    @Autowired
//    protected ConcertRepository concertRepository;
//
//    @Autowired
//    protected SeatRepository seatRepository;
//
//    @Autowired
//    protected ReservationRepository reservationRepository;
//
//    @Autowired
//    protected PayRepository payRepository;
//
//    @Autowired
//    protected EntityManager entityManager;
//
//    public Member createMember(String loginId, Long balance) {
//        return Member.builder()
//                .memberLoginId(loginId)
//                .balance(balance)
//                .build();
//    }
//
//    public Queue createQueue(Long memberId, LocalDateTime expiredAt) {
//        return new Queue(memberId, expiredAt.toEpochSecond(ZoneOffset.UTC));
//    }
//
//    public Concert createConcert(String singer) {
//        return Concert.builder()
//                .singer(singer)
//                .build();
//    }
//
//    public ConcertDetail createConcertDetail(Concert concert, String name, LocalDateTime date) {
//        return ConcertDetail.builder()
//                .concert(concert)
//                .name(name)
//                .date(date)
//                .build();
//    }
//
//    public Seat createSeat(ConcertDetail concertDetail, Member member, int seatNo, Long price, LocalDateTime reservedAt) {
//        return Seat.builder()
//                .concert(concertDetail)
//                .member(member)
//                .seatNo(seatNo)
//                .price(price)
//                .reservedAt(reservedAt)
//                .build();
//    }
//
//    public Reservation createReservation(Member member, Seat seat, Long price) {
//        return Reservation.builder()
//                .memberId(member.getId())
//                .seat(seat)
//                .seatNo(seat.getSeatNo())
//                .price(price)
//                .build();
//    }
//
//    protected void setUpQueue() {
//        List<Member> memberList = List.of(
//                createMember("A1", 5000L),
//                createMember("A2", 4000L),
//                createMember("A3", 4000L),
//                createMember("A4", 4000L),
//                createMember("A5", 4000L),
//                createMember("A6", 3000L)
//        );
//        memberRepository.saveAll(memberList);
//
//        Long memberId = findFirstMemberId();
//
//        Set<String> activeSet = Set.of(
//                memberId + ":" + LocalDateTime.of(2024, 6, 12, 0, 0, 0).toEpochSecond(ZoneOffset.UTC),
//                (memberId + 1) + ":" + LocalDateTime.of(2024, 6, 12, 0, 0, 0).toEpochSecond(ZoneOffset.UTC),
//                (memberId + 2) + ":" + LocalDateTime.of(2024, 6, 12, 0, 5, 0).toEpochSecond(ZoneOffset.UTC),
//                (memberId + 3) + ":" + LocalDateTime.of(2024, 6, 12, 0, 5, 0).toEpochSecond(ZoneOffset.UTC)
//        );
//
//        for (int i = 4; i < 6; i++) {
//            queueRepository.addWaitingQueue(memberId + i);
//        }
//
//        queueRepository.addActiveQueues(activeSet);
//    }
//
//    protected void setUpConcert() {
//        Member member = createMember("A1", 5000L);
//        memberRepository.save(member);
//
//        Long memberId = findFirstMemberId();
//        String value = memberId + ":" + LocalDateTime.now().plusMinutes(1).toEpochSecond(ZoneOffset.UTC);
//        queueRepository.addActiveQueues(Set.of(value));
//
//        Concert concert = createConcert("박효신");
//        concertRepository.saveConcert(concert);
//
//        List<ConcertDetail> detailList = List.of(
//                createConcertDetail(concert, concert.getSinger(), LocalDateTime.of(2024, 6, 12, 0, 0, 0)),
//                createConcertDetail(concert, concert.getSinger(), LocalDateTime.of(2024, 6, 12, 0, 0, 0).plusDays(1)),
//                createConcertDetail(concert, concert.getSinger(), LocalDateTime.of(2024, 6, 12, 0, 0, 0).plusDays(2))
//        );
//
//        concertRepository.saveConcertDetailAll(detailList);
//
//        ConcertDetail concertDetail = findFirstConcertDetail();
//        List<Seat> seatList = List.of(
//                createSeat(concertDetail, null, 1, 5000L, null),
//                createSeat(concertDetail, null, 2, 5000L, null),
//                createSeat(concertDetail, null, 3, 5000L, null),
//                createSeat(concertDetail, null, 4, 5000L, null),
//                createSeat(concertDetail, null, 5, 5000L, null)
//        );
//        seatRepository.saveAll(seatList);
//    }
//
//    protected void setUpMember() {
//        Member member = createMember("A1", 5000L);
//        memberRepository.save(member);
//    }
//
//    protected void setUpReservation() {
//        Member member = createMember("A1", 5000L);
//        memberRepository.save(member);
//
//        Long memberId = findFirstMemberId();
//        String value = memberId + ":" + LocalDateTime.now().plusMinutes(1).toEpochSecond(ZoneOffset.UTC);
//        queueRepository.addActiveQueues(Set.of(value));
//
//        Concert concert = createConcert("박효신");
//        concertRepository.saveConcert(concert);
//
//        List<ConcertDetail> detailList = List.of(
//                createConcertDetail(concert, concert.getSinger(), LocalDateTime.of(2024, 6, 12, 0, 0, 0)),
//                createConcertDetail(concert, concert.getSinger(), LocalDateTime.of(2024, 6, 12, 0, 0, 0).plusDays(1)),
//                createConcertDetail(concert, concert.getSinger(), LocalDateTime.of(2024, 6, 12, 0, 0, 0).plusDays(2))
//        );
//
//        concertRepository.saveConcertDetailAll(detailList);
//
//        ConcertDetail concertDetail = findFirstConcertDetail();
//        List<Seat> seatList = List.of(
//                createSeat(concertDetail, null, 1, 5000L, null),
//                createSeat(concertDetail, null, 2, 5000L, LocalDateTime.now().plusMinutes(6))
//        );
//        seatRepository.saveAll(seatList);
//    }
//
//    protected void setUpPay() {
//        Member member = createMember("A1", 5000L);
//        memberRepository.save(member);
//
//        Long memberId = findFirstMemberId();
//        String value = memberId + ":" + LocalDateTime.now().plusMinutes(1).toEpochSecond(ZoneOffset.UTC);
//        queueRepository.addActiveQueues(Set.of(value));
//
//        Concert concert = createConcert("박효신");
//        concertRepository.saveConcert(concert);
//
//        List<ConcertDetail> detailList = List.of(
//                createConcertDetail(concert, concert.getSinger(), LocalDateTime.of(2024, 6, 12, 0, 0, 0)),
//                createConcertDetail(concert, concert.getSinger(), LocalDateTime.of(2024, 6, 12, 0, 0, 0).plusDays(1)),
//                createConcertDetail(concert, concert.getSinger(), LocalDateTime.of(2024, 6, 12, 0, 0, 0).plusDays(2))
//        );
//
//        concertRepository.saveConcertDetailAll(detailList);
//
//        ConcertDetail concertDetail = findFirstConcertDetail();
//        List<Seat> seatList = List.of(
//                createSeat(concertDetail, null, 1, 4000L, null),
//                createSeat(concertDetail, member, 2, 4000L, LocalDateTime.now().plusMinutes(6)),
//                createSeat(concertDetail, member, 3, 6000L, LocalDateTime.now().plusMinutes(6))
//        );
//        seatRepository.saveAll(seatList);
//
//        List<Reservation> reservationList = List.of(
//                createReservation(member, seatList.get(1), 4000L),
//                createReservation(member, seatList.get(2), 6000L)
//        );
//        reservationRepository.saveAll(reservationList);
//    }
//
//    protected Long findFirstMemberId() {
//        return (Long) entityManager.createNativeQuery("SELECT id FROM MEMBER LIMIT 1")
//                .getSingleResult();
//    }
//
//    protected Long findFirstConcertId() {
//        return (Long) entityManager.createNativeQuery("SELECT concert_id FROM CONCERT LIMIT 1")
//                .getSingleResult();
//    }
//
//    protected Long findFirstConcertDetailId() {
//        return (Long) entityManager.createNativeQuery("SELECT concert_detail_id FROM CONCERT_DETAIL LIMIT 1")
//                .getSingleResult();
//    }
//
//    protected ConcertDetail findFirstConcertDetail() {
//        return (ConcertDetail) entityManager.createNativeQuery("SELECT * FROM CONCERT_DETAIL LIMIT 1", ConcertDetail.class)
//                .getSingleResult();
//    }
//
//    protected Seat findFirstSeat() {
//        return (Seat) entityManager.createNativeQuery("SELECT * FROM SEAT LIMIT 1", Seat.class)
//                .getSingleResult();
//    }
//
//    protected Long findFirstReservationId() {
//        return (Long) entityManager.createNativeQuery("SELECT id FROM RESERVATION LIMIT 1")
//                .getSingleResult();
//    }
//
//    protected HttpEntity<String> setHeader(Long memberId) {
//        // 헤더 설정
//        HttpHeaders headers = new HttpHeaders();
//        headers.set("memberId", String.valueOf(memberId));
//
//        return new HttpEntity<>(headers);
//    }
//
//    protected <T> HttpEntity<T> setHeader(Long memberId, T body) {
//        HttpHeaders headers = new HttpHeaders();
//        headers.set("memberId", String.valueOf(memberId));
//        headers.setContentType(MediaType.APPLICATION_JSON);
//
//        return new HttpEntity<>(body, headers);
//    }
//
//    protected <T> HttpEntity<T> setHeaderNoCheck(T body) {
//        HttpHeaders headers = new HttpHeaders();
//        headers.setContentType(MediaType.APPLICATION_JSON);
//
//        return new HttpEntity<>(body, headers);
//    }
//}
