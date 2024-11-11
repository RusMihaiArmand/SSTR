package utcn.aut;

import org.eclipse.paho.client.mqttv3.IMqttDeliveryToken;
import org.eclipse.paho.client.mqttv3.MqttCallback;
import org.eclipse.paho.client.mqttv3.MqttClient;
import org.eclipse.paho.client.mqttv3.MqttConnectOptions;
import org.eclipse.paho.client.mqttv3.MqttException;
import org.eclipse.paho.client.mqttv3.MqttMessage;
import org.eclipse.paho.client.mqttv3.persist.MemoryPersistence;


import org.json.JSONObject;


public class Position {

  String broker;
  String subscribeTopic;
  String publishTopic;
  String name;
  int tokens;

  MqttClient client;

  public Position(String broker, String subscribeTopic, String publishTopic, String name,
      int tokens) {
    this.broker = broker;
    this.subscribeTopic = subscribeTopic;
    this.publishTopic = publishTopic;
    this.name = name;
    this.tokens = tokens;
  }

  public void activation() {

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

            actMsg = jsonObj.has("tokens") ? jsonObj.getString("tokens") : "0";
            int addedTokens;

            try {
              addedTokens = Integer.parseInt(actMsg);
            } catch (NumberFormatException e) {
              addedTokens = 0;
            }


            if (addedTokens > 0) {
              System.out.println(name + " > Added " + addedTokens + " tokens");
              addTokens(addedTokens);
            }

            if (tokens > 0) {
              executeAction();
            }
          } else {
            if (actMsg.equals("Clear")) {
              resetTokens();
              System.out.println(
                  name + " > " + " cleared tokens.");
            } else {
              if (actMsg.equals("Ping")) {
                System.out.println(
                    name + " > " + tokens + " tokens here. Pinged successfully on topic '"
                        + subscribeTopic + "'");
              }
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

  private void addTokens(int t)
  {
    tokens = tokens + t;
  }

  private void resetTokens()
  {
    tokens = 0;
  }

  private void executeAction() throws MqttException {

    addTokens(-1);

    MqttMessage mqttMessage = new MqttMessage("{\"msg\": \"Execute\"}".getBytes());
    mqttMessage.setQos(1);

    System.out.println(name + " > Publishing response to topic '" + publishTopic + "'...");

    client.publish(publishTopic, mqttMessage);
    System.out.println(
        name + " > Response message published to topic '" + publishTopic + "'");

  }


}
