package ifmo.dma.microdb.listeners

import com.fasterxml.jackson.databind.ObjectMapper
import com.fasterxml.jackson.databind.exc.UnrecognizedPropertyException
import ifmo.dma.microdb.dto.UserDTO
import ifmo.dma.microdb.entity.User
import ifmo.dma.microdb.repo.UserRepo
import ifmo.dma.microdb.services.MessageProcessorService
import org.hibernate.PropertyValueException
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.data.redis.connection.Message
import org.springframework.data.redis.connection.MessageListener
import org.springframework.stereotype.Component
import java.util.*

@Component
class MessageListenerUser @Autowired constructor(
    @Autowired private val userRepo: UserRepo,
    @Autowired private val messageProcessorService: MessageProcessorService
) : MessageListener {
    private val mapper = ObjectMapper()

    private val responseQueueName = "md-user-response"

    override fun onMessage(message: Message, bytes: ByteArray?) {
        val messageTree = mapper.readTree(String(message.body))
        val commandName = messageTree.get("command")?.asText()
        if (commandName == null) {
            messageProcessorService.pushError(
                responseQueueName,
                "Command undefined!",
                500
            )
        }
        when (commandName) {
            "findUserByLogin" -> {
                val user: Optional<User> = userRepo.findUserByLogin((messageTree.get("payload").get("login").asText()))
                if (user.isPresent) {
                    val login = user.get().login
                    if (login != null) {
                        messageProcessorService.push(
                            responseQueueName,
                            mapOf(Pair("login", login))
                        )
                    }
                } else {
                    messageProcessorService.pushError(
                        responseQueueName,
                        "The user with login (${messageTree.get("payload").get("login").asText()} doesn't exist",
                        500
                    )
                }

            }

            "existsUserByLogin" -> {
                val userExists = userRepo.existsUserByLogin(messageTree.get("payload").get("login").asText())
                messageProcessorService.push(responseQueueName, userExists)


            }

            "save" -> {
                try {
                    val userDTO: UserDTO =
                        mapper.readValue(messageTree.get("payload").get("user").asText(), UserDTO::class.java)

                    val user = User()

                    user.login = userDTO.login
                    user.password = userDTO.password
                    user.username = userDTO.username
                    userRepo.save(user)
                } catch (e: UnrecognizedPropertyException) {
                    println("The '${e.propertyName}'-property didn't recognize")
                } catch (e: PropertyValueException) {
                    println("Error: missing required field '${e.propertyName}'")
                } catch (e: org.springframework.dao.DataIntegrityViolationException) {
                    println("DATA INTEGRITY ${e.message}")
                }
            }

            else -> {
                messageProcessorService.pushError(
                    responseQueueName,
                    "Wrong command on ${message.channel} channel! Try again!",
                    500
                )
            }
        }

    }

}
