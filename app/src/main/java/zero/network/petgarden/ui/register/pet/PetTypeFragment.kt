package zero.network.petgarden.ui.register.pet

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import zero.network.petgarden.R
import zero.network.petgarden.databinding.FragmentTypePetBinding
import zero.network.petgarden.model.entity.Pet
import zero.network.petgarden.ui.register.OnNextListener
import zero.network.petgarden.util.onClick

class PetTypeFragment(private val listener: OnNextListener<Any>, private val pet: Pet) : Fragment() {
    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View = FragmentTypePetBinding.inflate(inflater, container, false).apply {
        DogButton.onClick {
            pet.type = getString(R.string.type_dog)
            listener.next(this@PetTypeFragment)
        }
        CatButton.onClick {
            pet.type = getString(R.string.type_cat)
            listener.next(this@PetTypeFragment)
        }
        OthersButton.onClick {
            pet.type = getString(R.string.type_other)
            listener.next(this@PetTypeFragment)
        }
    }.root
}