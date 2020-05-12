package zero.network.petgarden.model.behaivor

import zero.network.petgarden.model.entity.Duration
import zero.network.petgarden.model.entity.Task

/**
 * @author CarlosEduardoL
 * Abstract representation of a planner
 */
interface IPlanner {
    /**
     * list of spaces of time in where the planner is free to take new tasks
     */
    var availabilities: List<Duration>

    /**
     * list of task to do
     */
    var tasks: List<Task>

    /**
     * add a new task if have the availability else return false
     */
    fun addTask(task: Task): Boolean

    /**
     * add a new availability
     */
    fun addAvailability(duration: Duration, override: Boolean = false): Boolean
}