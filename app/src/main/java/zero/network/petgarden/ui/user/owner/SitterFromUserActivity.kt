package zero.network.petgarden.ui.user.owner

import android.os.Build
import android.os.Bundle
import androidx.annotation.RequiresApi
import androidx.appcompat.app.AppCompatActivity
import kotlinx.android.synthetic.main.activity_sitter__from_user.*
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import zero.network.petgarden.R
import zero.network.petgarden.databinding.ActivitySitterFromUserBinding
import zero.network.petgarden.model.entity.*
import zero.network.petgarden.tools.OnPetClickListener
import zero.network.petgarden.util.extra
import zero.network.petgarden.util.show
import java.util.*
import kotlin.concurrent.fixedRateTimer

class SitterFromUserActivity: AppCompatActivity(), OnPetClickListener{

    private lateinit var sitter: Sitter
    private lateinit var owner:Owner
    lateinit var binding: ActivitySitterFromUserBinding

    @RequiresApi(Build.VERSION_CODES.M)
    override fun onCreate(savedInstanceState: Bundle?){
        super.onCreate(savedInstanceState)
        binding =  ActivitySitterFromUserBinding.inflate(layoutInflater)
        setContentView(binding.root)

        //Load sitter and owner
        val extras = intent.extras
        sitter = extras!!.getSerializable("sitter") as Sitter
        owner = extras.getSerializable("owner") as Owner


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

        //Date to Long
        val start = HourToTimeInMilis(startTime.hour, startTime.minute)
        val end = HourToTimeInMilis(endTime.hour, endTime.minute)

        next_button.setOnClickListener {
            if (checkValidSchedule(start, end)) {
                if (sitter.availability != null) {
                    CoroutineScope(Dispatchers.Main).launch { contracting() }
                } else show("El cuidador que desea contratar no está disponible")
            }else
                show("El periodo de tiempo seleccionado no es válido")
        }

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


    @RequiresApi(Build.VERSION_CODES.M)
    private suspend fun contracting(){
        var numPets = owner.pets().size

        val start = HourToTimeInMilis(startTime.hour, startTime.minute)
        val end = HourToTimeInMilis(endTime.hour, endTime.minute)
        val duration = Duration(start, end, sitter.availability!!.cost)

        if(numPets==1) {
            val task = Task(owner.pets().first().id, duration)
            val successful = sitter.planner.addTask(task)

            if (successful) {
                sitter.saveInDB()
                show("El cuidador seleccionado ha sido contratado")
            }else
                show("EL cuidador no tiene disponibilidad en este horario")

        }else {
            val selectFragment =  SelectPetFragment(owner.pets().toList())
            val fragmentTransaction = supportFragmentManager.beginTransaction()
            fragmentTransaction.add(R.id.actualFragmentContainer, selectFragment, null).addToBackStack(null)
            fragmentTransaction.commit()
        }

        owner.sitterList.add(sitter.id)
        owner.saveInDB()
    }


    private fun HourToTimeInMilis(hour:Int, min:Int ):Long{
        val date = Calendar.getInstance().apply {
            set(Calendar.HOUR_OF_DAY, hour)
            set(Calendar.MINUTE, min)
        }

        return date.timeInMillis
    }

    @RequiresApi(Build.VERSION_CODES.M)
    override fun onPetClick(pet: Pet) {
        val start = HourToTimeInMilis(startTime.hour, startTime.minute)
        val end = HourToTimeInMilis(endTime.hour, endTime.minute)
        val duration = Duration(start, end, sitter.availability!!.cost)
        var task:Task = Task("", Duration(1,1,1))
        CoroutineScope(Dispatchers.Main).launch { task = Task(owner.pets().first().id, duration) }

        val successful = sitter.planner.addTask(task)

        if (successful) {
            sitter.saveInDB()
            show("El cuidador seleccionado ha sido contratado")
        }else
            show("El cuidador no tiene disponibilidad en este horario")
    }

    private  fun checkValidSchedule(startTime: Long, endTime: Long): Boolean{
        return startTime < endTime
    }
}