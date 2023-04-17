package ifmo.dma.microdb.listeners

import com.fasterxml.jackson.databind.DatabindException
import com.fasterxml.jackson.databind.ObjectMapper
import ifmo.dma.microdb.dto.MRequest
import ifmo.dma.microdb.entity.User
import ifmo.dma.microdb.repo.UserRepo
import ifmo.dma.microdb.services.MessageProcessorService
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
        try {
            val mRequest: MRequest = mapper.readValue(message.body, MRequest::class.java)

            when (mRequest.command) {
                "findUserByLogin" -> {
                    if (!mRequest.payload.keys.contains("login")) {
                        messageProcessorService.pushError(
                            responseQueueName,
                            "The property `login` undefined",
                            500
                        )
                        return
                    }
                    val user: Optional<User> = userRepo.findUserByLogin(mRequest.payload["login"] as String)
                    if (user.isPresent) {
                        messageProcessorService.push(
                            responseQueueName,
                            mapOf(Pair("user", user.get()))
                        )
                    } else {
                        messageProcessorService.pushError(
                            responseQueueName,
                            "The user with login (${mRequest.payload["login"] as String} doesn't exist",
                            500
                        )
                    }

                }

                "existsUserByLogin" -> {
                    if (!mRequest.payload.keys.contains("login")) {
                        messageProcessorService.pushError(
                            responseQueueName,
                            "The property `login` undefined",
                            500
                        )
                        return
                    }
                    val userExists = userRepo.existsUserByLogin(mRequest.payload["login"] as String)
                    messageProcessorService.push(responseQueueName, mapOf(Pair("exists", userExists)))
                }

                "save" -> {
                        if (!mRequest.payload.keys.contains("user")) {
                            messageProcessorService.pushError(
                                responseQueueName,
                                "The property `user` undefined",
                                500
                            )
                            return
                        }
                        val userMap: LinkedHashMap<*, *> = mRequest.payload["user"] as LinkedHashMap<*, *>
                        if (!(userMap.contains("login")&&userMap.contains("username")&&userMap.contains("password"))){
                            messageProcessorService.pushError(
                                responseQueueName,
                                "The format of `user` object wrong",
                                500
                            )
                            return
                        }
                        val user = User()

                        user.login = userMap["login"] as String
                        user.password = userMap["password"] as String
                        user.username = userMap["username"] as String
                        userRepo.save(user)
                        messageProcessorService.push(responseQueueName, user)

                }

                else ->
                    messageProcessorService.pushError(
                        responseQueueName,
                        "Wrong command ${mRequest.command} on ${message.channel} channel! Try again!",
                        500
                    )

            }
        } catch (e: DatabindException) {
            messageProcessorService.pushError(
                responseQueueName,
                "TOTAL WRONG REQUEST! FUCK YOU",
                500
            )
        }
    }
}
