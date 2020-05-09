package zero.network.petgarden.ui.user.owner

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.AdapterView
import android.widget.BaseAdapter
import android.widget.ImageView
import android.widget.TextView
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import zero.network.petgarden.R
import zero.network.petgarden.model.entity.Pet
import zero.network.petgarden.tools.OnPetClickListener

class AdapterSelectPet(private val pets: List<Pet>, private val listener: OnPetClickListener): BaseAdapter(),AdapterView.OnItemClickListener {


    override fun getView(position: Int, convertView: View?, parent: ViewGroup?): View {
        val inflater =
            parent!!.context.getSystemService(Context.LAYOUT_INFLATER_SERVICE) as LayoutInflater

        val view: View = inflater.inflate(R.layout.row_select_pet, null)
        val pet: Pet = pets[position]

        val photo  = view.findViewById<ImageView>(R.id.imagePet)
        val name = view.findViewById<TextView>(R.id.namePet)

        name.setText(pet.name)
        CoroutineScope(Dispatchers.Main).launch { photo.setImageBitmap(pet.loadImage()) }
        return view
    }

    override fun getItem(position: Int): Any { return Any() }
    override fun getItemId(position: Int): Long {return 1}
    override fun getCount(): Int {return 1}

    override fun onItemClick(parent: AdapterView<*>?, view: View?, position: Int, id: Long) {
        listener.onPetClick(pets[position])
    }

}