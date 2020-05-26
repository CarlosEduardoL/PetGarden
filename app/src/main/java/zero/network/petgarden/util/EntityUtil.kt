package zero.network.petgarden.util

import android.annotation.SuppressLint
import com.google.android.gms.auth.api.signin.GoogleSignInAccount
import com.google.firebase.database.FirebaseDatabase
import org.json.JSONObject
import zero.network.petgarden.model.behaivor.Entity
import zero.network.petgarden.model.behaivor.IUser
import zero.network.petgarden.model.component.Location
import zero.network.petgarden.model.entity.Owner
import zero.network.petgarden.model.entity.SitterIMP
import zero.network.petgarden.model.entity.User
import java.text.SimpleDateFormat
import java.util.*

suspend fun ownerByEmail(email: String): Owner? {
    val query = FirebaseDatabase.getInstance().reference
        .child(Owner.FOLDER).orderByChild("email").equalTo(email)
    return query.wait().children.firstOrNull()?.getValue(Owner::class.java)
}

suspend fun sitterByEmail(email: String): SitterIMP? {
    val query = FirebaseDatabase.getInstance().reference
        .child(SitterIMP.FOLDER).orderByChild("email").equalTo(email)
    return query.wait().children.firstOrNull()?.getValue(SitterIMP::class.java)
}

suspend fun userByEmail(email: String): IUser? {
    return sitterByEmail(email)?: ownerByEmail(email)
}

fun saveInDB(vararg entity: Entity) = entity.forEach { it.saveInDB() }

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