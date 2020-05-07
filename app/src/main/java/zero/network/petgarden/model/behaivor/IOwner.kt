package zero.network.petgarden.model.behaivor

import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers.Main
import kotlinx.coroutines.launch
import zero.network.petgarden.model.entity.Pet
import zero.network.petgarden.model.entity.Sitter

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
     * Add a new pet to the owner
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
    suspend fun petXSitters(): List<Pair<Pet, Sitter>>


    /**
     * java call of [petXSitters]
     */
    fun petXSitters(callBack: CallBack<List<Pair<Pet, Sitter>>>) = CoroutineScope(Main).launch {
        callBack.onResult(petXSitters())
    }

}