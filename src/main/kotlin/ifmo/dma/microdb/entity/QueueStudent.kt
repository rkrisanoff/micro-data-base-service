package ifmo.dma.microdb.entity

import jakarta.persistence.*
import org.hibernate.annotations.CreationTimestamp
import java.io.Serializable
import java.sql.Timestamp

@Embeddable
class QueueStudentId(
    @ManyToOne(fetch = FetchType.LAZY, cascade = [CascadeType.PERSIST, CascadeType.MERGE])
    @JoinColumn(name = "user_id", referencedColumnName = "id",nullable = false)
    var user: User? = null,
    @ManyToOne(fetch = FetchType.LAZY, cascade = [CascadeType.PERSIST, CascadeType.MERGE])
    @JoinColumn(name = "queue_id",referencedColumnName = "id", nullable = false)
    var queue: Queue? = null
) : Serializable

@Entity
@Table(name = "queue_student")
class QueueStudent {
    @EmbeddedId
    var queueStudentId: QueueStudentId? = null
    @ManyToOne(fetch = FetchType.LAZY)
    @MapsId("user_id")
    val user: User? = null
    @ManyToOne(fetch = FetchType.LAZY)
    @MapsId("queue_id")
    val queue: Queue? = null
    @Column(name="created_at")
    @CreationTimestamp
    var createdAt: Timestamp? = null
}