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
import org.eclipse.paho.client.mqttv3.MqttException;
import org.eclipse.paho.client.mqttv3.MqttMessage;

/**
 * Created by macbook on 3/7/16.
 */
public class BackgroundService extends IntentService{

    public static String TAG ="Backgrounder";
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
        String clientId = MqttClient.generateClientId();
        MqttAndroidClient client =
                new MqttAndroidClient(this.getApplicationContext(), "tcp://192.168.0.103:1883",
                        clientId);

        try {
            client.setCallback(new MqttCallback() {

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
            IMqttToken token = client.connect();

            token.setActionCallback(new IMqttActionListener() {
                @Override
                public void onSuccess(IMqttToken asyncActionToken) {
                    // We are connected
                    Log.d(TAG, "onSuccess");
                    subscribe(asyncActionToken);

                }

                @Override
                public void onFailure(IMqttToken asyncActionToken, Throwable exception) {
                    // Something went wrong e.g. connection timeout or firewall problems
                    Log.d(TAG, "onFailure");

                }
            });
        } catch (MqttException e) {
            e.printStackTrace();
        }
    }

    public void subscribe(IMqttToken token){

        String topic = "foo/bar/message";
        int qos = 0;
        try {
            IMqttToken subToken = token.getClient().subscribe(topic, qos);
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
}

