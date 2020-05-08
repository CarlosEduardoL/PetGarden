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
import kotlinx.coroutines.Dispatchers.Main
import kotlinx.coroutines.launch
import zero.network.petgarden.R
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
import zero.network.petgarden.ui.register.user.*
import zero.network.petgarden.ui.register.user.fragments.*
import zero.network.petgarden.ui.user.owner.OwnerActivity
import zero.network.petgarden.ui.user.sitter.SitterActivity
import zero.network.petgarden.util.extra
import zero.network.petgarden.util.show

/**
 * @author CarlosEduardoL
 */
class RegisterActivity : AppCompatActivity(),
    OnNextListener, PictureListener {

    private lateinit var nameFragment: NameRegisterFragment
    private lateinit var emailFragment: EmailRegisterFragment
    private lateinit var passFragment: PasswordRegisterFragment
    private lateinit var birthFragment: BirthRegisterFragment
    private lateinit var roleFragment: RoleRegisterFragment

    private lateinit var database: DatabaseReference

    private val user = User()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_register)
        supportFragmentManager.beginTransaction().apply {
            replace(R.id.header, ActionBarFragment("Register", true) { onBackPressed() })
            commit()
        }

        database = FirebaseDatabase.getInstance().reference

        emailFragment =
            EmailRegisterFragment(
                user,
                this
            )
        nameFragment =
            NameRegisterFragment(
                user,
                this
            )
        passFragment =
            PasswordRegisterFragment(
                user,
                this
            )
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
            replace(R.id.body, nameFragment)
            commit()
        }
    }

    override fun next(fragment: Fragment, state: IUser) {
        when (fragment) {
            nameFragment -> supportFragmentManager.beginTransaction()
                .replace(R.id.body, emailFragment)
                .addToBackStack(REGISTER_STACK)
                .commit()
            emailFragment -> supportFragmentManager.beginTransaction()
                .replace(R.id.body, passFragment)
                .addToBackStack(REGISTER_STACK)
                .commit()
            passFragment -> supportFragmentManager.beginTransaction()
                .replace(R.id.body, birthFragment)
                .addToBackStack(REGISTER_STACK)
                .commit()
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

    private fun <T> startUserView(iUser: IUser, clazz: Class<T>) {
        FirebaseAuth.getInstance()
            .createUserWithEmailAndPassword(iUser.email, iUser.password)
            .addOnSuccessListener {
                Intent(this, clazz).apply {
                    flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK
                    putExtra("user", iUser)
                    startActivity(this)
                }
            }
            .addOnFailureListener {
                show(it.message ?: "Unexpected Error, please retry")
            }
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        if (resultCode == Activity.RESULT_OK && data !== null && requestCode == PET_CALLBACK) {
            val owner = Owner(user)
            val pet: Pet = data.extra(PET_KEY) { return }
            CoroutineScope(Main).launch {
                owner.addPet(pet)
                owner.saveInDB()
            }
            startUserView(owner, OwnerActivity::class.java)
        }
    }

    override fun onPictureCaptured(entity: Entity) {
        when (entity) {
            is Owner -> Intent(this, PetRegisterActivity::class.java).apply {
                putExtra(TITLE_KEY, "Registrar Mascota")
                putExtra(PET_KEY, Pet())
                startActivityForResult(this,
                    PET_CALLBACK
                )
            }

            is Sitter -> {
                entity.saveInDB()
                startUserView(entity, SitterActivity::class.java)
            }
            else -> println("Error")
        }
    }

    companion object {
        private const val PET_CALLBACK = 2900
        private const val REGISTER_STACK = "REGISTER_STACK"

    }
}

