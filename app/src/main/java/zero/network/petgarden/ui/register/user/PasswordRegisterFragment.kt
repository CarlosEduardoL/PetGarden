package zero.network.petgarden.ui.register.user

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import kotlinx.android.synthetic.main.fragment_password_register.view.*

import zero.network.petgarden.R
import zero.network.petgarden.model.entity.User
import zero.network.petgarden.util.show
import zero.network.petgarden.util.toText

/**
 * A simple [Fragment] subclass.
 * @author CarlosEduardoL
 */
class PasswordRegisterFragment(
    private val user: User,
    private val listener: OnNextListener
) : Fragment() {

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? = inflater.inflate(R.layout.fragment_password_register, container, false).apply {

        passInput.setText(user.password)
        passConfirmInput.setText(user.password)

        nextButton.setOnClickListener {
            passInput.toText().let {
                if (it == passConfirmInput.toText()){
                    if (it.isEmpty()){
                        show(getString(R.string.field_error))
                    }else {
                        user.password = it
                        println("Pase xD")
                        listener.next(this@PasswordRegisterFragment, user)
                    }
                }else {
                    show(getString(R.string.pass_confirm_message))
                }
            }
        }
    }

}
