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
  ],
  summaryTrendStats: ['avg', 'min', 'med', 'p(50)', 'p(90)', 'p(95)', 'p(99)', 'max']
}

export default function () {

  let url = 'http://host.docker.internal:8080/api/concert/date/500';

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
