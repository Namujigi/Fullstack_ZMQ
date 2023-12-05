import org.zeromq.SocketType;
import org.zeromq.ZContext;
import org.zeromq.ZMQ;

public class lec_05_prg_01_req_rep_basic_server {
    public static void main(String[] args) throws Exception {
        ZContext context = new ZContext();
        ZMQ.Socket socket = context.createSocket(SocketType.REP);
        socket.bind("tcp://*:5555");

        while(true) {
            // Wait for next Request from Client
            byte[] message = socket.recv();
            System.out.println("Received Request: " + new String(message, ZMQ.CHARSET));

            // Do some 'Work'
            Thread.sleep(1000);

            // Send Reply back to Client
            String response = "World";
            socket.send(response.getBytes(ZMQ.CHARSET), 0);
        }
    }
}