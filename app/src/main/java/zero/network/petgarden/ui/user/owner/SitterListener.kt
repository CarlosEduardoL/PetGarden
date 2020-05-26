package zero.network.petgarden.ui.user.owner

import android.util.Log
import com.google.firebase.database.ChildEventListener
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.FirebaseDatabase
import zero.network.petgarden.model.entity.SitterIMP
import zero.network.petgarden.ui.base.PetGardenActivity

abstract class SitterListener: PetGardenActivity() {

    private val sitters = mutableListOf<SitterIMP>()
    private fun DataSnapshot.toSitter() = getValue(SitterIMP::class.java)!!
    private lateinit var listener: ChildEventListener

    override fun onResume() {
        super.onResume()
        sitters.removeAll { true }
        listener = FirebaseDatabase.getInstance()
            .reference.child("sitters").addChildEventListener(object : ChildEventListener {
                override fun onCancelled(error: DatabaseError) { Log.d("Error",error.message) }
                override fun onChildMoved(data: DataSnapshot, id: String?) { Log.d("Error","Esto no deberia pasar nunca, Asustate") }
                override fun onChildChanged(data: DataSnapshot, id: String?) {
                    val sitter = data.toSitter()
                    sitters.remove(sitters.first{ sitter.id == it.id })
                    sitters.add(sitter)
                    onSittersUpdate(sitters.sortedByDescending { it.rating })
                }
                override fun onChildAdded(data: DataSnapshot, id: String?) {
                    sitters.add(data.toSitter())
                    onSittersUpdate(sitters.sortedByDescending { it.rating })
                }
                override fun onChildRemoved(data: DataSnapshot) {
                    sitters.remove(data.toSitter())
                    onSittersUpdate(sitters.sortedByDescending { it.rating })
                }
            })
    }

    abstract fun onSittersUpdate(sitters: List<SitterIMP>)

    override fun onPause() {
        super.onPause()
        FirebaseDatabase.getInstance()
            .reference.child("sitters").removeEventListener(listener)
    }

}