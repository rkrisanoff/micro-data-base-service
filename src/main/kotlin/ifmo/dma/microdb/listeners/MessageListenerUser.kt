package ifmo.dma.microdb.listeners

import com.fasterxml.jackson.databind.ObjectMapper
import ifmo.dma.microdb.entity.User
import ifmo.dma.microdb.repo.UserRepo
import ifmo.dma.microdb.services.MessageProcessorService
import org.springframework.data.redis.connection.Message
import org.springframework.data.redis.connection.MessageListener
import org.springframework.stereotype.Component
import java.util.*

@Component
class MessageListenerUser(
    private val userRepo: UserRepo,
    private val messageProcessorService: MessageProcessorService,
    private val mapper: ObjectMapper,
    private val userResponseQueue: String,
) : MessageListener {

    override fun onMessage(message: Message, pattern: ByteArray?) {
        val request = mapper.readTree(message.body.decodeToString())
        val command = request.get("command").asText()
        val payload = request.get("payload")
        when (command) {
            "login" -> {
                val user: Optional<User> = userRepo.findUserByLogin(payload["login"].asText())
                if (user.isPresent) {
                    messageProcessorService.pushSuccessful(
                        userResponseQueue,
                        0,
                        mapOf(
                            Pair(
                                "user",
                                object {
                                    val id = user.get().id
                                    val login = user.get().login
                                    val password = user.get().password
                                    val fullName = user.get().fullName
                                    val isAdmin = user.get().group?.admin?.equals(user.get())
                                },
                            ),
                        ),
                    )
                } else {
                    messageProcessorService.pushSuccessful(
                        userResponseQueue,
                        1,
                        object {
                            val login = payload.get("login").asText()
                        },
                    )
                }
            }
//
//            "save" -> {
//                val user = User()
//                user.login = payload.get("user")["login"].asText()
//                user.password = payload.get("user")["password"].asText()
//                user.fullName = payload.get("user")["fullName"].asText()
//                userRepo.save(user)
//                messageProcessorService.push(userResponseQueue, user)
//            }

            "register" -> {
                if (userRepo.existsUserByLogin(payload["login"].asText())) {
                    messageProcessorService.pushSuccessful(
                        userResponseQueue,
                        1,
                        object {
                            val login = payload["login"].asText()
                        },
                    )
                } else {
                    val user = User()
                    user.login = payload["login"].asText()
                    user.password = payload["password"].asText()
                    user.fullName = payload["fullName"].asText()
                    userRepo.save(user)
                    messageProcessorService.pushSuccessful(
                        userResponseQueue,
                        0,
                        object {},
                    )
                }
            }

            else ->
                messageProcessorService.pushError(
                    userResponseQueue,
                    "Wrong command $command on ${message.channel} channel! Try again!",
                    -1,
                )
        }
    }
}
