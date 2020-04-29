package zero.network.petgarden.ui.register.pet

import android.Manifest
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.app.ActivityCompat
import androidx.fragment.app.Fragment
import zero.network.petgarden.databinding.FragmentPetPicBinding
import zero.network.petgarden.model.entity.Pet
import zero.network.petgarden.util.onClick

class PetPicFragment(private val listener: OnNextListener, private val pet: Pet) : Fragment() {

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View = FragmentPetPicBinding.inflate(inflater, container, false).apply {
        cameraButton.onClick {
            ActivityCompat.requestPermissions(activity!!, arrayOf(
                Manifest.permission.CAMERA,
                Manifest.permission.WRITE_EXTERNAL_STORAGE,
                Manifest.permission.READ_EXTERNAL_STORAGE
            ), REQUEST_PERMISSION_CODE)
        }
    }.root

    companion object {
        private const val REQUEST_PERMISSION_CODE = 0
    }
}