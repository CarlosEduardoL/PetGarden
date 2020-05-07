package zero.network.petgarden.util

import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ValueEventListener
import zero.network.petgarden.model.entity.Owner
import zero.network.petgarden.model.entity.Sitter
import kotlin.coroutines.resume
import kotlin.coroutines.resumeWithException
import kotlin.coroutines.suspendCoroutine

suspend fun ownerByEmail(email: String): Owner = suspendCoroutine{
    val query  = FirebaseDatabase.getInstance().reference
        .child(Sitter.FOLDER).orderByChild("email").equalTo(email)

    query.addListenerForSingleValueEvent(object: ValueEventListener {
        override fun onCancelled(error: DatabaseError) {
            it.resumeWithException(error.toException())
        }

        override fun onDataChange(dataSnapshot: DataSnapshot) {
            for (result in dataSnapshot.children) {
                it.resume(result.getValue(Owner::class.java)!!)
                break
            }
        }
    })
}

suspend fun sitterByEmail(email: String): Sitter = suspendCoroutine{
    val query  = FirebaseDatabase.getInstance().reference
        .child(Sitter.FOLDER).orderByChild("email").equalTo(email)

    query.addListenerForSingleValueEvent(object: ValueEventListener {
        override fun onCancelled(error: DatabaseError) {
            it.resumeWithException(error.toException())
        }

        override fun onDataChange(dataSnapshot: DataSnapshot) {
            for (result in dataSnapshot.children) {
                it.resume(result.getValue(Sitter::class.java)!!)
                break
            }
        }
    })
}