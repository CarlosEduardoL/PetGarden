package zero.network.petgarden.model.behaivor

import com.google.firebase.database.FirebaseDatabase

interface Entity {
    val id: String

    fun folder(): String

    fun saveInDB() {
        FirebaseDatabase.getInstance().reference.child(folder()).child(id).setValue(this)
    }
}