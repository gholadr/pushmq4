package co.ghola.pushmq4;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.util.Log;

/**
 * Created by gholadr on 3/1/16. restarts service in case of connectivity loss due to network disconnect.
 */

public class NetBootWatcher extends BroadcastReceiver {
    private final String TAG = NetBootWatcher.class.getSimpleName();
    @Override
    public void onReceive(Context context, Intent intent) {
        //here, check that the network connection is available. If yes, start your service. If not, stop your service.

        ConnectivityManager cm = (ConnectivityManager) context.getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo info = cm.getActiveNetworkInfo();
        if(!ServiceUtils.getInstance().serviceIsRunning(context) && DeviceStatus.isOnline(context)){
            //start service
            intent = new Intent(context, ForegroundService.class);
            intent.setAction(Constants.CONNECT_CLIENT);
            context.startService(intent);
            Log.d(TAG, getClass().getCanonicalName() + ": Connected. Starting MQTT service");
        }
        else {
            //stop service
            intent = new Intent(context, ForegroundService.class);
            context.stopService(intent);
            Log.d(TAG, getClass().getCanonicalName() + ": Disconnected. shutting down background service");
        }
    }
}