package zero.network.petgarden.ui.user.owner

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import zero.network.petgarden.R
import zero.network.petgarden.databinding.FragmentListSitterBinding

class ListSitterFragment(view: OwnerView) : Fragment(), OwnerView by view {



    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View = FragmentListSitterBinding.inflate(inflater, container, false).apply {

    }.root
}