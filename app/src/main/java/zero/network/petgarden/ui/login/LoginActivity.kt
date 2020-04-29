package zero.network.petgarden.ui.login

import android.annotation.SuppressLint
import android.content.Intent
import android.graphics.Color
import android.net.Uri
import android.os.Bundle
import android.provider.ContactsContract
import android.text.SpannableString
import android.text.Spanned
import android.text.method.LinkMovementMethod
import android.text.style.ClickableSpan
import android.util.Log
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
import com.google.firebase.auth.FirebaseUser
import kotlinx.android.synthetic.main.activity_login.view.*
import org.json.JSONException
import org.json.JSONObject
import java.net.MalformedURLException
import java.text.SimpleDateFormat
import java.util.*


class LoginActivity : AppCompatActivity() {

    private  val RC_SIGN_IN = 1
    private val callbackFacebook =CallbackManager.Factory.create()

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
                        if(isSitter(emailInput.toText()))
                            startFragmentSitter()
                        else
                            startFragmentOwner()
                    }else {
                        showToast(getString(R.string.no_register_info))
                    }
                }
        }

        //Google
        googleButton.setOnClickListener{

            val gso = GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
                        .requestEmail().requestProfile().build()

            val mGoogleSignInClient = GoogleSignIn.getClient(this, gso)

            val signInIntent = mGoogleSignInClient.signInIntent
            startActivityForResult(signInIntent, RC_SIGN_IN)
        }

        val fbookButton = findViewById<LoginButton>(R.id.facebookButton);

        val  callback = object: FacebookCallback<LoginResult>{
            override fun onSuccess(result: LoginResult) {
                System.out.println("entroSUccess")
                handleFacebookToken(result)
            }

            override fun onCancel() {
                System.out.println("entroCancel")
                TODO("not implemented") //To change body of created functions use File | Settings | File Templates.

            }

            override fun onError(error: FacebookException?) {
                System.out.println("entroError")
                TODO("not implemented") //To change body of created functions use File | Settings | File Templates.
            }
        }

        fbookButton.setPermissions("email", "user_birthday","user_posts");
        fbookButton.registerCallback(callbackFacebook, callback);

    }

    private fun handleFacebookToken(loginResult: LoginResult)  {
       val request: GraphRequest = GraphRequest.newMeRequest(loginResult.getAccessToken(), object:GraphRequest.GraphJSONObjectCallback {
           override fun onCompleted(objec: JSONObject, response: GraphResponse) {
               System.out.println("entro onComplented")
               try {
                   System.out.println("entroHandleFB")
                   val email = objec.getString("email");

                   if (userAlreadyExists(email)) {
                       if(isSitter(email))
                         startFragmentSitter()
                       else
                        startFragmentOwner()
                   }else {
                       val name = objec.getString("first_name");
                       val lastName = objec.getString("last_name");
                       val photo = "https://graph.facebook.com/" + (objec.getString("id"))+"/picture?width=500&height=500"
                       val birthday = SimpleDateFormat("dd/MM/yyyy").parse(objec.getString("birthday"))// O el formato es MM/dd/yyyy, lo averiguaremos
                       val user = User(UUID.randomUUID().toString(), name, lastName, email, "", birthday, photo, Location(0.0,0.0))

                       startFragmentRoleUser(user)
                   }

               } catch (e: JSONException) {
                   e.printStackTrace();
               } catch (e: MalformedURLException) {
                   e.printStackTrace();
               }
           }
       })
    }


    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)

        if (requestCode == RC_SIGN_IN) {
            val task: Task<GoogleSignInAccount> =
                GoogleSignIn.getSignedInAccountFromIntent(data)
                handleGoogleSignIn(task)
        }
        callbackFacebook.onActivityResult(requestCode, resultCode, data);
    }

    private fun handleGoogleSignIn(completedTask: Task<GoogleSignInAccount>) {
        try {
            val account = completedTask.getResult(ApiException::class.java)

            if(account!=null ) {
                val email = "" + account.email

                if (userAlreadyExists(email)) {
                    if(isSitter(email))
                        startFragmentSitter()
                    else
                        startFragmentOwner()

                }else {
                    val name = "" + account.givenName
                    val lastName = "" + account.familyName
                    var photo = account.photoUrl.toString()

                  if(photo==null)
                        photo=""

                    val user = User(UUID.randomUUID().toString(), name, lastName, email, "", Date(), photo, Location(0.0,0.0))

                    startFragmentRoleUser(user)
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

    private fun isSitter(email:String):Boolean{
        var isSitter = false

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
        return isSitter
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

    private  fun startFragmentSitter(){
        //Cambiar esto para que vaya hasta el mapa del sitter
        val intent = Intent(this, RegisterActivity::class.java)
        startActivity(intent)
    }

    private  fun startFragmentOwner(){
        //Cambiar esto para que vaya hasta el mapa del owner
        val intent = Intent(this, RegisterActivity::class.java)
        startActivity(intent)
    }

    private  fun startFragmentRoleUser(user:User){
        //Cambiar esto para que vaya hasta el fragment de roles
        val intent = Intent(this, RegisterActivity::class.java)
        intent.putExtra("user", user)
        startActivity(intent)
    }
}
