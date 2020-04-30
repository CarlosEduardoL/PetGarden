package zero.network.petgarden.ui.register.pet

import android.content.Intent
import android.graphics.Bitmap
import android.os.Bundle
import android.provider.MediaStore.ACTION_IMAGE_CAPTURE
import android.provider.MediaStore.EXTRA_OUTPUT
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import zero.network.petgarden.databinding.FragmentPetPicBinding
import zero.network.petgarden.model.entity.Pet
import zero.network.petgarden.ui.register.pet.PetRegisterActivity.Companion.CAMERA_INTENT
import zero.network.petgarden.util.fileToUri
import zero.network.petgarden.util.onClick
import java.io.File

class PetPicFragment(private val listener: OnNextListener, private val pet: Pet, private val image: Bitmap? = null) : Fragment() {

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View = FragmentPetPicBinding.inflate(inflater, container, false).apply {
        image?.let {
            petPic.setImageBitmap(image)
            nextButton.visibility = View.VISIBLE
        }
        cameraButton.onClick {
            val root = imageDir()
            startActivityForResult( Intent(ACTION_IMAGE_CAPTURE).apply { putExtra(
                EXTRA_OUTPUT, activity!!.fileToUri(root)) }, CAMERA_INTENT )

        }
    }.root

    fun imageDir() = File("${activity!!.getExternalFilesDir(null)}/${pet.id}.png")

}