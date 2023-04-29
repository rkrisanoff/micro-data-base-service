package ifmo.dma.microdb.repo

import ifmo.dma.microdb.entity.Group
import ifmo.dma.microdb.entity.Queue
import org.springframework.data.repository.CrudRepository
import java.util.*

interface QueueRepo : CrudRepository<Queue, Long>{
    fun findAllByGroupId(groupId:Long):List<Queue>
    fun findQueueByGroup(group: Group):Optional<Queue>
}