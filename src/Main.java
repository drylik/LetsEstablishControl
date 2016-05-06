import java.io.IOException;
import java.net.*;
import java.util.Scanner;

/**
 * Created by Андрей on 05.05.2016.
 */
public class Main {

    public static void main(String[] args) {
        Thread sender = new Thread(new Runnable() {
            @Override
            public void run() {
                DatagramSocket ds = null;
                DatagramPacket dp = null;
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
                    System.err.println(e);
                } finally {
                    if (ds != null) {
                        ds.close();
                    }
                }
            }
        });

        Thread receiver = new Thread(new Runnable() {
            @Override
            public void run() {
                byte[] msg = new byte[2500];
                DatagramSocket ds = null;
                DatagramPacket dp = null;
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
                } catch (SocketException e) {
                    System.err.println(e);
                } catch (UnknownHostException e) {
                    System.err.println(e);
                } catch (IOException e) {
                    System.err.println(e);
                } finally {
                    if (ds != null) {
                        ds.close();
                    }
                }

            }
        });

        //sender.start();
        //receiver.start();
        //TODO: terrible way to get IPs list, need new one
        Scanner sc = new Scanner(System.in);
        System.out.print("Input start IP: ");
        Integer startIP = Integer.parseInt(sc.nextLine());
        try {
            InetAddress address;
            for (int i = startIP; i <= 200; i++) {
                address = InetAddress.getByName("192.168.1." + i);
                System.out.print(address.isReachable(5000) ? address.getHostName() + "\n" : "");
            }
        } catch (IOException e) {
            System.out.println(e);
        }

        System.out.println("DONE!");
    }
}
