package ifmo.dma.microdb.messaging

import ifmo.dma.microdb.repo.*
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.cache.annotation.EnableCaching
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration
import org.springframework.data.redis.connection.RedisConnectionFactory
import org.springframework.data.redis.connection.lettuce.LettuceConnectionFactory
import org.springframework.data.redis.core.RedisTemplate
import org.springframework.data.redis.listener.ChannelTopic
import org.springframework.data.redis.listener.RedisMessageListenerContainer
import org.springframework.data.redis.listener.Topic
import org.springframework.data.redis.listener.adapter.MessageListenerAdapter
import org.springframework.data.redis.repository.configuration.EnableRedisRepositories
import org.springframework.data.redis.serializer.StringRedisSerializer
import org.springframework.scheduling.annotation.EnableScheduling

@Configuration
@EnableRedisRepositories
@EnableCaching
@EnableScheduling
class RedisMessageBrokerConfig () {

    @Bean
    fun redisConnectionFactory(): RedisConnectionFactory {
        return LettuceConnectionFactory()
    }

    @Bean
    fun redisTemplate(redisConnectionFactory: RedisConnectionFactory): RedisTemplate<String, Any> {
        val template = RedisTemplate<String, Any>()
        template.setConnectionFactory(redisConnectionFactory)
        template.setDefaultSerializer(StringRedisSerializer())
        return template
    }

    @Bean
    fun messagePublisherBack(@Autowired redisTemplate: RedisTemplate<String,Any>):MessagePublisher{
        return MessagePublisher(redisTemplate,ChannelTopic("output"))
    }

    @Bean
    fun messageListenerAdapter(
        @Autowired messagePublisherBack: MessagePublisher,
        @Autowired userRepo: UserRepo,
        @Autowired groupRepo: GroupRepo,
        @Autowired queueRepo: QueueRepo,
        @Autowired queueRequestRepo: QueueRequestRepo,
        @Autowired userGroupRepo: UserGroupRepo
    ): MessageListenerAdapter {
        val messageListenerAdapter = MessageListenerAdapter(MessageListerBack(messagePublisherBack,SuperRepo(
            userRepo=userRepo,
            groupRepo = groupRepo,
            queueRepo = queueRepo,
            userGroupRepo = userGroupRepo,
            queueRequestRepo =  queueRequestRepo
        )))
        messageListenerAdapter.afterPropertiesSet()
        return messageListenerAdapter
    }


    @Bean
    fun redisMessageListenerContainer(
        redisConnectionFactory: RedisConnectionFactory,
        messageListenerAdapter: MessageListenerAdapter
    ): RedisMessageListenerContainer {
        val container = RedisMessageListenerContainer()
        container.setConnectionFactory(redisConnectionFactory)
        container.addMessageListener(messageListenerAdapter, topic())
        return container
    }


    @Bean
    fun topic(): Topic {
        return ChannelTopic("myTopic")
    }

}
