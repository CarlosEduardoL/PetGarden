package zero.network.petgarden.ui.register.user

import android.app.Activity
import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.DatabaseReference
import com.google.firebase.database.FirebaseDatabase
import kotlinx.android.synthetic.main.fragment_role_register.*
import zero.network.petgarden.R
import zero.network.petgarden.model.behaivor.IUser
import zero.network.petgarden.model.entity.Owner
import zero.network.petgarden.model.entity.Pet
import zero.network.petgarden.model.entity.User
import zero.network.petgarden.ui.register.pet.PetRegisterActivity
import zero.network.petgarden.ui.user.owner.OwnerActivity
import zero.network.petgarden.ui.user.sitter.SitterActivity
import zero.network.petgarden.util.extra
import zero.network.petgarden.util.show


class RegisterFacebookActivity(): AppCompatActivity() {

    companion object {
        private const val PET_CALLBACK = 2900
    }

    private var user: User = User()
    private lateinit var database: DatabaseReference

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.fragment_role_register)
        database = FirebaseDatabase.getInstance().reference
        user = intent.extras!!.get("user") as User



        sitterButton.setOnClickListener{
            FirebaseAuth.getInstance()
                .createUserWithEmailAndPassword(user.email, user.password)
                .addOnSuccessListener {
                    database.child("users").child("sitters").child(user.id).setValue(user)
                    intent = Intent(this, SitterActivity::class.java)
                    intent.putExtra("user", user)
                    startActivity(intent)
                }
                .addOnFailureListener {
                    show(it.message ?: "Unexpected Error, please retry")
                }
        }

        ownerButton.setOnClickListener{
            Intent(this, PetRegisterActivity::class.java).apply {
                putExtra(PetRegisterActivity.TITLE_KEY, "Registrar Mascota")
                putExtra(PetRegisterActivity.PET_KEY, Pet())
                startActivityForResult(this, PET_CALLBACK)}
        }
    }


    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        if (resultCode == Activity.RESULT_OK && data !== null && requestCode ==PET_CALLBACK){
            val owner = Owner(user).apply { pets.add(data.extra(PetRegisterActivity.PET_KEY){return}) }
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


    private fun <T> startUserView(state: IUser, clazz: Class<T>) {
        Intent(this, clazz).apply {
            flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK
            putExtra("user", state)
            startActivity(this)
        }
    }
}
