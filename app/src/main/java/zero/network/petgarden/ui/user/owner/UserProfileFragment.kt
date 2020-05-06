package zero.network.petgarden.ui.user.owner

import android.app.Activity
import android.content.Intent
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import kotlinx.android.synthetic.main.fragment_user_profile.*
import kotlinx.android.synthetic.main.fragment_user_profile.view.*
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import zero.network.petgarden.R
import zero.network.petgarden.model.entity.Owner
import zero.network.petgarden.model.entity.Pet
import zero.network.petgarden.tools.uploadImage
import zero.network.petgarden.ui.register.pet.PetRegisterActivity
import zero.network.petgarden.util.extra
import zero.network.petgarden.util.getPath
import zero.network.petgarden.util.show
import java.io.File

class UserProfileFragment : Fragment() {

    private val activity:OwnerActivity = getActivity() as OwnerActivity
    private val owner:Owner = activity.owner
    private val adapter = PetsAdapter(this)


    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View = inflater.inflate(R.layout.fragment_user_profile, container, false).apply {

        CoroutineScope(Dispatchers.Main).launch { photoUser.setImageBitmap(owner.image()) }
        nameUserTV.setText(owner.name)
        emailUserTV.setText(owner.email)

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

        addPet.setOnClickListener{
           val intent = Intent(getActivity(), PetRegisterActivity::class.java)
                intent.putExtra(PetRegisterActivity.TITLE_KEY, "Registrar Mascota")
                intent.putExtra(PetRegisterActivity.PET_KEY, Pet())
                startActivityForResult(intent, PET_CALLBACK)
        }
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        if (requestCode == GALLERY_CALLBACK && resultCode == Activity.RESULT_OK) {
            val uri = data!!.data
            val file = File(getPath(context!!, uri!!))

            CoroutineScope(Dispatchers.Main).launch { owner.uploadImage(file)}
            CoroutineScope(Dispatchers.Main).launch { photoUser.setImageBitmap(owner.image()) }

        } else if (resultCode == Activity.RESULT_OK && data !== null && requestCode == PET_CALLBACK) {
            owner.apply { pets.add(data.extra(PetRegisterActivity.PET_KEY) { return }) }
            owner.saveInDB()
            show("Su nueva mascota se agregó correctamente")

            activity.runOnUiThread { adapter.notifyDataSetChanged() }
        }
    }


     fun getOwner(): Owner {return owner}


    companion object{
        val GALLERY_CALLBACK = 1
        val PET_CALLBACK = 2
    }
}