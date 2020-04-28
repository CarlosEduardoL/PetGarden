package zero.network.petgarden.ui.register.user

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
import zero.network.petgarden.model.entity.Sitter
import zero.network.petgarden.model.entity.User
import zero.network.petgarden.ui.element.ActionBarFragment
import zero.network.petgarden.ui.register.OnNextListener
import zero.network.petgarden.ui.user.owner.OwnerActivity
import zero.network.petgarden.ui.user.sitter.SitterActivity
import zero.network.petgarden.util.show

/**
 * @author CarlosEduardoL
 */
class RegisterActivity : AppCompatActivity(), OnNextListener {

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

        emailFragment = EmailRegisterFragment(user, this)
        nameFragment = NameRegisterFragment(user, this)
        passFragment = PasswordRegisterFragment(user, this)
        birthFragment = BirthRegisterFragment(user, this)
        roleFragment = RoleRegisterFragment(user, this)


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
                is Owner -> FirebaseAuth.getInstance()
                    .createUserWithEmailAndPassword(state.email, state.password)
                    .addOnSuccessListener {
                        database.child("users").child("owners").child(state.id).setValue(state)
                        startUserView(state, OwnerActivity::class.java)
                    }
                    .addOnFailureListener {
                        show(it.message ?: "Unexpected Error, please retry")
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

    companion object {
        private const val REGISTER_STACK = "REGISTER_STACK"
    }
}

