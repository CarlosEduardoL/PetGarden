package zero.network.petgarden.util

import android.app.Notification
import android.app.NotificationChannel
import android.app.NotificationManager
import android.content.Context
import android.os.Build
import android.widget.RemoteViews
import androidx.core.app.NotificationCompat
import zero.network.petgarden.R

class NotificationUtils {

    companion object{
        const val  CHANNEL_ID = "PetGarden"
        const val CHANNEL_IMPORTANCE = NotificationManager.IMPORTANCE_HIGH
        const val CHANNEL_NAME = "Contracting"
        var consecutive =1

        fun createNotification(context: Context){
            val manager =  context.getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager

            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
                val channel = NotificationChannel(CHANNEL_ID, CHANNEL_NAME, CHANNEL_IMPORTANCE)
                manager.createNotificationChannel(channel)
            }

            val notificationLayout = RemoteViews(context.packageName, R.layout.notification_contracting)
            val customNotification = NotificationCompat.Builder(context, CHANNEL_ID)
                                                               .setSmallIcon(R.drawable.logo).setStyle(NotificationCompat.DecoratedCustomViewStyle())
                                                                .setCustomBigContentView(notificationLayout)
            manager.notify(1, customNotification.build())
            consecutive++
        }
    }


}