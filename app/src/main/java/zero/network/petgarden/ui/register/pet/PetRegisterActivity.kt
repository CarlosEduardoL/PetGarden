package zero.network.petgarden.ui.register.pet

import android.app.Activity
import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import androidx.fragment.app.Fragment
import zero.network.petgarden.R
import zero.network.petgarden.model.entity.Pet
import zero.network.petgarden.ui.element.ActionBarFragment
import zero.network.petgarden.util.extra
import zero.network.petgarden.util.show

class PetRegisterActivity : AppCompatActivity(), OnNextListener {

    private lateinit var pet: Pet

    private lateinit var typeFragment: PetTypeFragment
    private lateinit var nameFragment: PetNameFragment
    private lateinit var breedFragment: PetBreedFragment
    private lateinit var ageFragment: PetAgeFragment
    private lateinit var weightFragment: PetWeightFragment
    private lateinit var picFragment: PetPicFragment


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_pet_register)

        supportFragmentManager.beginTransaction()
            .replace(R.id.header, ActionBarFragment(extra(TITLE_KEY) { onError(it); return }, true) { onBackPressed() })
            .commit()


        pet = extra(EXTRA_KEY) { onError(it); return }

        typeFragment = PetTypeFragment(this, pet)
        nameFragment = PetNameFragment(this, pet)
        breedFragment = PetBreedFragment(this, pet)
        ageFragment = PetAgeFragment(this, pet)
        weightFragment = PetWeightFragment(this, pet)
        picFragment = PetPicFragment(this, pet)

        supportFragmentManager.beginTransaction()
            .replace(R.id.header, typeFragment)
            .commit()

    }

    override fun next(fragment: Fragment) {
        when (fragment) {
            typeFragment -> changeTo(nameFragment)
            nameFragment -> changeTo(breedFragment)
            breedFragment -> changeTo(ageFragment)
            ageFragment -> changeTo(weightFragment)
            weightFragment -> changeTo(picFragment)
            picFragment -> {
                setResult(Activity.RESULT_OK, Intent().apply { putExtra(EXTRA_KEY, pet) })
                finish()
            }
        }

    }

    private fun onError(error: String) {
        show(error)
        setResult(Activity.RESULT_CANCELED)
        finish()
    }

    private fun changeTo(fragment: Fragment) = supportFragmentManager.beginTransaction().replace(R.id.body, fragment).addToBackStack(REGISTER_STACK).commit()

    companion object {
        const val EXTRA_KEY = "PET"
        const val TITLE_KEY = "TITLE"
        private const val REGISTER_STACK = "REGISTER_STACK"
    }
}
