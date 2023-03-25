package ifmo.dma.microdb.repo

import ifmo.dma.microdb.entity.UserGroup
import ifmo.dma.microdb.entity.UserGroupId
import org.springframework.data.repository.CrudRepository

interface UserGroupRepo : CrudRepository<UserGroup, UserGroupId> 