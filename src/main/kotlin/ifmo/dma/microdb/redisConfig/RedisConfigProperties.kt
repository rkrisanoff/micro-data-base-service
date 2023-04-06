package ifmo.dma.microdb.redisConfig

import org.springframework.boot.context.properties.ConfigurationProperties
import org.springframework.boot.context.properties.EnableConfigurationProperties

@EnableConfigurationProperties
@ConfigurationProperties(prefix = "spring.data.redis")
data class RedisConfigProperties (
    var host: String = "127.0.0.1",
    var port: Int = 6666
    )