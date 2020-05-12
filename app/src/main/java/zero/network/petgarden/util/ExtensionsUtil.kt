
package zero.network.petgarden.util

import android.content.Intent
import android.view.View
import android.widget.EditText
import android.widget.Toast
import androidx.fragment.app.Fragment
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.Query
import com.google.firebase.database.ValueEventListener
import java.text.SimpleDateFormat
import java.util.*
import kotlin.coroutines.resume
import kotlin.coroutines.resumeWithException
import kotlin.coroutines.suspendCoroutine


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

fun <K,V : Collection<E>,E> List<Pair<K,V>>.simplify(): List<Pair<K,E>>{
    val list: MutableList<Pair<K,E>> = mutableListOf()
    forEach {
        list.addAll(it.second.map { value -> it.first to value })
    }
    return list
}
