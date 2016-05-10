package co.ghola.pushmq4;

/**
 * Created by gholadr on 4/28/16.
 */
public class Constants {

    public static final String CONNECT_CLIENT = "co.ghola.pushmq4.BackgroundService.action.CONNECT_CLIENT";
    public final static String SERVICE_CLASSNAME ="co.ghola.pushmq4.BackgroundService";
    public static final String ACTION_RESP = "co.ghola.pushmq4.action.MESSAGE_PROCESSED";
    public static final String BROKER_URL = "tcp://iot.eclipse.org:1883";
    public static final String LOCAL_BROKER_URL = "tcp://mosquitto.mysquar.com:1883";
    public static final String TOPIC = "co/ghola/mqtt/test";
    public static final int QoS = 2;

}