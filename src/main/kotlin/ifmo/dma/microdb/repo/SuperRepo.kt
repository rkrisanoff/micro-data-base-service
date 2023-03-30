package ifmo.dma.microdb.repo

class SuperRepo(
    private val userRepo: UserRepo,
    private val groupRepo: GroupRepo,
    private val queueRepo: QueueRepo,
    private val queueRequestRepo: QueueRequestRepo,
    private val userGroupRepo: UserGroupRepo
) {
    fun getUserRepo(): UserRepo {
        return userRepo; }

    fun getGroupRepo(): GroupRepo {
        return groupRepo; }

    fun getQueueRepo(): QueueRepo {
        return queueRepo; }

    fun getQueueRequestRepo(): QueueRequestRepo {
        return queueRequestRepo; }

    fun getUserGroupRepo(): UserGroupRepo {
        return userGroupRepo; }
}