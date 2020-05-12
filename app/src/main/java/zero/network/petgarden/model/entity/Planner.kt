package zero.network.petgarden.model.entity

import zero.network.petgarden.model.behaivor.IPlanner
import java.io.Serializable

data class Task(val petID: String, val duration: Duration)

class Planner(
    private val _availability: MutableList<Duration> = mutableListOf(),
    private val _tasks: MutableList<Task> = mutableListOf()
) : IPlanner, Serializable {

    override var availabilities: List<Duration> = _availability
        get() = _availability
        set(value) {
            println(value)
            field = value
        }


    override var tasks: List<Task> = _tasks
        get() = _tasks

    override fun addTask(task: Task): Boolean {
        availabilities.sorted().firstOrNull { it.contains(task.duration) }?.let {
            _availability.remove(it)
            _availability.addAll(it.extractSlice(task.duration))
            _tasks.add(task)
            return@addTask true
        }
        return false
    }

    override fun addAvailability(duration: Duration, override: Boolean): Boolean {
        val crashes = availabilities.filter { it.collide(duration) }
        return when {
            crashes.isEmpty() -> _availability.add(duration)
            override -> _availability.removeAll(crashes).let {
                _availability.add(duration)
            }
            else -> false
        }
    }
}