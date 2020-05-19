package zero.network.petgarden.ui.user.owner.recruitment

import android.annotation.SuppressLint
import android.os.Build
import android.os.Bundle
import android.view.LayoutInflater
import android.view.ViewGroup
import android.widget.TimePicker
import androidx.annotation.RequiresApi
import androidx.fragment.app.Fragment
import com.google.gson.Gson
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import zero.network.petgarden.databinding.FragmentRecruitmentBinding
import zero.network.petgarden.model.component.Duration
import zero.network.petgarden.model.component.Task
import zero.network.petgarden.model.notifications.FCMMessage
import zero.network.petgarden.model.notifications.Message
import zero.network.petgarden.model.notifications.OnResponseContractingListener
import zero.network.petgarden.services.FCMService
import zero.network.petgarden.util.*
import java.util.*

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


        FCMService.listener =object : OnResponseContractingListener {
            override fun responseContracting(response: String) {
                println("-------------Decision del sitter recibida------------------")
                if (response == NotificationUtils.ACCEPT){
                    owner.sitterList.add(sitter.id)
                    owner.saveInDB()
                    CoroutineScope(Dispatchers.Main).launch {show("El cuidador seleccionado ha sido contratado")}
                }else
                    CoroutineScope(Dispatchers.Main).launch {show("El cuidador no aceptó la oferta de contratación")}
            }
        }

        CoroutineScope(Dispatchers.Main).launch { photoSitter.setImageBitmap(sitter.image()) }
        nameSitterTxt.text = sitter.name
        emailSitterTxt.text = sitter.email

        //Availability
        if (sitter.availability != null) {
            val fromHour = getHour(sitter.availability!!.start)
            val fromMin = getMinute(sitter.availability!!.start)
            val toHour = getHour(sitter.availability!!.end)
            val toMin = getMinute(sitter.availability!!.end)

            scheduleSitter.text = "$fromHour:$fromMin a $toHour:$toMin"

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
                requestContracting(duration)
            }else
                show("El cuidador no tiene disponibilidad en este horario")
        }
    }

    private fun requestContracting(duration: Duration){
        var fcm = FCMMessage()
        val schedule = "Horario:  ${duration.start.getDate("hh:mm:ss")} a ${duration.end.getDate("hh:mm:ss")}"

        fcm.to = "/topics/${sitter.id}"
        fcm.data = Message(owner.id, owner.name, schedule, "$${duration.cost}/h")

        val gson  = Gson()
        val json =  gson.toJson(fcm)

        Thread(Runnable {
            POSTtoFCM(FCMMessage.API_KEY, json)
        }).start()
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
}
