package zero.network.petgarden.ui.login

import android.content.Intent
import android.graphics.Color
import android.net.Uri
import android.os.Bundle
import android.text.SpannableString
import android.text.Spanned
import android.text.method.LinkMovementMethod
import android.text.style.ClickableSpan
import android.view.View
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.google.firebase.auth.FirebaseAuth
import kotlinx.android.synthetic.main.activity_login.*
import zero.network.petgarden.R
import zero.network.petgarden.ui.register.user.RegisterActivity
import zero.network.petgarden.util.toText


class LoginActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_login)

        setRegisterButton()

        val fbAuth = FirebaseAuth.getInstance()

        loginButton.setOnClickListener {
            if (emailInput.text.isNotEmpty() && passwordInput.text.isNotEmpty())
                fbAuth.signInWithEmailAndPassword(emailInput.toText(), passwordInput.toText()).addOnCompleteListener {
                    if (it.isSuccessful){
                        Intent(this@LoginActivity, RegisterActivity::class.java).let {

                        }
                    }else {
                        showToast(getString(R.string.no_register_info))
                    }
                }
        }

        googleButton.setOnClickListener {
            showToast("¿Quien quiere hacerlo?")
            intentToWeb("https://developers.google.com/identity/sign-in/android/start-integrating")
        }

    }

    private fun setRegisterButton() {
        val content = getString(R.string.register)
        val lastWord = content.split(" ").last()
        val ss = SpannableString(content).apply {
            setSpan(
                object : ClickableSpan() {
                    override fun onClick(widget: View) {
                        startActivity(Intent(this@LoginActivity, RegisterActivity::class.java))
                    }
                },
                content.indexOf(lastWord),
                content.indexOf(lastWord) + lastWord.length,
                Spanned.SPAN_EXCLUSIVE_EXCLUSIVE
            )
        }
        registerButton.apply{
            text = ss
            movementMethod = LinkMovementMethod.getInstance()
            highlightColor = Color.TRANSPARENT
        }
    }

    private fun showToast(text: String) = Toast.makeText(this, text, Toast.LENGTH_LONG).show()

    private fun intentToWeb(link: String) {
        val i = Intent(Intent.ACTION_VIEW)
        i.data = Uri.parse(link)
        startActivity(i)
    }
}
