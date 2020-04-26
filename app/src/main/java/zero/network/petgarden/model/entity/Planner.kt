package zero.network.petgarden.model.entity

import zero.network.petgarden.model.behaivor.IPlanner

data class Task(val petID: String, val duration: Duration)

class Planner: IPlanner {
    private val _availability = mutableListOf<Duration>()
    override val availabilities: List<Duration>
        get() = _availability

    private val _tasks = mutableListOf<Task>()
    override val tasks: List<Task>
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
            crashes.isEmpty() ->  _availability.add(duration)
            override ->  _availability.removeAll(crashes).let {
                _availability.add(duration)
            }
            else -> false
        }
    }
}