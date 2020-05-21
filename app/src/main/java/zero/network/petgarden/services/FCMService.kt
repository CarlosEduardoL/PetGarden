package zero.network.petgarden.services

import android.util.Log
import android.widget.RemoteViews
import androidx.core.app.NotificationCompat
import com.google.android.gms.tasks.Task
import com.google.firebase.messaging.FirebaseMessaging
import com.google.firebase.messaging.FirebaseMessagingService
import com.google.firebase.messaging.RemoteMessage
import com.google.gson.Gson
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import org.json.JSONObject
import zero.network.petgarden.R
import zero.network.petgarden.model.entity.Sitter
import zero.network.petgarden.model.notifications.Message
import zero.network.petgarden.model.notifications.MessageArrival
import zero.network.petgarden.model.notifications.OnResponseContractingListener
import zero.network.petgarden.util.NotificationUtils
import zero.network.petgarden.util.sitterByEmail
import zero.network.petgarden.util.suscribeToTopic

class FCMService():FirebaseMessagingService() {


    override fun onMessageReceived(remoteMessage: RemoteMessage) {
        println("-------------------Tipo de mensaje: ${remoteMessage.data["type"].toString()}-----------------------------")

        if (remoteMessage.data["type"].toString() != MessageArrival.TYPE) {
            val obj = JSONObject(remoteMessage.data as Map<*, *>)
            val gson = Gson()

            val message = gson.fromJson<Message>(obj.toString(), Message::class.java)


            if (message.ownerId == "") {
                println("-------------respuesta del sitter desde onMessageReceived: ${message.responseContracting}------------------")

                if (message.responseContracting == NotificationUtils.ACCEPT)

                    listener.responseContracting(NotificationUtils.ACCEPT)
                else
                    listener.responseContracting(NotificationUtils.DECLINE)
            } else
                NotificationUtils.createNotification(this, message)

        }else {
            //Se hace lo de chasqui
        }
    }



        companion object{
            var listener:OnResponseContractingListener = object :OnResponseContractingListener{
                override fun responseContracting(response: String) {
                    println("-------------dentro de listener desde FCMService-------------------")
                }
            }
        }

}