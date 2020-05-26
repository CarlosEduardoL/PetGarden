package zero.network.petgarden.ui.user.owner.recruitment

import android.annotation.SuppressLint
import android.content.Context
import android.os.Build
import android.os.Bundle
import android.view.LayoutInflater
import android.view.ViewGroup
import android.widget.TimePicker
import androidx.annotation.RequiresApi
import androidx.core.content.edit
import androidx.fragment.app.Fragment
import com.google.gson.Gson
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import zero.network.petgarden.databinding.FragmentRecruitmentBinding
import zero.network.petgarden.model.component.Duration
import zero.network.petgarden.model.component.Task
import zero.network.petgarden.model.entity.Pet
import zero.network.petgarden.model.notifications.FCMMessage
import zero.network.petgarden.model.notifications.Message
import zero.network.petgarden.util.POSTtoFCM
import zero.network.petgarden.util.getDate
import zero.network.petgarden.util.show
import java.util.*
import kotlin.concurrent.thread

/**
 * A simple [Fragment] subclass.
 */
class RecruitmentFragment(view: RecruitmentView): RecruitmentView by view, Fragment(){

    private lateinit var binding: FragmentRecruitmentBinding

    @RequiresApi(Build.VERSION_CODES.M)
    @SuppressLint("SetTextI18n")
    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ) = FragmentRecruitmentBinding.inflate(inflater, container, false).apply {
        binding = this

        CoroutineScope(Dispatchers.Main).launch { photoSitter.setImageBitmap(sitter.image()) }
        nameSitterTxt.text = sitter.name
        emailSitterTxt.text = sitter.email

        //Availability
        if (sitter.availability != null) {
            val duration = sitter.availability!!
            scheduleSitter.text ="Horario:  ${duration.start.getDate("hh:mm:ss")} a ${duration.end.getDate("hh:mm:ss")}"

            //Cost
            val cost = sitter.availability!!.cost
            priceText.text = "$cost por hora"

        } else {
            scheduleSitter.text = "  No disponible"
            priceText.text = "No disponible"
        }

        //kindPets
        kindPet.text = sitter.kindPets

        //AddInfo
        addInfo.text = sitter.additional

        //24h format
        startTime.setIs24HourView(true)
        endTime.setIs24HourView(true)

        nextButton.setOnClickListener {

            //Date to Long
            val start = HourToTimeInMilis(startTime.hour, startTime.minute)
            val end = HourToTimeInMilis(endTime.hour, endTime.minute)

            if (checkValidSchedule(start, end)) {
                if (sitter.availability != null) {
                    CoroutineScope(Dispatchers.Main).launch {
                        contracting(startTime, endTime)
                    }
                } else show("El cuidador que desea contratar no está disponible")
            }else
                show("El periodo de tiempo seleccionado no es válido")
        }
    }.root


    @RequiresApi(Build.VERSION_CODES.M)
    private suspend fun contracting(startTime: TimePicker, endTime: TimePicker){

        val start = HourToTimeInMilis(startTime.hour, startTime.minute)
        val end = HourToTimeInMilis(endTime.hour, endTime.minute)
        val duration = Duration(
            start,
            end,
            sitter.availability!!.cost
        )

        selectPet()?.let {
            val task =
                Task(it.id, duration)
            val available = sitter.planner.addTask(task)

            if (available) {
                listenResponseContracting(it)
                requestContracting(duration)
            }else
                show("El cuidador no tiene disponibilidad en este horario")
        }
    }

    private fun requestContracting(duration: Duration){
        val fcm = FCMMessage()
        val schedule = "Horario:  ${duration.start.getDate("hh:mm:ss")} a ${duration.end.getDate("hh:mm:ss")}"

        fcm.to = "/topics/${sitter.id}"
        fcm.data = Message(sitter.id, owner.id, owner.name, schedule, "$${duration.cost}/h", Message.TYPE, "")
        val gson  = Gson()
        val json =  gson.toJson(fcm)

        thread{ POSTtoFCM(FCMMessage.API_KEY, json) }
    }

    private fun getHour(time:Long):Int{
        val date = Calendar.getInstance()
        date.timeInMillis = time
        return date.get(Calendar.HOUR_OF_DAY)
    }

    private fun getMinute(time:Long):Int{
        val date = Calendar.getInstance()
        date.timeInMillis = time
        return date.get(Calendar.MINUTE)
    }

    private fun HourToTimeInMilis(hour:Int, min:Int ):Long{
        val date = Calendar.getInstance().apply {
            set(Calendar.HOUR_OF_DAY, hour)
            set(Calendar.MINUTE, min)
        }
        return date.timeInMillis
    }

    private  fun checkValidSchedule(startTime: Long, endTime: Long): Boolean{
        return startTime < endTime
    }

    private fun listenResponseContracting(pet: Pet){
        val gson = Gson()
        val editor = context!!.getSharedPreferences(sitter.id, Context.MODE_PRIVATE).edit(true) {
            putString("owner",gson.toJson(owner))
            putString("sitter",gson.toJson(sitter))
            putString("pet",gson.toJson(pet))
        }
    }
}
