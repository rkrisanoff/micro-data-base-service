package ifmo.dma.microdb.entity
/* ktlint-disable no-wildcard-imports */

import jakarta.persistence.*
import org.hibernate.annotations.CreationTimestamp
import java.sql.Timestamp

@Entity
@Table(name = "queue")
class Queue {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id", nullable = false)
    var id: Long? = null

    @Column(name = "name", nullable = false)
    var name: String? = null

    @Column(name = "created_at")
    @CreationTimestamp
    var createdAt: Timestamp? = null

    @ManyToOne(fetch = FetchType.LAZY, cascade = [CascadeType.PERSIST, CascadeType.MERGE])
    @JoinColumn(name = "group_id", referencedColumnName = "id", nullable = false)
    var group: Group? = null

    @ManyToMany(fetch = FetchType.LAZY, cascade = [CascadeType.PERSIST, CascadeType.MERGE])
    @JoinTable(
        name = "queue_student",
        joinColumns = [JoinColumn(name = "queue_id")],
        inverseJoinColumns = [JoinColumn(name = "user_id")],
    )
    val users: MutableList<User> = mutableListOf()
}
