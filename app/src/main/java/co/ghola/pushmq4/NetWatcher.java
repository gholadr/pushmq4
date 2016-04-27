package co.ghola.pushmq4;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.util.Log;

import co.ghola.pushmq4.BackgroundService;



/**
 * Created by gholadr on 3/1/16. restarts service in case of connectivity loss due to network disconnect.
 */

public class NetWatcher extends BroadcastReceiver {
    private final String TAG = NetWatcher.class.getSimpleName();
    @Override
    public void onReceive(Context context, Intent intent) {
        //here, check that the network connection is available. If yes, start your service. If not, stop your service.

        ConnectivityManager cm = (ConnectivityManager) context.getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo info = cm.getActiveNetworkInfo();
        if(!ServiceUtils.getInstance().serviceIsRunning(context) && DeviceStatus.isOnline(context)){
            //start service
            intent = new Intent(context, BackgroundService.class);
            intent.setAction(Constants.CONNECT_CLIENT);
            context.startService(intent);
            Log.d(TAG, getClass().getCanonicalName() + ": Connected. Starting MQTT service");
        }
        else {
            //stop service
            intent = new Intent(context, BackgroundService.class);
            context.stopService(intent);
            Log.d(TAG, getClass().getCanonicalName() + ": Disconnected. shutting down socket service");
        }
    }
}