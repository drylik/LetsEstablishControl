import java.io.IOException;
import java.net.*;

/**
 * Created by Андрей on 06.05.2016.
 */
public class ThreadReceiver extends Thread {

    public void run() {
        byte[] msg = new byte[2500];
        DatagramSocket ds = null;
        DatagramPacket dp;
        try {
            while(true) {
                InetAddress address = InetAddress.getByName("192.168.1.120");
                ds = new DatagramSocket(55555, address);
                ds.setBroadcast(true);
                ds.setReuseAddress(true);
                dp = new DatagramPacket(msg, msg.length);
                ds.receive(dp);
                ds.setSoTimeout(10000);
                String text = new String(msg, 0, dp.getLength());
                System.out.println(text);
            }
        } catch (SocketTimeoutException e) {
            System.err.println("didn't receive!");
        } catch (IOException e) {
            System.err.println(e.getMessage());
        } finally {
            if (ds != null) {
                ds.close();
            }
        }
    }
}
