package zero.network.petgarden.ui.register.user

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import zero.network.petgarden.R
import zero.network.petgarden.databinding.FragmentRegisterNameBinding
import zero.network.petgarden.model.entity.User
import zero.network.petgarden.util.onClick
import zero.network.petgarden.util.show
import zero.network.petgarden.util.toText

/**
 * @author CarlosEduardoL
 */
class NameRegisterFragment(
    private val user: User,
    private val listener: OnNextListener
) : Fragment() {

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View = FragmentRegisterNameBinding.inflate(inflater, container, false).apply {
        nameInput.setText(user.name)
        nextButton.onClick {
            nameInput.text.toString().let {name ->
                if (name.isNotEmpty()) {
                    user.name = name
                    lastNameInput.toText().let {lastName ->
                        if (lastName.isNotEmpty()) {
                            user.lastName = lastName
                            listener.next(this@NameRegisterFragment, user)
                        }
                    }
                } else {
                    show(getString(R.string.field_error))
                }
            }
        }
    }.root

}
