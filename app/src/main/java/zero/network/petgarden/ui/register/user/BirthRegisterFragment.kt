package zero.network.petgarden.ui.register.user

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import kotlinx.android.synthetic.main.fragment_register_birth.view.*
import zero.network.petgarden.R
import zero.network.petgarden.model.entity.User
import zero.network.petgarden.ui.register.OnNextListener
import java.util.*

/**
 * A simple [Fragment] subclass.
 * @author CarlosEduardoL
 */
class BirthRegisterFragment(
    private val user: User,
    private val listener: OnNextListener
) : Fragment() {

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View = inflater.inflate(R.layout.fragment_register_birth, container, false).apply {
        val calendar = Calendar.getInstance();
        calendar.time = user.birthDay;

        birthInputDate.updateDate(calendar.get(Calendar.YEAR), calendar.get(Calendar.MONTH), calendar.get(Calendar.DAY_OF_MONTH))

    }

}
