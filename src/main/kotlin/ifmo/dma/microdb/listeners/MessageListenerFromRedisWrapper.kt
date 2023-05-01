package ifmo.dma.microdb.listeners

import com.fasterxml.jackson.core.JsonParseException
import com.fasterxml.jackson.databind.ObjectMapper
import com.networknt.schema.JsonSchema
import ifmo.dma.microdb.services.MessageProcessorService
import org.springframework.data.redis.connection.Message
import org.springframework.data.redis.connection.MessageListener

abstract class Gavno : MessageListener {
    override fun onMessage(message: Message, pattern: ByteArray?) {
        onMessage(message)
    }

    abstract fun onMessage(message: Message)
}

class MessageListenerFromRedisWrapper(
    private val listenerImpl: MessageListener,
    private val messageProcessorService: MessageProcessorService,
    private val mapper: ObjectMapper,
    private val responseQueueName: String,
    private val generalSchema: JsonSchema,
    private val specialJsonSchemas: Map<String, JsonSchema>,
) : MessageListener {
    override fun onMessage(message: Message, pattern: ByteArray?) {
        val content = message.body.decodeToString()
        // validate general request schema
        val request = mapper.readTree(content)
        try {
            val errors = generalSchema.validate(request)
            if (errors.isNotEmpty()) {
                messageProcessorService.pushError(
                    responseQueueName,
                    errors.first().message,
                    errors.first().code.toInt(),
                )
                return
            }
        } catch (e: JsonParseException) {
            messageProcessorService.pushError(
                responseQueueName,
                e.originalMessage,
                -1000,
            )
            return
        }
        val command = request.get("command").asText()
        val payload = request.get("payload")
        println(specialJsonSchemas.keys.toString())
        if (!specialJsonSchemas.contains(command)) {
            messageProcessorService.pushError(
                responseQueueName,
                "Wrong command $command on ${message.channel} channel! Try again!",
                6,
            )
            return
        }

        // validate spec command schema

        val errors = specialJsonSchemas[command]!!.validate(payload)
        if (errors.isNotEmpty()) {
            messageProcessorService.pushError(
                responseQueueName,
                errors.first().message,
                errors.first().code.toInt(),
            )
            return
        }
        try {
            listenerImpl.onMessage(message, pattern)
        } catch (ex: Exception) {
            ex.printStackTrace()
            messageProcessorService.pushError(
                responseQueueName,
                "Could not perform operation due to an unexpected error: ${ex.message}",
                3
            )
        }
    }
}
