package zero.network.petgarden.ui.user.owner

import android.util.Log
import androidx.appcompat.app.AppCompatActivity
import com.google.firebase.database.ChildEventListener
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.FirebaseDatabase
import zero.network.petgarden.model.behaivor.CallBack
import zero.network.petgarden.model.entity.Sitter

abstract class SitterListener: AppCompatActivity() {

    private val sitters = mutableListOf<Sitter>()
    private fun DataSnapshot.toSitter() = getValue(Sitter::class.java)!!
    private lateinit var listener: ChildEventListener

    override fun onResume() {
        super.onResume()
        listener = FirebaseDatabase.getInstance()
            .reference.child("sitters").addChildEventListener(object : ChildEventListener {
                override fun onCancelled(error: DatabaseError) { Log.d("Error",error.message) }
                override fun onChildMoved(data: DataSnapshot, id: String?) { Log.d("Error","Esto no deberia pasar nunca, Asustate") }
                override fun onChildChanged(data: DataSnapshot, id: String?) {
                    val sitter = data.toSitter()
                    sitters.remove(sitters.first{ sitter.id == it.id })
                    sitters.add(sitter)
                    onSittersUpdate(sitters)
                }
                override fun onChildAdded(data: DataSnapshot, id: String?) {
                    sitters.add(data.toSitter())
                    onSittersUpdate(sitters)
                }
                override fun onChildRemoved(data: DataSnapshot) {
                    sitters.remove(data.toSitter())
                    onSittersUpdate(sitters)
                }
            })
    }

    abstract fun onSittersUpdate(sitters: List<Sitter>)

    override fun onPause() {
        super.onPause()
        FirebaseDatabase.getInstance()
            .reference.child("sitters").removeEventListener(listener)
    }

}