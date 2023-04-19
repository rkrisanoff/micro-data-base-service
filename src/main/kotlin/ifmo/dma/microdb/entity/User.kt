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
    var username: String? = null

    @ManyToOne(fetch = FetchType.LAZY, cascade = [CascadeType.PERSIST, CascadeType.MERGE])
    @JoinColumn(name = "group_id")
    var group: Group? = null
    @OneToOne(mappedBy = "admin", fetch = FetchType.LAZY, cascade = [CascadeType.ALL])
    var myGroup: Group? = null

   // fun createGroup(Gro)
}