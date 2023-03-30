package ifmo.dma.microdb.repo

import org.springframework.beans.factory.annotation.Autowired
import org.springframework.cache.annotation.EnableCaching
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration

@Configuration
@EnableCaching

class RepoConfig {

    @Bean
    fun superRepo(
        @Autowired userRepo: UserRepo,
        @Autowired groupRepo: GroupRepo,
        @Autowired queueRepo: QueueRepo,
        @Autowired userGroupRepo: UserGroupRepo,
        @Autowired queueRequestRepo: QueueRequestRepo
    ) : SuperRepo
    {
        return SuperRepo(
            userRepo=userRepo,
            groupRepo = groupRepo,
            queueRepo = queueRepo,
            userGroupRepo = userGroupRepo,
            queueRequestRepo =  queueRequestRepo
        )
    }
}