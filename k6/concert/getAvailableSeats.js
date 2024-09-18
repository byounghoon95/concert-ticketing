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

export default function () {

  let url = 'http://host.docker.internal:8080/api/concert/seat/500';

  let params = {
    headers: {
      'Content-Type': 'application/json',
      'memberId': 1,
    },
  };

  let res = http.get(url, params);
  check(res, {
    'status was 200': (r) => r.status == 200,
  });

  sleep(1);
}
