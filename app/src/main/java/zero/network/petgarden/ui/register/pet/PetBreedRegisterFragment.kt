package zero.network.petgarden.ui.register.pet

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import kotlinx.android.synthetic.main.fragment_pet_name_register.view.*
import zero.network.petgarden.R
import zero.network.petgarden.model.entity.Pet

class PetBreedRegisterFragment(private val listener: OnNextListener, private val pet: Pet) : Fragment() {

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? = inflater.inflate(R.layout.fragment_pet_breed_register, container, false).apply {
        val nameInput = petNameInput
        nameInput.setText(pet.name)
        nextButton.setOnClickListener {
            pet.name
            listener.next(this@PetBreedRegisterFragment, pet)
        }
    }

}