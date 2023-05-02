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
        try {
            val content = message.body.decodeToString()
            // validate general request schema

            try {
                val request = mapper.readTree(content)

                val generalErrors = generalSchema.validate(request)
                if (generalErrors.isNotEmpty()) {
                    messageProcessorService.pushError(
                        responseQueueName,
                        generalErrors.first().message,
                        generalErrors.first().code.toInt(),
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
                        -6,
                    )
                    return
                }

                // validate spec command schema

                val specialErrors = specialJsonSchemas[command]!!.validate(payload)
                if (specialErrors.isNotEmpty()) {
                    messageProcessorService.pushError(
                        responseQueueName,
                        specialErrors.first().message,
                        specialErrors.first().code.toInt(),
                    )
                    return
                }

                listenerImpl.onMessage(message, pattern)

            } catch (e: JsonParseException) {
                e.printStackTrace()
                messageProcessorService.pushError(
                    responseQueueName,
                    e.originalMessage,
                    -1000,
                )
                return
            }

        } catch (ex: Exception) {

            messageProcessorService.pushError(
                responseQueueName,
                "Could not perform operation due to an unexpected error: ${ex.message}",
                -9
            )
        }
    }
}
