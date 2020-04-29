package zero.network.petgarden.tools

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.Query

@Dao
interface ImgRegDao {
    @Insert
    fun insert(imgReg: ImgReg)

    @Query("Select * from imgreg where id = (:id)")
    fun get(id: String): ImgReg
}