package zero.network.petgarden.services

import android.content.Context
import com.google.firebase.messaging.FirebaseMessagingService
import com.google.firebase.messaging.RemoteMessage
import com.google.gson.Gson
import org.json.JSONObject
import zero.network.petgarden.model.entity.Owner
import zero.network.petgarden.model.entity.Pet
import zero.network.petgarden.model.entity.Sitter
import zero.network.petgarden.model.notifications.Message
import zero.network.petgarden.model.notifications.MessageArrival
import zero.network.petgarden.model.notifications.OnResponseContractingListener
import zero.network.petgarden.util.NotificationArriveUtil
import zero.network.petgarden.util.NotificationUtils
import zero.network.petgarden.util.saveInDB

class FCMService :FirebaseMessagingService() {


    override fun onMessageReceived(remoteMessage: RemoteMessage) {

        if (remoteMessage.data["type"].toString() == Message.TYPE) {
            val obj = JSONObject(remoteMessage.data as Map<*, *>)
            val gson = Gson()

            val message = gson.fromJson(obj.toString(), Message::class.java)

            if (message.ownerId == "") {

                val sitterID = message.sitterId //Aqui va el id del sitter
                val shared = getSharedPreferences(sitterID, Context.MODE_PRIVATE)

                if (shared.contains("owner") && shared.contains("pet") && shared.contains("sitter")){
                    val owner = gson.fromJson(shared.getString("owner", ""), Owner::class.java)
                    val sitter = gson.fromJson(shared.getString("sitter",""), Sitter::class.java)
                    val pet = gson.fromJson(shared.getString("pet",""), Pet::class.java)
                    if (message.responseContracting == NotificationUtils.ACCEPT){
                        owner.sitterList[sitter.id] = sitter.id
                        pet.sitterID = sitter.id
                        saveInDB(owner, pet, sitter)

                        NotificationUtils.createSimpleNotification(this,
                            "El cuidador  ha aceptado la solicitud de contratación")
                        println("------------notificacion de respuesta de contratacion lanzada")

                    }else {
                        NotificationUtils.createSimpleNotification(this,
                            "El cuidador ha rechazado la solicitud contratación")
                        println("------------notificacion de respuesta de contratacion lanzada")

                    }
                }
                shared.edit().clear().apply()
            } else
                NotificationUtils.createNotification(this, message)

        }else {
            //Se hace lo de chasqui
            val obj = JSONObject(remoteMessage.data as Map<*,*>)
            val gson = Gson()
            val message = gson.fromJson<MessageArrival>(obj.toString(),MessageArrival::class.java)
            NotificationArriveUtil.createNotification(this, message)
        }
    }



        companion object{
            var listener:OnResponseContractingListener = object :OnResponseContractingListener{
                override fun responseContracting(response: String) {
                }
            }
        }

}