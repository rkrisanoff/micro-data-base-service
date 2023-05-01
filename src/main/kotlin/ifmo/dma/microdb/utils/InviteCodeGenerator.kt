package ifmo.dma.microdb.utils

import org.springframework.stereotype.Component
import kotlin.random.Random
@Component
class InviteCodeGenerator {
    fun generateRandomString(): String {
        val charPool: List<Char> = ('a'..'z') + ('A'..'Z') + ('0'..'9') // Создаем пул символов
        return (1..5) // Указываем, что нужно сгенерировать строку из 5 символов
            .map { Random.nextInt(0, charPool.size) } // Создаем список случайных индексов символов из пула
            .map(charPool::get) // Получаем символы по индексу из пула и создаем список символов
            .joinToString("")
    }
}
