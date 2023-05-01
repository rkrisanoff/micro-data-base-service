package ifmo.dma.microdb.redisConfig

import org.springframework.cache.annotation.EnableCaching
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration
import org.springframework.data.redis.connection.RedisConnectionFactory
import org.springframework.data.redis.connection.lettuce.LettuceConnectionFactory
import org.springframework.data.redis.core.RedisTemplate
import org.springframework.data.redis.listener.ChannelTopic
import org.springframework.data.redis.listener.RedisMessageListenerContainer
import org.springframework.data.redis.listener.adapter.MessageListenerAdapter
import org.springframework.data.redis.repository.configuration.EnableRedisRepositories
import org.springframework.data.redis.serializer.StringRedisSerializer
import org.springframework.scheduling.annotation.EnableScheduling

@Configuration
@EnableRedisRepositories
@EnableCaching
@EnableScheduling
class RedisMessageBrokerConfig {

    @Bean
    fun redisConnectionFactory(redisConfigProperties: RedisConfigProperties): RedisConnectionFactory {
        return LettuceConnectionFactory(redisConfigProperties.host, redisConfigProperties.port)
    }

    @Bean
    fun redisTemplate(redisConnectionFactory: RedisConnectionFactory): RedisTemplate<String, Any> {
        val template = RedisTemplate<String, Any>()
        template.setConnectionFactory(redisConnectionFactory)
        template.setDefaultSerializer(StringRedisSerializer())
        return template
    }

    @Bean
    fun redisMessageListenerContainer(
        redisConnectionFactory: RedisConnectionFactory,
        messageListenerUserAdapter: MessageListenerAdapter,
        messageListenerGroupAdapter: MessageListenerAdapter,
        messageListenerQueueAdapter: MessageListenerAdapter,

    ): RedisMessageListenerContainer {
        val container = RedisMessageListenerContainer()
        container.setConnectionFactory(redisConnectionFactory)
        container.addMessageListener(messageListenerUserAdapter, ChannelTopic("md-user-request"))
        container.addMessageListener(messageListenerGroupAdapter, ChannelTopic("md-group-request"))
        container.addMessageListener(messageListenerQueueAdapter, ChannelTopic("md-queue-request"))

        return container
    }
}
