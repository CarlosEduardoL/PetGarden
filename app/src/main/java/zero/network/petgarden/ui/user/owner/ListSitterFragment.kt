package zero.network.petgarden.ui.user.owner

import android.content.Intent
import android.os.Bundle
import android.text.Editable
import android.text.TextWatcher
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.LinearLayoutManager
import zero.network.petgarden.databinding.FragmentListSitterBinding
import zero.network.petgarden.model.entity.Sitter
import java.io.Serializable
import java.util.logging.Filter


class ListSitterFragment(view: OwnerView) : Fragment(), OwnerView by view {

    private lateinit var adapterSitters: SittersAdapter

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View = FragmentListSitterBinding.inflate(inflater, container, false).apply {

         adapterSitters = SittersAdapter(sitters.toSet().toList(), owner)
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
            val i = Intent(activity, FilterActivity::class.java)
            i.putExtra("sitters", sitters as Serializable)
            startActivityForResult(i, FILTER)
        }

    }.root

    override fun onDestroyView() {
        super.onDestroyView()
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        adapterSitters.sitters = data!!.extras!!.getSerializable("sittersFiltered") as List<Sitter>
        adapterSitters.notifyDataSetChanged()
    }

    companion object {
        private const val FILTER = 1
    }
}