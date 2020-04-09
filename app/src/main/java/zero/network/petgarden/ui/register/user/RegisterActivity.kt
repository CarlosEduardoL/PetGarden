package zero.network.petgarden.ui.register.user

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import androidx.fragment.app.Fragment
import zero.network.petgarden.R
import zero.network.petgarden.model.entity.User
import zero.network.petgarden.ui.element.ActionBarFragment
import zero.network.petgarden.ui.register.OnNextListener

/**
 * @author CarlosEduardoL
 */
class RegisterActivity : AppCompatActivity(), OnNextListener {

    private lateinit var nameFragment: NameRegisterFragment
    private lateinit var emailFragment: EmailRegisterFragment
    private lateinit var passFragment: PasswordRegisterFragment
    private lateinit var birthFragment: BirthRegisterFragment
    private lateinit var roleFragment: RoleRegisterFragment

    private val user = User()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_register)
        supportFragmentManager.beginTransaction().apply {
            replace(R.id.header, ActionBarFragment("Register", true) { onBackPressed() })
            commit()
        }

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

    override fun next(fragment: Fragment) {
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
            roleFragment -> {
                // Aqui falta decidir como vamos a manejar lo de l rol
            }
        }
    }

    companion object {
        const val REGISTER_STACK = "REGISTER_STACK"
    }
}
