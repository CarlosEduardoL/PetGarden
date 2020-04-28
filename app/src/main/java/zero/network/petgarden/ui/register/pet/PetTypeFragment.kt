package zero.network.petgarden.ui.register.pet

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import kotlinx.android.synthetic.main.fragment_type_pet.view.*
import zero.network.petgarden.R
import zero.network.petgarden.model.entity.Pet

class PetTypeFragment(private val listener: OnNextListener, private val pet: Pet) : Fragment() {
    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View = inflater.inflate(R.layout.fragment_type_pet, container, false).apply {
        DogButton.setOnClickListener {
            pet.type = getString(R.string.type_dog)
        }
        CatButton.setOnClickListener {
            pet.type = getString(R.string.type_cat)
        }
        OthersButton.setOnClickListener {
            pet.type = getString(R.string.type_other)
        }
    }
}