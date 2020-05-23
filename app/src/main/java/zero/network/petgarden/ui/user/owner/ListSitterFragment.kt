package zero.network.petgarden.ui.user.owner

import android.content.Intent
import android.os.Bundle
import android.text.Editable
import android.text.TextWatcher
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.appcompat.app.AppCompatActivity
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.LinearLayoutManager
import zero.network.petgarden.databinding.FragmentListSitterBinding
import zero.network.petgarden.model.component.Filter
import zero.network.petgarden.util.filter


class ListSitterFragment(view: OwnerView) : Fragment(), OwnerView by view {


    private var filter = Filter()
    private var adapterSitters: SittersAdapter? = null

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View = FragmentListSitterBinding.inflate(inflater, container, false).apply {

        adapterSitters?.stopAnimation()
        adapterSitters = SittersAdapter(owner, sitters.filter(filter))

        listSitters.apply {
            layoutManager = LinearLayoutManager(context)
            adapter = adapterSitters
        }
        searchBar.addTextChangedListener(object : TextWatcher {
            override fun afterTextChanged(s: Editable?) {}
            override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {}
            override fun onTextChanged(s: CharSequence, start: Int, before: Int, count: Int) {
                adapterSitters?.update(sitters.filter { "${it.name} ${it.lastName}".contains(s,true) }.filter(filter))
            }

        })

        filterButton.setOnClickListener{
            val i = Intent(context, FilterActivity::class.java)
            startActivityForResult(i, FILTER)
        }

    }.root

    override fun onDetach() {
        super.onDetach()
        adapterSitters?.stopAnimation()
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        if (resultCode == AppCompatActivity.RESULT_OK && data != null && requestCode == FILTER){
            filter = data.extras!!.getSerializable("filter") as Filter
        }
    }

    companion object {
        private const val FILTER = 1
    }
}