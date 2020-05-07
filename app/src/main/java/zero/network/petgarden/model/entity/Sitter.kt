package zero.network.petgarden.model.entity

import android.graphics.Bitmap
import android.graphics.BitmapFactory
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ValueEventListener
import zero.network.petgarden.model.behaivor.Entity
import zero.network.petgarden.model.behaivor.IPlanner
import zero.network.petgarden.model.behaivor.ISitter
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
) : IUser by user, IPlanner by planner, Serializable, Entity, ISitter {

    val availability: Duration?
        get() = planner.availabilities.min()

    private var _clients: MutableSet<Owner>? = null
    private var _pets: MutableSet<Pet>? = null

    override suspend fun clients(): Set<Owner> {
        _clients?.let {
            return it
        }
        downloadPets()
        return clients()
    }

    override suspend fun pets(): Set<Pet> {
        _pets?.let {
            return it
        }
        downloadPets()
        return pets()
    }

    override suspend fun clientsXPets(): Map<Owner, Set<Pet>> {
        return clients().map {
            it to pets().filter { pet -> pet.ownerID == it.id }.toSet()
        }.toMap()
    }

    private suspend fun downloadPets(){
        val query = FirebaseDatabase.getInstance().reference
            .child(Pet.FOLDER).orderByChild("sitterID").equalTo(id)
        _pets = suspendCoroutine { continuation ->
            query.addListenerForSingleValueEvent(object : ValueEventListener {
                override fun onCancelled(e: DatabaseError) =
                    continuation.resumeWithException(e.toException())
                override fun onDataChange(data: DataSnapshot) = continuation.resume(mutableSetOf<Pet>().apply{
                    addAll(data.children.map { it.getValue(Pet::class.java)!! })
                })
            })
        }!!
    }

    private suspend fun downloadOwners(){
        val query = FirebaseDatabase.getInstance().reference
            .child(Owner.FOLDER).orderByChild("sitterList").equalTo(id)
        _clients = suspendCoroutine { continuation ->
            query.addListenerForSingleValueEvent(object : ValueEventListener {
                override fun onCancelled(e: DatabaseError) =
                    continuation.resumeWithException(e.toException())
                override fun onDataChange(data: DataSnapshot) = continuation.resume(mutableSetOf<Owner>().apply{
                    addAll(data.children.map { it.getValue(Owner::class.java)!! })
                })
            })
        }!!
    }

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
