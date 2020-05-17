package zero.network.petgarden.ui.user.owner.recruitment

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import zero.network.petgarden.databinding.FragmentSelectPetBinding
import zero.network.petgarden.model.entity.Pet
import zero.network.petgarden.tools.OnPetClickListener
import kotlin.coroutines.Continuation
import kotlin.coroutines.resume

class SelectPetFragment(
    private val pets: List<Pet>,
    private val continuation: Continuation<Pet?>
) : Fragment(), OnPetClickListener {

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View = FragmentSelectPetBinding.inflate(inflater, container, false).apply {

        val adapterPets =
            AdapterSelectPet(
                pets,
                this@SelectPetFragment
            )
        listPet.apply {
            adapter = adapterPets
        }
    }.root

    override fun onPetClick(pet: Pet) {
        activity!!.supportFragmentManager.popBackStack()
        continuation.resume(pet)
    }
}
