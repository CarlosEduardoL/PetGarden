package zero.network.petgarden.util;

import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.util.Log;

import androidx.core.app.NotificationCompat;

import com.google.android.gms.common.api.internal.PendingResultFacade;

import zero.network.petgarden.R;
import zero.network.petgarden.model.notifications.MessageArrival;
import zero.network.petgarden.ui.login.LoginActivity;
import zero.network.petgarden.ui.user.owner.MapFragment;
import zero.network.petgarden.ui.user.owner.OwnerActivity;
import zero.network.petgarden.ui.user.sitter.MapSitterFragment;

public class NotificationArriveUtil {

    public static final String CHANNEL_ID = "PetGardenArrival";
    public static final String CHANNEL_NAME = "MessagesArrival";
    public static final int CHANNEL_IMPORTANCE = NotificationManager.IMPORTANCE_HIGH;
    public static int consecutive = 1;

    public static void createNotification(Context context, MessageArrival msg){
        NotificationManager manager = (NotificationManager) context.getSystemService(Context.NOTIFICATION_SERVICE);

        if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.O) {
            NotificationChannel channel = new NotificationChannel(CHANNEL_ID,CHANNEL_NAME,CHANNEL_IMPORTANCE);
            manager.createNotificationChannel(channel);
        }

        // Create an explicit intent for an Activity in your app
        Intent intent = new Intent(context, LoginActivity.class);
        //Se está verificando si la actividad está activa y si el fragmento está activo: Manejarlos
        Log.e(">>NotArrUtil","ActivActivi?"+OwnerActivity.active);
        Log.e(">>NotArrUtil","ActivFragm?"+ MapFragment.active);
        PendingIntent pendingIntent = PendingIntent.getActivity(context, 1, intent, PendingIntent.FLAG_UPDATE_CURRENT);

        NotificationCompat.Builder builder = new NotificationCompat.Builder(context, CHANNEL_ID)
                .setContentTitle(msg.getTitulo())
                .setContentText(msg.getBody())
                .setSmallIcon(R.drawable.logo)
                .setContentIntent(pendingIntent)
                .setAutoCancel(true);

        manager.notify(consecutive,builder.build());
        consecutive++;

    }
}
