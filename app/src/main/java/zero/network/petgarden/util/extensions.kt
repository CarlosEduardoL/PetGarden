
package zero.network.petgarden.util

import android.app.Activity
import android.view.View
import android.widget.DatePicker
import android.widget.EditText
import android.widget.Toast
import androidx.fragment.app.Fragment
import java.util.*


// Get the string text from an EditText
fun EditText.toText() = this.text.toString()

// Show a toast whit long duration in a fragment
fun Fragment.show(message: String) =
    Toast.makeText(this.activity, message, Toast.LENGTH_LONG).show()

// Show a toast whit long duration in a [Activity]
fun Activity.show(message: String) =
    Toast.makeText(this, message, Toast.LENGTH_LONG).show()

// Short the setOnCLickListenerMethod
fun View.onClick(action: (View) -> Unit) = this.setOnClickListener { action(it) }

