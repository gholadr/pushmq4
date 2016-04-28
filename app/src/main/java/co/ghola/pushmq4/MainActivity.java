package co.ghola.pushmq4;

import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Color;
import android.os.Bundle;
import android.support.v4.content.LocalBroadcastManager;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.app.NotificationCompat;
import android.util.Log;
import android.widget.TextView;

import org.greenrobot.eventbus.EventBus;
import org.greenrobot.eventbus.Subscribe;
import org.greenrobot.eventbus.ThreadMode;

public class MainActivity extends AppCompatActivity {
    private static String TAG = MainActivity.class.getSimpleName();
    private PushReceiver receiver;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        if(!ServiceUtils.getInstance().serviceIsRunning(getApplicationContext()) && DeviceStatus.isOnline(this)) {
            Intent mServiceIntent = new Intent(this, BackgroundService.class);
            startService(mServiceIntent);
        }
        EventBus.getDefault().register(this);

    }

    @Override
    protected void onResume(){
        super.onResume();
        updateValuesFromSharedPrefs();
        IntentFilter filter = new IntentFilter(Constants.ACTION_RESP);
        filter.addCategory(Intent.CATEGORY_DEFAULT);
        receiver = new PushReceiver();
        LocalBroadcastManager.getInstance(getBaseContext()).registerReceiver(receiver, filter);
    }

    @Override
    protected void onPause() {
        super.onPause();
        // Unregister the listener when the application is paused
        LocalBroadcastManager.getInstance(getBaseContext()).unregisterReceiver(receiver);
    }

    @Subscribe(threadMode = ThreadMode.MAIN)
    public void doThis(String text){
        updateValuesFromSharedPrefs();
    }

    private void updateValuesFromSharedPrefs(){
        TextView broker = (TextView) findViewById(R.id.broker);
        String brokerValue = HelperSharedPreferences.getSharedPreferencesString(getApplicationContext(),HelperSharedPreferences.SharedPreferencesKeys.brokerKey, getResources().getString( R.string.broker_text_default));
        broker.setText("Broker:" + brokerValue);
        TextView topic = (TextView) findViewById(R.id.topic);
        topic.setText("Topic:" + Constants.TOPIC);
        TextView result = (TextView) findViewById(R.id.message);
        String textValue = HelperSharedPreferences.getSharedPreferencesString(getApplicationContext(),HelperSharedPreferences.SharedPreferencesKeys.messageKey, getResources().getString( R.string.message_text_default));
        result.setText("Msg:" +textValue);
    }

    public static class PushReceiver extends BroadcastReceiver
    {
        @Override
        public void onReceive(Context context, Intent intent)
        {
            String notificationTitle = "MQTT Test";
            String notificationDesc = "It works!";

            if ( intent.getStringExtra("message") != null )
            {
                notificationDesc = intent.getStringExtra("message");
            }

            Log.d(TAG, "onReceive");
            int notificationID = 100;
            Bitmap largeIcon = BitmapFactory.decodeResource(context.getResources(), R.drawable.ic_launcher);
            NotificationCompat.Builder nBuilder = new NotificationCompat.Builder(
                    context);
            intent = new Intent(context, MainActivity.class);
            PendingIntent pendingIntent = PendingIntent.getActivity(context, 1234, intent, PendingIntent.FLAG_UPDATE_CURRENT);

            nBuilder
                    .setContentTitle(notificationTitle)
                    .setContentText(notificationDesc)
                    .setSmallIcon(R.drawable.icon_alpha)
                    .setLights(Color.MAGENTA, 3000, 3000)
                    .setLargeIcon(largeIcon) //setLargeIcon(R.drawable.ic_launcher)
                    .setContentIntent(pendingIntent)
                    .setAutoCancel(true);
            NotificationManager mNotificationManager =
                    (NotificationManager) context.getSystemService(Context.NOTIFICATION_SERVICE);
            mNotificationManager.notify(notificationID, nBuilder.build());
        }
    }
}
