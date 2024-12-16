package utcn.aut;

import com.rabbitmq.client.*;

import java.io.IOException;
import java.util.concurrent.TimeoutException;

public class Actuator {

    private static String queueChannel = "controllerE";

    private static String exchangeChannel = "actuatorE";

    private static int heatPow = 0;
    private static int humPow = 0;


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

                String[] parts = msg.split(" ");

                try {
                    Thread.sleep(2000);
                } catch (InterruptedException e) {
                    Thread.currentThread().interrupt();
                }

                heatPow = Integer.parseInt(parts[1]);
                humPow = Integer.parseInt(parts[3]);

                if (heatPow > 0) {
                    System.out.println("[Actuator] Heater power set to " + heatPow);
                } else {
                    if (heatPow < 0) {
                        System.out.println("[Actuator] Cooler power set to " + -heatPow);
                    } else {
                        System.out.println("[Actuator] Heater/cooler OFF");
                    }
                }

                if (humPow > 0) {
                    System.out.println("[Actuator] Humidifier power set to " + humPow);
                } else {
                    if (humPow < 0) {
                        System.out.println("[Actuator] Dehumidifier power set to " + -humPow);
                    } else {
                        System.out.println("[Actuator] Humidifier/dehumidifier OFF");
                    }
                }

              try {
                Thread.sleep(3000);
              } catch (InterruptedException e) {
                throw new RuntimeException(e);
              }


                channel.basicPublish(exchangeChannel, "", MessageProperties.PERSISTENT_TEXT_PLAIN,
                        msg.getBytes("UTF-8"));
                System.out.println("[Actuator] Sent '" + msg + "' to exchange: " + exchangeChannel);


            };

            channel.basicConsume(queueName, true, deliverCallback, consumerTag -> {
            });

            while (true) {
                try {
                    Thread.sleep(3000);
                } catch (InterruptedException e) {
                    Thread.currentThread().interrupt();
                    break;
                }
            }

        }

    }

}
