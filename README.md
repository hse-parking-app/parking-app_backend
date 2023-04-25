# parkings

### Роли

Предусмотрены две роли: `ADMIN` и `APP_USER`.\
`ADMIN` имеет доступ ко всем методам.\
`APP_USER` имеет доступ только к тем методам, которые необходимы приложению.

Доступные методы помечены аннотацией

```java
@PreAuthorize("hasAnyAuthority('ADMIN', 'APP_USER')")
```

в классах в модуле controller (позже будут вынесены в отдельный эндпоинт)

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
 docker-compose build && docker-compose up
```

Поднимется приложение на порту `8080`. Swagger-спецификация доступна по адресу:

```url
http://localhost:8080/swagger.html
```
