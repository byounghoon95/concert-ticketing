import http from 'k6/http';
import { check, sleep } from 'k6';

/**
 * 30초마다 3000명씩 토큰 발급 요청
 * 한번 대기열 순번 요청 시 10초 후 재요청
 * 30초에 500명씩 active 로 전환
 * */
export const options = {
  stages: [
    { target: 1000, duration: '30s' },
    { target: 2000, duration: '30s' },
    { target: 3000, duration: '30s' },
    { target: 4000, duration: '30s' },
    { target: 5000, duration: '30s' },
    { target: 6000, duration: '30s' },
    { target: 6500, duration: '30s' },
    { target: 6000, duration: '30s' },
    { target: 4000, duration: '30s' },
    { target: 2000, duration: '30s' },
    { target: 0, duration: '30s' },
  ]
}

const baseUrl = "http://host.docker.internal:8080";

export default function () {

  const memberId = Math.floor(Math.random() * 10000) + 1;

  let url = baseUrl + '/api/queue/' + memberId;

  let params = {
    headers: {
      'Content-Type': 'application/json',
      'memberId': memberId,
    },
  };

  let res = http.get(url, params);
  check(res, {
    'status was 200': (r) => r.status == 200,
  });
}
