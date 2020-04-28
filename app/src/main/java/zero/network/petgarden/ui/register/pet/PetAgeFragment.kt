package zero.network.petgarden.ui.register.pet

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.fragment.app.Fragment
import kotlinx.android.synthetic.main.fragment_age_of_pet.view.*
import zero.network.petgarden.R
import zero.network.petgarden.model.entity.Pet

class PetAgeFragment(private val listener: OnNextListener, private val pet: Pet) : Fragment() {
    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View = inflater.inflate(R.layout.fragment_age_of_pet, container, false).apply {
        val age = agePet.apply { text = "${pet.years}" }
        fun TextView.value() = text.toString().toInt()
        buttonDown.setOnClickListener {
            if (age.value() > 0) age.text = (age.value() - 1).toString()
        }
        buttonUp.setOnClickListener {
            age.text = (age.value() + 1).toString()
        }
        nextButton.setOnClickListener {
            pet.years = age.value()
            listener.next(this@PetAgeFragment)
        }
    }

}