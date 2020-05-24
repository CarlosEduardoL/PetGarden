package zero.network.petgarden.ui.user.owner

import android.annotation.SuppressLint
import android.app.AlertDialog
import android.view.LayoutInflater.from
import android.view.View
import android.view.ViewGroup
import android.widget.BaseAdapter
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import zero.network.petgarden.databinding.RowPetBinding
import zero.network.petgarden.model.entity.Pet
import zero.network.petgarden.util.onClick

class PetsAdapter(private var pets: List<Pet>,private val modifyPet: (Pet)->Unit): BaseAdapter() {

    @SuppressLint("ViewHolder", "SetTextI18n")
    override fun getView(position: Int, convertView: View?, parent: ViewGroup) =
        RowPetBinding.inflate(from(parent.context), parent, false).apply {
            val pet:Pet = pets[position]

            breedTV.text = pet.breed
            agePet.text = "${pet.years}"
            recommendationsTV.text = pet.about
            CoroutineScope(Dispatchers.Main).launch { imagePet.setImageBitmap(pet.loadImage()) }

            root.onClick {
                AlertDialog.Builder(root.context)
                    .setMessage("¿Deseas editar la informacion de ${pet.name}?")
                    .setPositiveButton("Si"){dialog,_->
                        dialog.dismiss()
                        modifyPet(pet)
                    }.setNegativeButton("No"){dialog, _ ->
                        dialog.cancel()
                    }
                    .show()
            }
        }.root

     fun updateListPets(updatedPets: List<Pet>){
        pets = updatedPets
        notifyDataSetChanged()
    }

    override fun getItem(position: Int): Any = pets[position]

    override fun getItemId(position: Int): Long = position.toLong()

    override fun getCount(): Int = pets.size

}