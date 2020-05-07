package zero.network.petgarden.model.entity

import android.graphics.Bitmap
import android.graphics.BitmapFactory
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ValueEventListener
import zero.network.petgarden.model.behaivor.Entity
import zero.network.petgarden.model.behaivor.IPlanner
import zero.network.petgarden.model.behaivor.IUser
import zero.network.petgarden.tools.downloadImage
import zero.network.petgarden.util.saveURLImageOnFile
import java.io.Serializable
import kotlin.coroutines.resume
import kotlin.coroutines.resumeWithException
import kotlin.coroutines.suspendCoroutine

data class Sitter(
    private val user: User = User(),
    var rating: Double = 0.0,
    var kindPets: String = "Nothing Especial",
    var additional: String = "Nothing Especial",
    val clients: MutableList<String> = mutableListOf(),
    private val planner: Planner = Planner()
) : IUser by user, IPlanner by planner, Serializable, Entity {

    val availability: Duration?
        get() = planner.availabilities.min()

    suspend fun image(): Bitmap {
        imageURL?.let {
            return BitmapFactory.decodeFile(saveURLImageOnFile(it, "$id.png").path)
                .apply { Bitmap.createScaledBitmap(this, width/4, height/4,false) }
        }
        return downloadImage()
    }

    override fun folder() = FOLDER

    companion object {
        const val FOLDER = "sitters"
    }

}
