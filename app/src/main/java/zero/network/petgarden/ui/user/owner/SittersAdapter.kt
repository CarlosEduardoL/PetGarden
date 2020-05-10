package zero.network.petgarden.ui.user.owner

import android.annotation.SuppressLint
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.FragmentActivity
import androidx.fragment.app.FragmentTransaction
import androidx.recyclerview.widget.RecyclerView
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers.Main
import kotlinx.coroutines.launch
import zero.network.petgarden.R
import zero.network.petgarden.databinding.RowSitterBinding
import zero.network.petgarden.model.entity.Owner
import zero.network.petgarden.model.entity.Sitter
import zero.network.petgarden.util.getDate

class SittersAdapter(var sitters: List<Sitter>, owner: Owner) :
    RecyclerView.Adapter<SittersAdapter.SitterViewHolder>() {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): SitterViewHolder = SitterViewHolder(
            RowSitterBinding.inflate(LayoutInflater.from(parent.context))
        )

    override fun getItemCount(): Int = sitters.size

    override fun onBindViewHolder(holder: SitterViewHolder, position: Int) =
        holder.bind(sitters[position])

    class SitterViewHolder(private val view: RowSitterBinding) :
        RecyclerView.ViewHolder(view.root) {

        private var job = CoroutineScope(Main).launch { }

        fun bind(sitter: Sitter) {
            job.cancel()
            job = CoroutineScope(Main).launch { view.photoSitterList.setImageBitmap(sitter.image()) }
            view.MyRating.rating = sitter.rating.toFloat()
            view.nameSitterList.text = "${sitter.name} ${sitter.lastName}"
            sitter.availability?.let {
                view.schedule.text = "${it.start.getDate("hh:mm:ss")} a ${it.end.getDate("hh:mm:ss")}"
                view.price.text = "$${it.cost}"
            }


            itemView.setOnClickListener(View.OnClickListener { v: View ->
                val fragmentProfile = SitterProfileFromUser(sitter, (itemView.context as OwnerActivity))
                val fragmentManager = (itemView.context as OwnerActivity).supportFragmentManager
                val fragmentTransaction: FragmentTransaction =
                    fragmentManager.beginTransaction()
                fragmentTransaction.add(R.id.activity_owner_container, fragmentProfile, null)
                fragmentTransaction.commit()
            })
        }
    }

}
