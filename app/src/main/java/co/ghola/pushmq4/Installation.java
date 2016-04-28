package co.ghola.pushmq4;

import android.content.Context;
import android.util.Log;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.RandomAccessFile;
import java.util.UUID;



/**
 * Created by gholadr on 3/1/16. Singleton class that sets and retrieves a unique identifier for the device
 */
public final class Installation{
    public static  String deviceId = null;
    private static final String INSTALLATION = "INSTALLATION";
    private static final String TAG = Installation.class.getSimpleName();

    private Installation() {

    }

    public synchronized static String getUniqueDeviceId(Context context) {
        if (deviceId == null) {
            File installation = new File(context.getFilesDir(), INSTALLATION);
            try {
                if (!installation.exists())
                    writeInstallationFile(installation);
                deviceId = readInstallationFile(installation);

            } catch (Exception e) {
                throw new RuntimeException(e);
            }
        }
        Log.d(TAG, Installation.class + " unique deviceId:" + deviceId);
        return deviceId;
    }
    private static String readInstallationFile(File installation) throws IOException {
        RandomAccessFile f = new RandomAccessFile(installation, "r");
        byte[] bytes = new byte[(int) f.length()];
        f.readFully(bytes);
        f.close();
        return new String(bytes);
    }

    private static void writeInstallationFile(File installation) throws IOException {
        FileOutputStream out = new FileOutputStream(installation);
        String id = UUID.randomUUID().toString();
        out.write(id.getBytes());
        out.close();
    }
}




