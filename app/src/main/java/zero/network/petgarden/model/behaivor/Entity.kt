package zero.network.petgarden.model.behaivor

import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.FirebaseDatabase
import zero.network.petgarden.model.entity.Pet

/**
 * @author CarlosEduardoL
 * This class represent an entity in the DB
 */
interface Entity {

    /**
     * Is the uuid of the entity
     */
    var id: String

    /**
     * is the DB branch where the entity will be saved
     */
    fun folder(): String

    /**
     * upload the entity to the DB
     */
    fun saveInDB() {
        if ( this is Pet || id == FirebaseAuth.getInstance().currentUser?.uid)
            FirebaseDatabase.getInstance().reference.child(folder()).child(id).setValue(this)
        else
            println(this)
    }

}