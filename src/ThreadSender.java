import java.io.IOException;
import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.InetAddress;
import java.util.Scanner;

/**
 * Created by Андрей on 06.05.2016.
 */
public class ThreadSender extends Thread {
    public void run() {
        byte[] msg = new byte[2500];
        DatagramSocket ds = null;
        DatagramPacket dp;
        try {
            InetAddress otherAddress = InetAddress.getByName("192.168.1.120");
            InetAddress myAddress = InetAddress.getByName("192.168.1.127");
            ds = new DatagramSocket(6666, myAddress);
            ds.setBroadcast(true);
            ds.setReuseAddress(true);
            Scanner sc = new Scanner(System.in);
            while (true) {
                System.out.println("Input comand to send");
                String message = sc.nextLine();
                dp = new DatagramPacket(message.getBytes(), message.length(), otherAddress, 55555);
                ds.send(dp);
                System.out.println("sent!");
                String text = " ";
                char a = ' ';
                //because of uncut cbuf in receiver's class I can't detect, when to stop receiving answers
                for ( ; a != '>'; ) {
                    dp = new DatagramPacket(msg, msg.length);
                    ds.receive(dp);
                    text = new String(msg, 0, dp.getLength());
                    System.out.println(text);
                    a = text.charAt(text.length() - 1);
                }
            }
        } catch (IOException e) {
            System.err.println(e.getMessage());
        } finally {
            if (ds != null) {
                ds.close();
            }
            //System.exit(-1);
        }
    }
}
