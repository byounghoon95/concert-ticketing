package com.example.concertticketing.util;

import com.example.concertticketing.exception.CustomException;
import com.example.concertticketing.exception.ErrorEnum;
import com.slack.api.Slack;
import com.slack.api.methods.MethodsClient;
import com.slack.api.methods.request.chat.ChatPostMessageRequest;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

@Slf4j
@Component
public class SlackClient {

    @Value("${slack.token}")
    private String token;

    @Value("${slack.channel}")
    private String channel;

    public void sendMessage(String message) {
        try {
            MethodsClient methods = Slack.getInstance().methods(token);

            ChatPostMessageRequest request = ChatPostMessageRequest.builder()
                    .channel(channel)
                    .text(message)
                    .build();

            methods.chatPostMessage(request);
            log.info("Slack - Test Message 전송 완료 : {}", message);
        } catch (Exception e) {
            throw new CustomException(ErrorEnum.SLACK_ERROR);
        }
    }
}
