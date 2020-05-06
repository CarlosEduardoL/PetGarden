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
import kotlinx.android.synthetic.main.fragment_user_profile.view.*
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import zero.network.petgarden.R
import zero.network.petgarden.model.entity.Owner
import zero.network.petgarden.util.show


class ChangePasswordFragment: Fragment(){

    private val activity:OwnerActivity = getActivity() as OwnerActivity
    private val owner: Owner = activity.owner

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View = inflater.inflate(R.layout.fragment_change_password, container, false).apply {

        nameUserTV.setText(owner.name)
        emailUserTV.setText(owner.email)

        updatePass.setOnClickListener{
           val currentPass =  currentPassTV.text.toString()
            val newPass = newPassTV.text.toString()
                if (currentPass.equals(owner.password)) {
                    if (newPass.isNotEmpty())
                        CoroutineScope(Dispatchers.Main).launch { updatePassword(newPass)}
                    else
                        show("Su nueva contraseña no contiene caracteres")
                } else {
                    show("Contraseña actual incorrecta")
                }
            }
        }


    suspend fun updatePassword(password:String){
        val ref = FirebaseDatabase.getInstance().getReference()
        val ownersRef: DatabaseReference = ref.child("users").child("owners")
        val ownerRef: DatabaseReference = ownersRef.child(owner.id)
        val ownerUpdates: MutableMap<String, Any> = HashMap()
        ownerUpdates["password"] = password

        ownerRef.updateChildren(ownerUpdates)
    }

}