package zero.network.petgarden.services

import android.util.Log
import com.google.android.gms.tasks.Task
import com.google.firebase.messaging.FirebaseMessaging
import com.google.firebase.messaging.FirebaseMessagingService
import com.google.firebase.messaging.RemoteMessage
import com.google.gson.Gson
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import org.json.JSONObject
import zero.network.petgarden.model.entity.Sitter
import zero.network.petgarden.model.notifications.Message
import zero.network.petgarden.util.sitterByEmail
import zero.network.petgarden.util.suscribeToTopic

class FCMService:FirebaseMessagingService() {

    override fun onMessageReceived(remoteMessage: RemoteMessage) {
        println("-------------------Mensaje recibido${remoteMessage.data}-----------------------------")
        val obj  = JSONObject(remoteMessage.data as Map<*, *>)
        val gson = Gson()
        val message = gson.fromJson<Message>(obj.toString(), Message::class.java)

        if (message.handshake){
            var sitter = Sitter()
            CoroutineScope(Dispatchers.Main).launch {sitter = sitterByEmail(message.emailSitter)!! }
            FirebaseMessaging.getInstance().unsubscribeFromTopic(sitter.id)  .addOnCompleteListener { task: Task<Void?> ->
                if (task.isSuccessful) Log.e(
                    ">>>>>>>>>>>",
                    "desuscripcion exitosa"
                )
            }
            suscribeToTopic("${message.ownerId} ${sitter.id}")

        }else{
            println("------------- Notificacion contratacion exitosa--------------------------------")
        }

    }
}