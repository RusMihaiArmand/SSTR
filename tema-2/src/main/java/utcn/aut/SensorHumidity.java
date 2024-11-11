package utcn.aut;

import com.rabbitmq.client.ConnectionFactory;
import com.rabbitmq.client.Connection;
import com.rabbitmq.client.Channel;
import com.rabbitmq.client.MessageProperties;
import java.io.IOException;
import java.util.Random;
import java.util.concurrent.TimeoutException;

public class SensorHumidity {

  private static String exchangeChannel = "sensor2E";


  public static void main(String[] args) throws IOException, TimeoutException {

    Random random = new Random();

    ConnectionFactory factory = new ConnectionFactory();
    factory.setHost("localhost");

    try (Connection connection = factory.newConnection();
        Channel channel = connection.createChannel()) {

      channel.exchangeDeclare(exchangeChannel, "fanout");

      while (true) {

        int hum = random.nextInt(31) + 25;
        System.out.println("[Sensor2] Recorded this humidity: " + hum);

        String msg = String.valueOf(hum);

        channel.basicPublish(exchangeChannel, "", MessageProperties.PERSISTENT_TEXT_PLAIN,
            msg.getBytes("UTF-8"));
        System.out.println("[Sensor2] Sent '" + msg + "' to exchange: " + exchangeChannel);

        try {
          Thread.sleep(5000);
        } catch (InterruptedException e) {
          Thread.currentThread().interrupt();
        }

      }

    }
  }

}
