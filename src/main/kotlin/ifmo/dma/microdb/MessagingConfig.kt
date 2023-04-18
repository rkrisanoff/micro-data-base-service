package ifmo.dma.microdb

import com.fasterxml.jackson.databind.ObjectMapper
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration

@Configuration
class MessagingConfig {
    @Bean
    fun mapper(): ObjectMapper {
        return ObjectMapper()
    }
}