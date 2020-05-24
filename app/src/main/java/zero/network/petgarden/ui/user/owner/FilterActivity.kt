package zero.network.petgarden.ui.user.owner

import android.content.Intent
import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import kotlinx.android.synthetic.main.activity_filter.*
import zero.network.petgarden.databinding.ActivityFilterBinding
import zero.network.petgarden.model.component.Filter
import zero.network.petgarden.model.entity.Sitter


class FilterActivity : AppCompatActivity() {

    private lateinit var sitters:List<Sitter>
    lateinit var binding:ActivityFilterBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding =  ActivityFilterBinding.inflate(layoutInflater)
        setContentView(binding.root)

        val filter = Filter()

        initTimePicker(filter.fromHour, filter.fromMins, filter.toHour, filter.toMins)

        next_button.setOnClickListener {
            fromPriceET.text.toString().let { minTxt ->
                if (minTxt.isNotEmpty()) {
                    filter.min = minTxt.toInt()
                }
            }

            toPriceET.text.toString().let { maxTxt ->
                if (maxTxt.isNotEmpty()) {
                    filter.max = maxTxt.toInt()
                }
            }

            fromTP.apply {
                filter.fromHour = hour
                filter.fromMins = minute
            }

            toTP.apply {
                filter.toHour = hour
                filter.toMins = minute
            }

            filter.numStars = ratingBar.rating

            val intent = Intent()
            intent.putExtra("filter", filter)
            setResult(RESULT_OK, intent)
            finish()
        }
    }

    private fun initTimePicker(fromHour: Int, fromMins: Int, toHour: Int, toMins: Int){
        fromTP.apply {
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
