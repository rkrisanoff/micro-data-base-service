package ifmo.dma.microdb.entity

import jakarta.persistence.*

@Entity
@Table(name = "user")
class User {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id", nullable = false)
    var id: Long? = null
    var login: String? = null
    var password: String? = null
    var username: String? = null
}