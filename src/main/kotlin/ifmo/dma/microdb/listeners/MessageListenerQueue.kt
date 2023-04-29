package ifmo.dma.microdb.listeners

import com.fasterxml.jackson.databind.ObjectMapper
import ifmo.dma.microdb.entity.Queue
import ifmo.dma.microdb.entity.QueueStudent
import ifmo.dma.microdb.entity.QueueStudentId
import ifmo.dma.microdb.entity.User
import ifmo.dma.microdb.repo.GroupRepo
import ifmo.dma.microdb.repo.QueueRepo
import ifmo.dma.microdb.repo.QueueStudentRepository
import ifmo.dma.microdb.repo.UserRepo
import ifmo.dma.microdb.services.MessageProcessorService
import ifmo.dma.microdb.utils.InviteCodeGenerator
import org.springframework.data.redis.connection.Message
import org.springframework.data.redis.connection.MessageListener
import org.springframework.stereotype.Component
import java.util.*

@Component
class MessageListenerQueue(
    private val userRepo: UserRepo,
    private val groupRepo: GroupRepo,
    private val queueStudentIdRepo: QueueStudentRepository,
    private val messageProcessorService: MessageProcessorService,
    private val mapper: ObjectMapper,
    private val groupResponseQueue: String,
    private val inviteCodeGen: InviteCodeGenerator, private val queueRepo: QueueRepo
) : MessageListener {
    override fun onMessage(message: Message, bytes: ByteArray?) {
        val content = message.body.decodeToString()
        val request = mapper.readTree(content)
        val command = request.get("command").asText()
        val payload = request.get("payload")

        when (command) {
            "createQueue" -> {
                val userId = payload.get("userId").asLong()
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
                val maybeManagedGroup = groupRepo.findGroupByAdmin(maybeUser.get())
                if (maybeManagedGroup.isEmpty) {
                    messageProcessorService.pushSuccessful(
                        groupResponseQueue, 3,
                        object {
                            val userId = userId
                        })
                    return
                }
                val queue = Queue()
                queue.group = maybeManagedGroup.get()
                queue.name = payload.get("queueName").asText()
                queueRepo.save(queue)
                messageProcessorService.pushSuccessful(
                    groupResponseQueue, 0,
                    object {})
            }

            "deleteQueue" -> {
                val userId = payload.get("userId").asLong()
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
                val maybeManagedGroup = groupRepo.findGroupByAdmin(maybeUser.get())
                if (maybeManagedGroup.isEmpty) {
                    messageProcessorService.pushSuccessful(
                        groupResponseQueue, 3,
                        object {
                            val userId = userId
                        })
                    return
                }
                val maybeQueue = queueRepo.findById(payload.get("queueId").asLong())
                if (maybeQueue.isEmpty) {
                    messageProcessorService.pushSuccessful(
                        groupResponseQueue, 4,
                        object {
                            val queueId = payload.get("queueId").asLong()
                        })
                    return
                }
                queueRepo.delete(maybeQueue.get())
                messageProcessorService.pushSuccessful(
                    groupResponseQueue, 0,
                    object {})
            }

            "enterQueue" -> {
                val userId = payload.get("userId").asLong()
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

                val maybeQueue = queueRepo.findById(payload.get("queueId").asLong())
                if (maybeQueue.isEmpty) {
                    messageProcessorService.pushSuccessful(
                        groupResponseQueue, 3,
                        object {
                            val queueId = payload.get("queueId").asLong()
                        })
                    return
                }
                if (maybeQueue.get().group?.equals(maybeUser.get().group) == false) {
                    messageProcessorService.pushSuccessful(
                        groupResponseQueue, 4,
                        object {
                            val userId = userId
                            val queueId = payload.get("queueId").asLong()
                            val groupId = maybeGroup.get().id
                        })
                    return
                }
                val queueStudentId = QueueStudentId()
                queueStudentId.queue = maybeQueue.get()
                queueStudentId.user = maybeUser.get()
                val queueStudent = QueueStudent()
                if (queueStudentIdRepo.existsById(queueStudentId)) {
                    messageProcessorService.pushSuccessful(
                        groupResponseQueue, 5,
                        object {
                            val userId = userId
                            val queueId = payload.get("queueId").asLong()
                        })
                    return
                }
                queueStudent.queueStudentId = queueStudentId
                queueStudentIdRepo.save(queueStudent)
                messageProcessorService.pushSuccessful(
                    groupResponseQueue, 0,
                    object {})
            }

            "quitQueue" -> {
                val userId = payload.get("userId").asLong()
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

                val maybeQueue = queueRepo.findById(payload.get("queueId").asLong())
                if (maybeQueue.isEmpty) {
                    messageProcessorService.pushSuccessful(
                        groupResponseQueue, 3,
                        object {
                            val queueId = payload.get("queueId").asLong()
                        })
                    return
                }
                if (maybeQueue.get().group?.equals(maybeUser.get().group) == false) {
                    messageProcessorService.pushSuccessful(
                        groupResponseQueue, 4,
                        object {
                            val queueId = payload.get("queueId").asLong()
                            val groupId = maybeGroup.get().id
                        })
                    return
                }
                if (!maybeQueue.get().users.contains(maybeUser.get())) {
                    messageProcessorService.pushSuccessful(
                        groupResponseQueue, 5,
                        object {
                            val userId = userId
                            val queueId = payload.get("queueId").asLong()
                        })
                    return
                }

                maybeQueue.get().users.remove(maybeUser.get())

                messageProcessorService.pushSuccessful(
                    groupResponseQueue, 0,
                    object {
                    })
            }

            "getQueue" -> {
                val userId = payload.get("userId").asLong()
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

                val maybeQueue = queueRepo.findById(payload.get("queueId").asLong())
                if (maybeQueue.isEmpty) {
                    messageProcessorService.pushSuccessful(
                        groupResponseQueue, 3,
                        object {
                            val queueId = payload.get("queueId").asLong()
                        })
                    return
                }
                if (maybeQueue.get().group?.equals(maybeUser.get().group) == false) {
                    messageProcessorService.pushSuccessful(
                        groupResponseQueue, 4,
                        object {
                            val userId = userId
                            val queueId = payload.get("queueId").asLong()
                            val groupId = maybeGroup.get().id
                        })
                    return
                }
                maybeQueue.get()
                val studentsList = queueStudentIdRepo.findAllByQueue(maybeQueue.get())

                messageProcessorService.pushSuccessful(
                    groupResponseQueue, 0,
                    object {
                        val queueId = maybeQueue.get().id
                        val queueName = maybeQueue.get().name
                        val users = studentsList.map { standing ->
                            {
                                object {
                                    val fullName = standing.user?.fullName
                                    val id = standing.user?.id
                                    val gotInQueue = standing.createdAt
                                }
                            }
                        }

                    })
            }

            "getQAllQueues" -> {
                val userId = payload.get("userId").asLong()
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
                if (maybeGroup.isEmpty) {
                    messageProcessorService.pushSuccessful(
                        groupResponseQueue, 0,
                        object {
                            val queueList = maybeGroup.get().queues.map { queue ->
                                {
                                    {
                                        object {
                                            val id = queue.id
                                            val queueName = queue.name
                                            val recordsNumber = queueStudentIdRepo.countAllByQueue(queue)
                                        }
                                    }
                                }
                            }
                        })
                    return
                }
                maybeGroup.get().queues


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