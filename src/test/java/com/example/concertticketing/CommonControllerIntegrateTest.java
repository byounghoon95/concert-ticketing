package com.example.concertticketing;

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
import com.example.concertticketing.domain.queue.service.QueueService;
import jakarta.persistence.EntityManager;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.web.client.TestRestTemplate;
import org.springframework.boot.test.web.server.LocalServerPort;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
public abstract class CommonControllerIntegrateTest {
    @LocalServerPort
    protected int port;

    @Autowired
    protected TestRestTemplate restTemplate;

    @Autowired
    protected QueueService queueService;

    @Autowired
    protected QueueRepository queueRepository;

    @Autowired
    protected MemberRepository memberRepository;

    @Autowired
    protected ConcertRepository concertRepository;

    @Autowired
    protected SeatRepository seatRepository;

    @Autowired
    protected EntityManager entityManager;

    protected void setUpQueue() {
        List<Queue> queueList = new ArrayList<>();
        List<Member> memberList = new ArrayList<>();
        for (int i = 0; i < 9; i++) {
            Member member = Member.builder()
                    .memberLoginId("A" + i)
                    .balance(Long.valueOf(i * 1000))
                    .build();
            memberList.add(member);

            QueueStatus status;
            LocalDateTime time = null;
            if (i < 3) {
                status = QueueStatus.EXPIRED;
                time = LocalDateTime.of(2024, 6, 12, 0, 0, 0);
            } else if (i < 6) {
                status = QueueStatus.ACTIVE;
                time = LocalDateTime.of(2024, 6, 12, 0, 5, 0);
            } else {
                status = QueueStatus.WAIT;
            }

            Queue queue = Queue.builder()
                    .token(UUID.randomUUID())
                    .member(member)
                    .expiredAt(time)
                    .status(status)
                    .build();
            queueList.add(queue);
        }
        memberRepository.saveAll(memberList);
        queueRepository.saveAll(queueList);
    }

    protected void setUpConcert() {
        Member member = Member.builder()
                .memberLoginId("A1")
                .build();

        memberRepository.save(member);

        Queue queue = Queue.builder()
                .token(UUID.randomUUID())
                .member(member)
                .status(QueueStatus.WAIT)
                .build();

        queueRepository.save(queue);

        Concert concert = Concert.builder()
                .singer("박효신")
                .build();
        concertRepository.saveConcert(concert);

        for (int j = 0; j < 3; j++) {
            ConcertDetail concertDetail = ConcertDetail.builder()
                    .concert(concert)
                    .name(concert.getSinger() + (j + 1))
                    .date(LocalDateTime.of(2024, 6, 12, 0, 0, 0).plusDays(j))
                    .build();
            concertRepository.saveConcertDetail(concertDetail);
        }

        Long concertDetailId = findFirstConcertDetailId();
        ConcertDetail concertDetail = ConcertDetail.builder()
                .id(concertDetailId)
                .build();
        for (int k = 0; k < 5; k++) {
            Seat seat = Seat.builder()
                    .concert(concertDetail)
                    .member(member)
                    .seatNo(k + 1)
                    .price((long) (1000 * (k + 1)))
                    .build();
            seatRepository.save(seat);
        }
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

    protected HttpEntity<String> setHeader(Long memberId) {
        // 헤더 설정
        HttpHeaders headers = new HttpHeaders();
        headers.set("memberId", String.valueOf(memberId));

        return new HttpEntity<>(headers);
    }
}
