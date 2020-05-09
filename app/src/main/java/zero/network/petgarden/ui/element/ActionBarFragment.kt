package zero.network.petgarden.ui.element

import android.content.Intent
import android.content.Intent.*
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import com.google.firebase.auth.FirebaseAuth
import kotlinx.android.synthetic.main.fragment_action_bar.view.*
import zero.network.petgarden.R
import zero.network.petgarden.ui.login.LoginActivity


/**
 * A simple [Fragment] subclass.
 */
class ActionBarFragment(
    private val title: String,
    private val backButton: Boolean = false,
    private val closeButton: Boolean = false,
    private val onBack: () -> Unit
) : Fragment() {

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View = inflater.inflate(R.layout.fragment_action_bar, container, false).apply {
        titleText.text = title
        backButton.apply {
            if (!this@ActionBarFragment.backButton) visibility = View.GONE
            setOnClickListener{ onBack() }
        }
        exitButton.apply {
            if (!this@ActionBarFragment.closeButton) visibility = View.GONE
            setOnClickListener {
                FirebaseAuth.getInstance().signOut()

                Intent(activity, LoginActivity::class.java).apply {
                    addFlags(FLAG_ACTIVITY_NEW_TASK or FLAG_ACTIVITY_CLEAR_TASK or FLAG_ACTIVITY_CLEAR_TOP)
                    activity?.startActivity(this)
                }
            }
        }
    }

}
