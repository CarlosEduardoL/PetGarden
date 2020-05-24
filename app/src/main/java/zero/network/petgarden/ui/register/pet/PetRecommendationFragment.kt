package zero.network.petgarden.ui.register.pet

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import zero.network.petgarden.databinding.FragmentPetRecommendationBinding
import zero.network.petgarden.model.entity.Pet
import zero.network.petgarden.ui.register.OnNextListener
import zero.network.petgarden.util.onClick

class PetRecommendationFragment(private val listener: OnNextListener<Any>, private val pet: Pet) : Fragment() {
    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View = FragmentPetRecommendationBinding.inflate(inflater, container, false).apply {
        fun value() = recommendationTV.text.toString()
        nextButton.onClick {
                pet.about = value()
                listener.next(this@PetRecommendationFragment)
        }
    }.root
}