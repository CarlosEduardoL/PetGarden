package zero.network.petgarden.ui.register.user.fragments

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import kotlinx.android.synthetic.main.fragment_role_register.view.*
import zero.network.petgarden.R
import zero.network.petgarden.model.behaivor.IUser
import zero.network.petgarden.model.entity.Owner
import zero.network.petgarden.model.entity.SitterIMP
import zero.network.petgarden.model.entity.User
import zero.network.petgarden.ui.register.OnNextListener

class RoleRegisterFragment(
    private val user: User,
    private val listener: OnNextListener<IUser>
) : Fragment() {

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View = inflater.inflate(R.layout.fragment_role_register, container, false).apply {
        ownerButton.setOnClickListener {
            listener.next(this@RoleRegisterFragment, Owner(user))
        }
        sitterButton.setOnClickListener {
            listener.next(this@RoleRegisterFragment, SitterIMP(user))
        }
    }

}
