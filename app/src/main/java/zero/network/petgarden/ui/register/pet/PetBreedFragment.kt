package zero.network.petgarden.ui.register.pet

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import zero.network.petgarden.R
import zero.network.petgarden.model.entity.Pet

class PetBreedFragment(private val listener: OnNextListener, private val pet: Pet) : Fragment() {

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? = inflater.inflate(R.layout.fragment_pet_breed, container, false).apply {

    }

}