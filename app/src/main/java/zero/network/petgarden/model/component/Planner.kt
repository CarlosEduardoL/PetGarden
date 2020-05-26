package zero.network.petgarden.model.component

import java.io.Serializable

data class Task(var petID: String = "", var duration: Duration = Duration()): Serializable{

    fun getTotalCost(): Double{
        val durationTotal = duration.end - duration.start
        val durationInHours = (durationTotal.toDouble()/1000)/3600

        return durationInHours*duration.cost.toDouble()
    }

    fun isFinalized(): Boolean{
        return System.currentTimeMillis()>= duration.end

    }
}

data class Planner(
    var availabilities: MutableList<Duration> = mutableListOf(),
    var tasks: MutableList<Task> = mutableListOf()
) : Serializable {


    fun addTask(task: Task): Boolean {
        availabilities.sorted().firstOrNull { it.contains(task.duration) }?.let {
            availabilities.remove(it)
            availabilities.addAll(it.extractSlice(task.duration))
            tasks.add(task)
            return@addTask true
        }
        return false
    }

    fun addAvailability(duration: Duration, override: Boolean): Boolean {
        val crashes = availabilities.filter { it.collide(duration) }
        return when {
            crashes.isEmpty() -> availabilities.add(duration)
            override -> availabilities.removeAll(crashes).let {
                availabilities.add(duration)
            }
            else -> false
        }
    }

    fun getTaskByID(id: String): Task?{

        return tasks.firstOrNull { it.petID == id }
    }
}