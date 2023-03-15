package ifmo.dma.microdb.repo

import org.springframework.data.repository.CrudRepository
import ifmo.dma.microdb.entity.Group
import org.springframework.stereotype.Repository

@Repository

interface GroupRepo:CrudRepository<Group,Long> {

}