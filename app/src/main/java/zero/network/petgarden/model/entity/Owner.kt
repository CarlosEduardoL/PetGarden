package zero.network.petgarden.model.entity

import android.graphics.Bitmap
import com.google.firebase.database.FirebaseDatabase
import zero.network.petgarden.model.behaivor.Entity
import zero.network.petgarden.model.behaivor.IOwner
import zero.network.petgarden.model.behaivor.IUser
import zero.network.petgarden.tools.downloadImage
import zero.network.petgarden.tools.uploadImage
import zero.network.petgarden.util.saveURLImageOnFile
import zero.network.petgarden.util.wait
import java.io.Serializable

/**
 * @author CarlosEduardoL
 */
data class Owner(
    private val user: User = User(),
    override var id: String = "",
    val sitterList: MutableList<String> = mutableListOf()
) : IUser by user, Serializable, Entity, IOwner {

    private var _pets: MutableSet<Pet>? = null
    private var _sitter: MutableSet<Sitter>? = null

    suspend fun image(): Bitmap {
        imageURL?.let {
            uploadImage(saveURLImageOnFile(it, "temp.png"))
            imageURL = null
            saveInDB()
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

    override suspend fun addPet(pet: Pet): Boolean {
        pet.ownerID = id
        _pets?.let {
            return if (it.add(pet)) {
                pet.saveInDB(); true
            } else false
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

    private suspend fun downloadPets() {
        val query = FirebaseDatabase.getInstance().reference
            .child(Pet.FOLDER).orderByChild("ownerID").equalTo(id)
        _pets = query.wait().children.map { it.getValue(Pet::class.java)!! }.toMutableSet()
    }

    private suspend fun downloadSitters() {
        _sitter = FirebaseDatabase.getInstance().reference
            .child(Sitter.FOLDER).orderByChild("clients").orderByValue().equalTo(id)
            .wait().children.map { it.getValue(Sitter::class.java)!! }.toMutableSet()
    }

    companion object {
        const val FOLDER = "owners"
    }
}

