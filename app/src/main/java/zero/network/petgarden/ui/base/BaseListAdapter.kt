package zero.network.petgarden.ui.base

import androidx.recyclerview.widget.RecyclerView

abstract class BaseListAdapter<T>(private var elements: List<T>): RecyclerView.Adapter<BaseViewHolder<T>>() {

    override fun getItemCount(): Int = elements.size

    override fun onBindViewHolder(holder: BaseViewHolder<T>, position: Int) {
        holder.bind(elements[position])
    }

    fun update(elements: List<T>) {
        this.elements = elements
        notifyDataSetChanged()
    }

}