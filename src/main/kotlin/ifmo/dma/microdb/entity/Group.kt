package ifmo.dma.microdb.entity

import jakarta.persistence.*


@Entity
@Table(name = "group")
class Group {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id", nullable = false)
    var id: Int? = null
    @Column(name = "name", nullable = false)
    var name: String? = null
    @OneToOne(fetch = FetchType.LAZY, cascade = [CascadeType.ALL])
    @JoinColumn(name="admin_id", referencedColumnName = "id", nullable = false, unique = true)
    var admin: User? = null
    @Column(name="invite_code", unique = true,nullable = false)
    var inviteCode: String? = null

    @OneToMany(mappedBy = "group",fetch = FetchType.LAZY, cascade = [CascadeType.PERSIST, CascadeType.MERGE])
    var members: MutableSet<User> = HashSet()

    fun addMember(user: User) {
        members.add(user)
        user.group = this
    }

}