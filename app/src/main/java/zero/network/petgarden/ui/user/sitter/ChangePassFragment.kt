package zero.network.petgarden.ui.user.owner

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import com.google.firebase.database.DatabaseReference
import com.google.firebase.database.FirebaseDatabase
import kotlinx.android.synthetic.main.fragment_change_password.*
import kotlinx.android.synthetic.main.fragment_change_password.emailUserTV
import kotlinx.android.synthetic.main.fragment_change_password.nameUserTV
import kotlinx.android.synthetic.main.fragment_change_password.updatePass
import kotlinx.android.synthetic.main.fragment_owner_profile.view.*
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import zero.network.petgarden.R
import zero.network.petgarden.databinding.FragmentChangePassBinding
import zero.network.petgarden.model.entity.Owner
import zero.network.petgarden.model.entity.Sitter
import zero.network.petgarden.model.entity.User
import zero.network.petgarden.util.show


class ChangePassFragment(val sitter: Sitter): Fragment(){

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View = FragmentChangePassBinding.inflate(inflater, container, false).apply {

        nameUserTV.text = sitter.name
        emailUserTV.text = sitter.email

        updatePass.setOnClickListener{
            val currentPass =  currentPassTV.text.toString()
            val newPass = newPassTV.text.toString()

            if (currentPass == sitter.password) {
                if (newPass.isNotEmpty()) {
                    CoroutineScope(Dispatchers.Main).launch { updatePassword(newPass) }
                    show("Contraseña actualizada correctamente")
                    fragmentManager!!.popBackStack()
                }else
                    show("Su nueva contraseña no contiene caracteres")
            } else {
                show("Contraseña actual incorrecta")
            }
        }
    }.root


    private suspend fun updatePassword(password:String){
        val ref = FirebaseDatabase.getInstance().reference
        val sittersRef: DatabaseReference = ref.child("sitters")
        val sitterRef: DatabaseReference = sittersRef.child(sitter.id)
        val sitterUpdates: MutableMap<String, Any> = HashMap()
        sitterUpdates["password"] = password

        sitterRef.updateChildren(sitterUpdates)
    }

}