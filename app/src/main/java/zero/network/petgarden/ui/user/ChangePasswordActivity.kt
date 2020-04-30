package zero.network.petgarden.ui.user

import android.app.Activity
import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.FirebaseDatabase
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers.IO
import kotlinx.coroutines.Dispatchers.Main
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.launch
import kotlinx.coroutines.tasks.await
import zero.network.petgarden.R
import zero.network.petgarden.databinding.ActivityChangePasswordBinding
import zero.network.petgarden.model.behaivor.IUser
import zero.network.petgarden.model.entity.Sitter
import zero.network.petgarden.util.extra
import zero.network.petgarden.util.onClick
import zero.network.petgarden.util.show

class ChangePasswordActivity : AppCompatActivity() {

    private lateinit var binding: ActivityChangePasswordBinding


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityChangePasswordBinding.inflate(layoutInflater)
        setContentView(binding.root)
        val user = extra<IUser>("user"){
            show(it); setResult(Activity.RESULT_CANCELED)
            finish();return
        }
        binding.apply {
            emailUserTxt.text = user.email
            nameUserTxt.text = user.name
            updatePass.onClick {
                when {
                    user.password != currentPass.text.toString() -> show(getString(R.string.wrong_password))
                    currentPass.text.toString() == newPass.text.toString() -> show(getString(R.string.password_cannot_be_equals))
                    else -> {
                        CoroutineScope(Main).launch {
                            FirebaseAuth.getInstance().currentUser?.updatePassword(newPass.text.toString())?.await()
                            user.password = newPass.text.toString()
                            FirebaseDatabase.getInstance().reference.child("users").child(when(user){
                                is Sitter -> "sitters"
                                else -> "owners"
                            }).updateChildren(mapOf(user.id to user)).await()
                            setResult(Activity.RESULT_OK, Intent().putExtra("user",user))
                            finish()
                        }
                    }
                }
            }
        }
    }
}
