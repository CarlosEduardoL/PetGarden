package zero.network.petgarden.util;

import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.text.Editable;
import android.util.Log;

import androidx.core.app.NotificationCompat;

import com.google.android.gms.common.api.internal.PendingResultFacade;
import com.google.gson.Gson;

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

        NotificationCompat.Builder builder = new NotificationCompat.Builder(context, CHANNEL_ID)
                .setContentTitle(msg.getTitulo())
                .setContentText("Tu mascota ha llegado")
                .setSmallIcon(R.drawable.logo)
                .setAutoCancel(true);

        Gson gson = new Gson();
        if(!OwnerActivity.isActive()){
            SharedPreferences.Editor editor= context.getSharedPreferences("dialog",Context.MODE_PRIVATE).edit();
            Intent intent = new Intent(context, LoginActivity.class);
            editor.putString("messageArrival",gson.toJson(msg));

            editor.apply();
            PendingIntent pendingIntent = PendingIntent.getActivity(context, 1, intent, PendingIntent.FLAG_UPDATE_CURRENT);
            builder.setContentIntent(pendingIntent);
        }else{
            //Cuando se sepa mandar datos a la activity, cambiar aqui

        }
/*        Intent intent = new Intent(context, OwnerActivity.class);
        intent.putExtra("show_dialog", "show_dialog");
        PendingIntent contentIntent = PendingIntent.getActivity(context, 0, intent, PendingIntent.FLAG_UPDATE_CURRENT);
        builder.setContentIntent(contentIntent);*/

        manager.notify(consecutive,builder.build());
        consecutive++;

    }
}
