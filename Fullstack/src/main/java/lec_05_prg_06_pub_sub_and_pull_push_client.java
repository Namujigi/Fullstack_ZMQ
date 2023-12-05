import org.zeromq.SocketType;
import org.zeromq.ZContext;
import org.zeromq.ZMQ;

import java.util.Random;

public class lec_05_prg_06_pub_sub_and_pull_push_client {
    public static void main(String[] args) throws Exception {
        ZContext context = new ZContext();
        ZMQ.Socket subscriber = context.createSocket(SocketType.SUB);
        subscriber.subscribe("".getBytes(ZMQ.CHARSET)); //set socket option
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
                System.out.println("I: receive message " + message);
            }
            else {
                int rand = random.nextInt(100);
                String msg;
                if (rand < 10) {
                    msg = Integer.toString(rand);
                    publisher.send(msg.getBytes(ZMQ.CHARSET), 0);
                    System.out.println("I: sending message " + msg);
                }
            }
        }
    }
}
