//package com.example.queueservice;
//
//import com.example.queueservice.domain.concert.service.ConcertServiceImpl;
//import com.example.queueservice.domain.member.service.MemberServiceImpl;
//import com.example.queueservice.domain.pay.service.PayServiceImpl;
//import com.example.queueservice.domain.queue.service.QueueServiceImpl;
//import com.example.queueservice.domain.reservation.application.ReservationFacade;
//import com.example.queueservice.interfaces.api.concert.ConcertController;
//import com.example.queueservice.interfaces.api.member.MemberController;
//import com.example.queueservice.interfaces.api.pay.PayController;
//import com.example.queueservice.interfaces.api.queue.QueueController;
//import com.example.queueservice.interfaces.api.reservation.ReservationController;
//import com.fasterxml.jackson.databind.ObjectMapper;
//import org.springframework.beans.factory.annotation.Autowired;
//import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
//import org.springframework.boot.test.mock.mockito.MockBean;
//import org.springframework.test.context.ActiveProfiles;
//import org.springframework.test.web.servlet.MockMvc;
//
//@ActiveProfiles("test")
//@WebMvcTest(controllers = {
//        ConcertController.class,
//        PayController.class,
//        MemberController.class,
//        QueueController.class,
//        ReservationController.class,
//})
//public abstract class CommonControllerTest {
//    @Autowired
//    protected MockMvc mockMvc;
//
//    @Autowired
//    protected ObjectMapper objectMapper;
//
//    @MockBean
//    protected MemberServiceImpl memberService;
//
//    @MockBean
//    protected QueueServiceImpl queueService;
//
//    @MockBean
//    protected ConcertServiceImpl concertService;
//
//    @MockBean
//    protected ReservationFacade reservationFacade;
//
//    @MockBean
//    protected PayServiceImpl payService;
//}
