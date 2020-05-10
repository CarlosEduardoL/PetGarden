package zero.network.petgarden.tools

import android.content.Context
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.net.Uri
import androidx.room.Room
import com.google.firebase.storage.FirebaseStorage
import kotlinx.coroutines.Dispatchers.IO
import kotlinx.coroutines.tasks.await
import kotlinx.coroutines.withContext
import zero.network.petgarden.model.behaivor.Entity
import zero.network.petgarden.model.entity.Owner
import zero.network.petgarden.model.entity.Pet
import zero.network.petgarden.model.entity.Sitter
import zero.network.petgarden.ui.login.LoginActivity
import zero.network.petgarden.util.saveURLImageOnFile
import java.io.File
import java.io.FileInputStream
import java.io.FileOutputStream
import java.io.IOException


private lateinit var db: ImgRegDatabase
lateinit var appContext: Context

fun appRoot() = appContext.getExternalFilesDir(null)


fun LoginActivity.initDatabase() {
    appContext = applicationContext
    File(appRoot(), Pet.FOLDER).let { if (!it.exists()) it.mkdir() }
    File(appRoot(), Sitter.FOLDER).let { if (!it.exists()) it.mkdir() }
    File(appRoot(),Owner.FOLDER).let { if (!it.exists()) it.mkdir() }
    db = Room.databaseBuilder(
        appContext,
        ImgRegDatabase::class.java,
        "regDataBase"
    ).build()
}

@Throws(IOException::class)
fun copy(src: File, dst: File) {
    FileInputStream(src).use { `in` ->
        FileOutputStream(dst).use { out ->
            val buf = ByteArray(1024)
            var len: Int
            while (`in`.read(buf).also { len = it } > 0) {
                out.write(buf, 0, len)
            }
        }
    }
}

/**
 * Upload Image to [FirebaseStorage] and but conserve a local copy
 * and save the register in the [db]
 */
suspend fun Entity.uploadImage(temp: File) = withContext(IO) {
    val image = File(appRoot(),"${folder()}/$id.png")
    if (temp.renameTo(image)) temp.delete()
    val fStorage = FirebaseStorage.getInstance().reference
    if (image.exists() && image.isFile) {
        val file = Uri.fromFile(image)

        fStorage.child("${folder()}/$id.png").putFile(file).await()
        val metadata = fStorage.child("${folder()}/$id.png").metadata.await()
        val time = metadata.creationTimeMillis
        val imgReg = ImgReg(image.name, time)
        db.imgRegDao().insert(imgReg)
    }
}

/**
 * Download Image from [FirebaseStorage] if is necessary
 * if any copy exist locally, it's used
 * @return [Bitmap] with the wanted image
 */
suspend fun Entity.downloadImage(): Bitmap = BitmapFactory.decodeFile(
         downloadImageFile().path
).let { Bitmap.createScaledBitmap(it, it.width/4, it.height/4,false) }

private suspend fun Entity.downloadImageFile(): File = withContext(IO){
    val reg = db.imgRegDao().get(id)
    val fStorage = FirebaseStorage.getInstance().reference
    val file = File(appRoot(),"${folder()}/$id.png")
    val metadata = fStorage.child("${folder()}/$id.png").metadata.await()
    if (reg !== null) {
        if (metadata.creationTimeMillis == reg.date && file.exists()) {
            return@withContext file
        }
    }
    val url = fStorage.child("${folder()}/$id.png").downloadUrl.await()
    saveURLImageOnFile(url.toString(), "${folder()}/$id.png")
    db.imgRegDao().insert(ImgReg(id, metadata.creationTimeMillis))
    return@withContext file
}
