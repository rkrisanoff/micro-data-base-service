package ifmo.dma.microdb.services

import org.springframework.data.redis.connection.MessageListener
import org.springframework.data.redis.connection.RedisConnectionFactory
import org.springframework.stereotype.Service
import java.time.Duration
import java.util.concurrent.CountDownLatch
import java.util.concurrent.TimeUnit
import java.util.concurrent.atomic.AtomicReference

@Service
class RedisMessageService(
    private val redisConnectionFactory: RedisConnectionFactory,
) {
    fun publish(channel: String, message: String) {
        val connection = redisConnectionFactory.connection
        connection.publish(channel.toByteArray(), message.toByteArray())
        connection.close()
    }

    fun push(queue: String, message: String) {
        val connection = redisConnectionFactory.connection
        connection.commands().lPush(
            queue.toByteArray(),
            message.toByteArray(),
        )
        connection.close()
    }

    fun pop(queue: String, timeout: Duration): String {
        val connection = redisConnectionFactory.connection

        val response = connection.commands().bLPop(
            timeout.toSeconds().toInt(),
            queue.toByteArray(),
        )!![1]!!.decodeToString()
        connection.close()
        return response
    }

    /**
     * @param toChannel - name of channel where the message will be sent
     * @param message - message that will be sent
     * @param fromChannel - name of channel where the response will receive
     * @param timeout - time that the method will wait until close connection and return null
     */
    fun publishAndWaitForResponse(
        toChannel: String,
        message: String,
        fromChannel: String,
        timeout: Duration,
    ): String? {
        val connection = redisConnectionFactory.connection
        val response = AtomicReference<String>()
        val latch = CountDownLatch(1)
        val messageListener = MessageListener { receivedMessage, _ ->
            response.set(String(receivedMessage.body))
            latch.countDown()
        }
        connection.subscribe(messageListener, fromChannel.toByteArray())
        publish(toChannel, message)
        try {
            if (latch.await(timeout.toMillis(), TimeUnit.MILLISECONDS)) {
                connection.close()
                return response.get()
            }
        } catch (e: InterruptedException) {
            Thread.currentThread().interrupt()
        } finally {
            connection.close()
        }
        return null
    }

    fun publishAndPop(
        toChannel: String,
        message: String,
        fromQueue: String,
        timeout: Duration,
    ): String? {
        val connection = redisConnectionFactory.connection
        connection.commands().publish(
            toChannel.toByteArray(),
            message.toByteArray(),
        )

        val response = connection.commands().bLPop(
            timeout.toSeconds().toInt(),
            fromQueue.toByteArray(),
        )?.get(1)?.decodeToString()
        connection.close()
        return response
    }
}
