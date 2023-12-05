import org.zeromq.SocketType;
import org.zeromq.ZContext;
import org.zeromq.ZMQ;

public class lec_05_prg_05_pub_sub_and_pull_push_server {
    public static void main(String[] args) throws Exception {
        ZContext context = new ZContext();
        ZMQ.Socket publisher = context.createSocket(SocketType.PUB);
        publisher.bind("tcp://*:5557");
        ZMQ.Socket collector = context.createSocket(SocketType.PULL);
        collector.bind("tcp://*:5558");

        while(true) {
            byte[] string = collector.recv(0);
            String message = new String(string, ZMQ.CHARSET);
            System.out.println("I: publishing update " + message);
            publisher.send(message.getBytes(ZMQ.CHARSET), 0);
        }
    }
}
