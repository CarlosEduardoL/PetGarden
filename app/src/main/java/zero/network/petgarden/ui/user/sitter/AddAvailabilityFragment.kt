package zero.network.petgarden.ui.user.sitter

import android.os.Build
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.annotation.RequiresApi
import androidx.fragment.app.Fragment
import kotlinx.android.synthetic.main.fragment_add_availability.*
import zero.network.petgarden.databinding.FragmentAddAvailabilityBinding
import zero.network.petgarden.model.entity.Duration
import zero.network.petgarden.model.entity.Sitter
import zero.network.petgarden.util.show
import java.util.*

class AddAvailabilityFragment(var sitter:Sitter): Fragment() {
    @RequiresApi(Build.VERSION_CODES.M)
    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View = FragmentAddAvailabilityBinding.inflate(inflater, container, false).apply {

        var price = 2500
        incPrice.setOnClickListener{incrementPrice(price)}
        decPrice.setOnClickListener{decrementPrice(price)}

        addFreeSched.setOnClickListener{
            addAvailability()
        }
    }.root

    @RequiresApi(Build.VERSION_CODES.M)
    private fun addAvailability(){
        val duration:Duration = Duration(
            hourToTimeInMilis(startTimeTask.hour, startTimeTask.minute),
            hourToTimeInMilis(endTimeTask.hour, endTimeTask.minute),
            priceTV.text.toString().toInt())

        val isAvailable = sitter.addAvailability(duration)

        if (isAvailable){
            show("Su disponibilidad ha sido fijada")
            fragmentManager!!.popBackStack()

        }else
            show("Usted ya ha fijado una disponibilidad em este horario")
    }

    private fun setFormatTime(){
        startTimeTask.setIs24HourView(true)
        endTimeTask.setIs24HourView(true)
    }


    private fun hourToTimeInMilis(hour:Int, min:Int):Long{
        val date = Calendar.getInstance().apply {
            set(Calendar.HOUR_OF_DAY, hour)
            set(Calendar.MINUTE, min)
        }

        return date.timeInMillis
    }

    private fun incrementPrice(price: Int){
       var updatedPrice = price + STEP_PRICE
        priceTV.text = "$$updatedPrice"
    }

    private fun decrementPrice(price: Int){
        var updatedPrice = price - STEP_PRICE
        priceTV.text = "$$updatedPrice"
    }

    companion object{
        const val  STEP_PRICE = 500
    }
}