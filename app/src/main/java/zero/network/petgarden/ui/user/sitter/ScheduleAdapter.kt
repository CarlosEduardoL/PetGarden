package zero.network.petgarden.ui.user.sitter

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import zero.network.petgarden.databinding.RowTaskBinding
import zero.network.petgarden.ui.base.BaseViewHolder
import zero.network.petgarden.ui.user.sitter.ScheduleAdapter.ClientHolder
import zero.network.petgarden.util.getDate

class ScheduleAdapter : RecyclerView.Adapter<ClientHolder>(){

    var tasks: List<Pair<String, Long>> = listOf()
        set(value) {
            field = value
            notifyDataSetChanged()
        }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int) = ClientHolder (
        RowTaskBinding.inflate(LayoutInflater.from(parent.context))
    )

    override fun getItemCount(): Int = tasks.size

    override fun onBindViewHolder(holder: ClientHolder, position: Int) = holder.bind(tasks[position])

    inner class ClientHolder(private val binding: RowTaskBinding): BaseViewHolder<Pair<String, Long>>(binding){
        override fun bind(element: Pair<String, Long>){
            binding.name.text = element.first
            binding.time.text = element.second.getDate("hh:mm:ss")
        }
    }
}