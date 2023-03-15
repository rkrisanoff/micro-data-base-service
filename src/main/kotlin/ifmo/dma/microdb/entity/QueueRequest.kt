package ifmo.dma.microdb.entity

import jakarta.persistence.*
import java.io.Serializable
import javax.persistence.Id

@Embeddable
class QueueRequestId(
    @ManyToOne(targetEntity = User::class, cascade = [CascadeType.ALL])
    @JoinColumn(name = "user_id", referencedColumnName = "id",nullable = false,)
    var userId: Long? = null,
    @ManyToOne(targetEntity = User::class, cascade = [CascadeType.ALL])
    @JoinColumn(name = "queue_id",referencedColumnName = "id", nullable = false,)
    var queueId: Long? = null
) : Serializable

@Entity
@Table(name = "queue_request")
class QueueRequest {
    @EmbeddedId
    var queueRequestId: QueueRequestId? = null
}