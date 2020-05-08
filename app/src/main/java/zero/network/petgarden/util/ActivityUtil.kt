package zero.network.petgarden.util

import android.app.Activity
import android.content.Intent
import android.net.Uri
import android.widget.Toast
import androidx.core.content.FileProvider
import zero.network.petgarden.model.behaivor.IUser
import java.io.File


/**
 *  Show a toast whit long duration in a [Activity]
 */
fun Activity.show(message: String) =
    Toast.makeText(this, message, Toast.LENGTH_LONG).show()

/**
 * get extra information from intent
 */
inline fun<reified T> Activity.extra(key: String, onError: (error: String) -> Nothing) : T{
    val ext = intent.extras?:onError("No extras")
    return if (ext.containsKey(key)){
        val obj = ext[key]
        if (obj is T) obj
        else onError("the object with key $key is not type ${T::class.java}")
    }else onError("key $key doesn't exist")
}

/**
 * get the uri from [file]
 */
fun Activity.fileToUri(file: File): Uri = FileProvider.getUriForFile(this, this.packageName, file)

/**
 *
 */
fun <T> Activity.startUserView(state: IUser, clazz: Class<T>) {
    Intent(this, clazz).apply {
        flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK
        putExtra("user", state)
        startActivity(this)
    }
}