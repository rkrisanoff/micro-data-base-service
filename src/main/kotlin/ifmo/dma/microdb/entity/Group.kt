package ifmo.dma.microdb.entity

import jakarta.persistence.*


@Entity
@Table(name = "group")
class Group {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id", nullable = false)
    var id: Long? = null
    var name: String? = null
    @Column(name="admin_id")
    var adminId: Long? = null
    @Column(name="invite_code")
    var inviteCode: String? = null


}