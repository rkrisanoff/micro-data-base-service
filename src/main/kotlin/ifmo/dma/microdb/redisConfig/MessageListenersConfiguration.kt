package ifmo.dma.microdb.redisConfig

import ifmo.dma.microdb.listeners.MessageListenerUser
import ifmo.dma.microdb.listeners.MessageListerExample
import ifmo.dma.microdb.listeners.MessageListerExample2
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration
import org.springframework.data.redis.connection.MessageListener
import org.springframework.data.redis.listener.adapter.MessageListenerAdapter

@Configuration
class MessageListenersConfiguration {
    private fun wrapInAdapter(messageListener: MessageListener):MessageListenerAdapter{
        val messageListenerAdapter = MessageListenerAdapter(messageListener)
        messageListenerAdapter.afterPropertiesSet()
        return messageListenerAdapter
    }

    @Bean
    fun messageListenerExampleAdapter():MessageListenerAdapter{
        return wrapInAdapter(MessageListerExample())
    }

    @Bean
    fun messageListenerExample2Adapter():MessageListenerAdapter{
        return wrapInAdapter(MessageListerExample2())
    }
    @Bean
    fun messageListenerUserAdapter(messageListenerUser:MessageListenerUser):MessageListenerAdapter{
        return wrapInAdapter(messageListenerUser)
    }

}