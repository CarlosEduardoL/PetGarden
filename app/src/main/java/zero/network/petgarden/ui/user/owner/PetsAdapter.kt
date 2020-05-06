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

class PetsAdapter(private val fragment: UserProfileFragment): BaseAdapter() {


    override fun getView(position: Int, convertView: View?, parent: ViewGroup?): View {

        val inflater =
            parent!!.context.getSystemService(Context.LAYOUT_INFLATER_SERVICE) as LayoutInflater
        val view: View = inflater.inflate(R.layout.row_pet, null)

        val pet:Pet =fragment.getOwner().pets.get(position)

        val breed = view.findViewById<TextView>(R.id.breedTV)
        val age = view.findViewById<TextView>(R.id.agePet)
        val recommendations = view.findViewById<TextView>(R.id.recommendationsTV)
        val photo  = view.findViewById<ImageView>(R.id.imagePet)

        breed.setText(pet.breed)
        age.setText(pet.years)
        recommendations.setText(pet.about)
        CoroutineScope(Dispatchers.Main).launch { photo.setImageBitmap(pet.loadImage()) }

        return view
    }

    override fun getItem(position: Int): Any {
        return fragment.getOwner().pets.get(position)
    }

    override fun getItemId(position: Int): Long {
        TODO("not implemented") //To change body of created functions use File | Settings | File Templates.
    }

    override fun getCount(): Int {
        return fragment.getOwner().pets.size
    }
}