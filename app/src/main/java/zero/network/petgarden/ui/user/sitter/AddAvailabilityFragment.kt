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
import zero.network.petgarden.model.behaivor.Sitter
import zero.network.petgarden.model.component.Duration
import zero.network.petgarden.util.show
import java.util.*

class AddAvailabilityFragment(var sitter: Sitter, var dateSelected:Calendar): Fragment() {

    private var price = 2500

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View = FragmentAddAvailabilityBinding.inflate(inflater, container, false).apply {
        startTimeTask.setIs24HourView(true)
        endTimeTask.setIs24HourView(true)

        incPrice.setOnClickListener{incrementPrice()}
        decPrice.setOnClickListener{decrementPrice()}

        addFreeSched.setOnClickListener{
            addAvailability()
        }
    }.root

    @RequiresApi(Build.VERSION_CODES.M)
    private fun addAvailability(){
        val duration: Duration =
            Duration(
                hourToTimeInMilis(startTimeTask.hour, startTimeTask.minute),
                hourToTimeInMilis(endTimeTask.hour, endTimeTask.minute),
                priceTV.text.toString().toInt()
            )

        val isAvailable = sitter.planner.addAvailability(duration, override = false)
        if (isAvailable){
            sitter.saveInDB("Called By ${this::class.java.name} in line ${Throwable().stackTrace[0]
                .lineNumber}")
            show("Su disponibilidad ha sido fijada")
            fragmentManager!!.popBackStack()
        }else
            show("Usted ya ha fijado una disponibilidad em este horario")
    }




    private fun hourToTimeInMilis(hour:Int, min:Int):Long{
            dateSelected.apply {
                set(Calendar.HOUR_OF_DAY, hour)
                set(Calendar.MINUTE, min) }

        return dateSelected.timeInMillis
    }

    private fun incrementPrice(){
        price += STEP_PRICE
        priceTV.text = "$price"
    }

    private fun decrementPrice(){
        price -= STEP_PRICE
        priceTV.text = "$price"
    }

    companion object{
        const val  STEP_PRICE = 500
    }
}