package co.ghola.pushmq4;

import android.app.IntentService;
import android.app.Service;
import android.content.Intent;
import android.support.v4.content.LocalBroadcastManager;
import android.util.Log;
import android.widget.TextView;

import org.eclipse.paho.android.service.MqttAndroidClient;
import org.eclipse.paho.client.mqttv3.IMqttActionListener;
import org.eclipse.paho.client.mqttv3.IMqttDeliveryToken;
import org.eclipse.paho.client.mqttv3.IMqttToken;
import org.eclipse.paho.client.mqttv3.MqttCallback;
import org.eclipse.paho.client.mqttv3.MqttClient;
import org.eclipse.paho.client.mqttv3.MqttConnectOptions;
import org.eclipse.paho.client.mqttv3.MqttException;
import org.eclipse.paho.client.mqttv3.MqttMessage;
import org.eclipse.paho.client.mqttv3.persist.MqttDefaultFilePersistence;

/**
 * Created by macbook on 3/7/16.
 */
public class BackgroundService extends IntentService{

    private static String TAG =BackgroundService.class.getSimpleName();
    private MqttAndroidClient client = null;


    /**
     * Creates an IntentService.  Invoked by your subclass's constructor.
     *
     */
    public BackgroundService() {
        super(TAG);
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        Log.i(TAG, "Service running");
        if (intent == null)
            onHandleIntent(new Intent());
        else {
            onHandleIntent(intent);
        }
        return Service.START_STICKY;
    }
    @Override
    protected void onHandleIntent(Intent intent) {
        //no need to reinstantiate the client again
        if (client != null) return;

        final String topic = "co/ghola/mqtt/test";
        final int QoS = 2;
        String clientId = "android-client-" + Installation.getUniqueDeviceId(getApplicationContext());
        String broker = "tcp://192.168.0.101:1883";
        // String broker = "tcp://iot.eclipse.org:1883";


        String tmpDir = getFilesDir().toString();
        Log.d(TAG, tmpDir);
        MqttDefaultFilePersistence mqttDefaultFilePersistence = new MqttDefaultFilePersistence(tmpDir);

        client = new MqttAndroidClient(this.getApplicationContext(), broker, clientId, mqttDefaultFilePersistence);
        client.setCallback(new MqttCallback() {
            @Override
            public void connectionLost(Throwable cause) {
                Log.d(TAG, "Connection was lost! ");
            }

            @Override
            public void messageArrived(String topic, MqttMessage message) throws Exception {
                Log.d(TAG, "Message Arrived!: " + topic + ": " + new String(message.getPayload()));
                // TextView textView = (TextView) findViewById(R.id.text);
                // textView.setText(new String(message.getPayload()));
   //             Intent push = new Intent(getApplicationContext(), MainActivity.class);

                Intent push = new Intent(getApplicationContext(), MainActivity.PushReceiver.class);
                push.setAction(Constants.ACTION_RESP);
                push.addCategory(Intent.CATEGORY_DEFAULT);
                push.putExtra("message", message.toString());

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


        client.connect(connOpt, new IMqttActionListener() {
            @Override
            public void onSuccess(IMqttToken asyncActionToken) {
                System.out.println("Connection Success!");
                try {
                    Log.d(TAG, String.format("Subscribing to %s, QoS %s", topic, QoS));
                    client.subscribe(topic, QoS);
                    Log.d(TAG, String.format("Subscribed to %s, QoS %s", topic, QoS));
                    // System.out.println("Publishing message..");
                    // mqttAndroidClient.publish("co/ghola/mqtt/test", new MqttMessage("Bonjour!!".getBytes()));
                } catch (MqttException ex) {

                }

            }

            @Override
            public void onFailure(IMqttToken asyncActionToken, Throwable exception) {
                Log.d(TAG, "Connection Failure!");
            }
        });
    }
    catch (MqttException ex) {

    }
    }


    @Override
    public void onDestroy() {
        Log.d(TAG, "onDestroy");
        if(client != null){
            try {

                client.disconnect();

            }catch (MqttException e){
                Log.e(TAG,e.getMessage());
            }
        }
    }
}

