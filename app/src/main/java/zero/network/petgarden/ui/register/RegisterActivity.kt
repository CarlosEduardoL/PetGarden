package zero.network.petgarden.ui.register

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import zero.network.petgarden.R
import zero.network.petgarden.RegisterBirthFragment
import zero.network.petgarden.ui.element.ActionBarFragment

class RegisterActivity : AppCompatActivity() {

    private lateinit var nameFragment: RegisterNameFragment
    private lateinit var emailFragment: RegisterEmailFragment
    private lateinit var birthFragment: RegisterBirthFragment

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_register)
        supportFragmentManager.beginTransaction().apply{
            replace(R.id.header, ActionBarFragment("Register", true){finish()})
            commit()
        }
        birthFragment = RegisterBirthFragment()
        supportFragmentManager.beginTransaction().apply{
            replace(R.id.body, birthFragment)
            commit()
        }
    }
}
