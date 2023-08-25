import { faker } from '@faker-js/faker/locale/pt_BR'

export const generateUser = () => ({
  name: `USER_TEST - ${faker.person.firstName()}`,
  nickname: faker.person.middleName(),
  birthdate: faker.date.birthdate(),
  stack: faker.helpers.arrayElements(["C#", "JS", "Java", "TS", "Rust", "Haskell", "Go", "Kotlin"])
})