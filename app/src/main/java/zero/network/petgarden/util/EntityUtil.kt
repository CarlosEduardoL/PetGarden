package zero.network.petgarden.util

import android.annotation.SuppressLint
import com.google.android.gms.auth.api.signin.GoogleSignInAccount
import com.google.firebase.database.FirebaseDatabase
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers.Main
import kotlinx.coroutines.launch
import org.json.JSONObject
import zero.network.petgarden.model.behaivor.CallBack
import zero.network.petgarden.model.behaivor.IUser
import zero.network.petgarden.model.entity.Location
import zero.network.petgarden.model.entity.Owner
import zero.network.petgarden.model.entity.Sitter
import zero.network.petgarden.model.entity.User
import java.text.SimpleDateFormat
import java.util.*

suspend fun ownerByEmail(email: String): Owner? {
    val query = FirebaseDatabase.getInstance().reference
        .child(Owner.FOLDER).orderByChild("email").equalTo(email)
    return query.wait().children.firstOrNull()?.getValue(Owner::class.java)
}

suspend fun sitterByEmail(email: String): Sitter? {
    val query = FirebaseDatabase.getInstance().reference
        .child(Sitter.FOLDER).orderByChild("email").equalTo(email)
    return query.wait().children.firstOrNull()?.getValue(Sitter::class.java)
}

suspend fun userByEmail(email: String): IUser? {
    return sitterByEmail(email)?: ownerByEmail(email)
}

suspend fun allSitters(): List<Sitter> = FirebaseDatabase.getInstance()
    .reference.child("sitters").wait()
    .children.map { it.getValue(Sitter::class.java)!! }

fun allSitters(callBack: CallBack<List<Sitter>>) = CoroutineScope(Main).launch {
    callBack.onResult(allSitters())
}

val GoogleSignInAccount.user
    get() = User(
        givenName!!,
        familyName!!,
        email!!,
        "123456",
        Date(),
        photoUrl.toString(),
        Location(0.0, 0.0)
    )

val JSONObject.user
    @SuppressLint("SimpleDateFormat")
    get() = User(
        getString("first_name"),
        getString("last_name"),
        getString("email"),
        "",
        SimpleDateFormat("dd/MM/yyyy").parse(getString("birthday"))!!,
        "https://graph.facebook.com/${getString("id")}/picture?width=500&height=500"
    )