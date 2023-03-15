package ifmo.dma.microdb.repo;

import ifmo.dma.microdb.entity.Queue
import org.springframework.data.repository.CrudRepository

interface QueueRepository : CrudRepository<Queue, Long> {
}