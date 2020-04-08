package zero.network.petgarden.ui.register.user

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import zero.network.petgarden.R
import zero.network.petgarden.ui.element.ActionBarFragment

class RegisterActivity : AppCompatActivity() {

    private lateinit var nameFragment: NameRegisterFragment
    private lateinit var emailFragment: EmailRegisterFragment
    private lateinit var birthFragment: BirthRegisterFragment

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_register)
        supportFragmentManager.beginTransaction().apply{
            replace(R.id.header, ActionBarFragment("Register", true){finish()})
            commit()
        }
        birthFragment =
            BirthRegisterFragment()
        supportFragmentManager.beginTransaction().apply{
            replace(R.id.body, birthFragment)
            commit()
        }
    }
}
