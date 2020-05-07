package zero.network.petgarden.tools

import android.app.NotificationChannel
import android.app.NotificationManager
import android.content.Context
import android.os.Build
import androidx.core.app.NotificationCompat
import kotlinx.coroutines.channels.consumesAll
import zero.network.petgarden.R

class NotificationUtils {

    companion object{
        val CHANNEL_ID  ="PetGarden"
        val CHANNEL_NAME ="Service Contracting"
        val CHANNEL_IMPORTANCE =NotificationManager.IMPORTANCE_HIGH
        val CONTRACTING_SITTER = "Tienes un nuevo cliente"
        var consecutive = 1

        fun  createNotification(context:Context, message:String){
            val manager = context.getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
                val channel = NotificationChannel(CHANNEL_ID, CHANNEL_NAME, CHANNEL_IMPORTANCE)
                manager.createNotificationChannel(channel)
            }

            val builder = NotificationCompat.Builder(context, CHANNEL_ID)
                .setContentTitle(CONTRACTING_SITTER).setContentText(message).setSmallIcon(R.mipmap.ic_launcher)

            manager.notify(consecutive, builder.build())
            consecutive++
        }
    }
}