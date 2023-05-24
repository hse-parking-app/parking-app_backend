# parkings

[![build + tests run](https://github.com/hse-parking-app/parking-app_backend/actions/workflows/run-tests-and-coverage.yml/badge.svg)](https://github.com/hse-parking-app/parking-app_backend/actions/workflows/run-tests-and-coverage.yml)
[![codecov](https://codecov.io/github/hse-parking-app/parking-app_backend/branch/develop/graph/badge.svg?token=HEKFYWMSEO)](https://codecov.io/github/hse-parking-app/parking-app_backend)

### Роли

Предусмотрены две роли: `ADMIN` и `APP_USER`.\
`ADMIN` имеет доступ ко всем методам.\
`APP_USER` имеет доступ только к тем методам, которые необходимы приложению.

Доступные `APP_USER` методы либо оканчиваются на `/employee`, либо относятся к `auth-controller`

### Модели

В `ADMIN` методах необходимо указывать все поля в моделях, но для `APP_USER` некоторые могут заполняться автоматически

<details>
  <summary>Вот примеры моделей без ненужных полей...</summary>

#### Car

```
{
    model          string
    lengthMeters   number ($double)
    weightTons     number ($double)
    registryNumber string
}
```

#### Employee

```
{
    name     string
    email    string
    password string
}
```

#### Reservation

```
{
    carId         string ($uuid)
    parkingSpotId string ($uuid)
    startTime     string ($date-time)
    endTime       string ($date-time)
}
```

</details>

### Время

Бэкенд всегда ориентируется на UTC время

### Пользователи

В БД есть несколько пользователей для тестов:

```
login: a@a.a
password: a
roles: APP_USER, ADMIN
```

У пользователя b в БД имеется машина

```
login: b@b.b
password: b
roles: APP_USER
```

```
login: c@c.c
password: c
roles: APP_USER
```

### Запуск проекта

```bash
 docker compose build && docker compose up
```

Поднимется приложение на порту `8080`. Swagger-спецификация доступна по адресу:

```url
http://localhost:8080/swagger.html
```
