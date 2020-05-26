package zero.network.petgarden.ui.user.owner

import android.annotation.SuppressLint
import android.content.Intent
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import kotlinx.coroutines.*
import kotlinx.coroutines.Dispatchers.Main
import zero.network.petgarden.databinding.RowSitterBinding
import zero.network.petgarden.model.entity.Owner
import zero.network.petgarden.model.entity.SitterIMP
import zero.network.petgarden.ui.base.BaseListAdapter
import zero.network.petgarden.ui.base.BaseViewHolder
import zero.network.petgarden.ui.user.owner.recruitment.SitterFromUserActivity
import zero.network.petgarden.util.getDate

class SittersAdapter(val owner: Owner, sitters: List<SitterIMP> = listOf()) :
    BaseListAdapter<SitterIMP>(sitters) {

    private val scope = CoroutineScope(Main)

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): SitterViewHolder =
        SitterViewHolder(
            RowSitterBinding.inflate(LayoutInflater.from(parent.context)), scope
        )

    fun stopAnimation() {
        if (scope.isActive) scope.cancel()
    }

    inner class SitterViewHolder(private val view: RowSitterBinding, private val scope: CoroutineScope) :
        BaseViewHolder<SitterIMP>(view) {

        private var loadImage = scope.launch { }
        private var animation = scope.launch { }

        @SuppressLint("SetTextI18n")
        override fun bind(element: SitterIMP) {
            if (loadImage.isActive) loadImage.cancel()
            if (animation.isActive) animation.cancel()
            loadImage = scope.launch { view.photoSitterList.setImageBitmap(element.image()) }
            view.MyRating.rating = element.rating.toFloat()
            view.nameSitterList.text = "${element.name} ${element.lastName}"

            if (element.planner.availabilities.isEmpty()) {
                view.schedule.text = "Horario: No disponible"
                view.price.text = "Precio: No disponible"
            } else {
                animation = scope.launch {
                    while (isActive)
                        element.planner.availabilities.forEach {
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
                i.putExtra("sitter", element)
                i.putExtra("owner", owner)
                currentActivity.startActivity(i)
            }
        }


    }

}
