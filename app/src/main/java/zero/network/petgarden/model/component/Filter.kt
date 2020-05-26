package zero.network.petgarden.model.component

import zero.network.petgarden.model.entity.SitterIMP
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

    fun filterSitters(sitters: List<SitterIMP>): List<SitterIMP> {

        val fromDate = Calendar.getInstance().apply {
            set(Calendar.HOUR_OF_DAY, fromHour)
            set(Calendar.MINUTE, fromMins)
        }

        val toDate = Calendar.getInstance().apply {
            set(Calendar.HOUR_OF_DAY, toHour)
            set(Calendar.MINUTE, toMins)
        }

        val sittersFiltered: List<SitterIMP>

        sittersFiltered = sitters
            .asSequence()
            .filter { it.rating.toFloat() >= numStars }
            .filter { it.availability?.cost?:min in min..max }
            .filter {
                it.availability?.contains(
                    Duration(fromDate.timeInMillis, toDate.timeInMillis)
                )?:true || (fromHour+fromMins==0 && toHour+toMins ==23+59)
            }
            .toList()
        return sittersFiltered
    }
}