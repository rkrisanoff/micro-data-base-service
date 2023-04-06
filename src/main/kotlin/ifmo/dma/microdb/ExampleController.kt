package ifmo.dma.microdb

import ifmo.dma.microdb.services.RedisMessageService
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.RequestParam
import org.springframework.web.bind.annotation.RestController
import java.time.Duration

@RestController
class ExampleController @Autowired constructor(val redisMessageService: RedisMessageService) {
    private val defaultTimeout = Duration.ofSeconds(100)

    @GetMapping("/api/hello")
    fun hello(@RequestParam message: String): String {
        return redisMessageService.publishAndWaitForResponse(
            "input", message,
            "output", defaultTimeout
        )
            ?: "Message sent, but no response received yet. :("
    }
}