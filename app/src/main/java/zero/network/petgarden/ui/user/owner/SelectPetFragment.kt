package zero.network.petgarden.ui.user.owner

import android.os.Bundle
import android.text.Editable
import android.text.TextWatcher
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.LinearLayoutManager
import zero.network.pet.filterFragment
import zero.network.petgarden.R
import zero.network.petgarden.model.entity.Pet
import zero.network.petgarden.tools.OnPetClickListener

class SelectPetFragment(view: OwnerView, private val pets:List<Pet>) : Fragment(), OnPetClickListener, OwnerView by view {

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View = inflater.inflate(R.layout.fragment_select_pet, container, false).apply {

        val adapterPets = AdapterSelectPet(pets, this@SelectPetFragment)
    }

    override fun onPetClick(pet: Pet) {
        fragmentManager!!.popBackStack()
    }
}
