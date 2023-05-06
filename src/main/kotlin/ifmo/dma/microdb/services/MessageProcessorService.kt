package ifmo.dma.microdb.services

import com.fasterxml.jackson.databind.ObjectMapper
import ifmo.dma.microdb.dto.MResponse
import mu.KotlinLogging
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.stereotype.Service

@Service
class MessageProcessorService(
    @Autowired private val redisMessageService: RedisMessageService,
) {

    private val mapper = ObjectMapper()
    private val logger = KotlinLogging.logger {}

    fun pushSuccessful(queue: String, responseCode: Int, payload: Any) {
        logger.info { "\nResponse with responseCode $responseCode  and payload:\n${mapper.writeValueAsString(payload)}\n will be sent to the $queue" }

        redisMessageService.push(
            queue,
            mapper.writeValueAsString(
                MResponse(
                    true,
                    "",
                    responseCode,
                    payload,
                ),
            ),
        )
    }

    fun pushError(queue: String, errorMessage: String, responseCode: Int) {
        logger.error { "\nMessage of critical error with $responseCode will be sent to the $queue" }

        redisMessageService.push(
            queue,
            mapper.writeValueAsString(
                MResponse(
                    false,
                    errorMessage,
                    responseCode,
                    object {},
                ),
            ),
        )
    }
}
