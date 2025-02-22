package zero.network.petgarden.ui.register.user.fragments

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import com.google.firebase.auth.FirebaseAuth
import zero.network.petgarden.R
import zero.network.petgarden.databinding.FragmentPasswordRegisterBinding
import zero.network.petgarden.model.behaivor.IUser
import zero.network.petgarden.model.entity.User
import zero.network.petgarden.ui.register.OnNextListener
import zero.network.petgarden.util.show
import zero.network.petgarden.util.toText

/**
 * A simple [Fragment] subclass.
 * @author CarlosEduardoL
 */
class PasswordRegisterFragment(
    private val user: User,
    private val listener: OnNextListener<IUser>
) : Fragment() {

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? = FragmentPasswordRegisterBinding.inflate(inflater, container, false).apply {


        nextButton.setOnClickListener {
            if (passInput.toText().isEmpty() || passConfirmInput.toText().isEmpty())
                show(getString(R.string.field_error))
            else if (passInput.toText() != passConfirmInput.toText())
                show(getString(R.string.pass_confirm_message))
            else{
                val password = passInput.toText()
                val authUser = FirebaseAuth.getInstance().currentUser
                if (authUser != null) {
                    listener.next(this@PasswordRegisterFragment)
                } else FirebaseAuth.getInstance()
                    .createUserWithEmailAndPassword(user.email, password)
                    .addOnSuccessListener {
                        listener.next(this@PasswordRegisterFragment)
                    }
                    .addOnFailureListener { show(it.message ?: "Unexpected Error, please retry") }
            }
        }
    }.root

}
