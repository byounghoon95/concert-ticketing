package com.example.memberservice.util;

import com.example.memberservice.exception.CustomException;
import com.example.memberservice.exception.ErrorEnum;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.springframework.stereotype.Component;

@Component
public class JsonConverter {

    private final ObjectMapper objectMapper;

    public JsonConverter(ObjectMapper objectMapper) {
        this.objectMapper = objectMapper;
    }

    public <T> T fromJson(String json, Class<T> clazz) {
        T object;
        try {
            object = objectMapper.readValue(json, clazz);
        } catch (JsonProcessingException e) {
            throw new CustomException(ErrorEnum.CONVERT_ERROR);
        }

        return object;
    }

    public String toJson(Object obj) {
        String str;
        try {
            str = objectMapper.writeValueAsString(obj);
        } catch (JsonProcessingException e) {
            throw new CustomException(ErrorEnum.CONVERT_ERROR);
        }

        return str;
    }
}
