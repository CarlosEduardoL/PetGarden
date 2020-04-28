package zero.network.petgarden.ui.login

import android.annotation.SuppressLint
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
import com.facebook.*
import com.google.android.gms.auth.api.signin.GoogleSignIn
import com.google.android.gms.auth.api.signin.GoogleSignInAccount
import com.google.android.gms.auth.api.signin.GoogleSignInOptions
import com.google.android.gms.common.api.ApiException
import com.google.android.gms.tasks.Task
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.*
import kotlinx.android.synthetic.main.activity_login.*
import zero.network.petgarden.R
import zero.network.petgarden.model.entity.Location
import zero.network.petgarden.model.entity.User
import zero.network.petgarden.ui.register.user.RegisterActivity
import zero.network.petgarden.util.toText
import com.facebook.appevents.AppEventsLogger
import com.facebook.login.Login
import com.facebook.login.LoginResult
import com.facebook.login.widget.LoginButton
import kotlinx.android.synthetic.main.activity_login.view.*
import java.util.*


class LoginActivity : AppCompatActivity() {

    private  val RC_SIGN_IN = 1

    @SuppressLint("WrongViewCast")
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


        googleButton.setOnClickListener{

            val gso = GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
                        .requestEmail().requestProfile().build()

            val mGoogleSignInClient = GoogleSignIn.getClient(this, gso)

            val signInIntent = mGoogleSignInClient.signInIntent
            startActivityForResult(signInIntent, RC_SIGN_IN)
        }

        val mCallbackManager = CallbackManager.Factory.create()
        val loginButton = findViewById<LoginButton>(R.id.facebookButton);
        loginButton.registerCallback(mCallbackManager, object:FacebookCallback<LoginResult>{
            override fun onSuccess(result: LoginResult) {
                handleFacebookToken(result.accessToken)
            }

            override fun onCancel() {
                TODO("not implemented") //To change body of created functions use File | Settings | File Templates.
            }

            override fun onError(error: FacebookException?) {
                TODO("not implemented") //To change body of created functions use File | Settings | File Templates.
            }

        })

        facebookButton.setOnClickListener{
            FacebookSdk.sdkInitialize(applicationContext)

        }


    }

    private fun handleFacebookToken(accessToken: AccessToken) {

    }


    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)

        if (requestCode == RC_SIGN_IN) {
            val task: Task<GoogleSignInAccount> =
                GoogleSignIn.getSignedInAccountFromIntent(data)
            handleGoogleSignIn(task)
        }
    }

    private fun handleGoogleSignIn(completedTask: Task<GoogleSignInAccount>) {
        try {
            val account: GoogleSignInAccount? = completedTask.getResult(ApiException::class.java)

            if(account!=null ) {
                val email = "" + account.email
                val name = "" + account.givenName
                val lastName = "" + account.familyName
                val photo = account.photoUrl.toString()
                val user = User(UUID.randomUUID().toString(), name, lastName, email, "", Date(), photo, Location(0.0,0.0))

                if (userAlreadyExists(email)) {
                    //Es sitter? Mandelo a la activity de maps de sitter

                }else {
                    //Cambiar esto para que vaya hasta el fragment
                    val intent = Intent(this, RegisterActivity::class.java)
                    intent.putExtra("user", user)
                    startActivity(intent)
                }
            }

        } catch (e: ApiException) {
            showToast(getString(R.string.sign_in_google_error))

        }
    }

    private fun userAlreadyExists(email:String):Boolean{
        var isOwner = false
        var isSitter = false

        val queryBusquedaOwner: Query =
            FirebaseDatabase.getInstance().getReference().child("users").child("owners").orderByChild("email")
                .equalTo(email)

            queryBusquedaOwner.addListenerForSingleValueEvent(object:ValueEventListener{
                override fun onCancelled(p0: DatabaseError) {}

                override fun onDataChange(dataSnapshot: DataSnapshot) {
                    if (dataSnapshot.getChildrenCount() > 0L) {
                        isOwner =true
                    }
                }

            })

        val queryBusquedaSitter: Query =
            FirebaseDatabase.getInstance().getReference().child("users").child("sitters").orderByChild("email")
                .equalTo(email)

        queryBusquedaSitter.addListenerForSingleValueEvent(object:ValueEventListener{
            override fun onCancelled(p0: DatabaseError) {}

            override fun onDataChange(dataSnapshot: DataSnapshot) {
                if (dataSnapshot.getChildrenCount() > 0L) {
                    isSitter =true
                }
            }

        })

        return isSitter || isOwner
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
