import org.eclipse.paho.client.mqttv3.*;
import org.eclipse.paho.client.mqttv3.persist.MemoryPersistence;
import static spark.Spark.*;

public class Main {
    public static void main(String[] args) {
        port(8081);

        get("/", (request, response) -> "Hello!");
        get("/hello", (request, response) -> "Hello World!");
        post("/publish", (request, response) -> {
            final String topic = "co/ghola/mqtt/test";
            String broker = "tcp://iot.eclipse.org:1883";
           // String broker = "tcp://192.168.0.102:1883";
            String clientId = "java-mqtt-demo-app";
            String msg = request.queryParams("message");
            MemoryPersistence persistence = new MemoryPersistence();

            try {
                final MqttClient sampleClient = new MqttClient(broker, clientId, persistence);
                MqttConnectOptions connOpts = new MqttConnectOptions();
                connOpts.setCleanSession(true);

                sampleClient.setCallback(new MqttCallbackExtended() {

                    public void messageArrived(String topic, MqttMessage message) throws Exception {
                        // Not used
                    }

                    public void deliveryComplete(IMqttDeliveryToken token) {
                        // Not used
                    }

                    public void connectionLost(Throwable cause) {
                        System.out.println("Connection Lost: " + cause.getMessage());
                    }

                    public void connectComplete(boolean reconnect, String str) {
                        // Make or re-make subscriptions here
                        if (reconnect) {
                            System.out.println("Automatically Reconnected to Broker!");
                        } else {
                            System.out.println("Connected To Broker for the first time!");
                        }
                    }
                });

                System.out.println("Connecting to broker: " + broker);
                sampleClient.connect(connOpts);
                System.out.println("Connected");
                MqttMessage message = new MqttMessage(String.format("From Spark, with love:%s", msg).getBytes() );
                message.setQos(2);
                message.setRetained(false);
                System.out.println(message.toString());
                try {
                    if (sampleClient.isConnected()) {
                        sampleClient.publish(topic, message);
                    }
                    else{
                        sampleClient.reconnect();
                    }
                } catch (MqttException e) {
                    e.printStackTrace();
                }
            }
            catch (MqttException me) {
                System.out.println("reason " + me.getReasonCode());
                System.out.println("msg " + me.getMessage());
                System.out.println("loc " + me.getLocalizedMessage());
                System.out.println("cause " + me.getCause());
                System.out.println("excep " + me);
                me.printStackTrace();
            }
            response.status(200);
            return response.raw();
        });
    }
}