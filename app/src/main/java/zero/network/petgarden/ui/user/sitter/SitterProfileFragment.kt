package zero.network.petgarden.ui.user.sitter

import android.app.Activity
import android.content.Intent
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import com.facebook.AccessToken
import com.google.android.gms.auth.api.signin.GoogleSignIn
import kotlinx.android.synthetic.main.fragment_sitter_profile.*
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import zero.network.petgarden.R
import zero.network.petgarden.databinding.FragmentSitterProfileBinding
import zero.network.petgarden.tools.uploadImage
import zero.network.petgarden.ui.user.owner.ChangePassFragment
import zero.network.petgarden.ui.user.owner.ChangePasswordFragment
import zero.network.petgarden.util.getPath
import java.io.File

class SitterProfileFragment(view: SitterView) : Fragment(), SitterView by view {

    private lateinit var adapter: CustomersAdapter
    private lateinit var changePasswordFragment: ChangePassFragment

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View = FragmentSitterProfileBinding.inflate(inflater, container, false).apply {
        changePasswordFragment = ChangePassFragment(sitter)

        CoroutineScope(Dispatchers.Main).launch { adapter = CustomersAdapter(sitter.clientsXPets(), context!!)
        val x   = sitter.clientsXPets()
            println("numero de clientes: ${x.keys}  Numero de pets: ${x.values}")
        }
        CoroutineScope(Dispatchers.Main).launch { photoSitterIV.setImageBitmap(sitter.image()) }
        nameSitterTV.text = sitter.name
        emailSitterTV.text = sitter.email

        CoroutineScope(Dispatchers.Main).launch {println("-------Clientes sitter view = ${sitter.clients().size}")}

        if (AccessToken.getCurrentAccessToken()!=null || GoogleSignIn.getLastSignedInAccount(context)!=null) {
            changePasswordBtn.visibility = View.GONE
            println("---------------------Auth vigente-------------------------")
        }


        cameraBtn.setOnClickListener{
            val gal = Intent(Intent.ACTION_GET_CONTENT)
            gal.type = "image/*"
            startActivityForResult(gal, GALLERY_CALLBACK)
        }

        changePasswordBtn.setOnClickListener{
            val fragmentManager = activity!!.supportFragmentManager
            val fragmentTransaction = fragmentManager!!.beginTransaction()
            fragmentTransaction.replace(R.id.actualFragmentContainerSitter, changePasswordFragment).addToBackStack(null)
            fragmentTransaction.commit()
        }
    }.root


    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        if (requestCode == GALLERY_CALLBACK && resultCode == Activity.RESULT_OK) {
            val uri = data!!.data
            val file = File(getPath(context!!, uri!!)!!)

            CoroutineScope(Dispatchers.Main).launch {
                sitter.uploadImage(file)
                photoSitterIV.setImageBitmap(sitter.image())
            }
        }
    }

    companion object{
        const val GALLERY_CALLBACK = 1
        const val PET_CALLBACK = 2
    }
}