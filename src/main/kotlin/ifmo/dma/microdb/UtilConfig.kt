package ifmo.dma.microdb

import com.fasterxml.jackson.databind.ObjectMapper
import ifmo.dma.microdb.utils.InviteCodeGenerator
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration

@Configuration
class UtilConfig {
    @Bean
    fun mapper(): ObjectMapper {
        return ObjectMapper()
    }

    @Bean
    fun inviteCodeGen(): InviteCodeGenerator {
        return InviteCodeGenerator()
    }
}
