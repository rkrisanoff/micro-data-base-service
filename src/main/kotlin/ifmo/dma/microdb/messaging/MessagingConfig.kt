package ifmo.dma.microdb.messaging

import com.networknt.schema.JsonSchema
import ifmo.dma.microdb.utils.JsonSchemaReaderFromResources
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration

@Configuration
class MessagingConfig {

    @Bean
    fun userResponseQueue(): String {
        return "md-user-response"
    }

    @Bean
    fun groupResponseQueue(): String {
        return "md-group-response"
    }

    @Bean
    fun queueResponseQueue(): String {
        return "md-queue-response"
    }

    @Bean
    fun userJsonSchemas(jsonSchemaReaderFromResources: JsonSchemaReaderFromResources): Map<String, JsonSchema> {
        return mapOf(
            Pair(
                "login",
                jsonSchemaReaderFromResources.readJsonSchemaFromResource("payload/user/login.json"),
            ),
            Pair(
                "register",
                jsonSchemaReaderFromResources.readJsonSchemaFromResource("payload/user/register.json"),
            ),
        )
    }

    @Bean
    fun groupJsonSchemas(jsonSchemaReaderFromResources: JsonSchemaReaderFromResources): Map<String, JsonSchema> {
        return mapOf(
            Pair(
                "createGroup",
                jsonSchemaReaderFromResources.readJsonSchemaFromResource("payload/group/createGroup.json"),
            ),
            Pair(
                "enterGroup",
                jsonSchemaReaderFromResources.readJsonSchemaFromResource("payload/group/enterGroup.json"),
            ),
            Pair(
                "quitGroup",
                jsonSchemaReaderFromResources.readJsonSchemaFromResource("payload/group/quitGroup.json"),
            ),
            Pair(
                "deleteGroup",
                jsonSchemaReaderFromResources.readJsonSchemaFromResource("payload/group/deleteGroup.json"),
            ),
            Pair(
                "getGroup",
                jsonSchemaReaderFromResources.readJsonSchemaFromResource("payload/group/getGroup.json"),
            ),
        )
    }

    @Bean
    fun queueJsonSchemas(jsonSchemaReaderFromResources: JsonSchemaReaderFromResources): Map<String, JsonSchema> {
        return mapOf(
            Pair(
                "createQueue",
                jsonSchemaReaderFromResources.readJsonSchemaFromResource("payload/queue/createQueue.json"),
            ),
            Pair(
                "deleteQueue",
                jsonSchemaReaderFromResources.readJsonSchemaFromResource("payload/queue/deleteQueue.json"),
            ),
            Pair(
                "enterQueue",
                jsonSchemaReaderFromResources.readJsonSchemaFromResource("payload/queue/enterQueue.json"),
            ),
            Pair(
                "quitQueue",
                jsonSchemaReaderFromResources.readJsonSchemaFromResource("payload/queue/quitQueue.json"),
            ),
            Pair(
                "getQueue",
                jsonSchemaReaderFromResources.readJsonSchemaFromResource("payload/queue/getQueue.json"),
            ),
            Pair(
                "getQAllQueues",
                jsonSchemaReaderFromResources.readJsonSchemaFromResource("payload/queue/getQAllQueues.json"),
            ),
        )
    }

    @Bean
    fun generalJsonSchema(jsonSchemaReaderFromResources: JsonSchemaReaderFromResources): JsonSchema {
        return jsonSchemaReaderFromResources.readJsonSchemaFromResource("general.json")
    }
}
