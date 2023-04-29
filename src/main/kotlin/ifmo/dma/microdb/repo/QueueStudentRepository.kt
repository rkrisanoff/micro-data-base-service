package ifmo.dma.microdb.repo;

import ifmo.dma.microdb.entity.Queue
import ifmo.dma.microdb.entity.QueueStudent
import ifmo.dma.microdb.entity.QueueStudentId
import org.springframework.data.repository.CrudRepository

interface QueueStudentRepository : CrudRepository<QueueStudent, QueueStudentId> {
    fun findAllByQueue(queue: Queue): List<QueueStudent>
}