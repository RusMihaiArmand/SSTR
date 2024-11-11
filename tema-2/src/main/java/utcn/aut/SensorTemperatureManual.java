package utcn.aut;

import com.rabbitmq.client.ConnectionFactory;
import com.rabbitmq.client.Connection;
import com.rabbitmq.client.Channel;
import com.rabbitmq.client.MessageProperties;
import java.io.IOException;
import java.util.Scanner;
import java.util.concurrent.TimeoutException;

public class SensorTemperatureManual {

  private static String exchangeChannel = "sensor1E";


  public static void main(String[] args) throws IOException, TimeoutException {

    Scanner scanner = new Scanner(System.in);

    ConnectionFactory factory = new ConnectionFactory();
    factory.setHost("localhost");

    try (Connection connection = factory.newConnection();
        Channel channel = connection.createChannel()) {

      channel.exchangeDeclare(exchangeChannel, "fanout");

      while (true) {
        boolean ok = false;
        float temp = 0f;

        while (!ok) {
          System.out.println("[Sensor1] Enter measured temperature > ");

          if (scanner.hasNextFloat()) {
            temp = scanner.nextFloat();
            ok = true;
          } else {
            System.out.println("[Sensor1] Please enter a valid float number");
            scanner.next();
          }
        }

        temp = (int) (temp * 10) / 10.0f;
        System.out.println("[Sensor1] Recorded this temperature: " + temp);

        String msg = String.valueOf(temp);

        channel.basicPublish(exchangeChannel, "", MessageProperties.PERSISTENT_TEXT_PLAIN,
            msg.getBytes("UTF-8"));
        System.out.println("[Sensor1] Sent '" + msg + "' to exchange: " + exchangeChannel);
      }

    }
  }

}
