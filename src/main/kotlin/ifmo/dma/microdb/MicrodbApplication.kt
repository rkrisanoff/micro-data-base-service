package ifmo.dma.microdb

import org.springframework.boot.autoconfigure.SpringBootApplication
import org.springframework.boot.runApplication


@SpringBootApplication
class MicrodbApplication

fun main(args: Array<String>) {
    runApplication<MicrodbApplication>(*args)
}
