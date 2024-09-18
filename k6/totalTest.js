import http from 'k6/http';
import { check, sleep } from 'k6';

/**
 * 30초마다 1500명씩 토큰 발급 요청
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
    { target: 5500, duration: '30s' },
    { target: 5000, duration: '30s' },
    { target: 4500, duration: '30s' },
    { target: 4000, duration: '30s' },
    { target: 3500, duration: '30s' },
    { target: 3000, duration: '30s' },
    { target: 2500, duration: '30s' },
    { target: 2000, duration: '30s' },
    { target: 1500, duration: '30s' },
    { target: 1000, duration: '30s' },
    { target: 500, duration: '30s' },
    { target: 0, duration: '30s' },
  ]
}

const baseUrl = "http://localhost:8080";
// const baseUrl = "http://host.docker.internal:8080";

export default function () {
  const memberId = Math.floor(Math.random() * 10000) + 1;

  enqueue(memberId);

  let less500 = getQueueInfo(memberId);

  if (less500) {
    sleep(31);
    getAvailableDates(memberId);

    getAvailableSeats(memberId);

    const [reservationId,seatId] = reserveSeat(memberId);

    pay(reservationId, seatId, memberId);

    return;
  }

  sleep(10);
}

const enqueue = (memberId) => {
  const url = baseUrl + '/api/queue/issue';
  const params = createParam(memberId,'enqueue');
  const requestBody = JSON.stringify({
    memberId: memberId,
  });

  let res = http.post(url, requestBody, params);

  check(res, {
    'enqueue status was 200': (r) => r.status == 200,
  });

}

const getQueueInfo = (memberId) => {
  const url = baseUrl + '/api/queue/' + memberId;
  const params = createParam(memberId,'getQueueInfo');

  const res = http.get(url, params);

  return check(res, {
    'position is less then 500': (r) => JSON.parse(r.body).position <= 500,
  });
};

const getAvailableDates = (memberId) => {
  const url = baseUrl + '/api/concert/date/500';
  const params = createParam(memberId,'date');

  let res = http.get(url, params);
  check(res, {
    'getAvailableDates status was 200': (r) => r.status == 200,
  });

  sleep(1);
}

const getAvailableSeats = (memberId) => {
  const url = baseUrl + '/api/concert/seat/500';
  const params = createParam(memberId,'seat');

  const count = Math.floor(Math.random() * 3) + 1;

  for (let i = 0; i < count; i++) {
    let res = http.get(url, params,);

    check(res, {
      'getAvailableSeats status was 200': (r) => r.status == 200,
    });

    sleep(2);
  }

  sleep(5);
}

const reserveSeat = (memberId) => {
  const url = baseUrl + '/api/reserve';

  const seatId = getRandomSeatId();
  const params = createParam(memberId,"reserve");

  const requestBody = JSON.stringify({
    seatId: seatId,
    memberId: memberId
  });

  let res = http.post(url, requestBody, params);
  check(res, {
    'reserveSeat status was 200': (r) => r.status == 200,
  });

  sleep(1);

  return [JSON.parse(res.body).id, seatId];
};

const pay = (reservationId, seatId, memberId) => {
  const url = baseUrl + '/api/pay';

  const params = createParam(memberId,'pay');

  const requestBody = JSON.stringify({
    reservationId: reservationId,
    seatId: seatId,
    memberId: memberId,
  });

  let res = http.post(url, requestBody, params);

  check(res, {
    'pay status was 200': (r) => r.status == 200,
  });
};

const createParam = (memberId,name) => {
  return {
    headers: {
      'Content-Type': 'application/json',
      'memberId': memberId,
    },
    tags: {name: name}
  };
}

const getRandomSeatId = () => {
  return Math.floor(Math.random() * 5000) + 1;
}