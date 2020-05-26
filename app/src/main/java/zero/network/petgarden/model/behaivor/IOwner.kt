package zero.network.petgarden.model.behaivor

import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers.Main
import kotlinx.coroutines.launch
import zero.network.petgarden.model.entity.Pet
import zero.network.petgarden.model.entity.SitterIMP

/**
 * @author CarlosEduardoL
 * Interface that allows kotlin suspend methods from owner called in java environment
 */
interface IOwner {

    /**
     * @return the [IOwner]'s [Pet]s
     */
    suspend fun pets(): Set<Pet>

    /**
     * Get the [IOwner]'s [Pet]s and send it by callback
     */
    fun pets(callBack: CallBack<Set<Pet>>) = CoroutineScope(Main).launch {
        callBack.onResult(pets())
    }

    /**
     * Add a new [Pet] to the owner
     * if the pet is already added returns false
     */
    suspend fun addPet(pet: Pet): Boolean

    /**
     * java call of [addPet]
     */
    fun addPet(pet: Pet, callBack: CallBack<Boolean>) = CoroutineScope(Main).launch {
        callBack.onResult(addPet(pet))
    }

    /**
     * Return a list whit the owner pets whit sitters whit their sitters
     */
    suspend fun petXSitters(): List<Pair<Pet, SitterIMP>>


    /**
     * java call of [petXSitters]
     */
    fun petXSitters(callBack: CallBack<List<Pair<Pet, SitterIMP>>>) = CoroutineScope(Main).launch {
        callBack.onResult(petXSitters())
    }

}