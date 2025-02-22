package zero.network.petgarden.ui.element

import android.content.Intent
import android.content.Intent.*
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.View.VISIBLE
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import com.facebook.AccessToken
import com.google.android.gms.auth.api.signin.GoogleSignIn
import com.google.android.gms.auth.api.signin.GoogleSignInOptions
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.iid.FirebaseInstanceId
import com.google.firebase.messaging.FirebaseMessaging
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import zero.network.petgarden.databinding.FragmentActionBarBinding
import zero.network.petgarden.model.behaivor.CallBack
import zero.network.petgarden.model.behaivor.Entity
import zero.network.petgarden.model.entity.Owner
import zero.network.petgarden.ui.login.LoginActivity
import zero.network.petgarden.util.onClick
import zero.network.petgarden.util.userByEmail


/**
 * A simple [Fragment] subclass.
 */
class ActionBarFragment(
    private val title: String,
    private val isBackButton: Boolean = false,
    private val isCloseButton: Boolean = false,
    private val onBack: () -> Unit = {}
) : Fragment() {

    @JvmOverloads constructor(
        title: String,
        isBackButton: Boolean = false,
        isCloseButton: Boolean = false,
        onBack: CallBack<Boolean> = CallBack {}
    ) : this(title, isBackButton, isCloseButton, { onBack.onResult(isBackButton) })

    private lateinit var binding: FragmentActionBarBinding

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View = FragmentActionBarBinding.inflate(layoutInflater, container, false).apply {
        binding = this
        titleText.text = title
        backButton.apply {
            if (!isBackButton) visibility = View.GONE
            onClick { onBack() }
        }
        exitButton.apply {
            if (!isCloseButton) visibility = View.GONE
            onClick {
                val emailUser = FirebaseAuth.getInstance().currentUser!!.email
                CoroutineScope(Dispatchers.Main).launch {
                    when(val currentUser = userByEmail(emailUser!!)) {
                         is Entity -> FirebaseMessaging.getInstance().unsubscribeFromTopic(currentUser.id) }}

                FirebaseAuth.getInstance().signOut()
                deleteToken()
                Intent(activity, LoginActivity::class.java).apply {
                    addFlags(FLAG_ACTIVITY_NEW_TASK or FLAG_ACTIVITY_CLEAR_TASK or FLAG_ACTIVITY_CLEAR_TOP)
                    activity?.startActivity(this)
                }
            }
        }
    }.root

    fun update(title: String = this.title, isBackButton: Boolean = this.isBackButton, isCloseButton: Boolean = this.isCloseButton)= binding.apply {
        titleText.text = title
        if (!isBackButton) backButton.visibility = View.GONE
        else backButton.visibility = VISIBLE
        if (!isCloseButton) exitButton.visibility = View.GONE
        else exitButton.visibility = VISIBLE
    }

    fun setVisibility(visibility:Boolean){
        if(visibility) binding.root.visibility = VISIBLE
        else binding.root.visibility = View.GONE
    }


    fun deleteToken(){
        if (AccessToken.getCurrentAccessToken()!=null)
            AccessToken.setCurrentAccessToken(null)

        if (GoogleSignIn.getLastSignedInAccount(context)!=null) {
            val gso =
                GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN).build()

            val googleSignInClient = GoogleSignIn.getClient(context!!, gso)
            googleSignInClient.signOut()
        }
    }
}
