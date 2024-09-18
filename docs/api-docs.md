# 콘서트 예약 시스템 API

### 목차
1. [대기열 토큰 발급](#1-대기열-토큰-발급)
2. [대기열 토큰 조회](#2-대기열-토큰-조회)
3. [예약 가능 날짜 조회](#3-예약-가능-날짜-조회)
4. [예약 가능 좌석 조회](#4-예약-가능-좌석-조회)
5. [좌석 예약 요청](#5-좌석-예약-요청)
6. [잔액 조회](#6-잔액-조회)
7. [잔액 충전](#7-잔액-충전)
8. [결재](#8-결재)

## 1. 대기열 토큰 발급
- 대기열에 사용자를 추가하고 대기열 토큰을 반환한다
### Request
- URL: /api/queue/issue
- Method: POST
- Params
   ``` json
    {
        "memberId" : "String"
    }
    ```
### Response
``` json
{
    "code": "0000",
    "message": "SUCCESS",
    "data": {
        "token": "61313233-3334-3132-3331-310000000000",
        "memberId": 1,
        "position": 0,
        "status": "ACTIVE"
    }
}
```

## 2. 대기열 토큰 조회
- 유저의 발급된 토큰 정보를 조회한다
### Request
- URL: /api/queue/{memberId}
- Method: GET
- Params
    - Long : `memberId` (유저 아이디)
### Response
``` json
{
    "code": "0000",
    "message": "SUCCESS",
    "data": {
        "token": "61313233-3334-3132-3331-310000000000",
        "memberId": 1,
        "position": 0,
        "status": "ACTIVE"
    }
}
```

## 3. 예약 가능 날짜 조회
- 콘서트의 예약 가능한 날짜를 조회한다
### Request
- URL: /api/concert/date/{concertId}
- Method: GET
- Params
    - Long : `concertId` (콘서트 아이디)
### Response
``` json
    {
        "code": "0000",
        "message": "SUCCESS",
        "data": {
            "concertId": 1,
            "concertDates": [
                {
                    "concertDetailId": 1,
                    "dates": "2024-07-06T12:34:56"
                },
                {
                    "concertDetailId": 2,
                    "dates": "2024-07-07T12:34:56"
                }
            ]
        }
    }
```

## 4. 예약 가능 좌석 조회
- 콘서트의 예약 가능한 좌석을 조회한다
### Request
- URL: /api/concert/seat/{concertDetailId}
- Method: GET
- Params
    - Long : `concertDetailId` (콘서트 상세 아이디)
### Response
``` json
{
    "code": "0000",
    "message": "SUCCESS",
    "data": {
        "concertDetailId": 1,
        "concertSeats": [
            {
                "seatId": 1,
                "seatNo": 1
            },
            {
                "seatId": 2,
                "seatNo": 2
            }
        ]
    }
}
```

## 5. 좌석 예약 요청
- 콘서트 좌석을 예약한다
### Request
- URL: /api/reserve
- Method: POST
- Params
   ``` json
    {
        "seatId" : "Long",
        "memberId" : "Long"
    }
    ```
### Response
``` json
{
    "code": "0000",
    "message": "SUCCESS",
    "data": {
        "concertDetailId": 1,
        "concertSeats": [
            {
                "seatId": 1,
                "seatNo": 1
            },
            {
                "seatId": 2,
                "seatNo": 2
            }
        ]
    }
}
```

## 6. 잔액 조회
- 유저의 현재 잔액을 조회한다
### Request
- URL: /api/member/balance/{memberId}
- Method: GET
- Params
  - Long : `memberId` (유저 아이디)
### Response
``` json
{
    "code": "0000",
    "message": "SUCCESS",
    "data": {
        "balance": 5000
    }
}
```

## 7. 잔액 충전
- 유저의 잔액을 충전한다
### Request
- URL: /api/member/balance
- Method: POST
- Params
  ``` json
  {
      "memberId" : "Long",
      "balance" : "Long"
  }
  ```
### Response
``` json
{
    "code": "0000",
    "message": "SUCCESS",
    "data": {
        "balance": 6500
    }
}
```

## 8. 결재
- 예약된 좌석을 결재한다
### Request
- URL: /api/pay
- Method: POST
- Params
  ``` json
  {
      "memberId" : "Long",
      "seatId" : "Long",
      "reservationId" : "Long"
  }
  ```
### Response
``` json
{
    "code": "0000",
    "message": "SUCCESS",
    "data": {
        "amount": 4000,
        "seatNo": 1
    }
}
```