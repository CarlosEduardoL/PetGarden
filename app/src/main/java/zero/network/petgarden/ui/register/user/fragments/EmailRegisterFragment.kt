package zero.network.petgarden.ui.register.user.fragments

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import kotlinx.android.synthetic.main.fragment_register_email.view.*
import zero.network.petgarden.R
import zero.network.petgarden.model.entity.User
import zero.network.petgarden.ui.register.user.OnNextListener
import zero.network.petgarden.util.show
import zero.network.petgarden.util.toText

/**
 * @author CarlosEduardoL
 */
class EmailRegisterFragment(
    private val user: User,
    private val listener: OnNextListener
) : Fragment() {

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View = inflater.inflate(R.layout.fragment_register_email, container, false).apply{

        emailInput.setText(user.email)

        nextButton.setOnClickListener {
            if (!acceptButton.isChecked){
                show(getString(R.string.terms_message))
                return@setOnClickListener
            }
            emailInput.toText().let {
                if (it.isNotEmpty()){
                    user.email = it
                    listener.next(this@EmailRegisterFragment, user)
                }else {
                    show(getString(R.string.field_error))
                }
            }
        }
    }

}
