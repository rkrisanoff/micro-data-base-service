package ifmo.dma.microdb.services

import com.fasterxml.jackson.databind.ObjectMapper
import ifmo.dma.microdb.dto.MResponse
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.stereotype.Service


@Service
class MessageProcessorService(
    @Autowired private val redisMessageService: RedisMessageService,
) {

    private val mapper = ObjectMapper()

    fun pushSuccessful(queue: String, responseCode: Int,payload: Any) {
        redisMessageService.push(
            queue, mapper.writeValueAsString(
                MResponse(
                    true,
                    "",
                    responseCode,
                    payload
                )
            )
        )
    }

    fun pushError(queue: String, errorMessage: String, responseCode: Int) {
        redisMessageService.push(
            queue, mapper.writeValueAsString(
                MResponse(
                    false,
                    errorMessage,
                    responseCode,
                    object {}
                )
            )
        )
    }
}