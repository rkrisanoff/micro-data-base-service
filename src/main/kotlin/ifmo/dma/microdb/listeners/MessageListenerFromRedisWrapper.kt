package ifmo.dma.microdb.listeners

import com.fasterxml.jackson.core.JsonParseException
import com.fasterxml.jackson.databind.ObjectMapper
import com.networknt.schema.JsonSchema
import ifmo.dma.microdb.services.MessageProcessorService
import mu.KotlinLogging
import org.springframework.data.redis.connection.Message
import org.springframework.data.redis.connection.MessageListener

class MessageListenerFromRedisWrapper(
    private val listenerImpl: MessageListener,
    private val messageProcessorService: MessageProcessorService,
    private val mapper: ObjectMapper,
    private val responseQueueName: String,
    private val generalSchema: JsonSchema,
    private val specialJsonSchemas: Map<String, JsonSchema>,
) : MessageListener {

    private val logger = KotlinLogging.logger {}

    override fun onMessage(message: Message, pattern: ByteArray?) {
        try {
            val content = message.body.decodeToString()
            logger.info { "\nChannel ${message.channel} received the message:\n ${message.body.decodeToString()}\n" }
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

        } catch (e: Exception) {
            e.printStackTrace()
            messageProcessorService.pushError(
                responseQueueName,
                "Could not perform operation due to an unexpected error: ${e.message}",
                -9
            )
        }
    }
}
