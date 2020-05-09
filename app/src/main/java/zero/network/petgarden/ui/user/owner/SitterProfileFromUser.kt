package zero.network.petgarden.ui.user.owner

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import kotlinx.android.synthetic.main.fragment_sitter_profile_from_user.view.*
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import zero.network.petgarden.R
import zero.network.petgarden.model.entity.Sitter

class SitterProfileFromUser(private val sitter:Sitter) : Fragment() {
    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View = inflater.inflate(R.layout.fragment_sitter_profile_from_user, container, false).apply {

        //hacer un metodo init con todas los atributos
        CoroutineScope(Dispatchers.Main).launch { photoSitter.setImageBitmap(sitter.image()) }
        nameSitterTxt.setText(sitter.name)
        emailSitterTxt.setText(sitter.email)

        //Hacer un metodo en sitter que me tire la disponibilidad horario con la hora de inicio y la final
    }

}