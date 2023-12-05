import org.zeromq.*;

public class lec_05_prg_09_dealer_router_async_server {
    public static class ServerWorker extends Thread {
        private ZContext context;
        private int id;

        public ServerWorker(ZContext context, int id) {
            this.context = context;
            this.id = id;
        }

        public void run() {
            ZMQ.Socket worker = context.createSocket(SocketType.DEALER);
            worker.connect("inproc://backend");
            System.out.println("Worker#" + id + " started");

            while (true) {
                ZMsg msg = ZMsg.recvMsg(worker);
                ZFrame ident = msg.pop();
                ZFrame content = msg.pop();
                assert (content != null);
                msg.destroy();

                System.out.println("Worker#" + id + " received " + content + " from " + ident);
                ident.send(worker, ZFrame.REUSE + ZFrame.MORE);
                content.send(worker, ZFrame.REUSE);
            }
        }
    }

    public static class ServerTask extends Thread {
        private int num_server;
        public ServerTask(int num_server) {
            this.num_server = num_server;
        }

        public void run() {
            ZContext context = new ZContext();
            ZMQ.Socket frontend = context.createSocket(SocketType.ROUTER);
            frontend.bind("tcp://*:5570");

            ZMQ.Socket backend = context.createSocket(SocketType.DEALER);
            backend.bind("inproc://backend");

            ServerWorker[] workers = new ServerWorker[num_server];
            for (int i = 0; i < num_server; i++) {
                workers[i] = new ServerWorker(context, i);
                workers[i].start();
            }

            ZMQ.proxy(frontend, backend, null);
        }
    }

    public static void main(String[] args) throws Exception {
        int num_server = args.length >= 1 ? Integer.parseInt(args[0]) : 1;
        ServerTask server = new ServerTask(num_server);
        server.start();

        server.join();
    }
}