import http from 'k6/http';
import { check, sleep } from 'k6';

export const options = {
  stages: [
    { target: 100, duration: '20s' },
    { target: 200, duration: '20s' },
    { target: 300, duration: '20s' },
    { target: 200, duration: '20s' },
    { target: 100, duration: '20s' },
    { target: 0, duration: '20s' },
  ]
}

function getRandomSeatId() {
  return Math.floor(Math.random() * 500000) + 1;
}

function getRandomMemberId() {
  return Math.floor(Math.random() * 10000) + 1;
}

export default function () {

  let url = 'http://host.docker.internal:8080/api/reserve';

  const seatId = getRandomSeatId();
  const memberId = getRandomMemberId();

  const requestBody = JSON.stringify({
    seatId: seatId,
    memberId: memberId
  });

  let params = {
    headers: {
      'Content-Type': 'application/json',
      'memberId': memberId,
    },
  };

  let res = http.post(url, requestBody, params);
  check(res, {
    'status was 200': (r) => r.status == 200,
  });

  sleep(1);
}
