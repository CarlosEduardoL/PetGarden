package zero.network.petgarden.tools

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity
data class ImgReg(
    @PrimaryKey val id: String,
    @ColumnInfo(name = "date") val date: Long
)




