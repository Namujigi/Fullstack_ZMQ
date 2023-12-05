import org.zeromq.SocketType;
import org.zeromq.ZContext;
import org.zeromq.ZMQ;

import java.util.Random;

public class lec_05_prg_10_dealer_router_async_client {
    public static class ClientTask extends Thread {
        private String id;

        public ClientTask(String id) {
            this.id = id;
        }

        public void run() {
            ZContext context = new ZContext();
            ZMQ.Socket socket = context.createSocket(SocketType.DEALER);
            String identity = id;
            socket.setIdentity(identity.getBytes());
            socket.connect("tcp://localhost:5570");
            System.out.println("Client " + identity + " started");

            ZMQ.Poller poller = context.createPoller(1);
            poller.register(socket, ZMQ.Poller.POLLIN);
            int reqs = 0;

            while (true) {
                reqs++;
                System.out.println("Req #" + reqs + " sent..");
                String req = "request #" + reqs;
                socket.send(req.getBytes(ZMQ.CHARSET), 0);

                try {
                    Thread.sleep(1000);
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }

                if (poller.poll(100) > 0 && ZMQ.Poller.POLLIN > 0) {
                    byte[] msg_temp = socket.recv();
                    String msg = new String(msg_temp, ZMQ.CHARSET);
                    System.out.println(identity + " received: " + msg);
                }
            }
        }
    }

    public static void main(String[] args) throws Exception {
        String clientID = "client#" + (args.length >= 1 ? args[0] : "1");

        ClientTask client = new ClientTask(clientID);
        client.start();
    }
}
