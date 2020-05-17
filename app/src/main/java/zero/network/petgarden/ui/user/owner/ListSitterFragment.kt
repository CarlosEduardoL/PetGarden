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


class ListSitterFragment(view: OwnerView) : Fragment(), OwnerView by view {

    private var _sitters: List<Sitter>? = null
        get() = if (field == null) sitters
        else field

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View = FragmentListSitterBinding.inflate(inflater, container, false).apply {

        val adapterSitters = SittersAdapter(owner, _sitters!!)

        listSitters.apply {
            layoutManager = LinearLayoutManager(activity)
            adapter = adapterSitters
        }
        searchBar.addTextChangedListener(object : TextWatcher {
            override fun afterTextChanged(s: Editable?) {}
            override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {}
            override fun onTextChanged(s: CharSequence, start: Int, before: Int, count: Int) {
                adapterSitters.update(_sitters!!.filter { "${it.name} ${it.lastName}".contains(s,true) })
            }

        })

        filterButton.setOnClickListener{
            val i = Intent(activity, FilterActivity::class.java)
            i.putExtra("sitters", sitters as Serializable)
            startActivityForResult(i, FILTER)
        }

    }.root


    fun updateSitters(sitters: List<Sitter>){
        _sitters = sitters.toSet().toList()
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        _sitters = data!!.extras!!.getSerializable("sittersFiltered") as List<Sitter>
    }

    companion object {
        private const val FILTER = 1
    }
}