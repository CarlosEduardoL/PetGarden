package zero.network.petgarden.ui.register.pet

import android.app.Activity
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import zero.network.petgarden.R
import zero.network.petgarden.model.entity.Pet
import zero.network.petgarden.util.extra
import zero.network.petgarden.util.show

class PetRegisterActivity : AppCompatActivity() {

    private lateinit var pet: Pet

    private lateinit var typeRegisterFragment: PetTypeRegisterFragment
    private lateinit var nameRegisterFragment: PetNameRegisterFragment
    private lateinit var breedRegisterFragment: PetBreedRegisterFragment
    private lateinit var ageRegisterFragment: PetAgeRegisterFragment


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_pet_register)

        pet = extra(EXTRA_KEY){
            show(it)
            setResult(Activity.RESULT_CANCELED)
            finish()
            return
        }

    }

    companion object {
        const val EXTRA_KEY = "PET"
    }
}
