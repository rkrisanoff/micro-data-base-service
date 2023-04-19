package ifmo.dma.microdb.repo

import ifmo.dma.microdb.entity.User
import org.springframework.data.jpa.repository.JpaSpecificationExecutor
import org.springframework.data.repository.CrudRepository
import org.springframework.stereotype.Repository
import java.util.*


@Repository
interface UserRepo:CrudRepository<User,Long>, JpaSpecificationExecutor<User> {
    fun findUserByLogin(login:String) : Optional<User>
    fun existsUserByLogin(login:String):Boolean
}