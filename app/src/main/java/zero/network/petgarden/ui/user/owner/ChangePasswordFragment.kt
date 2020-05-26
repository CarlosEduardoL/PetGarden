package zero.network.petgarden.ui.user.owner

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import com.google.firebase.auth.FirebaseAuth
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import zero.network.petgarden.databinding.FragmentChangePasswordBinding
import zero.network.petgarden.model.entity.Owner
import zero.network.petgarden.util.show


class ChangePasswordFragment(val owner: Owner) : Fragment() {

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View = FragmentChangePasswordBinding.inflate(inflater, container, false).apply {

        nameUserTV.text = owner.name
        emailUserTV.text = owner.email
        CoroutineScope(Dispatchers.Main).launch { photoSitterIV.setImageBitmap(owner.image()) }

        updatePass.setOnClickListener {
            val currentPass = currentPassTV.text.toString()
            val newPass = newPassTV.text.toString()
            FirebaseAuth.getInstance().currentUser?.let {
                if (newPass.isNotEmpty()) {
                    CoroutineScope(Dispatchers.Main).launch { it.updatePassword(newPass) }
                    show("Contraseña actualizada correctamente")
                    fragmentManager!!.popBackStack()
                } else
                    show("Su nueva contraseña no contiene caracteres")
            }
        }
    }.root


}