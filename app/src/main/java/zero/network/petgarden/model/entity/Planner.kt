package zero.network.petgarden.model.entity

class Duration(
    val start: Long,
    val end: Long,
    val cost: Int
){
    fun extractSlice(duration: Duration): List<Duration>{
        return if (duration.end == end && duration.start == start){
            emptyList()
        }else if (duration.end == end){
            listOf(Duration(start, duration.start, cost))
        }else if (duration.start == start){
            listOf(Duration(duration.end,end, cost))
        }else{
            listOf(
                Duration(start, duration.start, cost),
                Duration(duration.end, end, cost)
            )
        }
    }

    fun contains(d: Duration) = d.start > start && d.end < end

    fun collide(d: Duration): Boolean {
        return d.start <= start && d.end >= start
                || d.start >= start && d.end <= end
                || d.start <= end && d.end >= end
                || d.start <= start && d.end >= end
    }

}

data class Task(val petID: String, val duration: Duration)

class Planner {
    private val _availability = mutableListOf<Duration>()
    val availability: List<Duration>
        get() = _availability

    private val _tasks = mutableListOf<Task>()
    val tasks: List<Task>
        get() = _tasks

    fun addTask(task: Task): Boolean {
        availability.firstOrNull { it.contains(task.duration) }?.let {
            _availability.remove(it)
            _availability.addAll(it.extractSlice(task.duration))
            _tasks.add(task)
            return@addTask true
        }
        return false
    }

    fun addAvailability(duration: Duration, override: Boolean = false): Boolean {
        val crashes = availability.filter { it.collide(duration) }
        return when {
            crashes.isEmpty() ->  _availability.add(duration)
            override ->  _availability.removeAll(crashes).let {
                _availability.add(duration)
            }
            else -> false
        }
    }
}