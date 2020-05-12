package zero.network.petgarden.ui.user.owner

import android.app.Activity
import android.content.Intent
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import com.facebook.AccessToken
import com.google.android.gms.auth.api.signin.GoogleSignIn
import kotlinx.android.synthetic.main.fragment_owner_profile.*
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import zero.network.petgarden.R
import zero.network.petgarden.databinding.FragmentOwnerProfileBinding
import zero.network.petgarden.model.behaivor.CallBack
import zero.network.petgarden.model.entity.Pet
import zero.network.petgarden.tools.uploadImage
import zero.network.petgarden.ui.register.pet.PetRegisterActivity
import zero.network.petgarden.util.extra
import zero.network.petgarden.util.getPath
import zero.network.petgarden.util.show
import java.io.File

class OwnerProfileFragment(view: OwnerView) : Fragment(), OwnerView by view {

    lateinit var petsAdapter: PetsAdapter
    private  lateinit var changePasswordFragment:ChangePasswordFragment

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ):  View = FragmentOwnerProfileBinding.inflate(inflater, container, false).apply {

        changePasswordFragment = ChangePasswordFragment(owner)

        CoroutineScope(Dispatchers.Main).launch{
            petsAdapter = PetsAdapter(owner.pets().toList())

            listPets.apply {
                adapter = petsAdapter
            }
        }

        CoroutineScope(Dispatchers.Main).launch {
            photoUserIV.setImageBitmap(owner.image())
        }

        nameUserTV.text = owner.name
        emailUserTV.text = owner.email

        if (AccessToken.getCurrentAccessToken()!=null || GoogleSignIn.getLastSignedInAccount(context)!=null)
            changePasswordBtn.visibility = View.GONE

        cameraBtn.setOnClickListener{

            val gal = Intent(Intent.ACTION_GET_CONTENT)
            gal.type = "image/*"
            startActivityForResult(gal, GALLERY_CALLBACK)
        }

        changePasswordBtn.setOnClickListener{
            val fragmentManager = activity!!.supportFragmentManager
            val fragmentTransaction = fragmentManager.beginTransaction()
            fragmentTransaction.add(R.id.actualFragmentContainer, changePasswordFragment).addToBackStack(null)
            fragmentTransaction.commit()

        }

        addPet.setOnClickListener{
           val intent = Intent(activity, PetRegisterActivity::class.java)
                intent.putExtra(PetRegisterActivity.TITLE_KEY, "Registrar Mascota")
                intent.putExtra(PetRegisterActivity.PET_KEY, Pet())
                startActivityForResult(intent, PET_CALLBACK)
        }
    }.root



    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        if (requestCode == GALLERY_CALLBACK && resultCode == Activity.RESULT_OK) {
            val uri = data!!.data
            val file = File(getPath(context!!, uri!!))

            CoroutineScope(Dispatchers.Main).launch { owner.uploadImage(file)}
            CoroutineScope(Dispatchers.Main).launch { photoUserIV.setImageBitmap(owner.image()) }

        } else if (resultCode == Activity.RESULT_OK && data !== null && requestCode == PET_CALLBACK) {
            owner.apply { addPet(data.extra(PetRegisterActivity.PET_KEY) { return }, CallBack { }) }
            owner.saveInDB()

            show("Su nueva mascota se agregó correctamente")
            CoroutineScope(Dispatchers.Main).launch { petsAdapter.updateListPets(owner.pets().toList()) }
        }
    }



    companion object{
        const val GALLERY_CALLBACK = 1
        const val PET_CALLBACK = 2
    }
}