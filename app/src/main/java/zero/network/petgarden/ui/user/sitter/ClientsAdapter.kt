package zero.network.petgarden.ui.user.sitter

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.BaseAdapter
import android.widget.ImageView
import android.widget.TextView
import kotlinx.android.synthetic.main.fragment_add_task.*
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import zero.network.petgarden.R
import zero.network.petgarden.model.entity.Owner
import zero.network.petgarden.model.entity.Pet
import zero.network.petgarden.model.entity.Sitter

class ClientsAdapter(private val ownersAndPets: Map<Owner, Set<Pet>>): BaseAdapter() {
    private val owners = ArrayList<Owner>(ownersAndPets.keys)
    private val pets = ArrayList<Set<Pet>>(ownersAndPets.values)


    override fun getView(position: Int, convertView: View?, parent: ViewGroup?): View {

        val inflater =
            parent!!.context.getSystemService(Context.LAYOUT_INFLATER_SERVICE) as LayoutInflater
        val view: View = inflater.inflate(R.layout.row_client, null)

        //Clients
        val nameOwner = view.findViewById<TextView>(R.id.nameOwnerList)
        val emailOwner= view.findViewById<TextView>(R.id.emailOwnerList)
        val photoOwner = view.findViewById<ImageView>(R.id.photoOwnerList)

        //Pet
        val imagePet = view.findViewById<ImageView>(R.id.imagePetList)
        val breed = view.findViewById<TextView>(R.id.breedListTV)
        val age = view.findViewById<TextView>(R.id.agePetList)
        val recommendations = view.findViewById<TextView>(R.id.recommendationsList)

        val owner: Owner = owners.get(position)
        val pet:Pet = pets.get(position).first() //Corregir

        nameOwner.setText(owner.name)
        emailOwner.setText(owner.email)
        CoroutineScope(Dispatchers.Main).launch { photoOwner.setImageBitmap(owner.image()) }


        CoroutineScope(Dispatchers.Main).launch { imagePet.setImageBitmap(pet.loadImage()) }
        breed.setText(pet.breed)
        age.setText(pet.years)
        recommendations.setText(pet.about)

        return view
    }

    override fun getItem(position: Int): Any {
        return owners[position]
    }

    override fun getItemId(position: Int): Long {
        TODO("not implemented") //To change body of created functions use File | Settings | File Templates.
    }

    override fun getCount(): Int {
        return owners.size
    }
}
