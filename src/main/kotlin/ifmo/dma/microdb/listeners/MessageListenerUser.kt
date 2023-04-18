package ifmo.dma.microdb.listeners

import com.fasterxml.jackson.databind.ObjectMapper
import com.networknt.schema.JsonSchema
import ifmo.dma.microdb.entity.User
import ifmo.dma.microdb.repo.UserRepo
import ifmo.dma.microdb.services.MessageProcessorService
import ifmo.dma.microdb.utils.JsonSchemaReaderFromResources
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.data.redis.connection.Message
import org.springframework.stereotype.Component
import java.util.*

@Component
class MessageListenerUser @Autowired constructor(
    @Autowired private val jsonSchemaReaderFromResources: JsonSchemaReaderFromResources,
    @Autowired private val userRepo: UserRepo,
    @Autowired private val messageProcessorService: MessageProcessorService,
    @Autowired private val mapper: ObjectMapper
) : MessageListenerMSD(messageProcessorService, mapper) {

    override val responseQueueName = "md-user-response"
    override val commandSet = setOf<String>("findUserByLogin", "existsUserByLogin", "save")
    override val schemas = mapOf<String, JsonSchema>(
        Pair(
            "general",
            jsonSchemaReaderFromResources.readJsonSchemaFromResource("general.json")
        ),
        Pair(
            "findUserByLogin",
            jsonSchemaReaderFromResources.readJsonSchemaFromResource("payload/user/findUserByLogin.json")

        ),
        Pair(
            "existsUserByLogin",
            jsonSchemaReaderFromResources.readJsonSchemaFromResource("payload/user/findUserByLogin.json")
        ),
        Pair(
            "save",
            jsonSchemaReaderFromResources.readJsonSchemaFromResource("payload/user/save.json")
        ),
    )


    override fun onMessage(message: Message, pattern: ByteArray?) {
        val content = message.body.decodeToString()
        !validateGeneral(content) && return
        val request = mapper.readTree(content)
        val command = request.get("command").asText()
        val payload = request.get("payload")
        if (!commandSet.contains(command)) {
            messageProcessorService.pushError(
                responseQueueName,
                "Wrong command $command on ${message.channel} channel! Try again!",
                500
            )
            return
        }

        !validatePayloadForCommand(command, payload) && return

        when (command) {
            "findUserByLogin" -> {
                val user: Optional<User> = userRepo.findUserByLogin(payload.get("login").asText())
                if (user.isPresent) {
                    messageProcessorService.push(
                        responseQueueName,
                        mapOf(Pair("user", user.get()))
                    )
                } else {
                    messageProcessorService.pushError(
                        responseQueueName,
                        "The user with login (${payload.get("login").asText()} doesnt exist",
                        500
                    )
                }

            }

            "existsUserByLogin" -> {
                val userExists = userRepo.existsUserByLogin(payload["login"].asText())
                messageProcessorService.push(responseQueueName, mapOf(Pair("exists", userExists)))
            }

            "save" -> {
                val user = User()
                user.login = payload.get("user")["login"].asText()
                user.password = payload.get("user")["password"].asText()
                user.username = payload.get("user")["username"].asText()
                userRepo.save(user)
                messageProcessorService.push(responseQueueName, user)
            }

            else ->
                messageProcessorService.pushError(
                    responseQueueName,
                    "Wrong command $command on ${message.channel} channel! Try again!",
                    500
                )
        }
    }
}
