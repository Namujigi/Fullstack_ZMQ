import org.zeromq.SocketType;
import org.zeromq.ZMQ;
import org.zeromq.ZContext;
import java.io.*;

public class lec_05_prg_04_pub_sub_basic_client {
    public static void main(String[] args) throws IOException {
        // Socket to talk to server
        ZContext context = new ZContext();
        ZMQ.Socket socket = context.createSocket(SocketType.SUB);

        System.out.println("Collecting updates from weather server...");
        socket.connect("tcp://localhost:5556");

        // Subscribe to zipcode, default is NYC, 10001
        String zip_filter = args.length >= 1 ? args[0] : "10001";
        socket.subscribe(zip_filter.getBytes(ZMQ.CHARSET.forName("UTF-8"))); //Set Socket Option

        // Process 5 updates
        int total_temp = 0;
        int update_nbr;

        for (update_nbr = 0; update_nbr < 20; update_nbr++) {
            byte[] message = socket.recv(0);
            String string = new String(message, ZMQ.CHARSET);
            String[] str_arr = string.split(" ");

            int zipcode = Integer.parseInt(str_arr[0]);
            int temperature = Integer.parseInt(str_arr[1]);
            int relhumidity = Integer.parseInt(str_arr[2]);

            total_temp += temperature;

            // Added from the original code
            System.out.println("Receive temperature for zipcode " + zip_filter + " was " + temperature + " F");
        }

        System.out.println('\n' + "Average temperature for zipcode " + zip_filter + " was " + (total_temp / (update_nbr + 1)) + " F");
    }
}
