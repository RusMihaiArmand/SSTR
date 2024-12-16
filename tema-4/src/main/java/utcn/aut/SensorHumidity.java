package utcn.aut;

import com.rabbitmq.client.*;

import java.io.IOException;
import java.util.Random;
import java.util.concurrent.TimeoutException;

public class SensorHumidity {

    private static String exchangeChannel = "sensor2E";

    private static double humidity = 55f;

    private static String queueChannel = "actuatorE";

    public static void main(String[] args) throws IOException, TimeoutException {

        Random random = new Random();

        ConnectionFactory factory = new ConnectionFactory();
        factory.setHost("localhost");


        try (Connection connection = factory.newConnection();
             Channel channel = connection.createChannel()) {

            channel.exchangeDeclare(queueChannel, "fanout");
            String queueName = channel.queueDeclare().getQueue();
            channel.queueBind(queueName, queueChannel, "");


            String msg1 = String.valueOf(humidity);
            channel.basicPublish(exchangeChannel, "", MessageProperties.PERSISTENT_TEXT_PLAIN,
                    msg1.getBytes("UTF-8"));
            System.out.println("[Sensor Hum - INIT] Sent '" + msg1 + "' to exchange: " + exchangeChannel);

            DeliverCallback deliverCallback = (consumerTag, deliveredMsg) -> {
                System.out.println();

                String msg = new String(deliveredMsg.getBody(), "UTF-8");
                System.out.println("[Sensor Hum] Got this message from Actuator >> '" + msg + "'");


//        try {
//          Thread.sleep(1000);
//        } catch (InterruptedException e) {
//          Thread.currentThread().interrupt();
//        }


                String[] parts = msg.split(" ");

                int heatPow = Integer.parseInt(parts[1]);
                int humPow = Integer.parseInt(parts[3]);


                double humChange = random.nextInt(401) / 100.00 - 2 + 3.00 * humPow / 100.00 - 0.5 * heatPow / 100.00;g

                humidity = humidity + humChange;
                if (humidity < 20f)
                    humidity = 20;
                if (humidity > 60f)
                    humidity = 60;
                System.out.println("[Sensor Hum] Recorded this humidity: " + humidity);

                msg = String.valueOf(humidity);


                channel.basicPublish(exchangeChannel, "", MessageProperties.PERSISTENT_TEXT_PLAIN,
                        msg.getBytes("UTF-8"));
                System.out.println("[Sensor Hum] Sent '" + msg + "' to exchange: " + exchangeChannel);

//          try {
//              Thread.sleep(5000);
//          } catch (InterruptedException e) {
//              throw new RuntimeException(e);
//          }


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
