import http from 'k6/http'
import { check, sleep } from 'k6'
import { Rate } from 'k6/metrics'

import { generateUser } from './user.js'

const submitFailRate = new Rate('failed register user')

const baseUrl = 'http://localhost:9999'
const urls = {
  people: `${baseUrl}/pessoas`,
  counting: `${baseUrl}/contagem-pessoas`
}

export const options = {
  stages: [
    // { duration: '30s', target: 20 },
    // { duration: '1m30s', target: 10 },
    { duration: '1s', target: 1 },
  ],
  thresholds: {
    http_req_failed: ['rate<0.01'], // http errors should be less than 1%
    http_req_duration: ['p(95)<100', 'p(99.9) < 200'], // 95% of requests should be below 100ms,  99.9% of requests should be below 200ms
    'failed register user': ['rate<0.1']
  }
}

const registerPerson = () => {
  const person = generateUser()
  const payload = JSON.stringify(person)

  const res = http.post(urls.people, payload)
  submitFailRate.add(res.status !== 201)

  return res.headers
}

export default function () {
  const headers = registerPerson()
  console.log(headers['Location'])

  // check(res, { 'status was 200': (r) => r.status == 200 });
  sleep(1)
}