package zero.network.petgarden.model.behaivor

import zero.network.petgarden.model.entity.Duration
import zero.network.petgarden.model.entity.Task

interface IPlanner {
    val availabilities: List<Duration>
    val tasks: List<Task>

    fun addTask(task: Task): Boolean
    fun addAvailability(duration: Duration, override: Boolean = false): Boolean
}