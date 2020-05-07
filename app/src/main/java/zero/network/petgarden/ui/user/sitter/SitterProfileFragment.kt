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
import kotlinx.android.synthetic.main.fragment_sitter_profile.view.*
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import zero.network.petgarden.R
import zero.network.petgarden.model.entity.Owner
import zero.network.petgarden.model.entity.Pet
import zero.network.petgarden.model.entity.Sitter
import zero.network.petgarden.tools.uploadImage
import zero.network.petgarden.ui.user.owner.ChangePasswordFragment
import zero.network.petgarden.util.getPath
import java.io.File

class SitterProfileFragment : Fragment() {

    private val activity:SitterActivity = getActivity() as SitterActivity
    private val sitter = activity.sitter
    private lateinit var adapter:ClientsAdapter

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View = inflater.inflate(R.layout.fragment_sitter_profile, container, false).apply {

        var ownersPets: Map<Owner, Set<Pet>>  = HashMap<Owner, Set<Pet>>()
        CoroutineScope(Dispatchers.Main).launch { ownersPets = sitter.clientsXPets()}
        adapter = ClientsAdapter(ownersPets)

        CoroutineScope(Dispatchers.Main).launch { photoSitterIV.setImageBitmap(sitter.image()) }
        nameSitterTV.setText(sitter.name)
        emailSitterTV.setText(sitter.email)

        if (AccessToken.getCurrentAccessToken()!=null || GoogleSignIn.getLastSignedInAccount(context)!=null)
            changePasswordBtn.setVisibility(View.GONE)

        cameraBtn.setOnClickListener{

            val gal = Intent(Intent.ACTION_GET_CONTENT)
            gal.type = "image/*"
            startActivityForResult(gal, GALLERY_CALLBACK)
        }

        changePasswordBtn.setOnClickListener{
            val changePasswordFragment = ChangePasswordFragment()
            val fragmentManager = activity.fragmentManager
            val fragmentTransaction = fragmentManager!!.beginTransaction()
            fragmentTransaction.add(R.id.activity_owner_container, changePasswordFragment)
            fragmentTransaction.commit()
        }
    }


    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        if (requestCode == GALLERY_CALLBACK && resultCode == Activity.RESULT_OK) {
            val uri = data!!.data
            val file = File(getPath(context!!, uri!!))

            CoroutineScope(Dispatchers.Main).launch { sitter.uploadImage(file)}
            CoroutineScope(Dispatchers.Main).launch { photoSitterIV.setImageBitmap(sitter.image()) }
        }
    }

    companion object{
        val GALLERY_CALLBACK = 1
        val PET_CALLBACK = 2
    }

    fun getSitter(): Sitter {return sitter}

}