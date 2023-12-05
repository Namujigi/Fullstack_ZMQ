import org.zeromq.SocketType;
import org.zeromq.ZMQ;
import org.zeromq.ZContext;

public class lec_05_prg_02_req_rep_basic_client {
    public static void main(String[] args) {
        ZContext context = new ZContext();

        // Socket to talk to server
        System.out.println("Connecting to hello world server...");
        ZMQ.Socket socket = context.createSocket(SocketType.REQ);
        socket.connect("tcp://localhost:5555");

        // Do 10 Requests, waiting each time for a response
        for (int i = 0; i < 10; i++) {
            System.out.println("Sending request " + i + " ...");

            String request = "Hello";
            socket.send(request.getBytes(ZMQ.CHARSET), 0);

            // Get the Reply
            byte[] message = socket.recv(0);
            System.out.println("Received reply " + i + " [ " + new String(message, ZMQ.CHARSET) + " ]");
        }
    }
}
