## Как подключить Redis



### Первичная конфигурация

Пишем в application.properties
```properties
spring.data.redis.host=localhost
spring.data.redis.port=6379
```
Добавляем в зависимости gradle:
```kotlin
dependencies {
        annotationProcessor("org.springframework.boot:spring-boot-configuration-processor")
        implementation("org.springframework.boot:spring-boot-starter-data-redis")
}
```
Добавляем в главный класс `@ConfigurationPropertiesScan`

Создаём класс 

Полностью копируем package [redisConfig](src/main/kotlin/ifmo/dma/microdb/redisConfig)

### Пассивные слушатели

Пишем классы ( лучше всего разместить в package [listeners](src/main/kotlin/ifmo/dma/microdb/listeners) )

Переопределяем метод `onMessage`

Например:

```kotlin
@Component
class MessageListerExample : MessageListener {
    override fun onMessage(message: Message, bytes: ByteArray?) {
        println("Examples:")
        println("Message from channel `${String(message.channel)}` received: ${String(message.body)}")
    }
}
```

Затем переходим в [MessageListenersConfiguration](src/main/kotlin/ifmo/dma/microdb/redisConfig/MessageListenersConfiguration.kt) и добавляем
```kotlin
    @Bean
    fun messageListenerExampleAdapter():MessageListenerAdapter{
        return wrapInAdapter(MessageListerExample())
    }
```

В [RedisBrokerMessageConfig](src/main/kotlin/ifmo/dma/microdb/redisConfig/RedisBrokerMessageConfig.kt) добавляем в метод

Не забываем в качестве второго аргумента передать имя канала, который наш метод будет прослушивать

```kotlin
    @Bean
    fun redisMessageListenerContainer(
        redisConnectionFactory: RedisConnectionFactory,
        // связываем
        messageListenerExampleAdapter: MessageListenerAdapter,
    ): RedisMessageListenerContainer {
        val container = RedisMessageListenerContainer()
        container.setConnectionFactory(redisConnectionFactory)
        // добавляем слушатель в контейнер
        container.addMessageListener(messageListenerExampleAdapter, ChannelTopic("example-channel-name"))
        
    return container
    }
```
### Активное сообщение

Тут всё просто - добавляем [RedisMessageService](src/main/kotlin/ifmo/dma/microdb/services/RedisMessageService.kt) в метод и пользуемся методом `publishAndWaitForResponse`. Если нужно просто отправить сообщение в канал и не ждать ответа - используем метод `publish`.

Например, так

```kotlin
@RestController
class ExampleController @Autowired constructor(val redisMessageService: RedisMessageService) {
    private val defaultTimeout = Duration.ofSeconds(100)

    @GetMapping("/api/hello")
    fun hello(@RequestParam message: String): String {
        return redisMessageService.publishAndWaitForResponse(
            "input", message,
            "output", defaultTimeout
        )
            ?: "Message sent, but no response received yet. :("
    }
}
```