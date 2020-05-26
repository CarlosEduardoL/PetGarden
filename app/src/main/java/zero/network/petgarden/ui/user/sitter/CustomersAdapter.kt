package zero.network.petgarden.ui.user.sitter
import android.content.Context
import android.database.DataSetObserver
import android.graphics.Typeface
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.BaseExpandableListAdapter
import android.widget.ExpandableListAdapter
import android.widget.TextView
import kotlinx.android.synthetic.main.row_client.view.*
import kotlinx.android.synthetic.main.row_pet.view.*
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import zero.network.petgarden.R
import zero.network.petgarden.model.entity.Owner
import zero.network.petgarden.model.entity.Pet


class CustomersAdapter(private val context: Context, private val ownersAndPets: Map<Owner, Set<Pet>>): BaseExpandableListAdapter() {

    private val listHeaders = ownersAndPets.keys.toList()


    override fun getGroup(groupPosition: Int): Any {
        return listHeaders[groupPosition]

    }

    override fun isChildSelectable(groupPosition: Int, childPosition: Int): Boolean {
        return true
    }

    override fun hasStableIds(): Boolean {
        return false
    }


    override fun getChildrenCount(groupPosition: Int): Int {
        return 1
    }

    override fun getChild(listPosition: Int, expandedListPosition: Int): Any {
        return ownersAndPets.get(listHeaders.get(listPosition))!!.toList()[expandedListPosition]
    }

    override fun getGroupId(groupPosition: Int): Long {
        return groupPosition.toLong()
    }

    override fun getGroupView(listPosition:Int, isExpanded: Boolean,
                              convertView: View?, parent: ViewGroup): View {
        println("-----Entro al inflater de los clientes-------")


        var convertView = convertView

        if (convertView == null) {
            val layoutInflater =
                this.context.getSystemService(Context.LAYOUT_INFLATER_SERVICE) as LayoutInflater
            convertView = layoutInflater.inflate(R.layout.row_client, null)
        }

        val owner = getGroup(listPosition) as Owner
        convertView?.apply {
            CoroutineScope(Dispatchers.Main).launch { photoOwnerList.setImageBitmap(owner.image()) }
            nameOwnerList.text = owner.name
            emailOwnerList.text = owner.email

        }
        return convertView!!
    }

    override fun getChildView(listPosition: Int, expandedListPosition: Int,
                              isLastChild:Boolean, convertView:View?, parent: ViewGroup): View {

        println("-----Entro al inflater de las mascotas-------")

        var convertView = convertView

        if (convertView == null) {
            val layoutInflater = this.context.getSystemService(Context.LAYOUT_INFLATER_SERVICE) as LayoutInflater
            convertView = layoutInflater.inflate(R.layout.row_pet, null)
        }

        val pet = getChild(listPosition, expandedListPosition) as Pet

        convertView?.apply { breedTV.text = pet.breed
            agePet.text = ("${pet.years} años")
            recommendationsTV.text = pet.about
            CoroutineScope(Dispatchers.Main).launch { imagePet.setImageBitmap(pet.loadImage()) }


        }
        return convertView!!
    }

    override fun getChildId(groupPosition: Int, childPosition: Int): Long {
       return childPosition.toLong()
    }

    override fun getGroupCount(): Int {
        return listHeaders.size

    }
}
