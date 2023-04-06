package ifmo.dma.microdb

import org.springframework.boot.autoconfigure.SpringBootApplication
import org.springframework.boot.context.properties.ConfigurationPropertiesScan
import org.springframework.boot.runApplication

@ConfigurationPropertiesScan
@SpringBootApplication
class MicrodbApplication

fun main(args: Array<String>) {
    runApplication<MicrodbApplication>(*args)
}
