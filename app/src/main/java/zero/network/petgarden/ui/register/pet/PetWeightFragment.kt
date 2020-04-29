package zero.network.petgarden.ui.register.pet

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import zero.network.petgarden.databinding.FragmentPetWeightBinding
import zero.network.petgarden.model.entity.Pet
import zero.network.petgarden.util.onClick

class PetWeightFragment(private val listener: OnNextListener, private val pet: Pet) : Fragment() {
    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View = FragmentPetWeightBinding.inflate(inflater, container, false).apply {
        petWeightInput.setText("${pet.weight}")
        fun value() = petWeightInput.text.toString().toInt()
        nextButton.onClick {
            if (value() > 0){
                pet.weight = value()
                listener.next(this@PetWeightFragment)
            }
        }
    }.root
}