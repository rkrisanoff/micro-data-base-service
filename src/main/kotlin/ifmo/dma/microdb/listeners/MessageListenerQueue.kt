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
import org.springframework.data.redis.connection.Message
import org.springframework.data.redis.connection.MessageListener
import org.springframework.stereotype.Component
import java.util.*

@Component
class MessageListenerQueue(
    private val userRepo: UserRepo,
    private val groupRepo: GroupRepo,
    private val queueRepo: QueueRepo,
    private val queueStudentIdRepo: QueueStudentRepository,
    private val messageProcessorService: MessageProcessorService,
    private val mapper: ObjectMapper,
    private val queueResponseQueue: String,

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
                        queueResponseQueue,
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
                        queueResponseQueue,
                        2,
                        object {
                            val userId = userId
                        },
                    )
                    return
                }
                val maybeManagedGroup = Optional.ofNullable(maybeUser.get().group)
                if (maybeGroup.get().admin!!.id?.toLong() != userId) {
                    messageProcessorService.pushSuccessful(
                        queueResponseQueue,
                        3,
                        object {
                            val userId = userId
                        },
                    )
                    return
                }

                val queue = Queue()
                queue.group = maybeManagedGroup.get()
                queue.name = payload.get("queueName").asText()
                queueRepo.save(queue)
                println(queueResponseQueue)
                messageProcessorService.pushSuccessful(
                    queueResponseQueue,
                    0,
                    object {},
                )
            }

            "deleteQueue" -> {
                val userId = payload.get("userId").asLong()
                val maybeUser: Optional<User> = userRepo.findUserById(userId)
                if (maybeUser.isEmpty) {
                    messageProcessorService.pushSuccessful(
                        queueResponseQueue,
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
                        queueResponseQueue,
                        2,
                        object {
                            val userId = userId
                        },
                    )
                    return
                }
                val maybeManagedGroup = groupRepo.findGroupByAdmin(maybeUser.get())
                if (maybeManagedGroup.isEmpty) {
                    messageProcessorService.pushSuccessful(
                        queueResponseQueue,
                        3,
                        object {
                            val userId = userId
                        },
                    )
                    return
                }
                val maybeQueue = queueRepo.findById(payload.get("queueId").asLong())
                if (maybeQueue.isEmpty) {
                    messageProcessorService.pushSuccessful(
                        queueResponseQueue,
                        4,
                        object {
                            val queueId = payload.get("queueId").asLong()
                        },
                    )
                    return
                }
                queueRepo.delete(maybeQueue.get())
                messageProcessorService.pushSuccessful(
                    queueResponseQueue,
                    0,
                    object {},
                )
            }

            "enterQueue" -> {
                val userId = payload.get("userId").asLong()
                val maybeUser: Optional<User> = userRepo.findUserById(userId)
                if (maybeUser.isEmpty) {
                    messageProcessorService.pushSuccessful(
                        queueResponseQueue,
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
                        queueResponseQueue,
                        2,
                        object {
                            val userId = userId
                        },
                    )
                    return
                }

                val maybeQueue = queueRepo.findById(payload.get("queueId").asLong())
                if (maybeQueue.isEmpty) {
                    messageProcessorService.pushSuccessful(
                        queueResponseQueue,
                        3,
                        object {
                            val queueId = payload.get("queueId").asLong()
                        },
                    )
                    return
                }
                if (maybeQueue.get().group!!.id != maybeUser.get().group!!.id) {
                    messageProcessorService.pushSuccessful(
                        queueResponseQueue,
                        4,
                        object {
                            val userId = userId
                            val queueId = payload.get("queueId").asLong()
                            val groupId = maybeGroup.get().id
                        },
                    )
                    return
                }
                val queueStudentId = QueueStudentId()
                queueStudentId.queue = maybeQueue.get()
                queueStudentId.user = maybeUser.get()
                val queueStudent = QueueStudent()
                if (queueStudentIdRepo.existsById(queueStudentId)) {
                    messageProcessorService.pushSuccessful(
                        queueResponseQueue,
                        5,
                        object {
                            val userId = userId
                            val queueId = payload.get("queueId").asLong()
                        },
                    )
                    return
                }
                queueStudent.queueStudentId = queueStudentId
                queueStudentIdRepo.save(queueStudent)
                messageProcessorService.pushSuccessful(
                    queueResponseQueue,
                    0,
                    object {},
                )
            }

            "quitQueue" -> {
                val userId = payload.get("userId").asLong()
                val maybeUser: Optional<User> = userRepo.findUserById(userId)
                if (maybeUser.isEmpty) {
                    messageProcessorService.pushSuccessful(
                        queueResponseQueue,
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
                        queueResponseQueue,
                        2,
                        object {
                            val userId = userId
                        },
                    )
                    return
                }

                val maybeQueue = queueRepo.findById(payload.get("queueId").asLong())
                if (maybeQueue.isEmpty) {
                    messageProcessorService.pushSuccessful(
                        queueResponseQueue,
                        3,
                        object {
                            val queueId = payload.get("queueId").asLong()
                        },
                    )
                    return
                }
                if (maybeQueue.get().group!!.id != maybeUser.get().group!!.id) {
                    messageProcessorService.pushSuccessful(
                        queueResponseQueue,
                        4,
                        object {
                            val userId = userId
                            val queueId = payload.get("queueId").asLong()
                            val groupId = maybeGroup.get().id
                        },
                    )
                    return
                }
                if (!maybeQueue.get().users.stream().anyMatch { user -> user.id == maybeUser.get().id }) {
                    messageProcessorService.pushSuccessful(
                        queueResponseQueue,
                        5,
                        object {
                            val userId = userId
                            val queueId = payload.get("queueId").asLong()
                        },
                    )
                    return
                }
                val queueStudentId = QueueStudentId()
                queueStudentId.user = maybeUser.get()
                queueStudentId.queue = maybeQueue.get()
                queueStudentIdRepo.delete(queueStudentIdRepo.findById(queueStudentId).get())
                messageProcessorService.pushSuccessful(
                    queueResponseQueue,
                    0,
                    object {
                    },
                )
            }

            "getQueue" -> {
                val userId = payload.get("userId").asLong()
                val maybeUser: Optional<User> = userRepo.findUserById(userId)
                if (maybeUser.isEmpty) {
                    messageProcessorService.pushSuccessful(
                        queueResponseQueue,
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
                        queueResponseQueue,
                        2,
                        object {
                            val userId = userId
                        },
                    )
                    return
                }

                val maybeQueue = queueRepo.findById(payload.get("queueId").asLong())
                if (maybeQueue.isEmpty) {
                    messageProcessorService.pushSuccessful(
                        queueResponseQueue,
                        3,
                        object {
                            val queueId = payload.get("queueId").asLong()
                        },
                    )
                    return
                }
                if (maybeQueue.get().group!!.id != maybeUser.get().group!!.id) {
                    messageProcessorService.pushSuccessful(
                        queueResponseQueue,
                        4,
                        object {
                            val userId = userId
                            val queueId = payload.get("queueId").asLong()
                            val groupId = maybeGroup.get().id
                        },
                    )
                    return
                }
                val studentsList = queueStudentIdRepo.findAllByQueue(maybeQueue.get()).map { standing ->
                    object {
                        val fullName = standing.user?.fullName
                        val id = standing.user?.id
                        val gotInQueue = standing.createdAt
                    }
                }
                messageProcessorService.pushSuccessful(
                    queueResponseQueue,
                    0,
                    object {
                        val queueId = maybeQueue.get().id
                        val queueName = maybeQueue.get().name
                        val users = studentsList
                    },
                )
            }

            "getAllQueues" -> {
                val userId = payload.get("userId").asLong()
                val maybeUser: Optional<User> = userRepo.findUserById(userId)
                if (maybeUser.isEmpty) {
                    messageProcessorService.pushSuccessful(
                        queueResponseQueue,
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
                        queueResponseQueue,
                        2,
                        object {
                            val userId = userId
                        },
                    )
                    return
                }
                    messageProcessorService.pushSuccessful(
                        queueResponseQueue,
                        0,
                        object {
                            val queueList = maybeGroup.get().queues.map { queue ->

                                object {
                                    val id = queue.id
                                    val queueName = queue.name
                                    val recordsNumber = queueStudentIdRepo.countAllByQueue(queue)
                                }

                            }
                        },
                    )
                    return
            }

            else -> {
                messageProcessorService.pushError(
                    queueResponseQueue,
                    "[Queue] Wrong command $command on ${message.channel.decodeToString()} channel! Try again!",
                    -1,
                )
            }
        }
    }
}
