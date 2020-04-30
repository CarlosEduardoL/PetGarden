
package zero.network.petgarden.util

import android.app.Activity
import android.content.Intent
import android.view.View
import android.widget.EditText
import android.widget.Toast
import androidx.fragment.app.Fragment


// Get the string text from an EditText
fun EditText.toText() = this.text.toString()

// Show a toast whit long duration in a fragment
fun Fragment.show(message: String) =
    Toast.makeText(this.activity, message, Toast.LENGTH_LONG).show()


// Short the setOnCLickListenerMethod
fun View.onClick(action: () -> Unit) = this.setOnClickListener { action() }

/**
 * get extra information from intent
 */
inline fun<reified T> Intent.extra(key: String, onError: (error: String) -> Nothing) : T{
    val ext = extras?:onError("No extras")
    return if (ext.containsKey(key)){
        val obj = ext[key]
        if (obj is T) obj
        else onError("the object with key $key is not type ${T::class.java}")
    }else onError("key $key doesn't exist")
}

