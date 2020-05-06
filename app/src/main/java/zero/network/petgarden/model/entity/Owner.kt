package zero.network.petgarden.model.entity

import android.graphics.Bitmap
import android.graphics.BitmapFactory
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ValueEventListener
import zero.network.petgarden.model.behaivor.Entity
import zero.network.petgarden.model.behaivor.IUser
import zero.network.petgarden.tools.downloadImage
import zero.network.petgarden.util.saveURLImageOnFile
import java.io.Serializable
import kotlin.coroutines.resume
import kotlin.coroutines.resumeWithException
import kotlin.coroutines.suspendCoroutine

data class Owner(private val user: User = User()): IUser by user, Serializable, Entity {


    suspend fun image(): Bitmap {
        imageURL?.let {
            return BitmapFactory.decodeFile(saveURLImageOnFile(it, "$id.png").path)
                .apply { Bitmap.createScaledBitmap(this, width/4, height/4,false) }
        }
        return downloadImage()
    }

    override fun folder() = FOLDER

    companion object {
        const val FOLDER = "owners"
        suspend fun ownerByEmail(email: String): Owner = suspendCoroutine{
            val query  = FirebaseDatabase.getInstance().reference
                .child(Sitter.FOLDER).orderByChild("email").equalTo(email)

            query.addListenerForSingleValueEvent(object: ValueEventListener {
                override fun onCancelled(error: DatabaseError) {
                    it.resumeWithException(error.toException())
                }

                override fun onDataChange(dataSnapshot: DataSnapshot) {
                    for (result in dataSnapshot.children) {
                        it.resume(result.getValue(Owner::class.java)!!)
                        break
                    }
                }
            })
        }
    }
}