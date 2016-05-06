import java.io.IOException;
import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.InetAddress;

/**
 * Created by Андрей on 06.05.2016.
 */
public class ThreadSender extends Thread {
    public void run() {
        DatagramSocket ds = null;
        DatagramPacket dp;
        try {
            //to send msgs, no need to arguments
            ds = new DatagramSocket();
            ds.setBroadcast(true);
            InetAddress address = InetAddress.getByName("192.168.1.127");
            String message = "Hello, kek!";
            dp = new DatagramPacket(message.getBytes(), message.length(), address, 55555);
            ds.send(dp);
            ds.setReuseAddress(true);
            System.out.println("sent!");
        } catch (IOException e) {
            System.err.println(e.getMessage());
        } finally {
            if (ds != null) {
                ds.close();
            }
        }
    }
}
