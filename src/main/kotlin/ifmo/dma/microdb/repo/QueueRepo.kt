package ifmo.dma.microdb.repo

import ifmo.dma.microdb.entity.Queue
import org.springframework.data.repository.CrudRepository

interface QueueRepo : CrudRepository<Queue, Long>