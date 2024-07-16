package com.example.concertticketing.api.concert;

import com.example.concertticketing.api.common.response.CommonResponse;
import com.example.concertticketing.domain.concert.model.*;
import com.example.concertticketing.domain.concert.repository.ConcertRepository;
import com.example.concertticketing.domain.concert.repository.SeatRepository;
import com.example.concertticketing.domain.exception.ErrorEnum;
import com.example.concertticketing.domain.member.model.Member;
import com.example.concertticketing.domain.member.repository.MemberRepository;
import com.example.concertticketing.domain.queue.model.Queue;
import com.example.concertticketing.domain.queue.model.QueueStatus;
import com.example.concertticketing.domain.queue.repository.QueueRepository;
import jakarta.persistence.EntityManager;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.web.client.TestRestTemplate;
import org.springframework.boot.test.web.server.LocalServerPort;
import org.springframework.core.ParameterizedTypeReference;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.ResponseEntity;

import java.time.LocalDateTime;
import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;

/**
 * TestRestTemplate 사용 시 RANDOM_PORT 아니면 Bean 주입 안됨
 * */
@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
public class ConcertControllerIntegrateTest {

    @LocalServerPort
    private int port;

    @Autowired
    private TestRestTemplate restTemplate;

    @Autowired
    private MemberRepository memberRepository;

    @Autowired
    private ConcertRepository concertRepository;

    @Autowired
    private QueueRepository queueRepository;

    @Autowired
    private SeatRepository seatRepository;

    @Autowired
    private EntityManager entityManager;

    void setUpConcert() {
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

    @AfterEach
    void tearDown() {
        queueRepository.deleteAllInBatch();
        concertRepository.deleteAllInBatch();
        memberRepository.deleteAllInBatch();
    }

    Long findFirstConcertId() {
        return (Long) entityManager.createNativeQuery("SELECT concert_id FROM CONCERT LIMIT 1")
                .getSingleResult();
    }

    Long findFirstConcertDetailId() {
        return (Long) entityManager.createNativeQuery("SELECT concert_detail_id FROM CONCERT_DETAIL LIMIT 1")
                .getSingleResult();
    }

    Long findFirstMemberId() {
        return (Long) entityManager.createNativeQuery("SELECT id FROM MEMBER LIMIT 1")
                .getSingleResult();
    }

    @DisplayName("예약 가능한 날짜 목록을 조회한다")
    @Test
    void getAvailableDates() {
        // given
        setUpConcert();
        Long concertId = findFirstConcertId();
        Long concertDetailId = findFirstConcertDetailId();
        Long memberId = findFirstMemberId();
        String url = "http://localhost:" + port + "/api/concert/date/" + concertId;

        // HttpEntity 에 헤더 포함
        HttpEntity<String> header = setHeader(memberId);

        // when
        ResponseEntity<CommonResponse<ConcertDate>> response = restTemplate.exchange(url, HttpMethod.GET, header, new ParameterizedTypeReference<>() {});
        CommonResponse<ConcertDate> body = response.getBody();
        ConcertDate concertDate = body.getData();

        // then
        assertThat(body.getCode()).isEqualTo(ErrorEnum.SUCCESS.getCode());
        assertThat(body.getMessage()).isEqualTo(ErrorEnum.SUCCESS.getMessage());
        assertThat(concertDate.concertId()).isEqualTo(concertId);
        assertThat(concertDate.concertDates().size()).isEqualTo(3);
        assertThat(concertDate.concertDates().get(0)).isInstanceOf(ConcertDateDetails.class);
        assertThat(concertDate.concertDates().get(1).concertDetailId()).isEqualTo(concertDetailId + 1);
        assertThat(concertDate.concertDates().get(1).dates()).isEqualTo(LocalDateTime.of(2024, 6, 12, 0, 0, 0).plusDays(1));
    }

    @DisplayName("예약 가능한 날짜 목록 조회 시 토큰이 없어 실패한다")
    @Test
    void getAvailableDates_fail_no_token() {
        // given
        setUpConcert();
        Long concertId = findFirstConcertId();
        Long memberId = findFirstMemberId() + 1;
        String url = "http://localhost:" + port + "/api/concert/date/" + concertId;

        // HttpEntity 에 헤더 포함
        HttpEntity<String> header = setHeader(memberId);

        // when
        ResponseEntity<CommonResponse<ConcertDate>> response = restTemplate.exchange(url, HttpMethod.GET, header, new ParameterizedTypeReference<>() {});
        CommonResponse<ConcertDate> body = response.getBody();
        ConcertDate concertDate = body.getData();

        // then
        assertThat(body.getCode()).isEqualTo(ErrorEnum.TOKEN_EXPIRED.getCode());
        assertThat(body.getMessage()).isEqualTo(ErrorEnum.TOKEN_EXPIRED.getMessage());
        assertThat(concertDate).isNull();
    }

    @DisplayName("예약 가능한 좌석 목록을 조회한다")
    @Test
    void getAvailableSeats() {
        // given
        setUpConcert();
        Long concertDetailId = findFirstConcertDetailId();
        Long memberId = findFirstMemberId();
        String url = "http://localhost:" + port + "/api/concert/seat/" + concertDetailId;

        // HttpEntity 에 헤더 포함
        HttpEntity<String> header = setHeader(memberId);

        // when
        ResponseEntity<CommonResponse<ConcertSeat>> response = restTemplate.exchange(url, HttpMethod.GET, header, new ParameterizedTypeReference<>() {});
        CommonResponse<ConcertSeat> body = response.getBody();
        ConcertSeat concertDate = body.getData();

        // then
        assertThat(body.getCode()).isEqualTo(ErrorEnum.SUCCESS.getCode());
        assertThat(body.getMessage()).isEqualTo(ErrorEnum.SUCCESS.getMessage());
        assertThat(concertDate.concertDetailId()).isEqualTo(concertDetailId);
        assertThat(concertDate.concertSeats().size()).isEqualTo(5);
        assertThat(concertDate.concertSeats().get(0)).isInstanceOf(ConcertSeatDetail.class);
        assertThat(concertDate.concertSeats().get(0).seatNo()).isEqualTo(1);
    }

    @DisplayName("예약 가능한 좌석 목록을 조회 시 토큰이 없어 실패한다")
    @Test
    void getAvailableSeats_fail_no_token() {
        // given
        setUpConcert();
        Long concertDetailId = findFirstConcertDetailId();
        Long memberId = findFirstMemberId() + 1;
        String url = "http://localhost:" + port + "/api/concert/seat/" + concertDetailId;


        // HttpEntity 에 헤더 포함
        HttpEntity<String> header = setHeader(memberId);

        // when
        ResponseEntity<CommonResponse<ConcertSeat>> response = restTemplate.exchange(url, HttpMethod.GET, header, new ParameterizedTypeReference<>() {});
        CommonResponse<ConcertSeat> body = response.getBody();
        ConcertSeat concertDate = body.getData();

        // then
        assertThat(body.getCode()).isEqualTo(ErrorEnum.TOKEN_EXPIRED.getCode());
        assertThat(body.getMessage()).isEqualTo(ErrorEnum.TOKEN_EXPIRED.getMessage());
        assertThat(concertDate).isNull();
    }

    private HttpEntity<String> setHeader(Long memberId) {
        // 헤더 설정
        HttpHeaders headers = new HttpHeaders();
        headers.set("memberId", String.valueOf(memberId));

        return new HttpEntity<>(headers);
    }
}
