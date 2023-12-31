package perf;

import zmq.Ctx;
import zmq.Msg;
import zmq.SocketBase;
import zmq.ZMQ;

public class InprocLat
{
    private InprocLat()
    {
    }

    static class Worker implements Runnable
    {
        private final Ctx ctx;
        private final int roundtripCount;

        Worker(Ctx ctx, int roundtripCount)
        {
            this.ctx = ctx;
            this.roundtripCount = roundtripCount;
        }

        @Override
        public void run()
        {
            SocketBase s = ZMQ.socket(ctx, ZMQ.ZMQ_REP);
            if (s == null) {
                printf("error in socket: %s\n");
                exit(1);
            }

            boolean rc = ZMQ.connect(s, "inproc://lat_test");
            if (!rc) {
                printf("error in connect: %s\n");
                exit(1);
            }

            Msg msg;

            for (int i = 0; i != roundtripCount; i++) {
                msg = ZMQ.recvMsg(s, 0);
                if (msg == null) {
                    printf("error in recvmsg: %s\n");
                    exit(1);
                }
                int r = ZMQ.sendMsg(s, msg, 0);
                if (r < 0) {
                    printf("error in sendmsg: %s\n");
                    exit(1);
                }
            }

            ZMQ.close(s);
        }

        private void exit(int i)
        {
            // TODO Auto-generated method stub
        }
    }

    public static void main(String[] argv) throws Exception
    {
        if (argv.length != 2) {
            printf("usage: inproc_lat <message-size> <roundtrip-count>\n");
            return;
        }

        int messageSize = atoi(argv[0]);
        int roundtripCount = atoi(argv[1]);

        Ctx ctx = ZMQ.init(1);
        if (ctx == null) {
            printf("error in init:");
            return;
        }

        SocketBase s = ZMQ.socket(ctx, ZMQ.ZMQ_REQ);
        if (s == null) {
            printf("error in socket: ");
            return;
        }

        boolean rc = ZMQ.bind(s, "inproc://lat_test");
        if (!rc) {
            printf("error in bind: ");
            return;
        }

        Thread localThread = new Thread(new Worker(ctx, roundtripCount));
        localThread.start();

        Msg smsg = ZMQ.msgInitWithSize(messageSize);

        printf("message size: %d [B]\n", messageSize);
        printf("roundtrip count: %d\n", roundtripCount);

        long watch = ZMQ.startStopwatch();

        for (int i = 0; i != roundtripCount; i++) {
            int r = ZMQ.sendMsg(s, smsg, 0);
            if (r < 0) {
                printf("error in sendmsg: %s\n");
                return;
            }
            Msg msg = ZMQ.recvMsg(s, 0);
            if (msg == null) {
                printf("error in recvmsg: %s\n");
                return;
            }
            if (ZMQ.msgSize(msg) != messageSize) {
                printf("message of incorrect size received\n");
                return;
            }
        }

        long elapsed = ZMQ.stopStopwatch(watch);

        double latency = (double) elapsed / (roundtripCount * 2);

        localThread.join();

        printf("average latency: %.3f [us]\n", latency);

        ZMQ.close(s);

        ZMQ.term(ctx);
    }

    private static int atoi(String string)
    {
        return Integer.parseInt(string);
    }

    private static void printf(String string)
    {
        System.out.println(string);
    }

    private static void printf(String string, Object... args)
    {
        System.out.printf((string) + "%n", args);
    }
}
