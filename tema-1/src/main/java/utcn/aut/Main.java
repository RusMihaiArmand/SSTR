package utcn.aut;


public class Main {


  public static void main(String[] args) {

    String broker = "tcp://broker.emqx.io:1883";

    Position p1 = new Position(broker, "topic_P1", "topic_T2", "P1", 0);
    p1.activation();
    Tranzition t2 = new Tranzition(broker, "topic_T2", "topic_P2", "T2", 1);
    t2.activation();

    Position p2 = new Position(broker, "topic_P2", "topic_T3", "P2", 0);
    p2.activation();
    Tranzition t3 = new Tranzition(broker, "topic_T3", "topic_P3", "T3", 1);
    t3.activation();
    Position p3 = new Position(broker, "topic_P3", "topic_T4", "P3", 0);
    p3.activation();

    Tranzition t4 = new Tranzition(broker, "topic_T4", "topic_P46", "T4", 1);
    t4.activation();

    Position p4 = new Position(broker, "topic_P46", "topic_T5", "P4", 0);
    p4.activation();
    Tranzition t5 = new Tranzition(broker, "topic_T5", "topic_P5", "T5", 1);
    t5.activation();
    Position p5 = new Position(broker, "topic_P5", "topic_T7", "P5", 0);
    p5.activation();

    Position p6 = new Position(broker, "topic_P46", "topic_T6", "P6", 0);
    p6.activation();
    Tranzition t6 = new Tranzition(broker, "topic_T6", "topic_P7", "T6", 1);
    t6.activation();
    Position p7 = new Position(broker, "topic_P7", "topic_T7", "P7", 0);
    p7.activation();

    Tranzition t7 = new Tranzition(broker, "topic_T7", "topic_P8", "T7", 2);
    t7.activation();
    Position p8 = new Position(broker, "topic_P8", "topic_T8", "P8", 0);
    p8.activation();
    Tranzition t8 = new Tranzition(broker, "topic_T8", "-", "T8", 1);
    t8.activation();


  }
}
