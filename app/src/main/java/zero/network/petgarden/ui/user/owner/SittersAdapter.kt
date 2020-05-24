package zero.network.petgarden.ui.user.owner

import android.annotation.SuppressLint
import android.content.Intent
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import kotlinx.coroutines.*
import kotlinx.coroutines.Dispatchers.Main
import zero.network.petgarden.databinding.RowSitterBinding
import zero.network.petgarden.model.entity.Owner
import zero.network.petgarden.model.entity.Sitter
import zero.network.petgarden.ui.user.owner.recruitment.SitterFromUserActivity
import zero.network.petgarden.util.getDate

class SittersAdapter(val owner: Owner, var sitters: List<Sitter> = listOf()) :
    RecyclerView.Adapter<SittersAdapter.SitterViewHolder>() {

    private val scope = CoroutineScope(Main)

    fun update(sitters: List<Sitter>) {
        this.sitters = sitters
        notifyDataSetChanged()
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): SitterViewHolder =
        SitterViewHolder(
            RowSitterBinding.inflate(LayoutInflater.from(parent.context)), scope
        )

    override fun getItemCount(): Int = sitters.size

    override fun onBindViewHolder(holder: SitterViewHolder, position: Int) =
        holder.bind(sitters[position], owner)

    fun stopAnimation() {
        if (scope.isActive) scope.cancel()
    }

    class SitterViewHolder(private val view: RowSitterBinding, private val scope: CoroutineScope) :
        RecyclerView.ViewHolder(view.root) {

        private var loadImage = scope.launch { }
        private var animation = scope.launch { }

        @SuppressLint("SetTextI18n")
        fun bind(sitter: Sitter, owner: Owner) {
            if (loadImage.isActive) loadImage.cancel()
            if (animation.isActive) animation.cancel()
            loadImage = scope.launch { view.photoSitterList.setImageBitmap(sitter.image()) }
            view.MyRating.rating = sitter.rating.toFloat()
            view.nameSitterList.text = "${sitter.name} ${sitter.lastName}"

            if (sitter.planner.availabilities.isEmpty()) {
                view.schedule.text = "Horario: No disponible"
                view.price.text = "Precio: No disponible"
            } else {
                animation = scope.launch {
                    while (isActive)
                        sitter.planner.availabilities.forEach {
                            view.schedule.text =
                                """Disponible desde: ${it.start.getDate("MM/dd hh:mm a")}
                                    |Disponible hasta: ${it.end.getDate("MM/dd hh:mm a")}""".trimMargin()
                            view.price.text = "Precio:  ${it.cost}/hora"
                            delay(3000)
                        }
                }
            }

            itemView.setOnClickListener { v: View ->
                val currentActivity = v.context
                val i = Intent(currentActivity, SitterFromUserActivity::class.java)
                i.putExtra("sitter", sitter)
                i.putExtra("owner", owner)
                currentActivity.startActivity(i)
            }
        }


    }

}
