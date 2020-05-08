package zero.network.petgarden.ui.register.user.activities

import android.app.Activity
import android.content.Intent
import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import androidx.fragment.app.Fragment
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.DatabaseReference
import com.google.firebase.database.FirebaseDatabase
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import zero.network.petgarden.R
import zero.network.petgarden.model.behaivor.CallBack
import zero.network.petgarden.model.behaivor.Entity
import zero.network.petgarden.model.behaivor.IUser
import zero.network.petgarden.model.entity.Owner
import zero.network.petgarden.model.entity.Pet
import zero.network.petgarden.model.entity.Sitter
import zero.network.petgarden.model.entity.User
import zero.network.petgarden.ui.element.ActionBarFragment
import zero.network.petgarden.ui.register.PictureFragment
import zero.network.petgarden.ui.register.PictureListener
import zero.network.petgarden.ui.register.pet.PetRegisterActivity
import zero.network.petgarden.ui.register.pet.PetRegisterActivity.Companion.PET_KEY
import zero.network.petgarden.ui.register.pet.PetRegisterActivity.Companion.TITLE_KEY
import zero.network.petgarden.ui.register.user.fragments.BirthRegisterFragment
import zero.network.petgarden.ui.register.user.OnNextListener
import zero.network.petgarden.ui.register.user.fragments.RoleRegisterFragment
import zero.network.petgarden.ui.user.owner.OwnerActivity
import zero.network.petgarden.ui.user.sitter.SitterActivity
import zero.network.petgarden.util.extra
import zero.network.petgarden.util.show
import zero.network.petgarden.util.startUserView

/**
 * @author CarlosEduardoL
 */
class RegisterGoogleActivity : AppCompatActivity(),
    OnNextListener, PictureListener {

    private lateinit var birthFragment: BirthRegisterFragment
    private lateinit var roleFragment: RoleRegisterFragment

    private lateinit var database: DatabaseReference
    private lateinit var user: User

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_register)
        supportFragmentManager.beginTransaction().apply {
            replace(R.id.header, ActionBarFragment("Register", true) { onBackPressed() })
            commit()
        }

        database = FirebaseDatabase.getInstance().reference
        user = extra("user") { return }


        birthFragment =
            BirthRegisterFragment(
                user,
                this
            )
        roleFragment =
            RoleRegisterFragment(
                user,
                this
            )


        supportFragmentManager.beginTransaction().apply {
            replace(R.id.body, birthFragment)
            commit()
        }
    }

    override fun next(fragment: Fragment, state: IUser) {
        when (fragment) {

            birthFragment -> supportFragmentManager.beginTransaction()
                .replace(R.id.body, roleFragment)
                .addToBackStack(REGISTER_STACK)
                .commit()
            roleFragment -> when (state) {
                is Entity -> supportFragmentManager.beginTransaction()
                    .replace(
                        R.id.body,
                        PictureFragment(this, state, getString(R.string.user_picture))
                    )
                    .addToBackStack(REGISTER_STACK)
                    .commit()
            }
        }
    }

    override fun onPictureCaptured(entity: Entity) {
        when (entity) {
            is Owner -> Intent(this, PetRegisterActivity::class.java).apply {
                putExtra(TITLE_KEY, "Registrar Mascota")
                putExtra(PET_KEY, Pet())
                startActivityForResult(this, PET_CALLBACK)
            }

            is Sitter -> {
                entity.saveInDB()
                startUserView(entity, SitterActivity::class.java)
            }
            else -> println("Error")
        }
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        if (resultCode == Activity.RESULT_OK && data !== null && requestCode == PET_CALLBACK) {
            val owner = Owner(user)
            val pet: Pet = data.extra(PET_KEY) { return }
            CoroutineScope(Dispatchers.Main).launch {
                owner.addPet(pet)
                owner.saveInDB()
            }
            startUserView(owner, OwnerActivity::class.java)
        }
    }

    companion object {
        private const val PET_CALLBACK = 2900
        private const val REGISTER_STACK = "REGISTER_STACK"
    }
}

