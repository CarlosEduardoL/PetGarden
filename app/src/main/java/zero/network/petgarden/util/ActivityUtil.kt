package zero.network.petgarden.util

import android.app.Activity
import android.content.Intent
import android.net.Uri
import android.widget.Toast
import androidx.core.content.FileProvider
import zero.network.petgarden.exception.InvalidUserClass
import zero.network.petgarden.model.behaivor.IUser
import zero.network.petgarden.model.entity.Owner
import zero.network.petgarden.model.entity.Sitter
import zero.network.petgarden.ui.register.user.FragmentStart
import zero.network.petgarden.ui.register.user.activities.RegisterActivity
import zero.network.petgarden.ui.user.owner.OwnerActivity
import zero.network.petgarden.ui.user.sitter.SitterActivity
import java.io.File


/**
 *  Show a toast whit long duration in a [Activity]
 */
fun Activity.show(message: String) =
    Toast.makeText(this, message, Toast.LENGTH_LONG).show()

/**
 * get extra information from intent
 */
inline fun <reified T> Activity.extra(key: String): T {
    val ext = intent.extras ?: throw Exception("No extras")
    return if (ext.containsKey(key)) {
        val obj = ext[key]
        if (obj is T) obj
        else throw Exception("the object with key $key is not type ${T::class.java}")
    } else throw Exception("key $key doesn't exist")
}


/**
 * get the uri from [file]
 */
fun Activity.fileToUri(file: File): Uri = FileProvider.getUriForFile(this, this.packageName, file)

/**
 * start the user view
 */
fun Activity.startUserView(
    user: IUser
) {
    val clazz = when(user){
        is Owner -> OwnerActivity::class.java
        is Sitter -> SitterActivity::class.java
        else -> throw InvalidUserClass("${user::class.simpleName} no is a valid class")
    }
    val intent = intent(clazz, "user" to user).apply {
        flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK
    }

    startActivity(intent)

}

fun Activity.startRegisterView(user: IUser, start: FragmentStart = FragmentStart.Name) =
    startActivity(
        intent(RegisterActivity::class.java, "user" to user).apply { putExtra("start", start) }
    )
