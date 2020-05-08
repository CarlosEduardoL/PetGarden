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
import android.view.View.GONE
import android.view.animation.AnimationUtils
import androidx.appcompat.app.AppCompatActivity
import com.facebook.CallbackManager
import com.facebook.FacebookCallback
import com.facebook.FacebookException
import com.facebook.GraphRequest
import com.facebook.login.LoginResult
import com.google.android.gms.auth.api.signin.GoogleSignIn
import com.google.android.gms.auth.api.signin.GoogleSignInAccount
import com.google.android.gms.auth.api.signin.GoogleSignInOptions
import com.google.android.gms.common.api.ApiException
import com.google.android.gms.tasks.Task
import com.google.firebase.auth.FacebookAuthProvider
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.*
import kotlinx.coroutines.*
import kotlinx.coroutines.Dispatchers.IO
import kotlinx.coroutines.Dispatchers.Main
import zero.network.petgarden.R
import zero.network.petgarden.databinding.ActivityLoginBinding
import zero.network.petgarden.model.entity.User
import zero.network.petgarden.tools.initDatabase
import zero.network.petgarden.ui.register.user.activities.RegisterActivity
import zero.network.petgarden.ui.register.user.activities.RegisterFacebookActivity
import zero.network.petgarden.ui.register.user.activities.RegisterGoogleActivity
import zero.network.petgarden.ui.user.owner.OwnerActivity
import zero.network.petgarden.ui.user.sitter.SitterActivity
import zero.network.petgarden.util.*
import kotlin.coroutines.resume
import kotlin.coroutines.suspendCoroutine
import kotlin.time.seconds


class LoginActivity : AppCompatActivity() {

    private val RC_SIGN_IN = 1
    private val callbackFacebook = CallbackManager.Factory.create()
    private lateinit var auth: FirebaseAuth

    lateinit var binding: ActivityLoginBinding

    @SuppressLint("WrongViewCast")
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityLoginBinding.inflate(layoutInflater)
        setContentView(binding.root)

        initDatabase()
        checkLogin(CoroutineScope(Main).launch {
            delay(3000)
            val animation = AnimationUtils.loadAnimation(this@LoginActivity, android.R.anim.fade_out)
            binding.splashScreen.startAnimation(animation)
            binding.splashScreen.visibility = GONE
        })

        setRegisterButton()

        auth = FirebaseAuth.getInstance()

        binding.apply {
            loginButton.onClick {
                if (emailInput.text.isNotEmpty() && passwordInput.text.isNotEmpty())
                    auth.signInWithEmailAndPassword(
                        emailInput.toText(),
                        passwordInput.toText()
                    ).addOnCompleteListener {
                        if (it.isSuccessful) {
                            CoroutineScope(Main).launch {
                                if (isSitter(emailInput.toText())) {
                                    startUserView(sitterByEmail(emailInput.toText()), SitterActivity::class.java)
                                } else {
                                    startUserView(ownerByEmail(emailInput.toText()), OwnerActivity::class.java)
                                }
                            }
                        } else {
                            show(getString(R.string.no_register_info))
                        }
                    }
            }

            //Google
            googleButton.onClick {
                val gso = GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
                    .requestEmail().requestProfile().build()

                val mGoogleSignInClient = GoogleSignIn.getClient(this@LoginActivity, gso)

                val signInIntent = mGoogleSignInClient.signInIntent
                startActivityForResult(signInIntent, RC_SIGN_IN)
            }


            setUpFacebook()
        }

        ClasePruebas(this)

    }

    private fun checkLogin(job: Job){
        FirebaseAuth.getInstance().currentUser?.let {
            CoroutineScope(Main).launch {
                if(job.isActive)job.cancel()
                if (isSitter(it.email!!)) {
                    startUserView(sitterByEmail(it.email!!), SitterActivity::class.java)
                } else {
                    startUserView(ownerByEmail(it.email!!), OwnerActivity::class.java)
                }
            }
        }
    }

    private fun ActivityLoginBinding.setUpFacebook(){
        facebookButton.setPermissions("email", "user_birthday")
        facebookButton.registerCallback(callbackFacebook, object : FacebookCallback<LoginResult> {
            override fun onSuccess(result: LoginResult) {
                CoroutineScope(Main).launch { handleFacebookToken(result) }
            }
            override fun onCancel() {}
            override fun onError(error: FacebookException) { show(error.message!!) }
        })
    }

    @SuppressLint("SimpleDateFormat")
    private suspend fun handleFacebookToken(loginResult: LoginResult) {
        val credential = FacebookAuthProvider.getCredential(loginResult.accessToken.token)

        val request: GraphRequest = GraphRequest.newMeRequest(
            loginResult.accessToken
        ) { obj, _ ->
            CoroutineScope(Main).launch {
                val email = obj.getString("email")
                if (userAlreadyExists(email)) {
                    if (isSitter(email))
                        startUserView(sitterByEmail(email), SitterActivity::class.java)
                    else
                        startUserView(ownerByEmail(email), OwnerActivity::class.java)
                } else {
                    startFragmentRoleUser(obj.user)
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
            CoroutineScope(Main).launch {
                val task: Task<GoogleSignInAccount> =
                    GoogleSignIn.getSignedInAccountFromIntent(data)
                handleGoogleSignIn(task)
            }
        }
        callbackFacebook.onActivityResult(requestCode, resultCode, data)
    }

    private suspend fun handleGoogleSignIn(completedTask: Task<GoogleSignInAccount>) {
        try {
            val account = completedTask.getResult(ApiException::class.java)!!

            val email = account.email!!

            if (userAlreadyExists(email)) {
                if (isSitter(email)) {
                    startUserView(sitterByEmail(email), SitterActivity::class.java)
                } else {
                    startUserView(ownerByEmail(email), OwnerActivity::class.java)
                }
            } else {
                startFragmentBirthday(account.user)
            }

        } catch (e: ApiException) {
            show(getString(R.string.sign_in_google_error))
        }
    }

    private suspend fun userAlreadyExists(email: String): Boolean = withContext(IO) {
        val isOwner: Boolean =
            FirebaseDatabase.getInstance().reference.child("owners")
                .orderByChild("email")
                .equalTo(email).isRegister()

        val isSitter: Boolean =
            FirebaseDatabase.getInstance().reference.child("sitters")
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

    private suspend fun isSitter(email: String): Boolean = withContext(IO) {
        FirebaseDatabase.getInstance().reference.child("sitters")
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

    private fun startFragmentRoleUser(user: User) {
        val intent = Intent(this, RegisterFacebookActivity::class.java)
        intent.putExtra("user", user)
        startActivity(intent)
    }

    private fun startFragmentBirthday(user: User) {
        val intent = Intent(this, RegisterGoogleActivity::class.java)
        intent.putExtra("user", user)
        startActivity(intent)
    }

}
