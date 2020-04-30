package zero.network.petgarden.tools

import android.content.Context
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.net.Uri
import androidx.room.Room
import com.google.firebase.storage.FirebaseStorage
import com.google.firebase.storage.StorageMetadata
import kotlinx.coroutines.Dispatchers.IO
import kotlinx.coroutines.tasks.await
import kotlinx.coroutines.withContext
import zero.network.petgarden.ui.login.LoginActivity
import zero.network.petgarden.util.saveURLImageOnFile
import java.io.File
import java.io.FileInputStream


private lateinit var db: ImgRegDatabase
private lateinit var appContext: Context

fun LoginActivity.initDatabase() {
    appContext = applicationContext
    db = Room.databaseBuilder(
        appContext,
        ImgRegDatabase::class.java,
        "regDataBase"
    ).build()
}

fun isCached(id: String) = db.imgRegDao().get(id) !== null

/**
 * Upload Image to [FirebaseStorage] and but conserve a local copy
 * and save the register in the [db]
 * @param image is always the id from the entity that save it
 * @param folder is the folder on [FirebaseStorage]
 */
suspend fun uploadImage(image: File, folder: String) = withContext(IO) {
    val fStorage = FirebaseStorage.getInstance().reference
    if (image.exists() && image.isFile) {
        val file = Uri.fromFile(image)

        fStorage.child("img/$folder/${file.lastPathSegment}").putFile(file).await()
        val metadata = fStorage.child("img/$folder/${file.lastPathSegment}").metadata.await()
        val time = metadata.creationTimeMillis
        val imgReg = ImgReg(image.name, time)
        db.imgRegDao().insert(imgReg)
    }
}

/**
 * Download Image from [FirebaseStorage] if is necessary
 * if any copy exist locally, it's used
 * @param id is the entity [id] and image name
 * @param folder is the folder name and entity type
 * @return [Bitmap] with the wanted image
 */
suspend fun downloadImage(id: String, folder: String): Bitmap = BitmapFactory.decodeFile(
    withContext(IO) {
        val reg = db.imgRegDao().get(id)
        val fStorage = FirebaseStorage.getInstance().reference
        val file = File("${appContext.getExternalFilesDir(null)}/$id.png")
        val metadata = fStorage.child("img/$folder/$id.png").metadata.await()
        if (reg !== null) {
            if (metadata.creationTimeMillis == reg.date && file.exists()) {
                return@withContext file
            }
        }
        val url = fStorage.child("img/$folder/$id.png").downloadUrl.await()
        saveURLImageOnFile(url.toString(), file)
        db.imgRegDao().insert(ImgReg(id, metadata.creationTimeMillis))

        return@withContext file
    }.path
).let { Bitmap.createScaledBitmap(it, it.width/4, it.height/4,false) }

