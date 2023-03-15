package ifmo.dma.microdb.repo

import ifmo.dma.microdb.entity.User
import org.springframework.data.jpa.repository.JpaRepository
import org.springframework.data.repository.CrudRepository
import org.springframework.stereotype.Repository

@Repository
interface UserRepo:CrudRepository<User,Long> {
}