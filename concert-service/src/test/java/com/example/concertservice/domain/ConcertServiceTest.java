//package com.example.concertservice.domain;
//
//import com.example.concertticketing.domain.concert.model.ConcertDate;
//import com.example.concertticketing.domain.concert.model.ConcertDetail;
//import com.example.concertticketing.domain.concert.model.ConcertSeat;
//import com.example.concertticketing.domain.concert.model.Seat;
//import com.example.concertticketing.domain.concert.repository.ConcertRepository;
//import org.junit.jupiter.api.DisplayName;
//import org.junit.jupiter.api.Test;
//import org.junit.jupiter.api.extension.ExtendWith;
//import org.mockito.InjectMocks;
//import org.mockito.Mock;
//import org.mockito.junit.jupiter.MockitoExtension;
//import org.springframework.test.context.ActiveProfiles;
//
//import java.time.LocalDateTime;
//import java.time.format.DateTimeFormatter;
//import java.util.ArrayList;
//import java.util.List;
//
//import static org.mockito.ArgumentMatchers.any;
//import static org.mockito.Mockito.when;
//
//@ActiveProfiles("test")
//@ExtendWith(MockitoExtension.class)
//class ConcertServiceTest {
//
//    @Mock
//    private ConcertRepository concertRepository;
//
//    @InjectMocks
//    private ConcertServiceImpl concertService;
//
//    @DisplayName("콘서트 별 예약 가능한 날짜를 조회한다")
//    @Test
//    void selectAvailableDates() {
//        // given
//        Long concertId = 1L;
//        Long concertDetailId = 1L;
//        LocalDateTime today = LocalDateTime.now();
//
//        List<ConcertDetail> details = new ArrayList<>();
//        for (int i = 0; i < 3; i++) {
//            ConcertDetail concert = ConcertDetail.builder()
//                    .id(concertDetailId + i)
//                    .date(today.plusDays(i))
//                    .build();
//
//            details.add(concert);
//        }
//
//
//        DateTimeFormatter formatter = DateTimeFormatter.ISO_DATE_TIME;
//
//        // when
//        when(concertRepository.findConcertDates(any())).thenReturn(details);
//
//        ConcertDate concertDates = concertService.selectAvailableDates(concertId);
//
//        // then
//        assertThat(concertDates.concertId()).isEqualTo(concertId);
//        assertThat(concertDates.concertDates().get(0).concertDetailId()).isEqualTo(1L);
//        assertThat(concertDates.concertDates().get(0).dates()).isEqualTo(today.format(formatter));
//    }
//
//    @DisplayName("콘서트 별 예약 가능한 좌석을 조회한다")
//    @Test
//    void getAvailableSeats() {
//        // given
//        Long concertDetailId = 1L;
//        Long seatId = 1L;
//        int seatNo = 1;
//
//        List<Seat> response = new ArrayList<>();
//        for (int i = 0; i < 3; i++) {
//            Seat seat = Seat.builder()
//                    .id(seatId + i)
//                    .seatNo(seatNo + i)
//                    .build();
//
//            response.add(seat);
//        }
//
//        // when
//        when(concertRepository.findAvailableSeats(any(),any())).thenReturn(response);
//
//        ConcertSeat concertSeats = concertService.selectAvailableSeats(concertDetailId);
//
//        // then
//        assertThat(concertSeats.concertDetailId()).isEqualTo(concertDetailId);
//        assertThat(concertSeats.concertSeats().get(0).seatId()).isEqualTo(1L);
//        assertThat(concertSeats.concertSeats().get(0).seatNo()).isEqualTo(1);
//    }
//}