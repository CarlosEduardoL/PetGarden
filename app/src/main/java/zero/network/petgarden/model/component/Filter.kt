package zero.network.petgarden.model.component

import zero.network.petgarden.model.entity.Sitter
import java.io.Serializable
import java.util.*

class Filter(
    var min: Int = 0,
    var max: Int = 20000,
    var fromHour: Int = 0,
    var fromMins: Int = 0,
    var toHour: Int = 23,
    var toMins: Int = 59,
    var numStars: Float = 0f
): Serializable {

    fun filterSitters(sitters: List<Sitter>): List<Sitter> {

        val fromDate = Calendar.getInstance().apply {
            set(Calendar.HOUR_OF_DAY, fromHour)
            set(Calendar.MINUTE, fromMins)
        }

        val toDate = Calendar.getInstance().apply {
            set(Calendar.HOUR_OF_DAY, toHour)
            set(Calendar.MINUTE, toMins)
        }

        val sittersFiltered: List<Sitter>

        sittersFiltered =
            (sitters.filter { it.availability != null }
                .filter { it.availability!!.cost in min..max }
                .filter {
                    it.availability!!.contains(
                        Duration(fromDate.timeInMillis, toDate.timeInMillis)
                    )
                } + sitters.filter { it.availability == null }).filter { it.rating.toFloat() >= numStars }
        return sittersFiltered.toSet().toList()
    }
}