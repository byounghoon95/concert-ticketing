인덱스를 적용하기 위해 데이터베이스에 데이터를 밀어넣었다. <br>
Member 테이블에 10만건, Reservation 에 약 천만건의 데이터를 넣고 조회를 진행하였다. <br>

인덱스를 고려할 땐 카디널리티가 높은 컬럼을 고려해야한다 <br>
카디널리티는 데이터의 중복 수치를 나타낸다
- 카디널리티가 높다 -> 한 컬럼이 갖고 있는 값의 중복도가 낮다
- 카디널리티가 낮다 -> 한 컬럼이 갖고 있는 값의 중복도가 높다

카디널리티가 높은 컬럼을 조회해야하는 이유는 천만건의 데이터 중 컬럼이 unique 한 경우와 중복값이 2개인 경우를 생각해보자 <br>
unique 하나의 값을 통해 찾을 수 있는 레코드는 한개이다 <br> 
B-tree 자료구조의 시간 복잡도는 O(log N) 이기에 연산은 약 23번 정도가 일어나고 결과값을 찾았기에 탐색은 멈춘다

하지만 중복값이 2개인 경우는 약 500만개의 데이터를 찾아야한다 <br>
이런 경우 탐색을 시작할 연산은 약 23번이 발생하지만 인덱스 레인지 스캔을 통해 500만건을 찾는 작업이 필요하다 <br>
이 때는, 인덱스보단 테이블 풀스캔을 하는것이 성능적으로 나을 수 있기에 테스트 결과를 통해 인덱스를 걸지 않을 수 있다.

# 인덱스 선정
테이블의 구조는 아래와 같다. 여기서 어떤 컬럼에 인덱스를 적용할 수 있을지 생각해보자. <br>
id : PK 이므로 자동으로 인덱스 생성된다

seat_id : 좌석별로 예약 내역을 확인한다. 현 테이블에서 조회 시 예약되고 취소된 히스토리의 확인이 가능한데 Seat 테이블에서도 <br>
&emsp;&emsp;&emsp;&nbsp;&nbsp;&nbsp;
예약 여부는 학인이 가능하고 단순 히스토리의 조회를 할 필요는 없다고 생각해 인덱스를 만들 필요가 없다고 판단했다

member_id : 한명의 유저가 몇개의 예약을 했는지를 알아보기 위함이다. 현 스펙에선 필요하지 않지만 <br>
&emsp;&emsp;&emsp;&emsp;&emsp;&nbsp;&nbsp;
실제로 유의미한 데이터이기에 빠른 조회 성능이 필요한 컬럼이다

concert_name,concert_date : 설계 시, 빠른 조회를 위해 concert_id 대신 이름과 날짜를 넣었다. 단순 조회용이므로 인덱스 필요하지 않다

price : 가격으로 조회할 경우는 거의 없으므로 필요하지 않다

status : RESERVED, CANCELED 값만 존재한다. RESERVED 인 레코드만 조회하고 싶을 시  

created_at : 예약 후 5분이 지나지 않으면 임시 저장 상태로 결제가 가능하다. 해당 컬럼은 예약 가능 상태를 확인하는데 사용된다 <br>
&emsp;&emsp;&emsp;&emsp;&emsp;&nbsp;
하지만, 결제 시 PK 인 id를 통해 레코드를 가져오고 비교하고 시간으로 값을 조회할 일이 없다고 생각이 들어 <br>
&emsp;&emsp;&emsp;&emsp;&emsp;&nbsp;
인덱스를 생성해서 얻을 수 있는 이점이 없다고 판단했다 
```text
Table RESERVATION {
  id bigint [primary key]
  seat_id bigint
  member_id bigint
  seat_no integer
  concertName varchar
  concert_date timestamp
  price integer
  status enum
  created_at timestamp
}
```

# 속도 비교
## PK 만 존재하는 경우
PK 만 존재하고 member_id 에는 인덱스가 없는 경우 속도 비교이다. 두 쿼리 모두 return 값은 1개이다.
```sql
SELECT * FROM reservation a JOIN member b ON a.member_id = b.id WHRER a.id = 50000 -- 0.000 sec
SELECT * FROM reservation a JOIN member b ON a.member_id = b.id WHERE a.member_id = 49999 -- 17.766
```
똑같이 한개의 레코드를 반환하는데 속도 차이는 17초 차이가 났다 <br>

### 실행계획 분석
```sql
SELECT * FROM reservation a JOIN member b ON a.member_id = b.id WHRER a.id = 50000
```

| id | select_type | table | type  | possible_keys | key     | key_len | ref   | rows | filtered | Extra |
|----|-------------|-------|-------|---------------|---------|---------|-------|------|----------|-------|
| 1  | SIMPLE      | a     | const | PRIMARY       | PRIMARY | 8       | const | 1    | 100.00   |       |
| 1  | SIMPLE      | b     | const | PRIMARY       | PRIMARY | 8       | const | 1    | 100.00   |       |

- id
  - SQL 문이 수행되는 순서를 나타낸다
  - id 가 작을수록 먼저 수행된 것인데 id 가 같은 값이라면 두개 테이블이 조인되었다고 볼 수 있다
    - reservation 과 member 가 join 되었기에 id 가 1 로 동일하다
- select_type
  - SELECT 문의 유형이다. FROM 에 위치하는지, 서브쿼리인지, UNION 절로 묶인 SELECT 인지를 나타낸다
  - SIMPLE 은 UNION, 내부 쿼리가 없는 단순한 SELECT 문이다
- table
  - 테이블 명이나 alias 를 표시한다
- type
  - 테이블의 데이터를 어떻게 찾을지에 관한 정보다. 테이블 풀 스캔할지, 인덱스 탈 지 등의 정보이다
  - const 는 고유 인덱스나 기본 키를 사용하여 단 1건의 데이터만 접근하여 얻은 경우 표시된다
- possible_keys
  - 옵티마이저가 사용할 수 있는 인덱스 목록을 표시한다. 실제 사용한 것이 아닌 사용가능한 인덱스이다
- key
  - 실제로 사용한 인덱스이다. 비효율적인 인덱스를 사용했거나 인덱스를 사용하지 않은 경우(null) 튜닝 대상이 된다
  - PRIMARY 이므로 PK를 사용하였다
- key_len
  - 사용한 인덱스 바이트 수이다
  - Long 타입이므로 8바이트이다. PK가 int 라면 4 가 표시된다
- ref
  - 인덱스가 비교하는 값이다
  - a.id = 50000 인 상수값이므로 const 가 출력되었다
- rows
  - 쿼리 실행을 위해 스캔해야 하는 예상 행 수를 나타낸다
  - 1이므로 출력을 위해 단 한건의 행만 스캔하면 되는 경우이다
- filtered
  - 조건에 의해 필터링된 행의 비율을 나타낸다
  - 100.00 이므로 모든 행이 조건을 충족한다

이번엔 인덱스가 없는 member_id 로 조회한 경우를 알아보자.
```sql
SELECT * FROM reservation a JOIN member b ON a.member_id = b.id WHERE a.member_id = 49999
```
| id | select_type | table | type  | possible_keys | key     | key_len | ref   | rows    | filtered | Extra       |
|----|-------------|-------|-------|---------------|---------|---------|-------|---------|----------|-------------|
| 1  | SIMPLE      | b     | const | PRIMARY       | PRIMARY | 8       | const | 1       | 100.00   |             |
| 1  | SIMPLE      | a     | ALL   |               |         |         |       | 9600853 | 10.00    | Using where |

- type
  - 이전과 달리 ALL 이 출력되었다. 인덱스가 아닌 테이블 풀스캔을 의미한다
- key
  - 인덱스가 사용되지 않았으므로 비어있다
- ref
  - 인덱스가 사용되지 않았으므로 비어있다
- rows
  - 9917074 으로 값을 찾기 위해 조회한 행의 개수이다.
  - 한개의 레코드를 검색하기 위해 많은 낭비되는 검색을 진행하였다
- filtered
  - member_id 의 distinct 값을 계산하면 97502 이다
  - 9600853 * 0.01 은 96008 인데 조인을 통해 대략 96008 개 정도의 컬럼이 필터링 되고 살아 남는다는 의미이다
  - 실행계획은 통계를 기반으로 하기에 값이 정확하게 맞지 않을 수 있다
- Extra
  - Using where 는 WHERE 절을 이용해 필터링 했음을 의미한다
  - Using where 는 일반적으로 발생할 수 있으며 index 를 타더라도 추가적인 필터링이 필요할 수 있다

이와 같이, 인덱스가 존재하지 않을 땐 테이블 풀스캔을 진행하고 그로 인해 훨씬 많은 행을 읽어 시간이 오래 걸림을 알 수 있었다

## member_id 에 인덱스를 생성한 경우
```sql
CREATE INDEX idx_member_id ON reservation(member_id)
SELECT * FROM reservation a JOIN member b ON a.member_id = b.id WHERE a.member_id = 49999 -- 0.000 sec
```
### 실행계획 분석
위와 똑같은 쿼리다. 인덱스를 거는 순간 속도가 0초로 줄어들었다. 실행계획을 확인해보자

| id | select_type | table | type  | possible_keys | key           | key_len | ref   | rows | filtered | Extra |
|----|-------------|-------|-------|---------------|---------------|---------|-------|------|----------|-------|
| 1  | SIMPLE      | a     | const | PRIMARY       | PRIMARY       | 8       | const | 1    | 100.00   |       |
| 1  | SIMPLE      | b     | ref   | idx_member_id | idx_member_id | 9       | const | 1    | 100.00   |       |

- type
  - ALL 에서 ref 으로 변경되었다 (인덱스로 조회)
- rows, filtered
  - 1 과 100으로 비효율 없이 값을 조회하였다

# 결과
17초 에서 0초로 시간이 줄어들었다. 이처럼 인덱스는 성능 개선을 위한 매우 강력한 도구이다. <br>
하지만, 인덱스를 무분별하게 사용하면 DML 에 영향을 줄 수 있기에 무분별한 생성은 금지이다. <br>
내 입장에선 현재 내가 짜고 있는 쿼리의 성능을 높히기 위해 인덱스를 생성할 수 있지만 <br>
운영 환경에선 이 테이블이 연관되어 있는 쿼리가 훨씬 많을 것이다. <br>
따라서, 인덱스 하나를 생성한것이 어플리케이션 전체에 큰 영향을 끼칠 수 있기에 <br>
인덱스는 최소한으로 가져가며 서버 전체에 어떤 영향을 끼치는지를 잘 확인해야한다

예를 들어, reservation 에서 member 의 정보를 보고 concert 를 보고 싶은 경우 <br>
혹은, member 와 concert 정보를 같이 보고 싶은 경우를 비교해 더 많이 조회되는 컬럼을 앞에두고 <br>
(member_id,concert_id) 로 결합 인덱스를 생성해볼 수 있다. <br>
member_id 의 카디널리티가 너무 높아 하나로 커버가 되지 않으면 concert_id 별개 인덱스를 추가한다 <br>
그렇게 하면 위 세 가지 경우를 커버할 수 있게 된다 <br>
혹은 세가지 경우 모두 성능이 중요하다면 (member_id), (concert_id), (member_id,concert_id) 인덱스를 생성해<br>
모든 경우에 최적의 조회 성능을 내도록 만들어 볼 수 있다. <br>
이처럼 인덱스를 생성할 땐 여러가지 경우의 수를 고려하고 인덱스를 생성해야 한다.