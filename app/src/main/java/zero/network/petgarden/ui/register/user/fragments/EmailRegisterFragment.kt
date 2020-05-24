package zero.network.petgarden.ui.register.user.fragments

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import zero.network.petgarden.R
import zero.network.petgarden.databinding.FragmentRegisterEmailBinding
import zero.network.petgarden.model.behaivor.IUser
import zero.network.petgarden.model.entity.User
import zero.network.petgarden.ui.register.OnNextListener
import zero.network.petgarden.util.show
import zero.network.petgarden.util.toText
import java.util.*

/**
 * @author CarlosEduardoL
 */
class EmailRegisterFragment(
    private val user: User,
    private val listener: OnNextListener<IUser>
) : Fragment() {

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View = FragmentRegisterEmailBinding.inflate(inflater, container, false).apply{

        emailInput.setText(user.email)

        nextButton.setOnClickListener {
            if (!acceptButton.isChecked){
                show(getString(R.string.terms_message))
                return@setOnClickListener
            }
            emailInput.toText().let {
                if (it.isNotEmpty()){
                    user.email = it.toLowerCase(Locale.ROOT)
                    listener.next(this@EmailRegisterFragment)
                }else {
                    show(getString(R.string.field_error))
                }
            }
        }
    }.root

}
