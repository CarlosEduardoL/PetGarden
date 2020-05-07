package zero.network.petgarden.model.entity

import android.graphics.Bitmap
import android.graphics.BitmapFactory
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ValueEventListener
import zero.network.petgarden.model.behaivor.Entity
import zero.network.petgarden.model.behaivor.IOwner
import zero.network.petgarden.model.behaivor.IUser
import zero.network.petgarden.tools.downloadImage
import zero.network.petgarden.util.saveURLImageOnFile
import java.io.Serializable
import kotlin.coroutines.resume
import kotlin.coroutines.resumeWithException
import kotlin.coroutines.suspendCoroutine

data class Owner(
    private val user: User = User(),
    val sitterList: MutableList<String> = mutableListOf()
): IUser by user, Serializable, Entity, IOwner {

    private var _pets : MutableSet<Pet>? = null
    private var _sitter: MutableSet<Sitter>? = null

    suspend fun image(): Bitmap {
        imageURL?.let {
            return BitmapFactory.decodeFile(saveURLImageOnFile(it, "$id.png").path)
                .apply { Bitmap.createScaledBitmap(this, width/4, height/4,false) }
        }
        return downloadImage()
    }

    override fun folder() = FOLDER

    override suspend fun pets(): Set<Pet> {
        _pets?.let {
            return it
        }
        downloadPets()
        return pets()
    }

    override suspend fun addPet(pet: Pet): Boolean{
        pet.ownerID = id
        _pets?.let {
            return if(it.add(pet)){
                pet.saveInDB(); true
            }else false
        }
        downloadPets()
        return addPet(pet)
    }

    override suspend fun petXSitters(): List<Pair<Pet, Sitter>> {
        _sitter?.let {
            return pets().asSequence()
                .filter { pet -> pet.sitterID != null }
                .map { pet -> pet to it.first { it.id == pet.sitterID } }
                .toList()
        }
        downloadSitters()
        return petXSitters()
    }

    private suspend fun downloadPets(){
        val query = FirebaseDatabase.getInstance().reference
            .child(Pet.FOLDER).orderByChild("ownerID").equalTo(id)
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

    private suspend fun downloadSitters(){
        val query = FirebaseDatabase.getInstance().reference
            .child(Sitter.FOLDER).orderByChild("clients").equalTo(id)
        _sitter = suspendCoroutine { continuation ->
            query.addListenerForSingleValueEvent(object : ValueEventListener {
                override fun onCancelled(e: DatabaseError) =
                    continuation.resumeWithException(e.toException())
                override fun onDataChange(data: DataSnapshot) = continuation.resume(mutableSetOf<Sitter>().apply{
                    addAll(data.children.map { it.getValue(Sitter::class.java)!! })
                })
            })
        }!!
    }

    companion object {
        const val FOLDER = "owners"
    }
}

