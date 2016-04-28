package co.ghola.pushmq4;

import android.app.ActivityManager;
import android.content.Context;

/**
 * Created by gholadr on 4/26/16.
 */
public class ServiceUtils {
    private static ServiceUtils ourInstance = new ServiceUtils();
    private static String TAG = ServiceUtils.class.getSimpleName();


    public static ServiceUtils getInstance() {
        return ourInstance;
    }

    private ServiceUtils() {
    }

    public boolean serviceIsRunning(Context context) {
        ActivityManager manager = (ActivityManager) context.getSystemService(Context.ACTIVITY_SERVICE);
        for (ActivityManager.RunningServiceInfo service : manager.getRunningServices(Integer.MAX_VALUE)) {
            if (Constants.SERVICE_CLASSNAME.equals(service.service.getClassName())) {
                return true;
            }
        }
        return false;
    }

}
