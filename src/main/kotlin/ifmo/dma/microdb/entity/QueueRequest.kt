package ifmo.dma.microdb.entity

import jakarta.persistence.*
import java.io.Serializable
import java.sql.Timestamp

@Embeddable
class QueueRequestId(
    @ManyToOne(targetEntity = User::class, cascade = [CascadeType.ALL])
    @JoinColumn(name = "user_id", referencedColumnName = "id",nullable = false,)
    var userId: Long? = null,
    @ManyToOne(targetEntity = User::class, cascade = [CascadeType.ALL])
    @JoinColumn(name = "queue_id",referencedColumnName = "id", nullable = false,)
    var queueId: Long? = null
) : Serializable {
    override fun hashCode(): Int {
        return super.hashCode()
    }

    override fun toString(): String {
        return super.toString()
    }

    override fun equals(other: Any?): Boolean {
        return super.equals(other)
    }
}

@Entity
@Table(name = "queue_request")
class QueueRequest {
    @EmbeddedId
    var queueRequestId: QueueRequestId? = null
    @Column(name="created_at")
    var createdAt: Timestamp? = null
}