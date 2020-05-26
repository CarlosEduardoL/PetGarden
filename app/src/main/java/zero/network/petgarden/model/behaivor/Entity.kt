package zero.network.petgarden.model.behaivor

import com.google.firebase.database.FirebaseDatabase
import java.io.Serializable

/**
 * @author CarlosEduardoL
 * This class represent an entity in the DB
 */
interface Entity : Serializable {

    /**
     * Is the uuid of the entity
     */
    var id: String

    /**
     * is the DB branch where the entity will be saved
     */
    fun folder(): String

    var debug: String

    /**
     * upload the entity to the DB
     */
    fun saveInDB(debug: String) {
        this.debug = debug
        FirebaseDatabase.getInstance().reference.child(folder()).child(id).setValue(this)
    }
}