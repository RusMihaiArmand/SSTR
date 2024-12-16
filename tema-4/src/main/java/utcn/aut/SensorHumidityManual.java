package utcn.aut;

import com.rabbitmq.client.ConnectionFactory;
import com.rabbitmq.client.Connection;
import com.rabbitmq.client.Channel;
import com.rabbitmq.client.MessageProperties;
import java.io.IOException;
import java.util.Scanner;
import java.util.concurrent.TimeoutException;

public class SensorHumidityManual {

  private static String exchangeChannel = "sensor2E";


  public static void main(String[] args) throws IOException, TimeoutException {

    Scanner scanner = new Scanner(System.in);

    ConnectionFactory factory = new ConnectionFactory();
    factory.setHost("localhost");

    try (Connection connection = factory.newConnection();
        Channel channel = connection.createChannel()) {

      channel.exchangeDeclare(exchangeChannel, "fanout");

      while (true) {
        boolean ok = false;
        int hum = 0;

        while (!ok) {
          System.out.println("[Sensor2] Enter measured humidity (percent) > ");

          if (scanner.hasNextInt()) {
            hum = scanner.nextInt();
            ok = true;
          } else {
            System.out.println("[Sensor2] Please enter a valid int number");
            scanner.next();
          }
        }

        System.out.println("[Sensor2] Recorded this humidity: " + hum);

        String msg = String.valueOf(hum);

        channel.basicPublish(exchangeChannel, "", MessageProperties.PERSISTENT_TEXT_PLAIN,
            msg.getBytes("UTF-8"));
        System.out.println("[Sensor2] Sent '" + msg + "' to exchange: " + exchangeChannel);
      }

    }
  }

}
