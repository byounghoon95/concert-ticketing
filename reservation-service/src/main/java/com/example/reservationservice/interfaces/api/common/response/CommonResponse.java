package com.example.reservationservice.interfaces.api.common.response;

import com.example.reservationservice.exception.ErrorEnum;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@NoArgsConstructor
public class CommonResponse<T> {
    private String code;
    private String message;
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
