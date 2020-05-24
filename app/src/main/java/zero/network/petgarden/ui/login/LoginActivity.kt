package zero.network.petgarden.ui.login

import android.Manifest
import android.annotation.SuppressLint
import android.content.Intent
import android.content.pm.PackageManager
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
import androidx.core.app.ActivityCompat
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
import com.google.firebase.auth.GoogleAuthProvider
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers.Main
import kotlinx.coroutines.Job
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import zero.network.petgarden.R
import zero.network.petgarden.databinding.ActivityLoginBinding
import zero.network.petgarden.model.entity.Owner
import zero.network.petgarden.model.entity.Sitter
import zero.network.petgarden.model.entity.User
import zero.network.petgarden.tools.initDatabase
import zero.network.petgarden.ui.register.user.FragmentStart
import zero.network.petgarden.util.*
import java.util.*


class LoginActivity : AppCompatActivity() {


    private val callbackFacebook = CallbackManager.Factory.create()
    private lateinit var auth: FirebaseAuth
    private lateinit var loginScope: CoroutineScope
    private var showDialog: Boolean = false

    private lateinit var binding: ActivityLoginBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityLoginBinding.inflate(layoutInflater)
        setContentView(binding.root)
        loginScope = CoroutineScope(Main)
        initDatabase()
        auth = FirebaseAuth.getInstance()

        checkLogin(loginScope.launch {
            delay(3000)
            val animation =
                AnimationUtils.loadAnimation(this@LoginActivity, android.R.anim.fade_out)
            binding.splashScreen.startAnimation(animation)
            binding.splashScreen.visibility = GONE
        })


        if (intent.hasExtra("show_dialog")) {
            val ss:String =extra("show_dialog")
            if(ss =="show_dialog"){
                showDialog = true
            }
        }


        requestPermission()


    }

    private fun requestPermission() {
        ActivityCompat.requestPermissions(
            this, arrayOf(
                Manifest.permission.CAMERA,
                Manifest.permission.WRITE_EXTERNAL_STORAGE,
                Manifest.permission.READ_EXTERNAL_STORAGE,
                Manifest.permission.ACCESS_FINE_LOCATION,
                Manifest.permission.INTERNET
            ), REQUEST_PERMISSION_CODE
        )
    }

    override fun onRequestPermissionsResult(
        requestCode: Int,
        permissions: Array<out String>,
        grantResults: IntArray
    ) {
        if (requestCode == REQUEST_PERMISSION_CODE && grantResults.all { it == PackageManager.PERMISSION_GRANTED }) {
            setUpRegisterButton()
            setUpLogin()
            setUpGoogle()
            setUpFacebook()
        } else {
            requestPermission()
        }
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        if (requestCode == RC_SIGN_IN) {
            loginScope.launch {
                val task: Task<GoogleSignInAccount> =
                    GoogleSignIn.getSignedInAccountFromIntent(data)
                handleGoogleSignIn(task)
            }
        }
        callbackFacebook.onActivityResult(requestCode, resultCode, data)
    }

    private fun checkLogin(job: Job) {
        auth.currentUser?.let {
            CoroutineScope(Main).launch {
                if (job.isActive) {
                    job.cancel()
                }

                sitterByEmail(it.email!!)?.let {
                    startUserView(it)
                }
                ownerByEmail(it.email!!)?.let {
                    if(showDialog){
                        startUserView(it, true)
                    }else{
                        startUserView(it, false)
                    }

                }

                val animation = AnimationUtils.loadAnimation(this@LoginActivity, android.R.anim.fade_out)
                binding.splashScreen.startAnimation(animation)
                binding.splashScreen.visibility = GONE

            }
        }
    }

    private fun setUpLogin() = binding.apply {
        loginButton.onClick {
            if (emailInput.text.isNotEmpty() && passwordInput.text.isNotEmpty()) loginScope.launch {
                auth.signInWithEmailAndPassword(
                    emailInput.toText().toLowerCase(Locale.ROOT),
                    passwordInput.toText()
                ).awaitOrException()?.let {
                    show(
                        """Esta cuenta no existe o ha sido creada usando facebook o google
                        De ser esta ultima opcion porfavor use la opcion correspondiente para iniciar session""".trimIndent()
                    )
                }

                sitterByEmail(emailInput.toText())?.let {
                    startUserView(it)
                }
                ownerByEmail(emailInput.toText())?.let {
                    startUserView(it)
                }
            }
        }
    }

    private fun setUpGoogle() = binding.apply {
        googleButton.onClick {
            val gso = GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
                .requestEmail().requestProfile()
                .requestIdToken(getString(R.string.default_web_client_id)).build()
            val mGoogleSignInClient = GoogleSignIn.getClient(this@LoginActivity, gso)
            val signInIntent = mGoogleSignInClient.signInIntent
            startActivityForResult(signInIntent, RC_SIGN_IN)
        }
    }

    private fun setUpFacebook() = binding.apply {
        facebookButton.setPermissions("email", "user_birthday")
        facebookButton.registerCallback(callbackFacebook, object : FacebookCallback<LoginResult> {
            override fun onSuccess(result: LoginResult) {
                loginScope.launch { handleFacebookToken(result) }
            }

            override fun onCancel() {}
            override fun onError(error: FacebookException) {
                show(error.message!!)
            }
        })
    }

    private fun setUpRegisterButton() {
        val content = getString(R.string.register)
        val lastWord = content.split(" ").last()
        val ss = SpannableString(content).apply {
            setSpan(
                object : ClickableSpan() {
                    override fun onClick(widget: View) {
                        startRegisterView(User())
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

    @SuppressLint("SimpleDateFormat")
    private suspend fun handleFacebookToken(loginResult: LoginResult) {
        val credential = FacebookAuthProvider.getCredential(loginResult.accessToken.token)
        auth.signInWithCredential(credential)
        val request: GraphRequest = GraphRequest.newMeRequest(loginResult.accessToken) { obj, _ ->
            loginScope.launch {
                val email = obj.getString("email")
                userByEmail(email)?.let {
                    when (it) {
                        is Sitter -> startUserView(it)
                        else -> startUserView(it)
                    }
                    return@launch
                }
                startRegisterView(obj.user, FragmentStart.Role)
            }
        }
        val parameters = Bundle()
        parameters.putString("fields", "id, first_name, last_name, email, birthday")
        request.parameters = parameters
        request.executeAsync()
    }

    private suspend fun handleGoogleSignIn(completedTask: Task<GoogleSignInAccount>) {
        try {
            val account = completedTask.getResult(ApiException::class.java)!!
            val email = account.email!!
            val credential = GoogleAuthProvider.getCredential(account.idToken, null)
            auth.signInWithCredential(credential)
            userByEmail(email)?.let {
                when (it) {
                    is Sitter -> startUserView(it)
                    is Owner -> startUserView(it)
                    else -> show(getString(R.string.sign_in_google_error))
                }
                return
            }
            startRegisterView(account.user, FragmentStart.BirthDay)

        } catch (e: ApiException) {
            show(getString(R.string.sign_in_google_error))
        }
    }

    companion object {
        private const val RC_SIGN_IN = 1
        private const val REQUEST_PERMISSION_CODE = 0
    }

}
