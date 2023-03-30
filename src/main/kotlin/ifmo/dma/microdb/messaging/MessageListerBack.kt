package ifmo.dma.microdb.messaging

import com.fasterxml.jackson.databind.JsonNode
import com.fasterxml.jackson.module.kotlin.jacksonObjectMapper
import com.fasterxml.jackson.module.kotlin.readValue
import ifmo.dma.microdb.entity.User
import ifmo.dma.microdb.repo.SuperRepo
import org.springframework.data.redis.connection.Message
import org.springframework.data.redis.connection.MessageListener


class MessageListerBack (
    private val messagePublisher : MessagePublisher,
    private val superRepo : SuperRepo,

) : MessageListener {
    private val jsonMapper = jacksonObjectMapper()

    override fun onMessage(message: Message, bytes: ByteArray?) {
        val channel = String(bytes!!)
        val body = String(message.body)
        println("Received message: $body on channel: $channel")
        val jsonRequest: JsonNode = jsonMapper.readTree(body)
        val userNode = jsonRequest.get("user")
        println(userNode.asText());
        val commandString : String = jsonRequest.get("command").asText();
        println(commandString);
        println("Received message: $commandString on channel: $channel")
        val user = jsonMapper.readValue<User>(userNode.traverse());
        messagePublisher.publish(body)
        superRepo.getUserRepo().save(user);
    }
}
