package utcn.aut;

import org.eclipse.paho.client.mqttv3.IMqttDeliveryToken;
import org.eclipse.paho.client.mqttv3.MqttCallback;
import org.eclipse.paho.client.mqttv3.MqttClient;
import org.eclipse.paho.client.mqttv3.MqttConnectOptions;
import org.eclipse.paho.client.mqttv3.MqttException;
import org.eclipse.paho.client.mqttv3.MqttMessage;
import org.eclipse.paho.client.mqttv3.persist.MemoryPersistence;


import org.json.JSONObject;


public class Tranzition {

  String broker;
  String subscribeTopic;
  String publishTopic;
  String name;
  int activationsNeeded;

  MqttClient client;

  public Tranzition(String broker, String subscribeTopic, String publishTopic, String name,
      int activationsNeeded) {
    this.broker = broker;
    this.subscribeTopic = subscribeTopic;
    this.publishTopic = publishTopic;
    this.name = name;
    this.activationsNeeded = activationsNeeded;
  }

  public void activation() {

    final int[] activations = {0};

    String clientId = "JavaCli-" + System.currentTimeMillis();
    MemoryPersistence persistence = new MemoryPersistence();

    try {

      client = new MqttClient(broker, clientId, persistence);

      MqttConnectOptions connOpts = new MqttConnectOptions();
      connOpts.setCleanSession(true);

      System.out.println(name + " > Connecting to broker: " + broker);
      client.connect(connOpts);
      System.out.println(name + " > Connected");

      client.subscribe(subscribeTopic);

      client.setCallback(new MqttCallback() {
        @Override
        public void connectionLost(Throwable cause) {
          System.out.println(name + " > CONNECTION LOST. REASON : " + cause.getMessage());
          System.exit(0);
        }

        @Override
        public void messageArrived(String topic, MqttMessage message) throws Exception {
          System.out.println(
              name + " > Message received on topic '" + topic + "': " + new String(
                  message.getPayload()));

          String payloadString = new String(message.getPayload());  // Convert payload to string
          JSONObject jsonObj = new JSONObject(payloadString);       // Parse string to JSON object

          String actMsg = jsonObj.has("msg") ? jsonObj.getString("msg") : "-";

          if (actMsg.equals("Execute")) {

            activations[0]++;

            if (activations[0] == activationsNeeded) {
              activations[0] = 0;
              executeAction();
            } else {
              System.out.println(
                  name + " > '" + activations[0] + " / " + activationsNeeded
                      + " tokens received; waiting for the rest...");
            }
          } else {
            if (actMsg.equals("Ping")) {
              System.out.println(
                  name + " > Pinged successfully on topic '" + subscribeTopic + "'");
            }
          }
        }

        @Override
        public void deliveryComplete(IMqttDeliveryToken token) {
          System.out.println(name + " > Delivery complete; TokenID = " + token.getMessageId());
        }
      });
    } catch (MqttException me) {
      me.printStackTrace();
    }
  }


  private void executeAction() throws MqttException {

    if (publishTopic.equals("-")) {
      System.out.println(name + " > FINISHED");
    } else {
      MqttMessage mqttMessage = new MqttMessage(
          "{\"msg\": \"Execute\", \"tokens\": \"1\"}".getBytes());
      mqttMessage.setQos(1);

      System.out.println(name + " > Publishing response to topic '" + publishTopic + "'...");

      client.publish(publishTopic, mqttMessage);
      System.out.println(
          name + " > Response message published to topic '" + publishTopic + "'");
    }
  }
}
