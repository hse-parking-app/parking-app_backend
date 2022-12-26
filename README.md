# fintech-parking *\*README expired. waiting for rework\**

## Cервис для бронирования парковочных мест

## Модель данных

Парковочное место:

- информацию о расположении
- может быть свободно/занято

Автомобиль:

- модель
- номер

Бронирование:

- время старта и время окончания
- ссылки на машину и на сотрудника
- период бронирования (незакрытая бронь автоматически закрывается в 00:00 каждого дня, бронировать автомобиль нельзя в
  субботу и воскресенье)

Один автомобиль может занять только одно парковочное место.

## API для управления моделями (CRUD)

## Ролевая модель

Только пользователь `ADMIN` может управлять паковочными местами и автомобилями.
Пользователь `USER` может управлять только бронированиями и просмотром парковочных мест.

## API для бронирования

Можно забронировать свободное место на определенное время 1 - 24 часа.
По истечению этого времени бронь должна сниматься.

## Тестовые данные

Пользователь с ролью `USER` - `user:password`, с ролью `ADMIN` - `admin:password`

## Запуск проекта

```cmd
 docker-compose build
```

```cmd
 docker-compose up
```

Поднимется приложение на порту `8080` и БД для него. Чтобы начать использовать проект, изучите swagger спецификацию по
адресу:

```url
http://localhost:8080/swagger.html
```
