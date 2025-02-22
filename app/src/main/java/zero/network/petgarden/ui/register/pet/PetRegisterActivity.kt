package zero.network.petgarden.ui.register.pet

import android.app.Activity
import android.content.Context
import android.content.Intent
import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import androidx.fragment.app.Fragment
import zero.network.petgarden.R
import zero.network.petgarden.model.behaivor.Entity
import zero.network.petgarden.model.entity.Pet
import zero.network.petgarden.ui.element.ActionBarFragment
import zero.network.petgarden.ui.element.picture.PictureFragment
import zero.network.petgarden.ui.element.picture.PictureListener
import zero.network.petgarden.ui.register.OnNextListener
import zero.network.petgarden.util.extra
import zero.network.petgarden.util.intent
import zero.network.petgarden.util.show

class PetRegisterActivity : AppCompatActivity(), OnNextListener<Any>,
    PictureListener {

    private lateinit var pet: Pet

    private lateinit var typeFragment: PetTypeFragment
    private lateinit var nameFragment: PetNameFragment
    private lateinit var breedFragment: PetBreedFragment
    private lateinit var ageFragment: PetAgeFragment
    private lateinit var weightFragment: PetWeightFragment
    private lateinit var picFragment: PictureFragment
    private lateinit var recommendationFragment: PetRecommendationFragment


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_pet_register)

        supportFragmentManager.beginTransaction()
            .replace(
                R.id.header,
                ActionBarFragment(
                    extra(TITLE_KEY),
                    true
                ) { onBackPressed() })
            .commit()


        pet = extra(PET_KEY)

        typeFragment = PetTypeFragment(this, pet)
        nameFragment = PetNameFragment(this, pet)
        breedFragment = PetBreedFragment(this, pet)
        ageFragment = PetAgeFragment(this, pet)
        weightFragment = PetWeightFragment(this, pet)
        recommendationFragment = PetRecommendationFragment(this, pet)
        picFragment = PictureFragment(
            this,
            pet,
            getString(R.string.pet_picture)
        )

        supportFragmentManager.beginTransaction()
            .replace(R.id.body, typeFragment)
            .commit()

    }

    override fun next(fragment: Fragment, vararg extra: Any) {
        when (fragment) {
            typeFragment -> changeTo(nameFragment)
            nameFragment -> changeTo(breedFragment)
            breedFragment -> changeTo(ageFragment)
            ageFragment -> changeTo(weightFragment)
            weightFragment -> changeTo(recommendationFragment)
            recommendationFragment -> changeTo(picFragment)
        }
    }


    private fun onError(error: String) {
        show(error)
        setResult(Activity.RESULT_CANCELED)
        finish()
    }

    private fun changeTo(fragment: Fragment) =
        supportFragmentManager.beginTransaction().replace(R.id.body, fragment)
            .addToBackStack(REGISTER_STACK).commit()

    override fun onPictureCaptured(entity: Entity) {
        setResult(Activity.RESULT_OK, Intent().apply { putExtra(PET_KEY, pet) })
        finish()
    }

    companion object {
        const val PET_KEY = "PET"
        const val TITLE_KEY = "TITLE"
        private const val REGISTER_STACK = "REGISTER_STACK"

        fun intent(context: Context, title: String, pet: Pet) =
            context.intent(PetRegisterActivity::class.java, PET_KEY to pet, TITLE_KEY to title)

    }
}
