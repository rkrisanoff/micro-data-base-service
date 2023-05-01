package ifmo.dma.microdb

import ifmo.dma.microdb.services.CustomPersistenceExceptionTranslator
import org.springframework.boot.autoconfigure.SpringBootApplication
import org.springframework.boot.context.properties.ConfigurationPropertiesScan
import org.springframework.boot.runApplication
import org.springframework.context.annotation.Bean

@ConfigurationPropertiesScan
@SpringBootApplication
class MicroDataBaseApplication

@Bean
fun persistenceExceptionTranslator(): CustomPersistenceExceptionTranslator {
    return CustomPersistenceExceptionTranslator()
}
fun main(args: Array<String>) {
    runApplication<MicroDataBaseApplication>(*args)
}
