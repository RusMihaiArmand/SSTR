package utcn.aut;

import com.rabbitmq.client.ConnectionFactory;
import com.rabbitmq.client.Connection;
import com.rabbitmq.client.Channel;
import com.rabbitmq.client.DeliverCallback;
import com.rabbitmq.client.MessageProperties;
import java.io.IOException;
import java.util.concurrent.TimeoutException;

public class Controller {

  private static String queueChannel1 = "sensor1E";
  private static String queueChannel2 = "sensor2E";

  private static String exchangeChannel = "controllerE";

  private static float tempRef = 35f;

  private static int humRef = 40;


  public static void main(String[] args) throws IOException, TimeoutException {

    ConnectionFactory factory = new ConnectionFactory();
    factory.setHost("localhost");

    try (Connection connection = factory.newConnection();
        Channel channel = connection.createChannel()) {

      channel.exchangeDeclare(queueChannel1, "fanout");
      String queueName1 = channel.queueDeclare().getQueue();
      channel.queueBind(queueName1, queueChannel1, "");

      channel.exchangeDeclare(queueChannel2, "fanout");
      String queueName2 = channel.queueDeclare().getQueue();
      channel.queueBind(queueName2, queueChannel2, "");

      DeliverCallback deliverCallback1 = (consumerTag, deliveredMsg) -> {
        String msg = new String(deliveredMsg.getBody(), "UTF-8");
        System.out.println("[Controller] Got this message from Sensor 1 >> '" + msg + "'");

        Float temp = Float.parseFloat(msg);
        Float tempDif = temp - tempRef;

        if (tempDif >= -2f && tempDif <= 2f) {
          msg = "[TEMP] Primary Heater OFF and Primary Cooler OFF";
        } else {
          if (tempDif > 2f && tempDif <= 15f) {
            msg = "[TEMP] Primary Cooler ON";
          } else {
            if (tempDif > 15f) {
              msg = "[TEMP] Primary Cooler ON and Secondary Cooler ON";
            } else {

              if (tempDif < -2f && tempDif >= -15f) {
                msg = "[TEMP] Primary Heater ON";
              } else {
                msg = "[TEMP] Primary Heater ON and Secondary Heater ON";
              }
            }
          }
        }

        channel.basicPublish(exchangeChannel, "", MessageProperties.PERSISTENT_TEXT_PLAIN,
            msg.getBytes("UTF-8"));
        System.out.println("[Controller] Sent '" + msg + "' to exchange: " + exchangeChannel);


      };

      DeliverCallback deliverCallback2 = (consumerTag, deliveredMsg) -> {
        String msg = new String(deliveredMsg.getBody(), "UTF-8");
        System.out.println("[Controller] Got this message from Sensor 2 >> '" + msg + "'");

        int hum = Integer.valueOf(msg);
        int humDif = hum - humRef;

        if (humDif >= -3 && humDif <= 3) {
          msg = "[HUM] Primary Humidifier OFF and Primary Dehumidifier OFF";
        } else {
          if (humDif > 3 && humDif <= 10) {
            msg = "[HUM] Primary Dehumidifier ON";
          } else {
            if (humDif > 10) {
              msg = "[HUM] Primary Dehumidifier ON and Secondary Dehumidifier ON";
            } else {
              if (humDif < -3 && humDif >= -10) {
                msg = "[HUM] Primary Humidifier ON";
              } else {
                msg = "[HUM] Primary Humidifier ON and Secondary Humidifier ON";
              }
            }
          }
        }

        channel.basicPublish(exchangeChannel, "", MessageProperties.PERSISTENT_TEXT_PLAIN,
            msg.getBytes("UTF-8"));
        System.out.println("[Controller] Sent '" + msg + "' to exchange: " + exchangeChannel);

      };

      channel.basicConsume(queueName1, true, deliverCallback1, consumerTag -> {
      });
      channel.basicConsume(queueName2, true, deliverCallback2, consumerTag -> {
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
