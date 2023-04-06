package ifmo.dma.microdb.listeners

import org.springframework.data.redis.connection.Message
import org.springframework.data.redis.connection.MessageListener
import org.springframework.stereotype.Component

@Component
class MessageListerExample2 : MessageListener {
    override fun onMessage(message: Message, bytes: ByteArray?) {
        println("Examples2:")
        println("Message from channel `${String(message.channel)}` received: ${String(message.body)}")
    }
}
