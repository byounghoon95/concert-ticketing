# 주요 API
- 유저 토큰 발급
- 콘서트 예약 가능 날짜 / 좌석
- 좌석 예약 요청
- 잔액 충전/조회
- 결제

주요 API 는 총 5개이다.<br>
캐시는 아래와 같은 상황에서 자주 사용한다 <br>
이를 기반으로 어디에 캐싱을 적용할 수 있을지 알아보자
- 데이터가 자주 변경되지 않는가?
- 데이터가 자주 조회 되는가?
- 계산이 복잡한가?

# 캐시 적용
### 유저 대기열 토큰 기능 `부적합`
레디스로 구현되어 캐싱 처리를 할 필요가 없다

### 콘서트 예약 가능 날짜 `적합`
콘서트 별 예약 가능한 날짜를 조회한다 <br>
한번 값이 등록되면 다른 콘서트가 등록되기 까지 시간이 걸린다 <br>
또한, 예약이나 결재 등 작업을 했을 때 콘서트의 데이터 정합성에는 영향이 가지 않기에 <br>
한번의 캐싱으로 수많은 트래픽을 처리할 수 있어 캐싱을 적용하기 적합하다

### 콘서트 예약 가능 좌석 `부적합`
위 3가지 요소와 비교하면 좌석 예약은 캐싱에 부적합하다 <br>
예약이 어느 시간마다 발생할지 모르지만 수많은 사람이 몰리는 경우 10초를 넘기진 않을 것이다 <br>
그럼 해당 API 의 캐시는 10초 짜리 캐시가 되고 별로 효용성을 느끼지 못할 수 있다 <br>

하지만 콘서트의 경우 누를 당시 예약 가능하더라도 그 사이에 예약이 되는 경우가 빈번히 발생한다 <br>
그러면 캐시를 짧은 시간에 등록하고 폐기하는 과정에서 실시간성이 조금 깨지는 것은 문제가 되지 않는다 <br>
그리고 실제 콘서트장의 경우 1만 좌석이 넘고 구역별로 나누더라도 좌석의 수가 적지 않다 <br>
또한, 좌석 예약 실패 시, 다른 좌석을 찾기 위해 refresh 를 하는 경우가 빈번한데 <br>
이러한 부분들은 서버에 많은 부하를 줄 수 있다 <br>

이러한 점을 고려할 때, 예약 가능 좌석을 캐싱하면 처음 조회하는 사람의 쿼리만 나가고 <br>
짧은 시간이지만 쿼리가 실행되지 않아 디비 부하를 줄일 수 있다 <br>
몇 초 되지 않아 별거 아니라고 생각할 수 있지만 조회 요청이 1만개가 있다면 <br>
해당 시간동안에는 쿼리를 실행하지 않을 수 있다 <br>
그리고 예약의 경우, 동시성 문제 예방을 위해 락을 걸어서 처리하는데 락이 비관적 락인 경우 <br>
읽기가 불가능한 문제를 캐시를 통해 해결 가능하다

해당 API는 위 원칙에 따르면 캐싱에 적합하지 않지만 상황에 따라 캐시를 적용해 볼 수 있다 <br>
하지만, 현 프로젝트에서는 캐싱을 적용하지 않는다 <br>
추후, 부하테스트에서 캐싱을 통해 API의 성능을 테스트해보고 더 나은 방법을 적용할 것이다 

### 좌석 예약 요청 `부적합`
조회 API 가 아니라 캐싱이 적합하지 않다

### 잔액 충전 `부적합`
조회 API 가 아니라 캐싱이 적합하지 않다

### 잔액 조회 `부적합`
사이트별로 유저의 금액 정보를 포현하는 방법이 다를 수 있다 <br>
예를 들면, 어떤 사이트는 마이페이지에 포인트가 있고 혹은, 헤더에 포인트를 적어놓은 경우가 존재한다 <br>
헤더에 포인트가 존재하는 경우 모든 페이지로 이동할 때마다 조회 요청이 발생하는데 이를 캐싱해 놓으면 성능이 향상될 수 있다 <br>
하지만, 현 개발에선 조회는 충전을 위한 잔액의 조회이고 이 경우 캐싱은 필요하지 않다

### 결제 `부적합`
결제를 위한 포인트 조회가 잦지 않고 결제 자체가 자주 발생하지 않는다 <br>
따라서, 캐싱을 적용하기 적합하지 않다

# 성능 비교
캐시를 적용한 메서드에 대해 10만건의 요청을 전송하였다 <br>
캐시를 적용한 경우가 아닌 경우에 비해 더 빠른 처리 속도를 보여주었다 <br>
쿼리 실행 시 시간은 0.000 sec 로 매우 빠르다 <br>
현재는 10만건에 대해 시간이 8초밖에 나지 않지만 쿼리 속도가 느릴 수록 이 차이는 커질 것이다 <br>

`캐시 O : 15 초` <br>
`캐시 X : 23 초` 

### 실행 쿼리
```sql
select
     concert_detail_id
    ,concert_id
    ,created_at
    ,date
    ,deleted_at
    ,modified_at
    ,name
    ,open_date <br> 
from concert_detail 
where (DELETED_AT IS NULL) and concert_id = 1;
```
### 실행  코드
``` java
public ConcertDate selectAvailableDates(Long concertId) {
    ConcertDate cacheData = concertRepository.findConcertDatesFromCache(concertId);
    if (cacheData != null) {
        return cacheData;
    }

    List<ConcertDateDetails> details = concertRepository.findConcertDates(concertId).stream()
            .map(entity -> new ConcertDateDetails(entity.getId(), entity.getDate()))
            .collect(Collectors.toList());

    ConcertDate concertDate = new ConcertDate(concertId, details);
    concertRepository.addCache(concertId,concertDate);

    return concertDate;
}

@DisplayName("콘서트 별 예약 가능한 날짜를 비동기로 조회한다")
@Test
void selectAvailableDates() {
    // given
    Long concertId = 1L;

    // when
    List<CompletableFuture<Void>> futures = new ArrayList<>();

    for (int i = 0; i < 100000; i++) {
        futures.add(CompletableFuture.runAsync(() -> concertService.selectAvailableDates(concertId)));
    }

    futures.stream()
            .forEach(future -> {
                try {
                    future.join();
                } catch (Exception e) {
                    System.out.println("Error : " + e.getMessage());
                }
            });
}
```