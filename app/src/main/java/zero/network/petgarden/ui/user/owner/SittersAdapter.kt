package zero.network.petgarden.ui.user.owner

import android.annotation.SuppressLint
import android.content.Intent
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.FragmentActivity
import androidx.fragment.app.FragmentTransaction
import androidx.recyclerview.widget.RecyclerView
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers.Main
import kotlinx.coroutines.launch
import zero.network.petgarden.databinding.RowSitterBinding
import zero.network.petgarden.model.entity.Owner
import zero.network.petgarden.model.entity.Sitter
import zero.network.petgarden.util.getDate

class SittersAdapter(var sitters: List<Sitter>, var owner: Owner) :
    RecyclerView.Adapter<SittersAdapter.SitterViewHolder>() {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): SitterViewHolder = SitterViewHolder(
            RowSitterBinding.inflate(LayoutInflater.from(parent.context))
        )

    override fun getItemCount(): Int = sitters.size

    override fun onBindViewHolder(holder: SitterViewHolder, position: Int) =
        holder.bind(sitters[position], owner)

    class SitterViewHolder(private val view: RowSitterBinding) :
        RecyclerView.ViewHolder(view.root) {

        private var job = CoroutineScope(Main).launch { }

        @SuppressLint("SetTextI18n")
        fun bind(sitter: Sitter, owner: Owner) {
            job.cancel()
            job = CoroutineScope(Main).launch { view.photoSitterList.setImageBitmap(sitter.image()) }
            view.MyRating.rating = sitter.rating.toFloat()
            view.nameSitterList.text = "${sitter.name} ${sitter.lastName}"

            sitter.availability?.let {
                view.schedule.text = "Dispoinibilidad horaria:  ${it.start.getDate("hh:mm:ss")} a ${it.end.getDate("hh:mm:ss")}"
                view.price.text = "Precio:  ${it.cost}/hora"
            }

            if (sitter.availability==null){
                view.schedule.text = "Horario: No disponible"
                view.price.text = "Precio: No disponible"
            }



            itemView.setOnClickListener(View.OnClickListener { v: View ->
                val currentActivity = v.context
                val i = Intent(currentActivity, SitterFromUserActivity::class.java)
                i.putExtra("sitter", sitter)
                i.putExtra("owner", owner)
                currentActivity.startActivity(i)
            })
        }
    }

}
