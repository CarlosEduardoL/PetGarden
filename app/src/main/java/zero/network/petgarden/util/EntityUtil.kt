package zero.network.petgarden.util

import com.facebook.GraphResponse
import com.google.android.gms.auth.api.signin.GoogleSignInAccount
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ValueEventListener
import org.json.JSONObject
import zero.network.petgarden.model.entity.Location
import zero.network.petgarden.model.entity.Owner
import zero.network.petgarden.model.entity.Sitter
import zero.network.petgarden.model.entity.User
import java.text.SimpleDateFormat
import java.util.*
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

val GoogleSignInAccount.user
    get() = User(givenName!!, familyName!!, email!!, "123456", Date(), photoUrl.toString(), Location(0.0, 0.0))

val JSONObject.user
    get() = User(
        getString("first_name"),
        getString("last_name"),
        getString("email"),
        "",
        SimpleDateFormat("dd/MM/yyyy").parse(getString("birthday"))!!,
        "https://graph.facebook.com/${getString("id")}/picture?width=500&height=500"
        )