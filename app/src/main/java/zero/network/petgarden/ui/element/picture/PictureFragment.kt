package zero.network.petgarden.ui.element.picture

import android.app.Activity.RESULT_OK
import android.content.Intent
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.net.Uri
import android.os.Bundle
import android.provider.MediaStore
import android.provider.MediaStore.ACTION_IMAGE_CAPTURE
import android.provider.MediaStore.EXTRA_OUTPUT
import android.view.LayoutInflater
import android.view.View
import android.view.View.VISIBLE
import android.view.ViewGroup
import androidx.appcompat.app.AlertDialog
import androidx.fragment.app.Fragment
import com.theartofdev.edmodo.cropper.CropImage
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers.IO
import kotlinx.coroutines.NonCancellable
import kotlinx.coroutines.launch
import zero.network.petgarden.R
import zero.network.petgarden.databinding.FragmentPictureBinding
import zero.network.petgarden.model.behaivor.Entity
import zero.network.petgarden.tools.appRoot
import zero.network.petgarden.tools.copy
import zero.network.petgarden.tools.uploadImage
import zero.network.petgarden.util.fileToUri
import zero.network.petgarden.util.getPath
import zero.network.petgarden.util.onClick
import java.io.File


class PictureFragment(
    private val listener: PictureListener,
    private val entity: Entity,
    private val tittle: String
) : Fragment() {

    private lateinit var binding: FragmentPictureBinding
    private val imageFile = File(appRoot(), "temp")

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View = FragmentPictureBinding.inflate(inflater, container, false).apply {
        binding = this
        title.text = tittle
        cameraButton.onClick {
            val options = arrayOf(getString(R.string.takePhoto), getString(R.string.chooseFromGallery))
            AlertDialog.Builder(context!!)
                .setTitle(getString(R.string.choose_picture))
                .setItems(options) { _, item ->
                    when {
                        options[item] == getString(R.string.takePhoto) -> startActivityForResult(
                            Intent(ACTION_IMAGE_CAPTURE).putExtra(EXTRA_OUTPUT, activity!!.fileToUri(imageFile)),
                            CAMERA_INTENT
                        )
                        options[item] == getString(R.string.chooseFromGallery) -> startActivityForResult(
                            Intent(Intent.ACTION_PICK, MediaStore.Images.Media.EXTERNAL_CONTENT_URI),
                            GALLERY_INTENT
                        )
                    }
                }
                .show()
        }
        nextButton.onClick {
            CoroutineScope(IO + NonCancellable).launch { entity.uploadImage(imageFile) }
            listener.onPictureCaptured(entity)
        }
    }.root

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        if (requestCode == CropImage.CROP_IMAGE_ACTIVITY_REQUEST_CODE) {
            val result = CropImage.getActivityResult(data)
            if (resultCode == RESULT_OK) {
                val uri = result.uri
                val file = File(getPath(context!!, uri!!)!!)
                copy(file,imageFile)
                binding.apply {
                    picture.visibility = VISIBLE
                    picture.setImageBitmap(
                        BitmapFactory.decodeFile(imageFile.path)
                            .let { Bitmap.createScaledBitmap(it, it.width / 4, it.height / 4, false) })
                    nextButton.visibility = VISIBLE
                }
            } else if (resultCode == CropImage.CROP_IMAGE_ACTIVITY_RESULT_ERROR_CODE) {
                throw result.error
            }
        }
        val uri = if (requestCode == CAMERA_INTENT && resultCode == RESULT_OK) {
            Uri.fromFile(imageFile)
        } else if (requestCode == GALLERY_INTENT && resultCode == RESULT_OK && data != null) {
            data.data!!
        }else null

        uri?.let {
            CropImage.activity(it)
                .setAutoZoomEnabled(true)
                .setAspectRatio(1,1)
                .setMinCropResultSize(300,300)
                .setMaxCropResultSize(500,500)
                .start(context!!, this)
        }
    }

    companion object {
        const val CAMERA_INTENT = 290
        const val GALLERY_INTENT = 291
    }

}