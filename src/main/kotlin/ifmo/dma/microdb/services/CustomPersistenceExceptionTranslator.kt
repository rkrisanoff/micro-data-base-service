package ifmo.dma.microdb.services

import org.slf4j.LoggerFactory
import org.springframework.dao.DataAccessException
import org.springframework.dao.support.PersistenceExceptionTranslator
import org.springframework.stereotype.Component
import java.sql.SQLException

@Component
class CustomPersistenceExceptionTranslator : PersistenceExceptionTranslator {

    private val logger = LoggerFactory.getLogger(CustomPersistenceExceptionTranslator::class.java)

    override fun translateExceptionIfPossible(ex: java.lang.RuntimeException): DataAccessException? {
        if (ex is DataAccessException) {
            val sqlException = getSqlException(ex)
            if (sqlException != null) {
                logger.error("Error executing SQL statement: ${sqlException.message}")
                return CustomDataAccessException(sqlException.message, sqlException)
            }
        }
        return null
    }

    private fun getSqlException(ex: Throwable?): SQLException? {
        if (ex is SQLException) {
            return ex
        } else if (ex != null) {
            return getSqlException(ex.cause)
        }
        return null
    }
}

class CustomDataAccessException(msg: String?, ex: Throwable?) : DataAccessException(msg, ex)
