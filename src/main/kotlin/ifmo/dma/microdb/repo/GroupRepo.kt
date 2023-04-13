package ifmo.dma.microdb.repo

import ifmo.dma.microdb.entity.Group
import org.springframework.data.repository.CrudRepository
import org.springframework.stereotype.Repository

@Repository

interface GroupRepo:CrudRepository<Group,Long> {

}