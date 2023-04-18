package ifmo.dma.microdb.listeners

import com.fasterxml.jackson.core.JsonParseException
import com.fasterxml.jackson.databind.JsonNode
import com.fasterxml.jackson.databind.ObjectMapper
import com.networknt.schema.JsonSchema
import ifmo.dma.microdb.services.MessageProcessorService
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.data.redis.connection.Message
import org.springframework.data.redis.connection.MessageListener
import org.springframework.stereotype.Component

@Component
abstract class MessageListenerMSD(
    @Autowired private val messageProcessorService: MessageProcessorService,
    @Autowired private val mapper: ObjectMapper
) : MessageListener {
    protected abstract val responseQueueName: String
    protected abstract val commandSet: Set<String>
    protected abstract val schemas: Map<String, JsonSchema>

    protected fun validateGeneral(content: String): Boolean {
        try {
            val errors = schemas["general"]!!.validate(mapper.readTree(content))
            if (errors.isNotEmpty()) {
                messageProcessorService.pushError(
                    responseQueueName,
                    errors.first().message,
//                    (errors.map { t -> t.message }).joinToString(prefix = "", postfix = "", separator = ", "),
                    errors.first().code.toInt()
                )
                return false
            }
        } catch (e: JsonParseException) {
            messageProcessorService.pushError(
                responseQueueName,
                e.originalMessage,
                -1000
            )
            return false
        }
        return true
    }

    protected fun validatePayloadForCommand(command: String, payload: JsonNode): Boolean {
        val errors = schemas[command]!!.validate(payload)
        if (errors.isNotEmpty()) {
            messageProcessorService.pushError(
                responseQueueName,
                errors.first().message,
//                (errors.map { t -> t.message }).joinToString(prefix = "", postfix = "", separator = ", "),
                errors.first().code.toInt()
            )
            return false
        }
        return true
    }

    abstract override fun onMessage(message: Message, pattern: ByteArray?)

}