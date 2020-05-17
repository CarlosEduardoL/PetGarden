package zero.network.petgarden.ui.element

import android.content.Intent
import android.content.Intent.*
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.View.VISIBLE
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import com.google.firebase.auth.FirebaseAuth
import zero.network.petgarden.databinding.FragmentActionBarBinding
import zero.network.petgarden.model.behaivor.CallBack
import zero.network.petgarden.ui.login.LoginActivity
import zero.network.petgarden.util.onClick


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
                FirebaseAuth.getInstance().signOut()
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


}
