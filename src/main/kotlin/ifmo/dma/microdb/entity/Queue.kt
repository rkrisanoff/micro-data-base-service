package ifmo.dma.microdb.entity

import java.sql.Timestamp

import jakarta.persistence.*
import org.hibernate.annotations.CreationTimestamp

@Entity
@Table(name = "queue")
class Queue {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id", nullable = false)
    var id: Int? = null
    @Column(name = "name", nullable = false)
    var name: String? = null
    @Column(name="created_at")
    @CreationTimestamp
    var createdAt: Timestamp? = null
    @ManyToOne(fetch = FetchType.LAZY, cascade = [CascadeType.PERSIST, CascadeType.MERGE])
    @JoinColumn(name = "group_id",referencedColumnName = "id", nullable = false)
    var group: Group? = null
}

