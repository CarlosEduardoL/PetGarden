package zero.network.petgarden.tools

import androidx.room.Database
import androidx.room.RoomDatabase

@Database(entities = [ImgReg::class], version = 1)
abstract class ImgRegDatabase : RoomDatabase() {
    abstract fun imgRegDao(): ImgRegDao
}