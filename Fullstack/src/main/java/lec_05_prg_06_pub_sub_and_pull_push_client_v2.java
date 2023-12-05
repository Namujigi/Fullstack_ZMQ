import org.zeromq.SocketType;
import org.zeromq.ZMQ;
import org.zeromq.ZContext;
import java.io.*;
import java.util.*;

public class lec_05_prg_06_pub_sub_and_pull_push_client_v2 {
    public static void main(String[] args) throws Exception {
        String clientID = args.length >= 1 ? args[2] : "1";

        ZContext context = new ZContext();
        ZMQ.Socket subscriber = context.createSocket(SocketType.SUB);
        subscriber.subscribe("".getBytes(ZMQ.CHARSET));
        subscriber.connect("tcp://localhost:5557");

        ZMQ.Socket publisher = context.createSocket(SocketType.PUSH);
        publisher.connect("tcp://localhost:5558");

        Random random = new Random();
        random.setSeed(System.currentTimeMillis());
        ZMQ.Poller poller = context.createPoller(1);
        poller.register(subscriber, ZMQ.Poller.POLLIN);

        while (true) {
            if (poller.poll(100) > 0 && ZMQ.Poller.POLLIN > 0) {
                byte[] string = subscriber.recv();
                String message = new String(string, ZMQ.CHARSET);
                System.out.println("client#" + clientID + ": receive status => " + message);
            }
            else {
                int rand = random.nextInt(100);
                String msg;
                if (rand < 10) {
                    Thread.sleep(1000);
                    msg = "(client#" + clientID + ":ON)";
                    publisher.send(msg.getBytes(ZMQ.CHARSET), 0);
                    System.out.println("client#" + clientID + ": send status - activated");
                }
                else if (rand > 90) {
                    Thread.sleep(1000);
                    msg = "(client#" + clientID + ":OFF)";
                    publisher.send(msg.getBytes(ZMQ.CHARSET), 0);
                    System.out.println("client#" + clientID + ": send status - deactivated");
                }
            }
        }
    }
}
