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
import kotlinx.android.synthetic.main.activity_login.*
import zero.network.petgarden.R
import zero.network.petgarden.ui.register.RegisterActivity


class LoginActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_login)
        setRegisterButton()
        facebookButton.setOnClickListener {
            showToast("¿Quien quiere hacerlo?")
            intentToWeb("https://developers.facebook.com/docs/facebook-login/android/?locale=es_ES#quickstarts-header")
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
                        Intent(this@LoginActivity, RegisterActivity::class.java).apply {
                            addFlags(Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK or Intent.FLAG_ACTIVITY_CLEAR_TOP)
                            startActivity(this)
                        }
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
