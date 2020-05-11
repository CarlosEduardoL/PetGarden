package zero.network.petgarden.ui.user.owner

import android.app.Activity
import android.content.Intent
import android.os.Build
import android.os.Bundle
import androidx.annotation.RequiresApi
import androidx.appcompat.app.AppCompatActivity
import kotlinx.android.synthetic.main.activity_filter.*
import zero.network.petgarden.databinding.ActivityFilterBinding
import zero.network.petgarden.model.entity.Duration
import zero.network.petgarden.model.entity.Sitter
import java.io.Serializable
import java.util.*
import kotlin.math.roundToInt


class FilterActivity : AppCompatActivity() {

    private lateinit var sitters:List<Sitter>
    lateinit var binding:ActivityFilterBinding

    @RequiresApi(Build.VERSION_CODES.M)
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding =  ActivityFilterBinding.inflate(layoutInflater)
        setContentView(binding.root)

        val extras = intent.extras
        sitters = extras!!.getSerializable("sitters") as List<Sitter>

        var min = 0
        var max = 20000
        var fromHour = 0
        var fromMins = 0
        var toHour = 23
        var toMins = 59

        if (fromTP ==null) println("-----------------------------------BEFRETimePickernull----------------------")
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

        var sittersFiltered:List<Sitter>
        if (numStars>0){
           sittersFiltered = sitters.filter { it.availability!=null }
                    .filter { it.availability!!.cost in min..max }
                    .filter { it.availability!!.contains(Duration(fromDate.timeInMillis, toDate.timeInMillis, 0))}
                    .filter{  it.rating.roundToInt()==numStars }
        }else {
            sittersFiltered =
                sitters.filter { it.availability!=null }
                    .filter { it.availability!!.cost in min..max }
                    .filter { it.availability!!.contains(Duration(fromDate.timeInMillis, toDate.timeInMillis, 0))}
                    .filter{  it.rating.roundToInt()>0 }

        }

        val intent = Intent()
        intent.putExtra("sittersFiltered", sittersFiltered as Serializable)
        setResult(RESULT_OK, intent)
        finish()
    }


    @RequiresApi(Build.VERSION_CODES.M)
    private fun initTimePicker(fromHour: Int, fromMins: Int, toHour: Int, toMins: Int){
        fromTP.apply {
            if (fromTP ==null) println("-----------------------------------TimePickernull----------------------")
            setIs24HourView(true)
            hour = fromHour
            minute = fromMins }

        toTP.apply {
            setIs24HourView(true)
            hour = toHour
            minute = toMins
        }

    }
}
