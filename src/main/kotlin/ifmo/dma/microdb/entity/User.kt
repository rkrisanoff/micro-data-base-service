package ifmo.dma.microdb.entity

import jakarta.persistence.*

@Entity
@Table(name = "user")
class User {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id", nullable = false)
    var id: Int? = null
    @Column(unique = true, nullable = false)
    var login: String? = null
    @Column(nullable = false)
    var password: String? = null
    @Column(nullable = false)
    var fullName: String? = null
    @ManyToOne(fetch = FetchType.LAZY, cascade = [CascadeType.PERSIST, CascadeType.MERGE])
    @JoinColumn(name = "group_id", referencedColumnName = "id")
    var group: Group? = null
    @ManyToMany(fetch = FetchType.LAZY, cascade = [CascadeType.PERSIST, CascadeType.MERGE])
    @JoinTable(
        name = "queue_student",
        joinColumns = [JoinColumn(name = "user_id")],
        inverseJoinColumns = [JoinColumn(name = "queue_id")]
    )
    var queues: MutableList<Queue> = mutableListOf()
}