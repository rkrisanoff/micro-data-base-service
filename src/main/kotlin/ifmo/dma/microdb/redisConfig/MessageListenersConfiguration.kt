package ifmo.dma.microdb.redisConfig

import com.fasterxml.jackson.databind.ObjectMapper
import com.networknt.schema.JsonSchema
import ifmo.dma.microdb.listeners.MessageListenerFromRedisWrapper
import ifmo.dma.microdb.services.MessageProcessorService
import org.springframework.beans.factory.annotation.Qualifier
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration
import org.springframework.data.redis.connection.MessageListener
import org.springframework.data.redis.listener.adapter.MessageListenerAdapter

@Configuration
class MessageListenersConfiguration {

    private fun wrapInAdapter(messageListener: MessageListener): MessageListenerAdapter {
        val messageListenerAdapter = MessageListenerAdapter(messageListener)
        messageListenerAdapter.afterPropertiesSet()
        return messageListenerAdapter
    }

    @Bean
    fun messageListenerUserAdapter(
        messageListenerUser: MessageListener,
        messageProcessorService: MessageProcessorService,
        mapper: ObjectMapper,
        @Qualifier("userResponseQueue")userResponseQueue: String,
        generalJsonSchema: JsonSchema,
        @Qualifier("userJsonSchemas")userJsonSchemas: Map<String, JsonSchema>,
    ): MessageListenerAdapter {
        return wrapInAdapter(
            MessageListenerFromRedisWrapper(
                messageListenerUser,
                messageProcessorService,
                mapper,
                userResponseQueue,
                generalJsonSchema,
                userJsonSchemas,
            ),
        )
    }

    @Bean
    fun messageListenerGroupAdapter(
        messageListenerGroup: MessageListener,
        messageProcessorService: MessageProcessorService,
        mapper: ObjectMapper,
        @Qualifier("groupResponseQueue")groupResponseQueue: String,
        generalJsonSchema: JsonSchema,
        @Qualifier("groupJsonSchemas")groupJsonSchemas: Map<String, JsonSchema>,
    ): MessageListenerAdapter {
        return wrapInAdapter(
            MessageListenerFromRedisWrapper(
                messageListenerGroup,
                messageProcessorService,
                mapper,
                groupResponseQueue,
                generalJsonSchema,
                groupJsonSchemas,
            ),
        )
    }

    @Bean
    fun messageListenerQueueAdapter(
        messageListenerQueue: MessageListener,
        messageProcessorService: MessageProcessorService,
        mapper: ObjectMapper,
        @Qualifier("queueResponseQueue")queueResponseQueue: String,
        generalJsonSchema: JsonSchema,
        @Qualifier("queueJsonSchemas")queueJsonSchemas: Map<String, JsonSchema>,
    ): MessageListenerAdapter {
        return wrapInAdapter(
            MessageListenerFromRedisWrapper(
                messageListenerQueue,
                messageProcessorService,
                mapper,
                queueResponseQueue,
                generalJsonSchema,
                queueJsonSchemas,
            ),
        )
    }
}
