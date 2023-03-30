package ifmo.dma.microdb.messaging

import org.springframework.data.redis.core.RedisTemplate
import org.springframework.data.redis.listener.Topic
import org.springframework.stereotype.Service

@Service
class MessagePublisher(private val redisTemplate: RedisTemplate<String, Any>,
                       private val topic: Topic
) {

    fun publish(message: String) {
        redisTemplate.convertAndSend(topic.topic, message)
    }

}
