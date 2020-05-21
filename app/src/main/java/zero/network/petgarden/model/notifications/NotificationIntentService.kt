package zero.network.petgarden.model.notifications

import android.app.IntentService
import android.app.NotificationManager
import android.content.Context
import android.content.Intent
import android.os.Handler
import android.os.Looper
import android.util.Log
import android.widget.Toast
import androidx.annotation.Nullable
import com.google.gson.Gson
import zero.network.petgarden.util.NotificationUtils
import zero.network.petgarden.util.POSTtoFCM
import zero.network.petgarden.util.extra
import zero.network.petgarden.util.getDate


class NotificationIntentService : IntentService("notificationIntentService") {

    override fun onHandleIntent(@Nullable intent: Intent?) {
        val bundle = intent!!.extras
        val message = bundle!!.get("message") as Message
        val manager =  this.getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager

        when (intent!!.action) {
            "decline" -> {
                println("-------------Decline clicked------------------")
                val declineHandler = Handler(Looper.getMainLooper())
                declineHandler.post(Runnable {
                    sendFCMessage(message, NotificationUtils.DECLINE)
                })
                manager.cancel(1)
            }
            "accept" -> {
                println("-------------accept clicked------------------")
                val acceptHandler = Handler(Looper.getMainLooper())
                acceptHandler.post(Runnable {
                    sendFCMessage(message, NotificationUtils.ACCEPT)
                })
                manager.cancel(1)
            }
        }
    }


    private fun sendFCMessage(message: Message, responseContracting:String){
        var fcm = FCMMessage()
        fcm.to = "/topics/${message.ownerId}"
        fcm.data = Message("", "", "", "", Message.TYPE, responseContracting)
        println("---------------------Mensaje enviado: ${fcm.data.toString()}")
        val gson  = Gson()
        val json =  gson.toJson(fcm)

        Thread(Runnable {
            POSTtoFCM(FCMMessage.API_KEY, json)
        }).start()
    }
}