package com.example.concertticketing;

import com.example.concertticketing.api.concert.ConcertController;
import com.example.concertticketing.api.member.MemberController;
import com.example.concertticketing.api.pay.PayController;
import com.example.concertticketing.api.queue.QueueController;
import com.example.concertticketing.api.reservation.ReservationController;
import com.example.concertticketing.domain.concert.service.ConcertServiceImpl;
import com.example.concertticketing.domain.member.service.MemberServiceImpl;
import com.example.concertticketing.domain.pay.service.PayServiceImpl;
import com.example.concertticketing.domain.queue.service.QueueServiceImpl;
import com.example.concertticketing.domain.reservation.service.ReservationServiceImpl;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.test.web.servlet.MockMvc;

@WebMvcTest(controllers = {
        ConcertController.class,
        PayController.class,
        MemberController.class,
        QueueController.class,
        ReservationController.class,
})
public abstract class CommonControllerTest {
    @Autowired
    protected MockMvc mockMvc;

    @Autowired
    protected ObjectMapper objectMapper;

    @MockBean
    protected MemberServiceImpl memberService;

    @MockBean
    protected QueueServiceImpl queueService;

    @MockBean
    protected ConcertServiceImpl concertService;

    @MockBean
    protected ReservationServiceImpl reservationService;

    @MockBean
    protected PayServiceImpl payService;
}
