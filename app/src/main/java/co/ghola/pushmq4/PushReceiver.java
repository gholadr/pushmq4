package co.ghola.pushmq4;

/**
 * Created by macbook on 3/7/16.
 */
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.support.v7.app.NotificationCompat;

import co.ghola.pushmq4.MainActivity;
import co.ghola.pushmq4.R;
/**
 * Created by gholadr on 3/1/16. BroadcastReceiver handles notification display from MQTT
 */
public class PushReceiver extends BroadcastReceiver
{
    @Override
    public void onReceive(Context context, Intent intent)
    {
        String notificationTitle = "MQTT Push Notification";
        String notificationDesc = "It works!";

        if ( intent.getStringExtra("message") != null )
        {
            notificationDesc = intent.getStringExtra("message");
        }

        int notificationID = 100;
        Bitmap largeIcon = BitmapFactory.decodeResource(context.getResources(), R.drawable.ic_launcher);
        NotificationCompat.Builder nBuilder = new NotificationCompat.Builder(
                context);
        nBuilder.setContentTitle(notificationTitle);
        nBuilder.setContentText(notificationDesc);
        nBuilder.setSmallIcon(R.drawable.icon_alpha);
        nBuilder.setLargeIcon(largeIcon); //setLargeIcon(R.drawable.ic_launcher);
        intent = new Intent(context, SecondActivity.class); //cheating - dont want onCreate to be invoked on MainActivity.
        intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_SINGLE_TOP);
        PendingIntent pendingIntent = PendingIntent.getActivity(context, 1234, intent, PendingIntent.FLAG_UPDATE_CURRENT);
        nBuilder.setContentIntent(pendingIntent);
        nBuilder.setAutoCancel(true);
        NotificationManager mNotificationManager =
                (NotificationManager) context.getSystemService(Context.NOTIFICATION_SERVICE);
        mNotificationManager.notify(notificationID, nBuilder.build());

    }
}