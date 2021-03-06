package co.ghola.pushmq4;

import android.app.IntentService;
import android.app.Service;
import android.content.Intent;
import android.util.Log;

import com.crashlytics.android.Crashlytics;

import org.eclipse.paho.android.service.MqttAndroidClient;
import org.eclipse.paho.client.mqttv3.IMqttActionListener;
import org.eclipse.paho.client.mqttv3.IMqttDeliveryToken;
import org.eclipse.paho.client.mqttv3.IMqttToken;
import org.eclipse.paho.client.mqttv3.MqttCallback;
import org.eclipse.paho.client.mqttv3.MqttConnectOptions;
import org.eclipse.paho.client.mqttv3.MqttException;
import org.eclipse.paho.client.mqttv3.MqttMessage;
import org.eclipse.paho.client.mqttv3.persist.MqttDefaultFilePersistence;
import org.greenrobot.eventbus.EventBus;

import io.fabric.sdk.android.Fabric;

/**
 * Created by gholadr on 4/28/16.
 */
public class BackgroundService extends IntentService{

    private static String TAG =BackgroundService.class.getSimpleName();
    private MqttAndroidClient client = null;
    public static boolean IS_SERVICE_RUNNING = false;

    public BackgroundService() {
        super(TAG);
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        Log.i(TAG, "Starting service...");
        if (intent == null)
            onHandleIntent(new Intent());
        else {
            onHandleIntent(intent);
        }
        return Service.START_STICKY;
    }

    @Override
    protected void onHandleIntent(Intent intent) {

        Fabric.with(this, new Crashlytics());
        //no need to reinstantiate the client again
        if (client != null) {
            Log.d(TAG, "MQTT client already instantiated");
            return;
        }

        String broker = Constants.LOCAL_BROKER_URL;
        HelperSharedPreferences.putSharedPreferencesString(getApplicationContext(),HelperSharedPreferences.SharedPreferencesKeys.brokerKey,broker);
        String clientId = "android-client-" + Installation.getUniqueDeviceId(getApplicationContext());
        String tmpDir = getFilesDir().toString();
        MqttDefaultFilePersistence mqttDefaultFilePersistence = new MqttDefaultFilePersistence(tmpDir);

        client = new MqttAndroidClient(this.getApplicationContext(), broker, clientId, mqttDefaultFilePersistence, MqttAndroidClient.Ack.AUTO_ACK);
        client.setTraceEnabled(true);

        client.setCallback(new MqttCallback() {

            @Override
            public void connectionLost(Throwable cause) {

                Log.d(TAG, "Connection was lost! ");
            }

            @Override
            public void messageArrived(String topic, MqttMessage message) throws Exception {
                Log.d(TAG, "Message Arrived!: " + topic + ": " + new String(message.getPayload()));
                HelperSharedPreferences.putSharedPreferencesString(getApplicationContext(),HelperSharedPreferences.SharedPreferencesKeys.messageKey, new String(message.getPayload()));
                Intent push = new Intent(getApplicationContext(), MainActivity.PushReceiver.class);
                push.setAction(Constants.ACTION_RESP);
                push.addCategory(Intent.CATEGORY_DEFAULT);
                push.putExtra("message", message.toString());
                EventBus.getDefault().post(message.toString());
                sendBroadcast(push);
            }

            @Override
            public void deliveryComplete(IMqttDeliveryToken token) {
                Log.d(TAG, "Delivery Complete!");
            }
        });

        try {
            MqttConnectOptions connOpt = new MqttConnectOptions();
            connOpt.setKeepAliveInterval(30);
            connOpt.setAutomaticReconnect(true);
            connOpt.setCleanSession(false);
            connOpt.setConnectionTimeout(15);

            client.connect(connOpt,getApplicationContext(), new IMqttActionListener() {

                @Override
                public void onSuccess(IMqttToken asyncActionToken) {
                    Log.d(TAG, "Connection Success!");
                    try {
                        Log.d(TAG, String.format("Subscribing to %s, QoS %s", Constants.TOPIC, Constants.QoS));
                        client.subscribe(Constants.TOPIC, Constants.QoS);
                        Log.d(TAG, String.format("Subscribed to %s, QoS %s", Constants.TOPIC, Constants.QoS));
                        //Log.d(TAG,String.valueOf(asyncActionToken.getSessionPresent()));

                    }
                    catch (MqttException ex) {
                        Log.d(TAG, ex.getCause().getMessage());
                    }
                }

                @Override
                public void onFailure(IMqttToken asyncActionToken, Throwable exception) {
                    Log.d(TAG, "Connection Failure!:");
                    exception.printStackTrace();
                }
            });

        }
        catch (MqttException ex) {
            Log.d(TAG, ex.getCause().getMessage());
        }
    }

    //called by system if device is low on resources, or if stopService is invoked.
    @Override
    public void onDestroy() {
        super.onDestroy();
        if(client != null){
            client.close();
            try {
                client.disconnect();
            } catch (MqttException e) {
                e.printStackTrace();
            }

            Log.d(TAG, "Releasing all resources");
        }
        else{
            Log.d(TAG, "Stopping service");
        }
    }
}

