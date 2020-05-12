package zero.network.petgarden.ui.user.sitter

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import zero.network.petgarden.databinding.RowTaskBinding
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

    class ClientHolder(private val binding: RowTaskBinding): RecyclerView.ViewHolder(binding.root){
        fun bind(task: Pair<String, Long>){
            binding.name.text = task.first
            binding.time.text = task.second.getDate("hh:mm:ss")
        }
    }
}