package ifmo.dma.microdb.repo

import ifmo.dma.microdb.entity.User
import org.springframework.data.jpa.repository.JpaSpecificationExecutor
import org.springframework.data.jpa.repository.Modifying
import org.springframework.data.jpa.repository.Query
import org.springframework.data.repository.CrudRepository
import org.springframework.data.repository.query.Param
import org.springframework.stereotype.Repository
import java.util.*


@Repository
interface UserRepo:CrudRepository<User,Long>, JpaSpecificationExecutor<User> {
    fun findUserByLogin(login:String) : Optional<User>
    fun existsUserByLogin(login:String):Boolean

    @Modifying
    @Query("UPDATE User u SET u.username = :username WHERE u.id = :id")
    fun updateUserNameById(@Param("username") username: String, @Param("id") id: Long): Int
//    fun updateNameById(name: String?, id: Long?): Int

}