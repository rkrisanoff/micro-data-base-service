package ifmo.dma.microdb

import org.springframework.boot.autoconfigure.SpringBootApplication
import org.springframework.boot.context.properties.ConfigurationPropertiesScan
import org.springframework.boot.runApplication

@ConfigurationPropertiesScan
@SpringBootApplication
class MicroDataBaseApplication

fun main(args: Array<String>) {
    runApplication<MicroDataBaseApplication>(*args)
}
