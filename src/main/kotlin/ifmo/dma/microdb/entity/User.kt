package ifmo.dma.microdb.entity
/* ktlint-disable no-wildcard-imports */

import jakarta.persistence.*
import jakarta.persistence.CascadeType.MERGE
import jakarta.persistence.CascadeType.PERSIST
import jakarta.persistence.FetchType.LAZY
import jakarta.persistence.GenerationType.IDENTITY

@Entity
@Table(name = "user")
class User {
    @Id
    @GeneratedValue(strategy = IDENTITY)
    @Column(name = "id", nullable = false)
    var id: Int? = null

    @Column(unique = true, nullable = false)
    var login: String? = null

    @Column(nullable = false)
    var password: String? = null

    @Column(nullable = false)
    var fullName: String? = null

    @ManyToOne(fetch = LAZY, cascade = [PERSIST, MERGE])
    @JoinColumn(name = "group_id", referencedColumnName = "id")
    var group: Group? = null

    @ManyToMany(fetch = LAZY, cascade = [PERSIST, MERGE])
    @JoinTable(
        name = "queue_student",
        joinColumns = [JoinColumn(name = "user_id")],
        inverseJoinColumns = [JoinColumn(name = "queue_id")],
    )
    var queues: MutableList<Queue> = mutableListOf()
}
