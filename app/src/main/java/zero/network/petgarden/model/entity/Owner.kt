package zero.network.petgarden.model.entity

import android.graphics.Bitmap
import com.google.firebase.database.FirebaseDatabase
import zero.network.petgarden.model.behaivor.Entity
import zero.network.petgarden.model.behaivor.IOwner
import zero.network.petgarden.model.behaivor.IUser
import zero.network.petgarden.tools.downloadImage
import zero.network.petgarden.util.wait
import java.io.Serializable

/**
 * @author CarlosEduardoL
 */
data class Owner(
    private val user: User = User(),
    override var id: String = "Default Value",
    var sitterList: MutableMap<String, String> = mutableMapOf(),
    override var debug: String = ""
) : IUser by user, Serializable, Entity, IOwner {

    private var _pets: Set<Pet>? = null
    private var _sitter: MutableSet<SitterIMP>? = null

    suspend fun image(): Bitmap = downloadImage()

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
            _pets = it.filter { it.id != pet.id }.toSet() + pet
            pet.saveInDB(
                "Called By ${this::class.java.name} in line ${Throwable().stackTrace[0]
                    .lineNumber}"
            )
            return true
        }
        downloadPets()
        return addPet(pet)
    }

    override suspend fun petXSitters(): List<Pair<Pet, SitterIMP>> {
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
        _pets = query.wait().children.map { it.getValue(Pet::class.java)!! }.toSet()
    }

    private suspend fun downloadSitters() {
        _sitter = FirebaseDatabase.getInstance().reference
            .child(SitterIMP.FOLDER).orderByChild("clients").orderByValue().equalTo(id)
            .wait().children.map { it.getValue(SitterIMP::class.java)!! }.toMutableSet()
    }

    companion object {
        const val FOLDER = "owners"
    }
}

