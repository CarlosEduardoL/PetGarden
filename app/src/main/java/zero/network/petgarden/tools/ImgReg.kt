package zero.network.petgarden.tools

import androidx.room.*
import java.util.*

@Entity
data class ImgReg(
    @PrimaryKey val id: String,
    @ColumnInfo(name = "date") val date: Date
)

@Dao
interface ImgRegDao {
    @Insert
    fun insert(imgReg: ImgReg)

    @Query("Select * from imgreg where id = (:id)")
    fun get(id: String): ImgReg
}

@Database(entities = [ImgReg::class], version = 1)
abstract class AppDatabase : RoomDatabase() {
    abstract fun imgRegDao(): ImgRegDao
}
