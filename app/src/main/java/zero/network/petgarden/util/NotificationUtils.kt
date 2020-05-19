package zero.network.petgarden.util

import android.app.NotificationChannel
import android.app.NotificationManager
import android.app.PendingIntent
import android.content.Context
import android.content.Intent
import android.os.Build
import android.widget.RemoteViews
import androidx.core.app.NotificationCompat
import zero.network.petgarden.R
import zero.network.petgarden.model.notifications.Message
import zero.network.petgarden.model.notifications.NotificationIntentService
import java.io.Serializable


class NotificationUtils {

    companion object{
        const val  CHANNEL_ID = "PetGarden"
        const val CHANNEL_IMPORTANCE = NotificationManager.IMPORTANCE_HIGH
        const val CHANNEL_NAME = "Contracting"
        const val ACCEPT = "y"
        const val DECLINE = "n"

        var consecutive =1

        fun createNotification(context: Context, message: Message){
            val manager =  context.getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager

            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
                val channel = NotificationChannel(CHANNEL_ID, CHANNEL_NAME, CHANNEL_IMPORTANCE)
                manager.createNotificationChannel(channel)
            }

            val notificationLayout = RemoteViews(context.packageName, R.layout.notification_contracting)
            addListenerAcceptBtn(context, message, notificationLayout)
            addListenerDeclineBtn(context, message, notificationLayout)

            setContentNotification(notificationLayout, message)
            val customNotification = NotificationCompat.Builder(context, CHANNEL_ID)
                                                               .setSmallIcon(R.drawable.logo)
                                                                .setCustomBigContentView(notificationLayout)
                                                                .setAutoCancel(true)
            manager.notify(1, customNotification.build())

            consecutive++
        }


        private fun setContentNotification(layout:RemoteViews, message: Message){
            layout.setTextViewText(R.id.nameOwnerContracting, message.ownerName)
            layout.setTextViewText(R.id.scheduleContracting, message.schedule)
            layout.setTextViewText(R.id.costContracting, message.cost)
        }


        private fun addListenerDeclineBtn(context: Context, message: Message, notificationLayout:RemoteViews) {
            //Launch intent when declineBtn is clicked
            val declineIntent = Intent(context, NotificationIntentService::class.java)
            declineIntent.action = "decline"
            declineIntent.putExtra("message", message as Serializable)
            notificationLayout.setOnClickPendingIntent(
                R.id.declineBtn,
                PendingIntent.getService(
                    context,
                    1,
                    declineIntent,
                    PendingIntent.FLAG_UPDATE_CURRENT
                )
            )
        }


        private fun addListenerAcceptBtn(context: Context, message: Message, notificationLayout:RemoteViews) {
            //Launch intent when declineBtn is clicked
            val intent = Intent(context, NotificationIntentService::class.java)
            intent.action = "accept"
            intent.putExtra("message", message as Serializable)
            notificationLayout.setOnClickPendingIntent(
                R.id.acceptContracting,
                PendingIntent.getService(
                    context,
                    2,
                    intent,
                    PendingIntent.FLAG_UPDATE_CURRENT
                )
            )
        }
    }
}