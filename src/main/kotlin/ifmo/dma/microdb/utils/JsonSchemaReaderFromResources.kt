package ifmo.dma.microdb.utils

import com.networknt.schema.JsonSchema
import com.networknt.schema.JsonSchemaFactory
import com.networknt.schema.SpecVersion
import org.springframework.core.io.support.PathMatchingResourcePatternResolver
import org.springframework.core.io.support.ResourcePatternResolver
import org.springframework.stereotype.Component

@Component
class JsonSchemaReaderFromResources {
    private fun readSchemaFromResourceAsString(path: String): String {
        val resolver: ResourcePatternResolver = PathMatchingResourcePatternResolver()

        return resolver.getResource("classpath:json-schemas/$path").inputStream.bufferedReader().use { it.readText() }
    }

    fun readJsonSchemaFromResource(path: String): JsonSchema {
        return JsonSchemaFactory.getInstance(SpecVersion.VersionFlag.V4).getSchema(readSchemaFromResourceAsString(path))
    }
}
