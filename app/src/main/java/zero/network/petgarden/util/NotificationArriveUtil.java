package zero.network.petgarden.util;

import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.content.Context;

import androidx.core.app.NotificationCompat;

import zero.network.petgarden.R;

public class NotificationArriveUtil {

    public static final String CHANNEL_ID = "PetGardenArrival";
    public static final String CHANNEL_NAME = "MessagesArrival";
    public static final int CHANNEL_IMPORTANCE = NotificationManager.IMPORTANCE_HIGH;
    public static int consecutive = 1;

    public static void createNotification(Context context, String msg){
        NotificationManager manager = (NotificationManager) context.getSystemService(Context.NOTIFICATION_SERVICE);

        if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.O) {
            NotificationChannel channel = new NotificationChannel(CHANNEL_ID,CHANNEL_NAME,CHANNEL_IMPORTANCE);
            manager.createNotificationChannel(channel);
        }

        NotificationCompat.Builder builder = new NotificationCompat.Builder(context, CHANNEL_ID)
                .setContentTitle("¡Tu mascota ha llegado!")
                .setContentText(msg)
                .setSmallIcon(R.drawable.perro);

        manager.notify(consecutive,builder.build());
        consecutive++;



    }
}
