import http from 'k6/http';
import { check, sleep } from 'k6';

export let options = {
  vus: 100, // 가상 사용자 수
  duration: '30s', // 테스트 실행 시간
};

// 총 memberId 개수
const TOTAL_MEMBER_IDS = 3000;

const startTime = new Date().getTime();

export default function () {

  let elapsedTime = new Date().getTime() - startTime;

  let memberId = Math.floor(elapsedTime / 1000) * 100 + (__VU - 1) % 100 + 1;

  let url = 'http://host.docker.internal:8080/api/concert/date/1';

  let params = {
    headers: {
      'Content-Type': 'application/json',
      'memberId': memberId.toString(),
    },
  };

  let res = http.get(url, params);
  check(res, {
    'status was 200': (r) => r.status == 200,
  });

  sleep(1);
}
