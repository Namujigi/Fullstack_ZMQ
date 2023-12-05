import org.zeromq.SocketType;
import org.zeromq.ZContext;
import org.zeromq.ZMQ;

public class lec_05_prg_11_dealer_router_async_client_thread {
    public class ClientTask extends Thread {
        private ZContext context;
        private ZMQ.Socket socket;
        private String id;
        private String identity;
        private ZMQ.Poller poller;

        public ClientTask(String id) {
            this.id = id;
        }

        private void recvHandler() {
            while (true) {
                if (poller.poll(100) > 0 && ZMQ.Poller.POLLIN > 0) {
                    byte[] msg_temp = socket.recv();
                    String msg = new String(msg_temp, ZMQ.CHARSET);
                    System.out.println(identity + " received: " + msg);
                }
            }
        }

        public void run() {
            this.context = new ZContext();
            this.socket = this.context.createSocket(SocketType.DEALER);
            this.identity = id;
            this.socket.setIdentity(identity.getBytes(ZMQ.CHARSET));
            this.socket.connect("tcp://localhost:5570");
            System.out.println("Client " + identity + " started");

            this.poller = context.createPoller(1);
            this.poller.register(socket, ZMQ.Poller.POLLIN);

            int reqs = 0;

            Thread clientThread = new Thread(this::recvHandler);
            clientThread.setDaemon(true);
            clientThread.start();

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
            }
        }
    }

    public static void main(String[] args) throws Exception {
        String clientID = "client#" + (args.length >= 1 ? args[0] : "1");

        lec_05_prg_10_dealer_router_async_client.ClientTask client = new lec_05_prg_10_dealer_router_async_client.ClientTask(clientID);
        client.start();
    }
}
