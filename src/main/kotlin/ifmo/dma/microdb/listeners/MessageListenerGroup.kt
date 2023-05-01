package ifmo.dma.microdb.listeners

import com.fasterxml.jackson.databind.ObjectMapper
import ifmo.dma.microdb.entity.Group
import ifmo.dma.microdb.entity.User
import ifmo.dma.microdb.repo.GroupRepo
import ifmo.dma.microdb.repo.UserRepo
import ifmo.dma.microdb.services.MessageProcessorService
import ifmo.dma.microdb.utils.InviteCodeGenerator
import org.springframework.data.redis.connection.Message
import org.springframework.data.redis.connection.MessageListener
import org.springframework.stereotype.Component
import java.util.*

@Component
class MessageListenerGroup(
    private val userRepo: UserRepo,
    private val groupRepo: GroupRepo,
    private val messageProcessorService: MessageProcessorService,
    private val mapper: ObjectMapper,
    private val groupResponseQueue: String,
    private val inviteCodeGen: InviteCodeGenerator
) : MessageListener {


//    fun pushWarningIfUserNotExist(payload: JsonNode) : Boolean{
//        val userId = payload.get("groupName").asLong()
//        val user: Optional<User> = userRepo.findUserById(userId)
//        if (user.isEmpty) {
//            messageProcessorService.pushSuccessful(
//                groupResponseQueue,1,
//                object{
//                    val userId = userId
//                })
//            return false
//        }
//        return true
//    }

    override fun onMessage(message: Message, bytes: ByteArray?) {
        val content = message.body.decodeToString()
        val request = mapper.readTree(content)
        val command = request.get("command").asText()
        val payload = request.get("payload")

        when (command) {
            "createGroup" -> {
                val userId = payload.get("userId").asLong()
                val user: Optional<User> = userRepo.findUserById(userId)
                if (user.isEmpty) {
                    messageProcessorService.pushSuccessful(
                        groupResponseQueue, 1,
                        object {
                            val userId = userId
                        })
                    return
                }
                val existingGroup = groupRepo.findGroupByAdmin(user.get())
                if (existingGroup.isPresent) {
                    messageProcessorService.pushSuccessful(
                        groupResponseQueue, 2,
                        object {
                            val userId = userId
                            val groupName = existingGroup.get().name
                        })
                    return

                }
                val admin = user.get()
                val creatableGroup = Group()
                creatableGroup.name = payload.get("string").asText()
                creatableGroup.admin = admin
                creatableGroup.members = mutableSetOf(admin)
                creatableGroup.inviteCode = inviteCodeGen.generateRandomString()
                val group = groupRepo.save(creatableGroup)
                messageProcessorService.pushSuccessful(
                    groupResponseQueue, 0,
                    object {
                        val groupId = group.id
                        val inviteCode = group.inviteCode
                    })
            }

            "enterGroup" -> {
                val userId = payload.get("id").asLong()
                val maybeUser: Optional<User> = userRepo.findUserById(userId)
                if (maybeUser.isEmpty) {
                    messageProcessorService.pushSuccessful(
                        groupResponseQueue, 1,
                        object {
                            val userId = userId
                        })
                    return
                }
                val existingGroup = groupRepo.findGroupByAdmin(maybeUser.get())
                if (existingGroup.isPresent) {
                    messageProcessorService.pushSuccessful(
                        groupResponseQueue, 2,
                        object {
                            val userId = userId
                            val groupName = existingGroup.get().name
                        })
                    return
                }
                val maybeNewGroup = groupRepo.findGroupByInviteCode(payload["inviteCode"].asText())
                if (maybeNewGroup.isEmpty) {
                    messageProcessorService.pushSuccessful(
                        groupResponseQueue, 3,
                        object {
                            val inviteCode = payload["inviteCode"].asText()
                        })
                    return
                }
                maybeNewGroup.get().addMember(maybeUser.get())
                groupRepo.save(maybeNewGroup.get())
                messageProcessorService.pushSuccessful(
                    groupResponseQueue, 0,
                    object {})
            }

            "quitGroup" -> {
                val userId = payload.get("id").asLong()
                val maybeUser: Optional<User> = userRepo.findUserById(userId)
                if (maybeUser.isEmpty) {
                    messageProcessorService.pushSuccessful(
                        groupResponseQueue, 1,
                        object {
                            val userId = userId
                        })
                    return
                }

                val maybeGroup = Optional.ofNullable(maybeUser.get().group)
                if (maybeGroup.isEmpty) {
                    messageProcessorService.pushSuccessful(
                        groupResponseQueue, 2,
                        object {
                            val userId = userId
                        })
                    return
                }
                println(maybeGroup.get().members)
                println(maybeUser.get())
                maybeGroup.get().members.remove(maybeUser.get())
                groupRepo.delete(maybeGroup.get())
                messageProcessorService.pushSuccessful(
                    groupResponseQueue, 0,
                    object {})
            }

            "getGroup" -> {
                val userId = payload.get("id").asLong()
                val maybeUser: Optional<User> = userRepo.findUserById(userId)
                if (maybeUser.isEmpty) {
                    messageProcessorService.pushSuccessful(
                        groupResponseQueue, 1,
                        object {
                            val userId = userId
                        })
                    return
                }

                val maybeGroup = Optional.ofNullable(maybeUser.get().group)
                if (maybeGroup.isEmpty) {
                    messageProcessorService.pushSuccessful(
                        groupResponseQueue, 2,
                        object {
                            val userId = userId
                        })
                    return
                }
                println(maybeGroup.get().members)
                println(maybeUser.get())
                maybeGroup.get().members.remove(maybeUser.get())
                groupRepo.delete(maybeGroup.get())
                messageProcessorService.pushSuccessful(
                    groupResponseQueue, 0,
                    object {
                        val groupCreds = object {
                            val groupId = maybeGroup.get().id
                            val groupName = maybeGroup.get().name
                            val inviteCode = maybeGroup.get().inviteCode
                        }
                    })
            }

            else ->
                messageProcessorService.pushError(
                    groupResponseQueue,
                    "Wrong command $command on ${message.channel} channel! Try again!",
                    -1
                )
        }
    }
}