package utcn.aut;

import com.rabbitmq.client.ConnectionFactory;
import com.rabbitmq.client.Connection;
import com.rabbitmq.client.Channel;
import com.rabbitmq.client.DeliverCallback;
import java.io.IOException;
import java.util.concurrent.TimeoutException;

public class Actuator {

  private static String queueChannel = "controllerE";

  private static boolean heatPrimOn = false;
  private static boolean heatSecOn = false;
  private static boolean coolPrimOn = false;
  private static boolean coolSecOn = false;

  private static boolean humPrimOn = false;
  private static boolean humSecOn = false;
  private static boolean dehumPrimOn = false;
  private static boolean dehumSecOn = false;


  public static void main(String[] args) throws IOException, TimeoutException {

    ConnectionFactory factory = new ConnectionFactory();
    factory.setHost("localhost");

    try (Connection connection = factory.newConnection();
        Channel channel = connection.createChannel()) {

      channel.exchangeDeclare(queueChannel, "fanout");
      String queueName = channel.queueDeclare().getQueue();
      channel.queueBind(queueName, queueChannel, "");

      DeliverCallback deliverCallback = (consumerTag, deliveredMsg) -> {
        String msg = new String(deliveredMsg.getBody(), "UTF-8");
        System.out.println("[Actuator] Got this message from Controller >> '" + msg + "'");

        if (msg.contains("[TEMP]")) {
          boolean heatPrimOn2 = false;
          boolean heatSecOn2 = false;
          boolean coolPrimOn2 = false;
          boolean coolSecOn2 = false;

          if (msg.contains("Primary Heater ON")) {
            heatPrimOn2 = true;
          }

          if (msg.contains("Secondary Heater ON")) {
            heatSecOn2 = true;
          }

          if (msg.contains("Primary Cooler ON")) {
            coolPrimOn2 = true;
          }

          if (msg.contains("Secondary Cooler ON")) {
            coolSecOn2 = true;
          }

          if (heatPrimOn != heatPrimOn2) {
            if (heatPrimOn2) {
              System.out.println("[Actuator] Turned on Primary Heater");
            } else {
              System.out.println("[Actuator] Turned off Primary Heater");
            }

            heatPrimOn = heatPrimOn2;
          }

          if (heatSecOn != heatSecOn2) {
            if (heatSecOn2) {
              System.out.println("[Actuator] Turned on Secondary Heater");
            } else {
              System.out.println("[Actuator] Turned off Secondary Heater");
            }

            heatSecOn = heatSecOn2;
          }

          if (coolPrimOn != coolPrimOn2) {
            if (coolPrimOn2) {
              System.out.println("[Actuator] Turned on Primary Cooler");
            } else {
              System.out.println("[Actuator] Turned off Primary Cooler");
            }

            coolPrimOn = coolPrimOn2;
          }

          if (coolSecOn != coolSecOn2) {
            if (coolSecOn2) {
              System.out.println("[Actuator] Turned on Secondary Cooler");
            } else {
              System.out.println("[Actuator] Turned off Secondary Cooler");
            }

            coolSecOn = coolSecOn2;
          }


        }

        if (msg.contains("[HUM]")) {
          boolean humPrimOn2 = false;
          boolean humSecOn2 = false;
          boolean dehumPrimOn2 = false;
          boolean dehumSecOn2 = false;

          if (msg.contains("Primary Humidifier ON")) {
            humPrimOn2 = true;
          }

          if (msg.contains("Secondary Humidifier ON")) {
            humSecOn2 = true;
          }

          if (msg.contains("Primary Dehumidifier ON")) {
            dehumPrimOn2 = true;
          }

          if (msg.contains("Secondary Dehumidifier ON")) {
            dehumSecOn2 = true;
          }

          if (humPrimOn != humPrimOn2) {
            if (humPrimOn2) {
              System.out.println("[Actuator] Turned on Primary Humidifier");
            } else {
              System.out.println("[Actuator] Turned off Primary Humidifier");
            }

            humPrimOn = humPrimOn2;
          }

          if (humSecOn != humSecOn2) {
            if (humSecOn2) {
              System.out.println("[Actuator] Turned on Secondary Humidifier");
            } else {
              System.out.println("[Actuator] Turned off Secondary Humidifier");
            }

            humSecOn = humSecOn2;
          }

          if (dehumPrimOn != dehumPrimOn2) {
            if (dehumPrimOn2) {
              System.out.println("[Actuator] Turned on Primary Dehumidifier");
            } else {
              System.out.println("[Actuator] Turned off Primary Dehumidifier");
            }

            dehumPrimOn = dehumPrimOn2;
          }

          if (dehumSecOn != dehumSecOn2) {
            if (dehumSecOn2) {
              System.out.println("[Actuator] Turned on Secondary Dehumidifier");
            } else {
              System.out.println("[Actuator] Turned off Secondary Dehumidifier");
            }

            dehumSecOn = dehumSecOn2;
          }
        }


      };

      channel.basicConsume(queueName, true, deliverCallback, consumerTag -> {
      });

      while (true) {
        try {
          Thread.sleep(1000);
        } catch (InterruptedException e) {
          Thread.currentThread().interrupt();
          break;
        }
      }

    }

  }

}
