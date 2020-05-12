package zero.network.petgarden.ui.user.owner

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.BaseAdapter
import android.widget.ImageView
import android.widget.TextView
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import zero.network.petgarden.R
import zero.network.petgarden.model.entity.Pet

class PetsAdapter(private var pets: List<Pet>): BaseAdapter() {


    override fun getView(position: Int, convertView: View?, parent: ViewGroup?): View {

        val inflater =
            parent!!.context.getSystemService(Context.LAYOUT_INFLATER_SERVICE) as LayoutInflater
        val view: View = inflater.inflate(R.layout.row_pet, null)

        val pet:Pet = pets[position]

        val breed = view.findViewById<TextView>(R.id.breedTV)
        val age = view.findViewById<TextView>(R.id.agePet)
        val recommendations = view.findViewById<TextView>(R.id.recommendationsTV)
        val photo  = view.findViewById<ImageView>(R.id.imagePet)

        breed.text = pet.breed
        age.text = ""+pet.years
        recommendations.text = pet.about
        CoroutineScope(Dispatchers.Main).launch { photo.setImageBitmap(pet.loadImage()) }

        return view
    }

     fun updateListPets(updatedPets: List<Pet>){
        pets = updatedPets
        notifyDataSetChanged()
    }

    override fun getItem(position: Int): Any = pets[position]

    override fun getItemId(position: Int): Long = position.toLong()

    override fun getCount(): Int = pets.size


}