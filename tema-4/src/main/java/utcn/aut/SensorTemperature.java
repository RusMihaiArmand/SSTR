package utcn.aut;


import com.rabbitmq.client.*;

import java.io.IOException;
import java.util.Random;
import java.util.concurrent.TimeoutException;

public class SensorTemperature {

    private static String exchangeChannel = "sensor1E";

    private static double temperature = 55f;

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


            String msg1 = String.valueOf(temperature);
            channel.basicPublish(exchangeChannel, "", MessageProperties.PERSISTENT_TEXT_PLAIN,
                    msg1.getBytes("UTF-8"));
            System.out.println("[Sensor Temp - INIT] Sent '" + msg1 + "' to exchange: " + exchangeChannel);


            DeliverCallback deliverCallback = (consumerTag, deliveredMsg) -> {

                System.out.println();

                String msg = new String(deliveredMsg.getBody(), "UTF-8");
                System.out.println("[Sensor Temp] Got this message from Actuator >> '" + msg + "'");


//        try {
//          Thread.sleep(1000);
//        } catch (InterruptedException e) {
//          Thread.currentThread().interrupt();
//        }


                String[] parts = msg.split(" ");

                int heatPow = Integer.parseInt(parts[1]);
                int humPow = Integer.parseInt(parts[3]);


                double tempChange = random.nextInt(401) / 100.00 - 2.00 + 3.00 * heatPow / 100.00 - 0.5 * humPow / 100.00;

                temperature = temperature + tempChange;

                if (temperature < 5f)
                    temperature = 5;

                if (temperature > 65f)
                    temperature = 65;

                System.out.println("[Sensor Temp] Recorded this temperature: " + temperature);

                msg = String.valueOf(temperature);


                channel.basicPublish(exchangeChannel, "", MessageProperties.PERSISTENT_TEXT_PLAIN,
                        msg.getBytes("UTF-8"));
                System.out.println("[Sensor Temp] Sent '" + msg + "' to exchange: " + exchangeChannel);


//                try {
//                    Thread.sleep(5000);
//                } catch (InterruptedException e) {
//                    throw new RuntimeException(e);
//                }


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
