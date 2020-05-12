package zero.network.petgarden.model.entity

import android.graphics.Bitmap
import com.google.firebase.database.FirebaseDatabase
import zero.network.petgarden.model.behaivor.Entity
import zero.network.petgarden.model.behaivor.ISitter
import zero.network.petgarden.model.behaivor.IUser
import zero.network.petgarden.tools.downloadImage
import zero.network.petgarden.tools.uploadImage
import zero.network.petgarden.util.saveURLImageOnFile
import zero.network.petgarden.util.wait
import java.io.Serializable

data class Sitter(
    private val user: User = User(),
    override var id: String = "",
    var rating: Double = 0.0,
    var kindPets: String = "Nothing Especial",
    var additional: String = "Nothing Especial",
    var planner: Planner = Planner()
) : IUser by user, Serializable, Entity, ISitter {

    val availability: Duration?
        get() {
            return planner.availabilities.min()
        }

    private var _clients: MutableSet<Owner>? = null
    private var _pets: MutableSet<Pet>? = null

    override suspend fun clients(): Set<Owner> {
        _clients?.let {
            return it
        }
        downloadOwners()
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
        _pets = query.wait().children.map { it.getValue(Pet::class.java)!! }.toMutableSet()
    }

    private suspend fun downloadOwners(){
        val query = FirebaseDatabase.getInstance().reference
            .child(Owner.FOLDER).orderByChild("sitterList").equalTo(id)
        _clients = query.wait().children.map { it.getValue(Owner::class.java)!! }.toMutableSet()
    }

    suspend fun image(): Bitmap {
        imageURL?.let {
            uploadImage(saveURLImageOnFile(it, "temp.png"))
            imageURL = null
            saveInDB()
        }
        return downloadImage()
    }

    override fun folder() = FOLDER

    companion object {
        const val FOLDER = "sitters"
    }

}
