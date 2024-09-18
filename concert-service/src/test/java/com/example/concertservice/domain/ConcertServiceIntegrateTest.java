//package com.example.concertservice.domain;
//
//import org.junit.jupiter.api.DisplayName;
//import org.junit.jupiter.api.Test;
//import org.springframework.beans.factory.annotation.Autowired;
//import org.springframework.boot.test.context.SpringBootTest;
//import org.springframework.test.context.ActiveProfiles;
//
//import java.util.ArrayList;
//import java.util.List;
//import java.util.concurrent.CompletableFuture;
//
//
///**
// * 더미 데이터 삽입으로 local 사용
// * */
//@ActiveProfiles("local")
//@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
//class ConcertServiceIntegrateTest {
//
//    @Autowired
//    private ConcertServiceImpl concertService;
//
//    @DisplayName("콘서트 별 예약 가능한 날짜를 비동기로 조회한다")
//    @Test
//    void selectAvailableDates() {
//        // given
//        Long concertId = 1L;
//
//        // when
//        List<CompletableFuture<Void>> futures = new ArrayList<>();
//
//        for (int i = 0; i < 100000; i++) {
//            futures.add(CompletableFuture.runAsync(() -> concertService.selectAvailableDates(concertId)));
//        }
//
//        futures.stream()
//                .forEach(future -> {
//                    try {
//                        future.join();
//                    } catch (Exception e) {
//                        System.out.println("Error : " + e.getMessage());
//                    }
//                });
//    }
//}
