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
import zero.network.petgarden.model.entity.Duration
import zero.network.petgarden.model.entity.Task
import zero.network.petgarden.model.notifications.FCMMessage
import zero.network.petgarden.model.notifications.Message
import zero.network.petgarden.util.POSTtoFCM
import zero.network.petgarden.util.show
import zero.network.petgarden.util.suscribeToTopic
import java.util.*

/**
 * A simple [Fragment] subclass.
 */
class RecruitmentFragment(view: RecruitmentView): RecruitmentView by view , Fragment(){

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
        val duration = Duration(start, end, sitter.availability!!.cost)

        selectPet()?.let {
            val task = Task(it.id, duration)
            val successful = sitter.planner.addTask(task)

            if (successful) {
                sitter.saveInDB()

                suscribeToTopic("${owner.id} ${sitter.id}")
                handshake()
                sendNotification(duration)

                show("El cuidador seleccionado ha sido contratado")
            }else
                show("EL cuidador no tiene disponibilidad en este horario")

            owner.sitterList.add(sitter.id)
            owner.saveInDB()
        }

    }

    private fun handshake(){
        val handshake = FCMMessage()
        handshake.to =  "/topics/"+sitter.id
        handshake.data = Message(sitter.email, owner.id, true)

        val gson  = Gson()
        val json =  gson.toJson(handshake)

        Thread(Runnable {
            POSTtoFCM(FCMMessage.API_KEY, json)
        }).start()
    }

    private fun sendNotification(duration: Duration){
        var fcm = FCMMessage()
        //                           |
        //                           v
        fcm.to = "/topics/${owner.id} ${sitter.id}"// <------- Quitar el espacio entre ownerID y SitterID -------->
        fcm.data = Message(sitter.email, owner.id, false)

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
