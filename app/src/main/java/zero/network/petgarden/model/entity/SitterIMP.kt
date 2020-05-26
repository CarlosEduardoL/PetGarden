package zero.network.petgarden.model.entity

import android.graphics.Bitmap
import com.google.firebase.database.FirebaseDatabase
import zero.network.petgarden.model.behaivor.IUser
import zero.network.petgarden.model.behaivor.Sitter
import zero.network.petgarden.model.component.Duration
import zero.network.petgarden.model.component.Planner
import zero.network.petgarden.tools.downloadImage
import zero.network.petgarden.util.times
import zero.network.petgarden.util.wait

data class SitterIMP(
    private val user: User = User(),
    override var id: String = "",
    override var rating: Double = 0.0,
    override var kindPets: String = "Nothing Especial",
    override var debug: String = "Nothing Especial",
    override var planner: Planner = Planner()
) : Sitter, IUser by user {

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
            .child(Pet.FOLDER).orderByChild("sitterID").equalTo(id).wait()
        _pets = query.children.map { it.getValue(Pet::class.java)!! }.toMutableSet()
    }

    private suspend fun downloadOwners(){
        val query = FirebaseDatabase.getInstance().reference
            .child(Owner.FOLDER).orderByChild("sitterList/$id").equalTo(id).wait()
        _clients = query.children.map { it.getValue(Owner::class.java)!! }.toMutableSet()
    }

    operator fun plus(sitterKey: Pair<SitterIMP, String>): zero.network.petgarden.model.entity.SitterIMP {
        val sitter = sitterKey.first
        val a = SitterIMP(
            if(sitter.user.name != "") sitter.user else user,
            if(sitter.id != "")sitter.id else id,
            if(sitter.rating != 0.0)sitter.rating else rating,
            if(sitter.kindPets != "Nothing Especial")sitter.kindPets else kindPets,
            if (sitter.debug != "Nothing Especial") sitter.debug else debug,
            if("planner" == sitterKey.second)sitter.planner else planner
        )
        if("planner" == sitterKey.second) {
            println("${"-"*30}planner${"-"*30}")
            println(sitterKey.first)
            println(a)
        }
        return a
    }

    override suspend fun image(): Bitmap = downloadImage()

    override fun folder() = FOLDER

    companion object {
        const val FOLDER = "sitters"
        private val defaultPlanner = Planner()
    }

}
