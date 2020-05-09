package zero.network.petgarden.ui.user.owner

import android.os.Build
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.annotation.RequiresApi
import androidx.fragment.app.Fragment
import kotlinx.android.synthetic.main.fragment_sitter_profile_from_user.*
import kotlinx.android.synthetic.main.fragment_sitter_profile_from_user.view.*
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import zero.network.petgarden.R
import zero.network.petgarden.model.entity.Duration
import zero.network.petgarden.model.entity.Pet
import zero.network.petgarden.model.entity.Sitter
import zero.network.petgarden.model.entity.Task
import zero.network.petgarden.tools.OnPetClickListener
import zero.network.petgarden.util.show
import java.util.*

class SitterProfileFromUser(private val sitter:Sitter, view: OwnerView) : Fragment(), OnPetClickListener, OwnerView by view{
    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View = inflater.inflate(R.layout.fragment_sitter_profile_from_user, container, false).apply {


        CoroutineScope(Dispatchers.Main).launch { photoSitter.setImageBitmap(sitter.image()) }
        nameSitterTxt.setText(sitter.name)
        emailSitterTxt.setText(sitter.email)

        //Availability
        if(sitter.availability!=null){
            val fromHour = getHour(sitter.availability!!.start)
            val fromMin = getMinute(sitter.availability!!.start)
            val toHour = getHour(sitter.availability!!.end)
            val toMin = getMinute(sitter.availability!!.end)

            scheduleSitter.setText("$fromHour:$fromMin a $toHour:$toMin")

            //Cost
            val cost  = sitter.availability!!.cost
            priceText.setText("$cost por hora")

        } else {
            scheduleSitter.setText("No disponible")
            priceText.setText("No disponible")
        }

        //kindPets
        kindPet.text = sitter.kindPets

        //AddInfo
        addInfo.text = sitter.additional

        //24h format
        startTime.setIs24HourView(true)
        endTime.setIs24HourView(true)

        next_button.setOnClickListener{
            if (sitter.availability!=null){
                //contracting
                show("El cuidador seleccionado ha sido contratado")
            }
            //contracting(


            else show("El cuidador que desea contratar no está disponible")
        }
    }.rootView

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
            sitter.addTask(task)
        }else {
           val selectFragment =  SelectPetFragment(this, owner.pets().toList())
            val fragmentTransaction = fragmentManager!!.beginTransaction()
            fragmentTransaction.add(R.id.activity_owner_container, selectFragment, null)
            fragmentTransaction.commit()
        }
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
        sitter.addTask(task)
    }
}