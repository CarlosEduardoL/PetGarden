package zero.network.petgarden.ui.register.pet

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import zero.network.petgarden.databinding.FragmentAgeOfPetBinding
import zero.network.petgarden.model.entity.Pet
import zero.network.petgarden.ui.register.OnNextListener
import zero.network.petgarden.util.onClick

class PetAgeFragment(private val listener: OnNextListener<Any>, private val pet: Pet) : Fragment() {
    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View = FragmentAgeOfPetBinding.inflate(inflater, container, false).apply {
        agePet.text = "${pet.years}"
        fun value() = agePet.text.toString().toInt()
        buttonDown.onClick {
            if (value() > 0) agePet.text = (value() - 1).toString()
        }
        buttonUp.onClick {
            agePet.text = (value() + 1).toString()
        }
        nextButton.onClick {
            pet.years = value()
            listener.next(this@PetAgeFragment)
        }
    }.root

}