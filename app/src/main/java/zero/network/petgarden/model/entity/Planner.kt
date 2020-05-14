package zero.network.petgarden.model.entity

import zero.network.petgarden.model.behaivor.IPlanner
import java.io.Serializable
import java.util.*

data class Task(var petID: String, var duration: Duration): Serializable

class Planner(
    private var _availability: MutableList<Duration> = mutableListOf(),
    private var _tasks: MutableList<Task> = mutableListOf()
) : IPlanner, Serializable {

    override var availabilities: List<Duration>
        get() = _availability
        set(value) {
            _availability = value.filter { it.end >= Date().time }.toMutableList()
        }


    override var tasks: List<Task>
        get() = _tasks
        set(value) {
            _tasks = value.filter { it.duration.end >= Date().time }.toMutableList()
        }

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