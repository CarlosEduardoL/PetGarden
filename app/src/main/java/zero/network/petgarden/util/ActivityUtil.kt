package zero.network.petgarden.util

import android.app.Activity
import android.widget.Toast


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