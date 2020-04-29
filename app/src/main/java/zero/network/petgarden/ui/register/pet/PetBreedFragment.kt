package zero.network.petgarden.ui.register.pet

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import zero.network.petgarden.R
import zero.network.petgarden.databinding.FragmentPetBreedBinding
import zero.network.petgarden.model.entity.Pet
import zero.network.petgarden.util.onClick

class PetBreedFragment(private val listener: OnNextListener, private val pet: Pet) : Fragment() {

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? = FragmentPetBreedBinding.inflate(inflater, container, false).apply {
        petBreedInput.setText(pet.breed)
        nextButton.onClick {
            if (petBreedInput.text.isNotEmpty()) {
                pet.breed = petBreedInput.text.toString()
                listener.next(this@PetBreedFragment)
            }
        }
    }.root

}