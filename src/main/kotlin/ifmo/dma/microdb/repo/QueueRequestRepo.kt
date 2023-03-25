package ifmo.dma.microdb.repo

import ifmo.dma.microdb.entity.QueueRequest
import ifmo.dma.microdb.entity.QueueRequestId
import org.springframework.data.repository.CrudRepository

interface QueueRequestRepo : CrudRepository<QueueRequest, QueueRequestId>