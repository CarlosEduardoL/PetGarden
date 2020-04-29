package zero.network.petgarden.tools

import androidx.room.Room
import kotlinx.coroutines.Dispatchers.IO
import kotlinx.coroutines.withContext
import zero.network.petgarden.ui.login.LoginActivity
import java.io.File

private lateinit var db: ImgRegDatabase

fun LoginActivity.initDatabase() {
    db = Room.databaseBuilder(
        applicationContext,
        ImgRegDatabase::class.java,
        "regDataBase"
    ).build()
}

suspend fun uploadImage(id: String, image: File) = withContext(IO) {

}

