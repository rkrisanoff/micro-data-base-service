package ifmo.dma.microdb.entity

import java.io.Serializable
import jakarta.persistence.*


@Embeddable
class UserGroupId(
    @ManyToOne(targetEntity = User::class, cascade = [CascadeType.ALL])
    @JoinColumn(name = "user_id",referencedColumnName = "id",nullable = false)
    var userId: Long? = null,
    @ManyToOne(targetEntity = Group::class, cascade = [CascadeType.ALL])
    @JoinColumn(name = "group_id",referencedColumnName = "id", nullable = false,)
    var groupId: Long? = null
) : Serializable

@Entity
@Table(name = "user_group")
class UserGroup {
    @EmbeddedId
    var userGroupId: UserGroupId? = null
}
