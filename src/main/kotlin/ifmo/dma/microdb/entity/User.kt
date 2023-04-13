package ifmo.dma.microdb.entity

import jakarta.persistence.*

@Entity
@Table(name = "user")
class User {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id", nullable = false)
    var id: Long? = null

    @Column(unique = true, nullable = false)
    var login: String? = null
    @Column(nullable = false)
    var password: String? = null
    @Column(nullable = false)
    var username: String? = null
}