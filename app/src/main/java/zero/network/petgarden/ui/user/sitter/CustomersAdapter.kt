package zero.network.petgarden.ui.user.sitter

import android.content.Context
import android.database.DataSetObserver
import android.graphics.Typeface
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
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


class CustomersAdapter(private val ownersAndPets: Map<Owner, Set<Pet>>, private val context: Context): ExpandableListAdapter{

    private val listHeaders = ownersAndPets.keys.toList()

    override fun getChildrenCount(listPosition:Int): Int {
        return ownersAndPets.get(listHeaders.get(listPosition))!!.size
    }

    override fun getGroup(groupPosition: Int): Any {
        return listHeaders[groupPosition]
    }

    override fun onGroupCollapsed(groupPosition: Int) {
        TODO("not implemented") //To change body of created functions use File | Settings | File Templates.
    }

    override fun isEmpty(): Boolean {
        TODO("not implemented") //To change body of created functions use File | Settings | File Templates.
    }

    override fun registerDataSetObserver(observer: DataSetObserver?) {
        TODO("not implemented") //To change body of created functions use File | Settings | File Templates.
    }

    override fun getChild(listPosition: Int, expandedListPosition: Int): Any {
        return ownersAndPets.get(listHeaders.get(listPosition))!!.toList()[expandedListPosition]
    }

    override fun onGroupExpanded(groupPosition: Int) {
        TODO("not implemented") //To change body of created functions use File | Settings | File Templates.
    }

    override fun getCombinedChildId(groupId: Long, childId: Long): Long {
        TODO("not implemented") //To change body of created functions use File | Settings | File Templates.
    }

    override fun getGroupId(groupPosition: Int): Long {
        return groupPosition.toLong()
    }

    override fun isChildSelectable(groupPosition: Int, childPosition: Int): Boolean {
        TODO("not implemented") //To change body of created functions use File | Settings | File Templates.
    }

    override fun hasStableIds(): Boolean {
        TODO("not implemented") //To change body of created functions use File | Settings | File Templates.
    }

    override fun getChildView(listPosition: Int, expandedListPosition: Int,
                              isLastChild:Boolean, convertView:View, parent: ViewGroup): View {

        var convertView = convertView

        if (convertView == null) {
            val layoutInflater = this.context.getSystemService(Context.LAYOUT_INFLATER_SERVICE) as LayoutInflater
            convertView = layoutInflater.inflate(R.layout.row_pet, null)
        }

        val pet = getChild(listPosition, expandedListPosition) as Pet
            //setear la info de los pets

        convertView.apply { breedTV.text = pet.breed
                            agePet.text = ""+pet.years+" años"
                            recommendationsTV.text = pet.about
            CoroutineScope(Dispatchers.Main).launch { imagePet.setImageBitmap(pet.loadImage()) }


        }
        return convertView
    }

    override fun areAllItemsEnabled(): Boolean {
        TODO("not implemented") //To change body of created functions use File | Settings | File Templates.
    }

    override fun getChildId(groupPosition: Int, childPosition: Int): Long {
        return childPosition.toLong()
    }

    override fun getCombinedGroupId(groupId: Long): Long {
        TODO("not implemented") //To change body of created functions use File | Settings | File Templates.
    }

    override fun getGroupView(listPosition:Int, isExpanded: Boolean,
                              convertView: View, parent: ViewGroup): View {
        var convertView = convertView

        if (convertView == null) {
            val layoutInflater =
                context.getSystemService(Context.LAYOUT_INFLATER_SERVICE) as LayoutInflater
            convertView = layoutInflater.inflate(R.layout.row_client, null)
        }

        val owner = getGroup(listPosition) as Owner
        convertView.apply {
            CoroutineScope(Dispatchers.Main).launch { photoOwnerList.setImageBitmap(owner.image()) }
            nameOwnerList.text = owner.name
            emailOwnerList.text = owner.email

        }
        return convertView
    }

    override fun unregisterDataSetObserver(observer: DataSetObserver?) {
        TODO("not implemented") //To change body of created functions use File | Settings | File Templates.
    }

    override fun getGroupCount(): Int {
        return listHeaders.size
    }

}
