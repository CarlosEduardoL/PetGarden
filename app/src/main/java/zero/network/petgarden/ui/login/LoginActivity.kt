package zero.network.petgarden.ui.login

import android.annotation.SuppressLint
import android.content.Intent
import android.graphics.Color
import android.os.Bundle
import android.text.SpannableString
import android.text.Spanned
import android.text.method.LinkMovementMethod
import android.text.style.ClickableSpan
import android.view.View
import androidx.appcompat.app.AppCompatActivity
import com.facebook.CallbackManager
import com.facebook.FacebookCallback
import com.facebook.FacebookException
import com.facebook.GraphRequest
import com.facebook.login.LoginResult
import com.facebook.login.widget.LoginButton
import com.google.android.gms.auth.api.signin.GoogleSignIn
import com.google.android.gms.auth.api.signin.GoogleSignInAccount
import com.google.android.gms.auth.api.signin.GoogleSignInOptions
import com.google.android.gms.common.api.ApiException
import com.google.android.gms.tasks.Task
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.*
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers.IO
import kotlinx.coroutines.Dispatchers.Main
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import zero.network.petgarden.R
import zero.network.petgarden.databinding.ActivityLoginBinding
import zero.network.petgarden.model.entity.Location
import zero.network.petgarden.model.entity.User
import zero.network.petgarden.tools.initDatabase
import zero.network.petgarden.ui.register.user.RegisterActivity
import zero.network.petgarden.ui.register.user.RegisterFacebookActivity
import zero.network.petgarden.ui.user.owner.OwnerActivity
import zero.network.petgarden.ui.user.sitter.SitterActivity
import zero.network.petgarden.util.show
import zero.network.petgarden.util.toText
import java.text.SimpleDateFormat
import java.util.*
import kotlin.coroutines.resume
import kotlin.coroutines.suspendCoroutine


class LoginActivity : AppCompatActivity() {

    private val RC_SIGN_IN = 1
    private val callbackFacebook = CallbackManager.Factory.create()

    lateinit var binding: ActivityLoginBinding

    @SuppressLint("WrongViewCast")
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityLoginBinding.inflate(layoutInflater)
        setContentView(binding.root)

        initDatabase()

        setRegisterButton()

        val fbAuth = FirebaseAuth.getInstance()

        binding.loginButton.setOnClickListener {
            if (binding.emailInput.text.isNotEmpty() && binding.passwordInput.text.isNotEmpty())
                fbAuth.signInWithEmailAndPassword(
                    binding.emailInput.toText(),
                    binding.passwordInput.toText()
                )
                    .addOnCompleteListener {
                        if (it.isSuccessful) {
                            CoroutineScope(Main).launch {  chooseFragment(binding)}
                        } else {
                            show(getString(R.string.no_register_info))
                        }
                    }
        }

        //Google
        binding.googleButton.setOnClickListener {

            val gso = GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
                .requestEmail().requestProfile().build()

            val mGoogleSignInClient = GoogleSignIn.getClient(this, gso)

            val signInIntent = mGoogleSignInClient.signInIntent
            startActivityForResult(signInIntent, RC_SIGN_IN)
        }

        val fbookButton = findViewById<LoginButton>(R.id.facebookButton)

        val callback = object : FacebookCallback<LoginResult> {
            override fun onSuccess(result: LoginResult) {
                println("entroSuccess")
                CoroutineScope(Main).launch{handleFacebookToken(result)}
            }

            override fun onCancel() {
                println("entroCancel")
            }

            override fun onError(error: FacebookException?) {
                println("entroError")
            }
        }

        fbookButton.setPermissions("email", "user_birthday", "user_posts")
        fbookButton.registerCallback(callbackFacebook, callback)

        ClasePruebas(this)

    }

    private suspend fun handleFacebookToken(loginResult: LoginResult) {
        val request: GraphRequest = GraphRequest.newMeRequest(
            loginResult.accessToken
        ) { obj, _ ->
            CoroutineScope(Main).launch {
                val email = obj.getString("email")

                if (userAlreadyExists(email)) {
                    if (isSitter(email))
                        startFragmentSitter()
                    else
                        startFragmentOwner()
                } else {
                    val name = obj.getString("first_name")
                    val lastName = obj.getString("last_name")
                    val photo =
                        "https://graph.facebook.com/" + (obj.getString("id")) + "/picture?width=500&height=500"
                    val birthday =
                        SimpleDateFormat("dd/MM/yyyy").parse(obj.getString("birthday"))// O el formato es MM/dd/yyyy??, lo averiguaremos
                    val user = User(
                        UUID.randomUUID().toString(),
                        name,
                        lastName,
                        email,
                        "1234567",
                        birthday,
                        photo,
                        Location(0.0, 0.0)
                    )

                    startFragmentRoleUser(user)
                }

            }
        }

        val parameters = Bundle()
        parameters.putString("fields", "id, first_name, last_name, email, birthday")
        request.parameters = parameters
        request.executeAsync()

    }


    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)

        if (requestCode == RC_SIGN_IN) {
            val task: Task<GoogleSignInAccount> =
                GoogleSignIn.getSignedInAccountFromIntent(data)
            CoroutineScope(Main).launch { handleGoogleSignIn(task) }
        }
        callbackFacebook.onActivityResult(requestCode, resultCode, data)
    }

    private suspend fun handleGoogleSignIn(completedTask: Task<GoogleSignInAccount>) {
        try {
            val account = completedTask.getResult(ApiException::class.java)

            if (account != null) {
                val email = "" + account.email

                if (userAlreadyExists(email)) {
                    if (isSitter(email))
                        startFragmentSitter()
                    else
                        startFragmentOwner()

                } else {
                    val name = "" + account.givenName
                    val lastName = "" + account.familyName
                    val photo = account.photoUrl.toString()


                    val user = User(
                        UUID.randomUUID().toString(),
                        name,
                        lastName,
                        email,
                        "123456",
                        Date(),
                        photo,
                        Location(0.0, 0.0)
                    )

                    startFragmentRoleUser(user)
                }
            }

        } catch (e: ApiException) {
            show(getString(R.string.sign_in_google_error))
        }
    }

    private suspend fun userAlreadyExists(email: String): Boolean = withContext(IO) {
        val isOwner: Boolean =
            FirebaseDatabase.getInstance().reference.child("users").child("owners")
                .orderByChild("email")
                .equalTo(email).isRegister()

        val isSitter: Boolean =
            FirebaseDatabase.getInstance().reference.child("users").child("sitters")
                .orderByChild("email")
                .equalTo(email).isRegister()

        println("sitterAfter$isSitter")
        return@withContext isSitter or isOwner
    }

    private suspend fun Query.isRegister(): Boolean = suspendCoroutine {
        val callback = object : ValueEventListener {
            override fun onCancelled(p0: DatabaseError) = it.resume(false)

            override fun onDataChange(dataSnapshot: DataSnapshot) =
                if (dataSnapshot.childrenCount > 0) it.resume(true)
                else it.resume(false)
        }
        addListenerForSingleValueEvent(callback)
    }

    private suspend fun isSitter(email: String): Boolean = withContext(IO){
        FirebaseDatabase.getInstance().reference.child("users").child("sitters")
            .orderByChild("email")
            .equalTo(email).isRegister()
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
        binding.registerButton.apply {
            text = ss
            movementMethod = LinkMovementMethod.getInstance()
            highlightColor = Color.TRANSPARENT
        }
    }


    private fun startFragmentSitter() {
        val intent = Intent(this, SitterActivity::class.java)
        println("fragmentSitter")
        startActivity(intent)
    }

    private fun startFragmentOwner() {
        val intent = Intent(this, OwnerActivity::class.java)
        startActivity(intent)
    }

    private fun startFragmentRoleUser(user: User) {
        val intent = Intent(this, RegisterFacebookActivity::class.java)
        intent.putExtra("user", user)
        startActivity(intent)
    }

    private suspend fun chooseFragment(binding: ActivityLoginBinding) {
        if (isSitter(binding.emailInput.toText()))
            startFragmentSitter()
        else
            startFragmentOwner()
    }

}
