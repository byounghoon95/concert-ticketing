# 주요기능
### 유저 토큰 발급
- 서비스를 이용할 토큰을 발급받는다
- 토큰은 유저의 UUID 와 해당 유저의 대기열을 관리할 수 있는 정보(대기 순서 or 잔여 시간 등) 를 포함한다
- 이후 모든 API 는 위 토큰을 이용해 대기열 검증을 통과해야 이용 가능하다

### 예약 가능 날짜 / 좌석
- 예약가능한 날짜와 해당 날짜의 좌석을 각각 조회한다
- 예약 가능한 날짜 목록을 조회할 수 있다
- 날짜 정보를 입력받아 예약가능한 좌석정보를 조회할 수 있다

### 좌석 예약 요청
- 날짜와 좌석 정보를 입력받아 좌석을 예약 처리한다
- 좌석 예약과 동시에 해당 좌석은 그 유저에게 n분간 임시 배정된다
- 만약 배정 시간 내에 결제가 완료되지 않는다면 좌석에 대한 임시 배정은 해제되어야 하며 만약 임시배정된 상태라면 다른 사용자는 예약할 수 없어야 한다

### 잔액 충전 / 조회
- 결제에 사용될 금액을 충전한다
- 사용자 식별자 및 충전할 금액을 받아 잔액을 충전한다
- 사용자 식별자를 통해 해당 사용자의 잔액을 조회한다

### 결제
- 결제 처리하고 결제 내역을 생성한다
- 결제가 완료되면 해당 좌석의 소유권을 유저에게 배정하고 대기열 토큰을 만료시킨다

# MileStone
깃허브 마일스톤으로 작성<br>
Date fields 에서 start 와 end 설정하면 조회 가능합니다<br>
https://github.com/users/byounghoon95/projects/7

# ERD
![ERDv1.png](src%2Fmain%2Fresources%2Fdocs%2FERDv1.png)

# Sequence Diagram
![sequence-diagram.jpg](src%2Fmain%2Fresources%2Fdocs%2Fsequence-diagram.jpg)

# API 명세
### [바로가기](https://github.com/byounghoon95/concert-ticketing/tree/master/src/main/resources/docs/api-docs.md)