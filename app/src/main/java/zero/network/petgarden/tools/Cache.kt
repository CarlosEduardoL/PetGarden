package zero.network.petgarden.tools

import android.content.Context
import android.net.Uri
import androidx.room.Room
import com.google.firebase.storage.FirebaseStorage
import com.google.firebase.storage.StorageMetadata
import kotlinx.coroutines.Dispatchers.IO
import kotlinx.coroutines.withContext
import zero.network.petgarden.ui.login.LoginActivity
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

/**
 * Upload Image to [FirebaseStorage] and but conserve a local copy
 * and save the register in the [db]
 * @param image is always the id from the entity that save it
 */
suspend fun uploadImage(image: File) = withContext(IO) {
    val fStorage = FirebaseStorage.getInstance()
    if (image.exists() && image.isFile) {
        val file = Uri.fromFile(image)

        fStorage.reference.child("img/${file.lastPathSegment}").putFile(file)
            .addOnSuccessListener {
                fStorage.reference.child("img/${file.lastPathSegment}").metadata.addOnSuccessListener {
                    val time = it.creationTimeMillis
                    val imgReg = ImgReg(image.name, time)
                    db.imgRegDao().insert(imgReg)
                }
            }
    }
}

suspend fun downloadImage(id: String) = withContext(IO){
    val reg = db.imgRegDao().get(id)
    if (reg !== null){
        val file = File("${appContext.getExternalFilesDir(null)}/")
    }
    return@withContext null
}

