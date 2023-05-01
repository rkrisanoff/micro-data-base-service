package ifmo.dma.microdb.repo

import ifmo.dma.microdb.entity.Group
import ifmo.dma.microdb.entity.User
import org.springframework.data.repository.CrudRepository
import org.springframework.stereotype.Repository
import java.util.*

@Repository
interface GroupRepo : CrudRepository<Group, Long> {
    fun findGroupByInviteCode(inviteCode: String): Optional<Group>
    fun findGroupByAdmin(user: User): Optional<Group>
}
