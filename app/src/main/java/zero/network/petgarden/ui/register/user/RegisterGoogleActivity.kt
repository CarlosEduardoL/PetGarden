package zero.network.petgarden.ui.register.user

import android.app.Activity
import android.content.Intent
import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import androidx.fragment.app.Fragment
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.DatabaseReference
import com.google.firebase.database.FirebaseDatabase
import zero.network.petgarden.R
import zero.network.petgarden.model.behaivor.IUser
import zero.network.petgarden.model.entity.Owner
import zero.network.petgarden.model.entity.Pet
import zero.network.petgarden.model.entity.Sitter
import zero.network.petgarden.model.entity.User
import zero.network.petgarden.ui.element.ActionBarFragment
import zero.network.petgarden.ui.register.pet.PetRegisterActivity
import zero.network.petgarden.ui.register.pet.PetRegisterActivity.Companion.PET_KEY
import zero.network.petgarden.ui.register.pet.PetRegisterActivity.Companion.TITLE_KEY
import zero.network.petgarden.ui.user.owner.OwnerActivity
import zero.network.petgarden.ui.user.sitter.SitterActivity
import zero.network.petgarden.util.extra
import zero.network.petgarden.util.show

/**
 * @author CarlosEduardoL
 */
class RegisterGoogleActivity : AppCompatActivity(),
    OnNextListener {

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

        birthFragment = BirthRegisterFragment(user, this)
        roleFragment = RoleRegisterFragment(user, this)


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
                is Owner -> Intent(this, PetRegisterActivity::class.java).apply {
                    putExtra(TITLE_KEY, "Registrar Mascota")
                    putExtra(PET_KEY, Pet())
                    startActivityForResult(this, PET_CALLBACK)
                }

                is Sitter -> FirebaseAuth.getInstance()
                    .createUserWithEmailAndPassword(state.email, state.password)
                    .addOnSuccessListener {
                        database.child("users").child("sitters").child(state.id).setValue(state)
                        startUserView(state, SitterActivity::class.java)
                    }
                    .addOnFailureListener {
                        show(it.message ?: "Unexpected Error, please retry")
                    }
            }
        }
    }


    private fun <T> startUserView(state: IUser, clazz: Class<T>) {
        Intent(this, clazz).apply {
            flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK
            putExtra("user", state)
            startActivity(this)
        }
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        if (resultCode == Activity.RESULT_OK && data !== null && requestCode == PET_CALLBACK){
            val owner = Owner(user).apply { pets.add(data.extra(PET_KEY){return}) }
            FirebaseAuth.getInstance()
                .createUserWithEmailAndPassword(owner.email, owner.password)
                .addOnSuccessListener {
                    database.child("users").child("owners").child(owner.id).setValue(owner)
                    startUserView(owner, OwnerActivity::class.java)
                }
                .addOnFailureListener {
                    show(it.message ?: "Unexpected Error, please retry")
                }
        }
    }

    companion object {
        private const val PET_CALLBACK = 2900
        private const val REGISTER_STACK = "REGISTER_STACK"
    }
}

