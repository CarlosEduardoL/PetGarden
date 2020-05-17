package zero.network.petgarden.ui.user.owner.recruitment

import android.annotation.SuppressLint
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


    @SuppressLint("ViewHolder")
    override fun getView(position: Int, convertView: View?, parent: ViewGroup): View {

        val view: View = LayoutInflater.from(parent.context).inflate(R.layout.row_select_pet, null,false)
        val pet: Pet = pets[position]

        val photo  = view.findViewById<ImageView>(R.id.imagePet)
        val name = view.findViewById<TextView>(R.id.namePet)

        name.text = pet.name
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