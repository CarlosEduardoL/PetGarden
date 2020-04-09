package zero.network.petgarden.ui.register.user

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import kotlinx.android.synthetic.main.fragment_register_name.view.*

import zero.network.petgarden.R
import zero.network.petgarden.model.entity.User
import zero.network.petgarden.ui.register.OnNextListener
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
    ): View = inflater.inflate(R.layout.fragment_register_name, container, false).apply {
        nameInput.setText(user.name)
        nextButton.setOnClickListener {
            nameInput.text.toString().let {name ->
                if (name.isNotEmpty()) {
                    user.name = name
                    lastNameInput.toText().let {lastName ->
                        if (lastName.isNotEmpty()) {
                            user.lastName = lastName
                            listener.next(this@NameRegisterFragment)
                        }
                    }
                } else {
                    show(getString(R.string.field_error))
                }
            }
        }
    }

}
