//package com.example.reservationservice;
//
//import com.example.concertticketing.domain.concert.service.ConcertServiceImpl;
//import com.example.concertticketing.domain.member.service.MemberServiceImpl;
//import com.example.concertticketing.domain.pay.service.PayServiceImpl;
//import com.example.concertticketing.domain.queue.service.QueueServiceImpl;
//import com.example.concertticketing.domain.reservation.application.ReservationFacade;
//import com.example.concertticketing.interfaces.api.concert.ConcertController;
//import com.example.concertticketing.interfaces.api.member.MemberController;
//import com.example.concertticketing.interfaces.api.pay.PayController;
//import com.example.concertticketing.interfaces.api.queue.QueueController;
//import com.example.concertticketing.interfaces.api.reservation.ReservationController;
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
