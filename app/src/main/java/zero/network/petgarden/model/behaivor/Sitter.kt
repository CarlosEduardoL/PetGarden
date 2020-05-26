package zero.network.petgarden.model.behaivor

import android.graphics.Bitmap
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers.Main
import kotlinx.coroutines.launch
import zero.network.petgarden.model.component.Planner
import zero.network.petgarden.model.entity.Owner
import zero.network.petgarden.model.entity.Pet
import java.io.Serializable

/**
 * @author CarlosEduardoL
 * Interface that allows kotlin suspend methods from sitter called in java environment
 */
interface Sitter: Serializable, Entity, IUser {

    suspend fun image(): Bitmap

    suspend fun clients(): Set<Owner>

    suspend fun pets(): Set<Pet>

    suspend fun clientsXPets(): Map<Owner, Set<Pet>>

    var rating: Double
    var kindPets: String
    var planner: Planner


    // Java Versions

    fun clients(callBack: CallBack<Set<Owner>>) = CoroutineScope(Main).launch { callBack.onResult(clients()) }

    fun pets(callBack: CallBack<Set<Pet>>) = CoroutineScope(Main).launch { callBack.onResult(pets()) }

    fun clientsXPets(callBack: CallBack<Map<Owner,Set<Pet>>>) = CoroutineScope(Main).launch { callBack.onResult(clientsXPets()) }

}