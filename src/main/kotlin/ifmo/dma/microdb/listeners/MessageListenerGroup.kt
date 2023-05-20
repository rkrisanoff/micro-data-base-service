package ifmo.dma.microdb.listeners

import com.fasterxml.jackson.databind.ObjectMapper
import ifmo.dma.microdb.entity.Group
import ifmo.dma.microdb.entity.User
import ifmo.dma.microdb.repo.GroupRepo
import ifmo.dma.microdb.repo.UserRepo
import ifmo.dma.microdb.services.MessageProcessorService
import ifmo.dma.microdb.utils.InviteCodeGenerator
import mu.KotlinLogging
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
    private val inviteCodeGen: InviteCodeGenerator,
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
private val logger = KotlinLogging.logger {}

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
                        groupResponseQueue,
                        1,
                        object {
                            val userId = userId
                        },
                    )
                    return
                }
                val existingGroup = Optional.ofNullable(user.get().group)

                if (existingGroup.isPresent) {
                    messageProcessorService.pushSuccessful(
                        groupResponseQueue,
                        2,
                        object {
                            val userId = userId
                            val groupName = existingGroup.get().name
                        },
                    )
                    return
                }
                val admin = user.get()
                val creatableGroup = Group()
                creatableGroup.name = payload.get("groupName").asText()
                creatableGroup.admin = admin
                creatableGroup.members = mutableSetOf(admin)
                creatableGroup.inviteCode = inviteCodeGen.generateRandomString()
                admin.group = creatableGroup
                val group = groupRepo.save(creatableGroup)
                userRepo.save(admin)
                messageProcessorService.pushSuccessful(
                    groupResponseQueue,
                    0,
                    object {
                        val groupId = group.id
                        val inviteCode = group.inviteCode
                    },
                )
            }

            "enterGroup" -> {
                val userId = payload.get("userId").asLong()
                val maybeUser: Optional<User> = userRepo.findUserById(userId)
                if (maybeUser.isEmpty) {
                    messageProcessorService.pushSuccessful(
                        groupResponseQueue,
                        1,
                        object {
                            val userId = userId
                        },
                    )
                    return
                }
                val existingGroup = Optional.ofNullable(maybeUser.get().group)
                if (existingGroup.isPresent) {
                    messageProcessorService.pushSuccessful(
                        groupResponseQueue,
                        2,
                        object {
                            val userId = userId
                            val groupName = existingGroup.get().name
                        },
                    )
                    return
                }
                val maybeNewGroup = groupRepo.findGroupByInviteCode(payload["inviteCode"].asText())
                if (maybeNewGroup.isEmpty) {
                    messageProcessorService.pushSuccessful(
                        groupResponseQueue,
                        3,
                        object {
                            val inviteCode = payload["inviteCode"].asText()
                        },
                    )
                    return
                }
                val group = maybeNewGroup.get()
                group.addMember(maybeUser.get())

                groupRepo.save(group)
                messageProcessorService.pushSuccessful(
                    groupResponseQueue,
                    0,
                    object {},
                )
            }

            "quitGroup" -> {
                val userId = payload.get("userId").asLong()
                val maybeUser: Optional<User> = userRepo.findUserById(userId)
                if (maybeUser.isEmpty) {
                    messageProcessorService.pushSuccessful(
                        groupResponseQueue,
                        1,
                        object {
                            val userId = userId
                        },
                    )
                    return
                }
                val user = maybeUser.get()

                val maybeGroup = Optional.ofNullable(user.group)
                if (maybeGroup.isEmpty) {
                    messageProcessorService.pushSuccessful(
                        groupResponseQueue,
                        2,
                        object {
                            val userId = userId
                        },
                    )
                    return
                }
                val group = maybeGroup.get()

                if (user.id == group.admin?.id) {
                    messageProcessorService.pushSuccessful(
                        groupResponseQueue,
                        3,
                        object {
                            val userId = userId
                            val groupId = group.id
                        },
                    )
                    return
                }
                group.members.remove(user)
                user.group = null
                userRepo.save(user)
                groupRepo.save(group)
                messageProcessorService.pushSuccessful(
                    groupResponseQueue,
                    0,
                    object {},
                )
            }

            "deleteGroup" -> {
                val userId = payload.get("userId").asLong()
                val maybeUser: Optional<User> = userRepo.findUserById(userId)
                if (maybeUser.isEmpty) {
                    messageProcessorService.pushSuccessful(
                        groupResponseQueue,
                        1,
                        object {
                            val userId = userId
                        },
                    )
                    return
                }
                val user = maybeUser.get()

                val maybeGroup = Optional.ofNullable(user.group)
                if (maybeGroup.isEmpty) {
                    messageProcessorService.pushSuccessful(
                        groupResponseQueue,
                        2,
                        object {
                            val userId = userId
                        },
                    )
                    return
                }
                val group = maybeGroup.get()

                if (group.admin!! != user) {
                    messageProcessorService.pushSuccessful(
                        groupResponseQueue,
                        3,
                        object {
                            val userId = userId
                            val adminId = group.admin!!.id
                            val groupId = group.id

                        },
                    )
                    return
                }
                group.members.forEach { user ->
                    run {
                        user.group = null
                        userRepo.save(user)
                    }
                }

                groupRepo.delete(group)

                messageProcessorService.pushSuccessful(
                    groupResponseQueue,
                    0,
                    object {},
                )
            }

            "getGroup" -> {
                val userId = payload.get("userId").asLong()
                val maybeUser: Optional<User> = userRepo.findUserById(userId)
                if (maybeUser.isEmpty) {
                    messageProcessorService.pushSuccessful(
                        groupResponseQueue,
                        1,
                        object {
                            val userId = userId
                        },
                    )
                    return
                }

                val maybeGroup = Optional.ofNullable(maybeUser.get().group)
                if (maybeGroup.isEmpty) {
                    messageProcessorService.pushSuccessful(
                        groupResponseQueue,
                        2,
                        object {
                            val userId = userId
                        },
                    )
                    return
                }
                messageProcessorService.pushSuccessful(
                    groupResponseQueue,
                    0,
                    object {
                        val groupId = maybeGroup.get().id
                        val groupName = maybeGroup.get().name
                        val inviteCode = maybeGroup.get().inviteCode
                    },
                )
            }
            "getGroupList" -> {
                val userId = payload.get("userId").asLong()
                val maybeUser: Optional<User> = userRepo.findUserById(userId)
                if (maybeUser.isEmpty) {
                    messageProcessorService.pushSuccessful(
                        groupResponseQueue,
                        1,
                        object {
                            val userId = userId
                        },
                    )
                    return
                }

                val maybeGroup = Optional.ofNullable(maybeUser.get().group)
                if (maybeGroup.isEmpty) {
                    messageProcessorService.pushSuccessful(
                        groupResponseQueue,
                        2,
                        object {
                            val userId = userId
                        },
                    )
                    return
                }
                maybeGroup.get().members.remove(maybeUser.get())
                groupRepo.delete(maybeGroup.get())
                messageProcessorService.pushSuccessful(
                    groupResponseQueue,
                    0,
                    object {
                        val userList = maybeGroup.get().members.map { user ->
                            object {
                                val userId = user.id
                                val login = user.login
                                val fullName = user.fullName
                            }

                        }
                    },
                )
            }

            else ->
                messageProcessorService.pushError(
                    groupResponseQueue,
                    "[Group] Wrong command $command on ${message.channel.decodeToString()} channel! Try again!",
                    -1,
                )
        }
    }
}
