package com.example.concertticketing.domain.common.response;

import com.example.concertticketing.domain.exception.ErrorEnum;
import com.fasterxml.jackson.annotation.JsonIgnore;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@NoArgsConstructor
public class CommonResponse<T> {
    private String code;
    private String message;
    @JsonIgnore
    private T data;

    @Builder
    public CommonResponse(String success, String message, T data) {
        this.code = success;
        this.message = message;
        this.data = data;
    }

    public static <T> CommonResponse<T> success(T data) {
        return new CommonResponse<>(ErrorEnum.SUCCESS.getCode(), ErrorEnum.SUCCESS.getMessage(), data);
    }

    public static <T> CommonResponse<T> error(String code, String message) {
        return new CommonResponse<>(code, message, null);
    }
}
