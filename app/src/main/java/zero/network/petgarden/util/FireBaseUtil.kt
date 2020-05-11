package zero.network.petgarden.util

import com.google.android.gms.tasks.Task
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.Query
import com.google.firebase.database.ValueEventListener
import kotlinx.coroutines.tasks.await
import kotlin.coroutines.resume
import kotlin.coroutines.resumeWithException
import kotlin.coroutines.suspendCoroutine

suspend fun Query.wait() = suspendCoroutine<DataSnapshot> {
    val listener = object : ValueEventListener {
        override fun onCancelled(error: DatabaseError) {
            it.resumeWithException(error.toException())
        }
        override fun onDataChange(result: DataSnapshot) {
            it.resume(result)
        }
    }
    addListenerForSingleValueEvent(listener)
}

suspend fun<T> Task<T>.awaitOrException(): Exception? = try {
    await()
    null
}catch (e: Exception){
    e
}