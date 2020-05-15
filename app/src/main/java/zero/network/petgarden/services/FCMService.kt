package zero.network.petgarden.services

import com.google.firebase.messaging.FirebaseMessagingService
import com.google.firebase.messaging.RemoteMessage

class FCMService:FirebaseMessagingService() {

    override fun onMessageReceived(p0: RemoteMessage?) {
        super.onMessageReceived(p0)
    }
}