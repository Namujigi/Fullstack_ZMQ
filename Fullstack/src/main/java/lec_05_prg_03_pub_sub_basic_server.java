import org.zeromq.SocketType;
import org.zeromq.ZContext;
import org.zeromq.ZMQ;
import java.util.Random;

public class lec_05_prg_03_pub_sub_basic_server {
    public static void main(String[] args) throws Exception {
        System.out.println("Publishing updates at weather server...");

        ZContext context = new ZContext();
        ZMQ.Socket socket = context.createSocket(SocketType.PUB);
        socket.bind("tcp://*:5556");

        Random random = new Random();
        while(true) {
            int zipcode = random.nextInt(100000);
            int temperature = random.nextInt(215) - 80;
            int relhumidity = random.nextInt(50) + 10;

            String response = zipcode + " " + temperature + " " + relhumidity;
            socket.send(response.getBytes(ZMQ.CHARSET), 0);
        }
    }
}
