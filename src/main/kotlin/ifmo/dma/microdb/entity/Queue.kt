package ifmo.dma.microdb.entity

import java.sql.Timestamp

import jakarta.persistence.*

@Entity
@Table(name = "group")
class Queue {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id", nullable = false)
    var id: Long? = null
    var name: String? = null
    @Column(name="created_at")
    var createdAt: Timestamp? = null
    @ManyToOne(targetEntity = Group::class, cascade = [CascadeType.ALL])
    @JoinColumn(name = "group_id",referencedColumnName = "id", nullable = false,)
    var groupId: Long? = null
}

