package zero.network.petgarden.util

import android.app.Activity
import android.content.Intent
import android.net.Uri
import android.widget.Toast
import androidx.core.content.FileProvider
import zero.network.petgarden.model.behaivor.IUser
import zero.network.petgarden.tools.logError
import zero.network.petgarden.ui.register.user.FragmentStart
import zero.network.petgarden.ui.register.user.activities.RegisterActivity
import java.io.File
import java.io.Serializable
import java.lang.Exception


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
fun <T> Activity.startUserView(user: IUser, clazz: Class<T>) = startActivity(
    intent(clazz, "user" to user).apply {
        flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK
    }
)

fun Activity.startRegisterView(user: IUser, start: FragmentStart = FragmentStart.Name) =
    startActivity(
        intent(RegisterActivity::class.java, "user" to user).apply { putExtra("start", start) }
    )

/**
 * @return a new Intent with activity context
 */
fun <T> Activity.intent(clazz: Class<T>, extra: Pair<String, Serializable>? = null) =
    Intent(this, clazz).apply { extra?.let { putExtra(extra.first, extra.second) } }