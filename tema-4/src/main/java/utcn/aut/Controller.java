package utcn.aut;

import com.rabbitmq.client.ConnectionFactory;
import com.rabbitmq.client.Connection;
import com.rabbitmq.client.Channel;
import com.rabbitmq.client.DeliverCallback;
import com.rabbitmq.client.MessageProperties;
import net.sourceforge.jFuzzyLogic.FIS;
import net.sourceforge.jFuzzyLogic.FunctionBlock;
import net.sourceforge.jFuzzyLogic.plot.JFuzzyChart;
import net.sourceforge.jFuzzyLogic.rule.Variable;

import java.io.IOException;
import java.util.concurrent.TimeoutException;

public class Controller {

    private static String queueChannel1 = "sensor1E";
    private static String queueChannel2 = "sensor2E";

    private static String exchangeChannel = "controllerE";

    private static float tempRef = 35.00f;
    private static float humRef = 40.00f;

    private static float tempDif = 0;
    private static float humDif = 0;

    private static int dataReceived = 0;
    private static int dataMustReceive = 2;


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

                Float tempReceived = Float.parseFloat(msg);
                tempDif = tempReceived - tempRef;

                dataReceived++;
                System.out.println("[Controller] Got data from " + dataReceived + " / " + dataMustReceive + " sensors");
                if (dataReceived == dataMustReceive) {
                    dataReceived = 0;

                    String filename = "src/main/java/utcn/aut/control.fcl";
                    FIS fis = FIS.load(filename, true);

                    if (fis == null) {
                        System.err.println("Can't load file: '" + filename + "'");
                        System.exit(1);
                    }

                    FunctionBlock fb = fis.getFunctionBlock(null);

                    fb.setVariable("temp", tempDif);
                    fb.setVariable("hum", humDif);

                    fb.evaluate();

                    fb.getVariable("heat_pow").defuzzify();
                    System.out.println("Heater power : " + fb.getVariable("heat_pow").getValue());
                    Variable heat_pow = fb.getVariable("heat_pow");

                    int heatVal = (int) Math.round(heat_pow.getValue());

                    fb.getVariable("hum_pow").defuzzify();
                    System.out.println("Humidifier power : " + fb.getVariable("hum_pow").getValue());
                    Variable hum_pow = fb.getVariable("hum_pow");

                    int humPow = (int) Math.round(hum_pow.getValue());

                    msg = "[HEAT] " + heatVal + " [HUM] " + humPow;


                    channel.basicPublish(exchangeChannel, "", MessageProperties.PERSISTENT_TEXT_PLAIN,
                            msg.getBytes("UTF-8"));
                    System.out.println("[Controller] Sent '" + msg + "' to exchange: " + exchangeChannel);

                }


            };

            DeliverCallback deliverCallback2 = (consumerTag, deliveredMsg) -> {
                String msg = new String(deliveredMsg.getBody(), "UTF-8");
                System.out.println("[Controller] Got this message from Sensor 2 >> '" + msg + "'");


                Float humReceived = Float.parseFloat(msg);
                humDif = humReceived - humRef;


                dataReceived++;
                System.out.println("[Controller] Got data from " + dataReceived + " / " + dataMustReceive + " sensors");
                if (dataReceived == dataMustReceive) {
                    dataReceived = 0;

                    String filename = "src/main/java/utcn/aut/control.fcl";
                    FIS fis = FIS.load(filename, true);

                    if (fis == null) {
                        System.err.println("Can't load file: '" + filename + "'");
                        System.exit(1);
                    }

                    FunctionBlock fb = fis.getFunctionBlock(null);

                    fb.setVariable("temp", tempDif);
                    fb.setVariable("hum", humDif);

                    fb.evaluate();

                    fb.getVariable("heat_pow").defuzzify();
                    System.out.println("Heater power : " + fb.getVariable("heat_pow").getValue());
                    Variable heat_pow = fb.getVariable("heat_pow");

                    int heatVal = (int) Math.round(heat_pow.getValue());

                    fb.getVariable("hum_pow").defuzzify();
                    System.out.println("Humidifier power : " + fb.getVariable("hum_pow").getValue());
                    Variable hum_pow = fb.getVariable("hum_pow");

                    int humPow = (int) Math.round(hum_pow.getValue());

                    msg = "[HEAT] " + heatVal + " [HUM] " + humPow;


                    channel.basicPublish(exchangeChannel, "", MessageProperties.PERSISTENT_TEXT_PLAIN,
                            msg.getBytes("UTF-8"));
                    System.out.println("[Controller] Sent '" + msg + "' to exchange: " + exchangeChannel);

                }

            };

            channel.basicConsume(queueName1, true, deliverCallback1, consumerTag -> {
            });
            channel.basicConsume(queueName2, true, deliverCallback2, consumerTag -> {
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
