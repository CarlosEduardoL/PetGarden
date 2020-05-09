package zero.network.pet

import androidx.annotation.RequiresApi
import zero.network.petgarden.model.entity.Duration
import zero.network.petgarden.ui.user.owner.SittersAdapter
import kotlin.math.roundToInt

import android.os.Build
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import kotlinx.android.synthetic.main.fragment_filter.*
import zero.network.petgarden.R
import zero.network.petgarden.model.entity.Sitter
import java.util.*

class filterFragment(var adapter: SittersAdapter, var sitters:List<Sitter>) : Fragment() {

    @RequiresApi(Build.VERSION_CODES.M)
    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View = inflater.inflate(R.layout.fragment_filter, container, false).apply {

        var min = 0
        var max = 20000
        var fromHour = 0
        var fromMins = 0
        var toHour = 23
        var toMins = 59

        initTimePicker(fromHour, fromMins, toHour, toMins)

        next_button.setOnClickListener {
            fromPriceET.text.toString().let { minTxt ->
                if (minTxt.isNotEmpty()) {
                    min = minTxt.toInt()
                }
            }

            toPriceET.text.toString().let { maxTxt ->
                if (maxTxt.isNotEmpty()) {
                    max = maxTxt.toInt()
                }
            }

            fromTP.apply {
                fromHour = hour
                fromMins = minute
            }

            toTP.apply {
                toHour = hour
                toMins = minute
            }

            var stars = ratingBar.rating.roundToInt()

            filterSitters(min, max, fromHour, fromMins, toHour, toMins, stars)
        }
    }


    private fun filterSitters(min:Int, max:Int, fromHour:Int,
                              fromMins:Int, toHour:Int, toMins:Int, numStars:Int){

        val fromDate = Calendar.getInstance().apply {
            set(Calendar.HOUR_OF_DAY, fromHour)
            set(Calendar.MINUTE, fromMins)
        }

        val toDate = Calendar.getInstance().apply {
            set(Calendar.HOUR_OF_DAY, toHour)
            set(Calendar.MINUTE, toMins)
        }

        if (numStars>0){
           adapter.sitters =
               sitters.filter { it.availability!=null }
                      .filter { it.availability!!.cost in min..max }
                      .filter { it.availability!!.contains(Duration(fromDate.timeInMillis, toDate.timeInMillis, 0))}
                      .filter{  it.rating.roundToInt()==numStars }
        }else {
            adapter.sitters =
                sitters.filter { it.availability!=null }
                    .filter { it.availability!!.cost in min..max }
                    .filter { it.availability!!.contains(Duration(fromDate.timeInMillis, toDate.timeInMillis, 0))}
                    .filter{  it.rating.roundToInt()>0 }

        }
        adapter.notifyDataSetChanged()
        activity!!.supportFragmentManager.popBackStack()
    }


    private fun initTimePicker(fromHour: Int, fromMins: Int, toHour: Int, toMins: Int){
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            fromTP.apply { hour = fromHour
                minute = fromMins }

            toTP.apply { hour = toHour
                minute = toMins
            }
        }
    }
}