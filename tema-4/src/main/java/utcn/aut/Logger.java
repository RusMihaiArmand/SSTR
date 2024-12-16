package utcn.aut;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.node.ArrayNode;
import com.fasterxml.jackson.databind.node.ObjectNode;
import com.rabbitmq.client.ConnectionFactory;
import com.rabbitmq.client.Connection;
import com.rabbitmq.client.Channel;
import com.rabbitmq.client.DeliverCallback;
import java.io.File;
import java.io.IOException;
import java.nio.file.Paths;
import java.time.format.DateTimeFormatter;
import java.util.concurrent.TimeoutException;
import java.time.LocalDateTime;

public class Logger {

  private static String queueChannel = "controllerE";

  private static String filePath = "data.json";


  public static void main(String[] args) throws IOException, TimeoutException {

    ConnectionFactory factory = new ConnectionFactory();
    factory.setHost("localhost");

    ObjectMapper mapper = new ObjectMapper();
    File file = new File(filePath);

    try (Connection connection = factory.newConnection();
        Channel channel = connection.createChannel()) {

      channel.exchangeDeclare(queueChannel, "fanout");
      String queueName = channel.queueDeclare().getQueue();
      channel.queueBind(queueName, queueChannel, "");

      DeliverCallback deliverCallback = (consumerTag, deliveredMsg) -> {
        String msg = new String(deliveredMsg.getBody(), "UTF-8");
        System.out.println("[Logger] Got this message from Controller >> '" + msg + "'");

        ArrayNode dataArray;
        if (file.exists()) {
          JsonNode node = mapper.readTree(file);
          dataArray = (ArrayNode) node;
        } else {
          dataArray = mapper.createArrayNode();
        }

        ObjectNode newEntry = mapper.createObjectNode();

        newEntry.put("time", LocalDateTime.now().format(DateTimeFormatter.ISO_LOCAL_DATE_TIME));
        newEntry.put("command", msg);

        dataArray.add(newEntry);

        mapper.writerWithDefaultPrettyPrinter().writeValue(Paths.get(filePath).toFile(), dataArray);


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
