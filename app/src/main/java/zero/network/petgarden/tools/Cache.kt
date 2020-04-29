package zero.network.petgarden.tools

import androidx.room.Room
import zero.network.petgarden.ui.login.LoginActivity
import java.io.File

private lateinit var db: AppDatabase

fun LoginActivity.initDatabase() {
    db = Room.databaseBuilder(
        applicationContext,
        AppDatabase::class.java, "database-name"
    ).build()
}

fun uploadImage(id: String, image: File) {

}

