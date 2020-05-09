package zero.network.petgarden.util

import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.Query
import com.google.firebase.database.ValueEventListener
import kotlin.coroutines.resume
import kotlin.coroutines.resumeWithException
import kotlin.coroutines.suspendCoroutine

suspend fun Query.wait() = suspendCoroutine<DataSnapshot>{
    object : ValueEventListener {
        override fun onCancelled(error: DatabaseError) {
            it.resumeWithException(error.toException())
        }
        override fun onDataChange(result: DataSnapshot) {
            it.resume(result)
        }
    }
}