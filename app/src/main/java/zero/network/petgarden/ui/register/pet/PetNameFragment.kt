package zero.network.petgarden.ui.register.pet

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import zero.network.petgarden.databinding.FragmentPetNameBinding
import zero.network.petgarden.model.entity.Pet
import zero.network.petgarden.ui.register.OnNextListener
import zero.network.petgarden.util.onClick
import zero.network.petgarden.util.toText

/**
 * A simple [Fragment] subclass.
 */
class PetNameFragment(private val listener: OnNextListener<Any>, private val pet: Pet) : Fragment() {

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? = FragmentPetNameBinding.inflate(inflater, container, false).apply {
        petNameInput.setText(pet.name)
        nextButton.onClick {
            pet.name = petNameInput.toText()
            listener.next(this@PetNameFragment)
        }
    }.root

}
