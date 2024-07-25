## OPTIMISTIC
- 해당 락은 트랜잭션이 자주 충돌하지 않을 것으로 가정하고 작동한다.
- 버전을 이용해 데이터가 읽힌 시점과 실제 업데이트 시점의 값을 비교해 데이터 무결성을 보장한다.
- OPTIMISTIC 은 버전으로 비교할 뿐 실제로 락을 거는 방식이 아니다
  - PESSIMISTIC 보다 빠를 수 있다
  - 디비 커넥션을 잡지 않아 서버에 부하가 덜하다

## OPTIMISTIC_FORCE_INCREMENT
- 데이터를 읽는 시점에 버전을 강제로 증가시킨다.

## PESSIMISTIC_READ
- READ 만 가능하다. 첫번째 스레드만 쓸 수 있고 나머지는 데드락 발생하며 롤백된다 
  - 필요 시 retry 로 트랜재션 재획득 필요
- select for share


## PESSIMISTIC_WRITE
- 한 스레드가 락을 획득하면 트랜잭션이 끝날 때까지 다른 스레드는 락을 획득할 수 없다
- select for update 

## PESSIMISTIC_FORCE_INCREMENT
- 10개의 스레드가 진입하더라도 select 에서 락을 획득할 수 없으면 모두 실패한다
- select for update nowait 
  - select 할 때 Lock 을 제어할 수 없으면 에러처리

## 트랜잭션이 FACADE 에 있는 경우
조회, 검증 예약을 파사드 계층에서 하나의 트랜잭션으로 관리하는 경우이다
```java
@Transactional
public Reservation reserveSeat(Long seatId, Long memberId) {
        log.info(Thread.currentThread().getName() + "---------------------- 시작");
        Seat seat = seatService.selectSeatWithLock(seatId);
        log.info(Thread.currentThread().getName() + "--------------- 좌석 조회");
        
        // 검증
        ================================================================
        
        // 5분동안 임시저장
        LocalDateTime reservedAt = seat.getReservedAt();
        Reservation.checkTempReserved(reservedAt);
        
        ================================================================

        seatService.reserveSeat(seat, LocalDateTime.now(), memberId);

        Reservation reservation = reservationService.reserveSeat(seat, memberId);
        log.info(Thread.currentThread().getName() + "---------------------- 완료");
        return reservation;
    }
}
```

### OPTIMISTIC
스레드가 동시에 메서드에 진입하고 동시에 조회한다. 트랜잭션이 끝날 때 버전을 보고 롤백하므로 <br>
Seat 수정 및 Reservation insert 쿼리까지 모두 실행 후 롤백한다
```
스레드1 시작
스레드2 시작

스레드1 좌석 조회
스레드2 좌석 조회

스레드1 완료 - 버전 증가
스레드2 완료 - 버전이 달라 롤백

Elapsed Time: 154 ms
```

### OPTIMISTIC_FORCE_INCREMENT
OPTIMISTIC 과 동일하게 모든 로직 실행 후 롤백한다
```
스레드1 시작
스레드2 시작

스레드1 좌석 조회 - 버전 증가
스레드2 좌석 조회 - 버전 증가

스레드1 완료
스레드2 완료 - 버전이 달라 롤백

Elapsed Time: 176 ms
```

### PESSIMISTIC_READ
스레드가 읽기는 가능하므로 동시에 읽는다. 하지만 쓰기가 불가능해 첫번째 쓰기를 수행하는 스레드만 살고 <br>
다른 스레드는 데드락이 걸리며 롤백 처리된다
```
스레드1 시작
스레드2 시작

스레드1 좌석 조회
스레드2 좌석 조회

스레드1 완료
스레드2 완료

Elapsed Time: 214 ms
```

### PESSIMISTIC_WRITE
읽기도 막는 락으로 첫번째 스레드가 락을 획득하면 다른 스레드는 조회도 불가하다.<br>
첫번째 스레드 완료 후 다른 스레드가 조회를 시작하는데 검증 로직에서 막혀 완료까지 내려가지 못한다

```
스레드1 시작
스레드2 시작

스레드1 좌석 조회
스레드1 완료

스레드2 좌석 조회
스레드2 롤백

Elapsed Time: 108 ms
```

### PESSIMISTIC_FORCE_INCREMENT
첫번째 스레드가 락을 획득하고 다른 스레드가 락에 접근한다.<br>
이 때, 락 획득 실패시 모두 롤백하므로 다른 스레드는 로직을 수행하지 못한다
```
스레드1 시작
스레드2 시작

스레드1 좌석 조회
스레드2 좌석 조회 - 실패

스레드1 완료

Elapsed Time: 108 ms
```

## 트랜잭션이 SERVICE 에 있는 경우
FACADE 에 있는 @Transactional 을 service 로 내려 트랜잭션의 범위를 축소하였다. <br>
위와 달리 객체 전달 시 서비스에서 유지가 되지 않아 id를 넘기고 서비스에서 재조회 하는 방식으로 수정하였다. <br>

그리고 PESSIMISTIC_READ 의 경우는 수정 시에 데드락이 발생하는데 상단에서 락을 걸면 <br>
seatService.reserveSeat 수행 시에는 락이 존재하지 않아 모든 수정이 발생했다. <br>
이를 막기 위해 처음 조회는 락을 하지 않고 seatService.reserveSeat 에서 seat 조회 시 락을 통해 제어하였다 

```java
public Reservation reserveSeat(Long seatId, Long memberId) {
        log.info(Thread.currentThread().getName() + "---------------------- 시작");
        // @ Transactional
        Seat seat = seatService.selectSeatWithLock(seatId);
        // Seat seat = seatService.selectSeat(seatId); // PESSIMISTIC_READ 락일 경우 

        log.info(Thread.currentThread().getName() + "--------------- 좌석 조회");

        // 5분동안 임시저장
        LocalDateTime reservedAt = seat.getReservedAt();
        Reservation.checkTempReserved(reservedAt);
        
        // @ Transactional
        seatService.reserveSeat(seatId, LocalDateTime.now(), memberId);
        
        log.info(Thread.currentThread().getName() + "---------------------- 좌석 수정");

        // @ Transactional
        Reservation reservation = reservationService.reserveSeat(seat, memberId); 
        log.info(Thread.currentThread().getName() + "---------------------- 완료");
        return reservation;
    }
}
```

### OPTIMISTIC
FACADE 에 있을때와 달리 좌석 수정 쿼리까지는 나간다. 하지만 수정 시점에 버전 체크를 통해 다른 스레드는 롤백된다
```
스레드1 시작
스레드2 시작

스레드1 좌석 조회
스레드2 좌석 조회

스레드1 좌석 수정 - 버전 증가
스레드2 좌석 수정 - 실패

스레드1 완료

Elapsed Time: 218 ms
```

### OPTIMISTIC_FORCE_INCREMENT
좌석 조회 시점에 버전이 바로 증가한다. 따라서, 다른 메서드들은 좌석 조회부터 불가하다
```
스레드1 시작
스레드2 시작

스레드1 좌석 조회 - 버전 증가
스레드2 좌석 조회 - 실패

스레드1 완료

Elapsed Time: 168 ms
```

### PESSIMISTIC_READ
seatService.reserveSeat 에서 스레드를 롤백하므로 검증 로직까지는 진행되고 <br>
수정에서 데드락 발생하며 실패한다.
```
스레드1 시작
스레드2 시작

스레드1 좌석 조회
스레드2 좌석 조회

스레드1 좌석 수정
스레드2 좌석 수정 - 실패

스레드1 완료

Elapsed Time: 173 ms
```

### PESSIMISTIC_WRITE
하나의 스레드가 완료된 후 다른 스레드가 로직을 수행한다. 하지만, 검증에서 실패하므로 하위 로직을 실행하지 못한다 <br>
위 코드에서 seatService.selectSeatWithLock 부터 seatService.reserveSeat 를 하나의 트랜잭션으로 묶어야 해서 <br>
해당범위 seatService 로 옮겨 리팩토링 하였습니다
```
스레드1 시작
스레드2 시작

스레드1 좌석 조회

스레드1 완료

스레드2 좌석 조회
스레드2 - 검증로직 에서 실패

Elapsed Time: 133 ms
```
### PESSIMISTIC_FORCE_INCREMENT
첫번째 스레드가 좌석 조회 시 락을 획득하고 버전이 증가하므로 다른 스레드는 좌석 조회에서 롤백된다
```
스레드1 시작
스레드2 시작

스레드1 좌석 조회
스레드2 좌석 조회 - 실패

스레드1 완료

Elapsed Time: 173 ms
```

## 결과
위 실험은 10개의 스레드를 생성 후 테스트 했다. <br>
테스트 결과, 나는 트랜잭션은 SERVICE 에 걸고 락은 OPTIMISTIC_FORCE_INCREMENT 로 하는게 가장 좋다고 결론을 내렸다. <br>

이유는 나의 비즈니스 로직은 좌석을 예약 하면 검증 후 예약 로그를 찍고 임시 예약상태로 변경하는데 <br>
실패하더라도 로직이 예약이 갖는 책임이 적고 디비나 서버가 죽지 않는 이상 실패할 확률이 낮다고 생각한다 <br> 
콘서트 특성 상 예약을 누르고 안되면 바로 다른 좌석을 찾아서 예약 하는데 현 비즈니스 로직에 스레드를 <br>
바로 튕겨주는 것이 고객이 바로 다른 좌석 예약을 시도할 수 있어 기다리다 실패하는 것 보단 빠른 실패가 더 적합하다고 생각한다. <br>

그리고, 파사드에 트랜젝션을 걸면 낙관락의 경우 모든 로직을 수행 후 커밋 시점에 롤백을 하는데 속도가 더 느리다. <br>
그럼 비관적락을 이용해 실패 시점을 조회시점으로 땡길 수 있는데 디비 부하를 생각하면 실패 시점이 같은 <br>
SERVICE 에서 낙관락을 사용하는 것이 더 적합하다고 판단하였다. 

따라서, OPTIMISTIC_FORCE_INCREMENT 을 사용했을 때 조회 시점부터 실패하므로 속도도 나쁘지 않고 <br>
retry 도 필요하지 않아 디비에 부하가 갈 일도 적다. 부하 테스트는 아직 하는법을 몰라서 <br>
많은 트래픽이 몰릴 경우의 테스트는 다음에 테스트 해보고 업데이트 할 예정이다.
