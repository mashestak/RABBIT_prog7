# Тестирование RabbitMQ с использованием Curl и Spring Boot

## Введение
Этот проект включает в себя:
1. **ProducerController**: Отправляет сообщения в очередь RabbitMQ.
2. **PublisherController**: Публикует сообщения в обмен RabbitMQ.
3. **Subscriber** и **Consumer**: Получают и обрабатывают сообщения, отправленные в очередь RabbitMQ.
4. **SubscriberWithRouting**: Подписан на различные очереди, например, `info` и `error`.

## Настройка окружения

### 1. Запуск контейнера RabbitMQ
Для начала создайте и запустите контейнер с RabbitMQ, используя Docker:

```bash
sudo docker run -d --hostname rabbitmq --name rabbitmq -p 5672:5672 -p 15672:15672 rabbitmq:3-management
```

Это запустит контейнер RabbitMQ с веб-интерфейсом для управления на порту 15672 и брокером сообщений на порту 5672.

### 2. Установка зависимостей
Убедитесь, что у вас установлен JDK 23, затем выполните команду для установки зависимостей:

```bash
mvn clean install
```

### 3. Запуск приложения
Теперь можно запустить Spring Boot приложение с помощью команды:

```bash
mvn spring-boot:run
```

После этого приложение будет доступно на порту 8080.

## Тестирование с использованием Curl

### Тест 1: Отправка сообщения через `ProducerController`

**Метод:** POST  
**URL:** `http://localhost:8080/api/producer/send?message=<your_message>`  

Пример:

```bash
curl -X POST "http://localhost:8080/api/producer/send?message=Hello%20RabbitMQ"
```

В ответ вы получите сообщение о том, что сообщение было отправлено в очередь RabbitMQ:

```
Message sent to queue: task_queue, message: Hello RabbitMQ
```

### Тест 2: Публикация сообщения через `PublisherController`

**Метод:** POST  
**URL:** `http://localhost:8080/api/publisher/publish?message=<your_message>&routingKey=<optional_routing_key>`  

Пример (с указанием маршрутизирующего ключа):

```bash
curl -X POST "http://localhost:8080/api/publisher/publish?message=Log%20message&routingKey=info"
```

Пример (без указания маршрутизирующего ключа):

```bash
curl -X POST "http://localhost:8080/api/publisher/publish?message=Log%20message"
```

В ответ вы получите сообщение, аналогичное:

```
Message published to exchange: direct_logs with routing key: info, message: Log message
```

или:

```
Message published to exchange: logs with routing key: no routing key, message: Log message
```

### Тест 3: Получение сообщения в `Subscriber`

**Метод:** N/A (это реактивный слушатель RabbitMQ)

Когда вы публикуете или отправляете сообщение через `PublisherController` или `ProducerController`, `Subscriber` автоматически получит сообщение из обмена `logs` и выведет его в консоль:

```
Received message: Log message
```

### Тест 4: Получение сообщения в `Consumer`

**Метод:** N/A (это реактивный слушатель RabbitMQ)

Когда вы публикуете или отправляете сообщение через `ProducerController` в очередь `task_queue`, `Consumer` автоматически получит сообщение и выведет его в консоль:

```
Received: Hello RabbitMQ
```

### Тест 5: Получение сообщений с маршрутизацией через `SubscriberWithRouting`

В `SubscriberWithRouting` добавлены два слушателя для очередей `info` и `error`. В зависимости от маршрутизирующего ключа, сообщение будет отправляться в одну из этих очередей.

**Метод:** N/A (это реактивный слушатель RabbitMQ)

#### Пример 1: Отправка сообщения в очередь `info`

Если вы отправляете сообщение с маршрутизирующим ключом `info`, `SubscriberWithRouting` получит сообщение через очередь `info`.

Команда для публикации:

```bash
curl -X POST "http://localhost:8080/api/publisher/publish?message=Informational%20message&routingKey=info"
```

В консоли будет:

```
Received Info: Informational message
```

#### Пример 2: Отправка сообщения в очередь `error`

Если вы отправляете сообщение с маршрутизирующим ключом `error`, `SubscriberWithRouting` получит сообщение через очередь `error`.

Команда для публикации:

```bash
curl -X POST "http://localhost:8080/api/publisher/publish?message=Error%20message&routingKey=error"
```

В консоли будет:

```
Received Error: Error message
```
