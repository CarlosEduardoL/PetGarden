package zero.network.petgarden.ui.register

import android.app.Activity
import android.content.Intent
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.os.Bundle
import android.provider.MediaStore.ACTION_IMAGE_CAPTURE
import android.provider.MediaStore.EXTRA_OUTPUT
import android.view.LayoutInflater
import android.view.View
import android.view.View.VISIBLE
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers.IO
import kotlinx.coroutines.NonCancellable
import kotlinx.coroutines.launch
import zero.network.petgarden.databinding.FragmentPictureBinding
import zero.network.petgarden.model.behaivor.Entity
import zero.network.petgarden.tools.appRoot
import zero.network.petgarden.tools.uploadImage
import zero.network.petgarden.util.fileToUri
import zero.network.petgarden.util.onClick
import java.io.File

class PictureFragment(private val listener: PictureListener, private val entity: Entity, private val tittle: String) : Fragment() {

    private lateinit var binding: FragmentPictureBinding
    private val imageFile = File(appRoot(), "temp")

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View = FragmentPictureBinding.inflate(inflater, container, false).apply {
        binding = this
        title.text = tittle
        cameraButton.onClick {
            val root = imageFile
            startActivityForResult(
                Intent(ACTION_IMAGE_CAPTURE).apply {
                    putExtra(
                        EXTRA_OUTPUT, activity!!.fileToUri(root)
                    )
                },
                CAMERA_INTENT
            )
        }
        nextButton.onClick {
            CoroutineScope(IO + NonCancellable).launch { entity.uploadImage(imageFile) }
            listener.onPictureCaptured(entity)
        }
    }.root

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        if (requestCode == CAMERA_INTENT && resultCode == Activity.RESULT_OK) {
            binding.apply {
                picture.visibility = VISIBLE
                picture.setImageBitmap(
                    BitmapFactory.decodeFile(imageFile.path)
                        .let { Bitmap.createScaledBitmap(it, it.width / 4, it.height / 4, false) })
                nextButton.visibility = VISIBLE
            }
        }
    }

    companion object {
        const val CAMERA_INTENT = 290
    }

}