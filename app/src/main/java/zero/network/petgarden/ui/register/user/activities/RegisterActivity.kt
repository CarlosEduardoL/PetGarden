package zero.network.petgarden.ui.register.user.activities

import android.annotation.SuppressLint
import android.app.Activity
import android.content.Intent
import android.location.LocationManager
import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import androidx.fragment.app.Fragment
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.DatabaseReference
import com.google.firebase.database.FirebaseDatabase
import zero.network.petgarden.R
import zero.network.petgarden.exception.AuthException
import zero.network.petgarden.exception.InvalidUserClass
import zero.network.petgarden.model.behaivor.CallBack
import zero.network.petgarden.model.behaivor.Entity
import zero.network.petgarden.model.behaivor.IUser
import zero.network.petgarden.model.entity.Owner
import zero.network.petgarden.model.entity.Pet
import zero.network.petgarden.model.entity.SitterIMP
import zero.network.petgarden.model.entity.User
import zero.network.petgarden.ui.element.ActionBarFragment
import zero.network.petgarden.ui.element.picture.PictureFragment
import zero.network.petgarden.ui.element.picture.PictureListener
import zero.network.petgarden.ui.register.OnNextListener
import zero.network.petgarden.ui.register.pet.PetRegisterActivity
import zero.network.petgarden.ui.register.pet.PetRegisterActivity.Companion.PET_KEY
import zero.network.petgarden.ui.register.user.FragmentStart
import zero.network.petgarden.ui.register.user.fragments.*
import zero.network.petgarden.util.extra
import zero.network.petgarden.util.startUserView
import zero.network.petgarden.model.component.Location as Location2

/**
 * @author CarlosEduardoL
 */
class RegisterActivity : AppCompatActivity(),
    OnNextListener<IUser>, PictureListener {

    private lateinit var nameFragment: NameRegisterFragment
    private lateinit var emailFragment: EmailRegisterFragment
    private lateinit var passFragment: PasswordRegisterFragment
    private lateinit var birthFragment: BirthRegisterFragment
    private lateinit var roleFragment: RoleRegisterFragment

    private lateinit var database: DatabaseReference

    private lateinit var user: User
    private lateinit var start: FragmentStart
    private var finished = false

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_register)
        supportFragmentManager.beginTransaction().apply {
            replace(R.id.header, ActionBarFragment("Register", true) { onBackPressed() })
            commit()
        }

        val location = lastLocation()

        database = FirebaseDatabase.getInstance().reference

        user = extra("user")
        user.location = location

        emailFragment = EmailRegisterFragment(user, this)
        nameFragment = NameRegisterFragment(user, this)
        passFragment = PasswordRegisterFragment(user, this)
        birthFragment = BirthRegisterFragment(user, this)
        roleFragment = RoleRegisterFragment(user, this)

        start = extra("start")

        supportFragmentManager.beginTransaction()
            .replace(
                R.id.body, when (start) {
                    FragmentStart.Name -> nameFragment
                    FragmentStart.Email -> emailFragment
                    FragmentStart.Password -> passFragment
                    FragmentStart.BirthDay -> birthFragment
                    FragmentStart.Role -> roleFragment
                }
            )
            .commit()

    }

    @SuppressLint("MissingPermission")
    private fun lastLocation(): zero.network.petgarden.model.component.Location {
        val lm = getSystemService(LOCATION_SERVICE) as LocationManager
        return lm.getProviders(true)
            .mapNotNull { lm.getLastKnownLocation(it) }
            .minBy { it.accuracy }
            ?.let { Location2(it.latitude, it.longitude) }
            ?: Location2(10.0, 10.0)
    }

    override fun next(fragment: Fragment, vararg extra: IUser) {
        when (fragment) {
            nameFragment -> changeView(emailFragment)
            emailFragment -> changeView(passFragment)
            passFragment -> changeView(birthFragment)
            birthFragment -> changeView(roleFragment)
            roleFragment -> for(user in extra) {
                if (user is Entity) {
                    FirebaseAuth.getInstance().currentUser?.let {
                        user.id = it.uid
                        changeView(PictureFragment(this, user, getString(R.string.user_picture)))
                    }?:throw AuthException("Some kind of error happen in firebase auth")
                }
            }
        }
    }

    private fun changeView(fragment: Fragment) = supportFragmentManager.beginTransaction()
        .replace(R.id.body, fragment).addToBackStack(REGISTER_STACK).commit()

    private fun finishRegister(user: Entity) {
        if (user is IUser) {
            user.saveInDB("Called By ${this::class.java.name} in line ${Throwable().stackTrace[0]
                .lineNumber}")
            finished = true
            startUserView(user)
        } else throw InvalidUserClass("${user::class.simpleName} no is a valid class")
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        if (resultCode == Activity.RESULT_OK && data !== null && requestCode == PET_CALLBACK) {
            val owner = Owner(user)
            FirebaseAuth.getInstance().currentUser?.let {
                owner.id = it.uid
            }?:throw Exception("Unexpected Error")
            val pet: Pet = data.extra(PET_KEY) { return }
            owner.addPet(pet, CallBack { println(pet.ownerID)})
            finishRegister(owner)
        }
    }

    override fun onPictureCaptured(entity: Entity) {
        when (entity) {
            is Owner -> startActivityForResult(
                PetRegisterActivity.intent(this, "Registrar Mascota", Pet()),
                PET_CALLBACK
            )
            is SitterIMP -> {
                finishRegister(entity)
            }
            else -> println("Error")
        }
    }

    override fun onDestroy() {
        if (!finished) FirebaseAuth.getInstance().currentUser?.delete()
        super.onDestroy()
    }

    companion object {
        private const val PET_CALLBACK = 2900
        private const val REGISTER_STACK = "REGISTER_STACK"

    }
}

