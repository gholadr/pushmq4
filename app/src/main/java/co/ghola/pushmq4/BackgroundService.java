package co.ghola.pushmq4;

import android.app.IntentService;
import android.app.Service;
import android.content.Intent;
import android.util.Log;

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
    private MqttAndroidClient client;

    /**
     * Creates an IntentService.  Invoked by your subclass's constructor.
     *
     */
    public BackgroundService() {
        super("BackgroundService");
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
        String clientId = Installation.getUniqueDeviceId(getApplicationContext());
        String GCP_URL = "tcp://172.17.8.252:1883";
        String LOCAL_URL = "tcp://192.168.1.252:1883";


        String tmpDir = System.getProperty("java.io.tmpdir");
        MqttDefaultFilePersistence mqttDefaultFilePersistence = new MqttDefaultFilePersistence(tmpDir);

        client =
                new MqttAndroidClient(this.getApplicationContext(), GCP_URL,
                        clientId,mqttDefaultFilePersistence);
        try {
            client.setCallback(new MessageCallback() {

                @Override
                public void connectionLost(Throwable cause) {
                    Log.d(TAG, "connection Lost");
                }

                @Override
                public void messageArrived(String topic, MqttMessage message) throws Exception {
                    Log.d(TAG, "delivered");

                    Intent push = new Intent(getApplicationContext(), PushReceiver.class);

                    //push.setPackage("co.ghola.pushmq.receivers");
                    push.putExtra("message", message.toString());
                    //push.setAction("pushMQ");
                    getApplicationContext().sendBroadcast(push);

                }

                @Override
                public void deliveryComplete(IMqttDeliveryToken token) {

                }
            });
        } catch (Exception e) {
            Log.e(TAG, e.getMessage());
        }

        try {

            MqttConnectOptions connOpt = new MqttConnectOptions();

            connOpt.setCleanSession(false);
            connOpt.setKeepAliveInterval(30);
            connOpt.setUserName("admin");
            connOpt.setPassword("password".toCharArray());

            IMqttToken token = client.connect(connOpt);

            token.setActionCallback(new IMqttActionListener() {
                @Override
                public void onSuccess(IMqttToken asyncActionToken) {
                    // We are connected
                    Log.d(TAG, "onSuccess");
                    if(asyncActionToken == null){
                        Log.d(TAG, "obj is null");
                    }
                    else{
                        Log.d(TAG, "asyncActionToken obj not null. session present: " + asyncActionToken.getSessionPresent());
                    }

                        subscribe(asyncActionToken);

                }

                @Override
                public void onFailure(IMqttToken asyncActionToken, Throwable exception) {
                    // Something went wrong e.g. connection timeout or firewall problems
                    Log.d(TAG, "onFailure");
                    try {
                        asyncActionToken.getClient().disconnect();
                    }catch(MqttException e){
                        Log.e(TAG, e.getMessage());
                    }
                }
            });

        } catch (MqttException e) {
            Log.e(TAG, e.getMessage());
        }

    }


    public void subscribe(IMqttToken subToken){

        String topic = "topic/event"; //foo/bar/message";
        int qos = 1;
        try {
            subToken = subToken.getClient().subscribe(topic, qos);
            subToken.setActionCallback(new IMqttActionListener() {
                @Override
                public void onSuccess(IMqttToken asyncActionToken) {
                    Log.d(TAG, "I'm now subscribed!");
                }

                @Override
                public void onFailure(IMqttToken asyncActionToken,
                                      Throwable exception) {
                    // The subscription could not be performed, maybe the user was not
                    // authorized to subscribe on the specified topic e.g. using wildcards
                }
            });
        } catch (MqttException e) {
            e.printStackTrace();
        }
    }


    @Override
    public void onDestroy() {
/*        Log.d(TAG, "onDestroy");
        if(.client != null){
            try {

                client.disconnect();

            }catch (MqttException e){
                Log.e(TAG,e.getMessage());
            }
        }*/
    }
}

