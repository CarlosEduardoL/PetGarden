package zero.network.petgarden.ui.user.owner

import android.os.Bundle
import android.text.Editable
import android.text.TextWatcher
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.LinearLayoutManager
import zero.network.pet.filterFragment
import zero.network.petgarden.R
import zero.network.petgarden.databinding.FragmentListSitterBinding

class ListSitterFragment(view: OwnerView) : Fragment(), OwnerView by view {

    private lateinit var filterFragment: filterFragment
    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View = FragmentListSitterBinding.inflate(inflater, container, false).apply {
        val adapterSitters = SittersAdapter(sitters)
        listSitters.apply {
            layoutManager = LinearLayoutManager(activity)
            adapter = adapterSitters
        }
        searchBar.addTextChangedListener(object : TextWatcher {
            override fun afterTextChanged(s: Editable?) {}
            override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {}
            override fun onTextChanged(s: CharSequence, start: Int, before: Int, count: Int) {
                adapterSitters.sitters = sitters.filter { "${it.name} ${it.lastName}".contains(s,true) }
                adapterSitters.notifyDataSetChanged()
            }

        })

        filterButton.setOnClickListener{
            if(filterFragment==null) filterFragment = filterFragment(adapterSitters, sitters)

            val fragmentManager = activity!!.supportFragmentManager
            val fragmentTransaction = fragmentManager!!.beginTransaction()
            fragmentTransaction.add(R.id.activity_owner_container, filterFragment)
            fragmentTransaction.commit()
        }

    }.root
}