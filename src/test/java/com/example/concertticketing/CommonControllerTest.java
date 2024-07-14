package com.example.concertticketing;

import com.example.concertticketing.api.member.MemberController;
import com.example.concertticketing.api.queue.QueueController;
import com.example.concertticketing.domain.member.service.MemberServiceImpl;
import com.example.concertticketing.domain.queue.service.QueueServiceImpl;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.test.web.servlet.MockMvc;

@WebMvcTest(controllers = {
        MemberController.class,
        QueueController.class,
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

}
