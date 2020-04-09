package zero.network.petgarden.util

import android.widget.EditText
import android.widget.Toast
import androidx.fragment.app.Fragment

// Get the string text from an EditText
fun EditText.toText() = this.text.toString()

// Show a toast whit long duration in a fragment
fun Fragment.show(message: String) = Toast.makeText(this.activity,message,Toast.LENGTH_LONG).show()
