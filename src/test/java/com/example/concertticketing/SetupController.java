package com.example.concertticketing;

import com.example.concertticketing.domain.concert.model.Concert;
import com.example.concertticketing.domain.concert.model.ConcertDetail;
import com.example.concertticketing.domain.concert.model.Seat;
import com.example.concertticketing.domain.concert.repository.ConcertRepository;
import com.example.concertticketing.domain.concert.repository.SeatRepository;
import com.example.concertticketing.domain.member.model.Member;
import com.example.concertticketing.domain.member.repository.MemberRepository;
import com.example.concertticketing.domain.pay.repository.PayRepository;
import com.example.concertticketing.domain.queue.repository.QueueRepository;
import com.example.concertticketing.domain.reservation.repository.ReservationRepository;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import java.time.LocalDateTime;
import java.time.ZoneOffset;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
public class SetupController {

    @Autowired
    private ConcertRepository concertRepository;
    @Autowired
    private QueueRepository queueRepository;
    @Autowired
    private MemberRepository memberRepository;
    @Autowired
    private SeatRepository seatRepository;
    @Autowired
    private ReservationRepository reservationRepository;
    @Autowired
    private PayRepository payRepository;

    @Test
    void setUpDB() {
        setUpMember();
        setUpConcert();
        setUpSeat();
    }

    @Test
    void setUpRedis() {
        Set<String> values = new HashSet<>();
        for (int i = 1; i <= 20000; i++) {
            String value = i + ":" + LocalDateTime.now().plusMinutes(1).toEpochSecond(ZoneOffset.UTC);
            values.add(value);
        }

        queueRepository.addActiveQueues(values);
    }

    private void setUpMember() {
        List<Member> members = new ArrayList<>();
        for (int i = 0; i < 20000; i++) {
            members.add(createMember("A" + i, 10000L));
        }

        memberRepository.saveAll(members);
    }

    private void setUpConcert() {
        List<ConcertDetail> details = new ArrayList<>();

        for (int i = 0; i < 1000; i++) {
            Concert concert = concertRepository.saveConcert(createConcert("A" + i));
            for (int j = 0; j < 10; j++) {
                details.add(createConcertDetail(concert, "A" + i + j, LocalDateTime.now()));
            }
        }

        concertRepository.saveConcertDetailAll(details);
    }

    private void setUpSeat() {
        List<Seat> seats = new ArrayList<>();
        for (int i = 1; i <= 1000; i++) {
            ConcertDetail concertDetail = concertRepository.findConcertDetail(Long.valueOf(i));
            for (int j = 0; j < 50; j++) {
                seats.add(createSeat(concertDetail, null, j + 1, 5000L, null));
            }
        }

        seatRepository.saveAll(seats);
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
}
