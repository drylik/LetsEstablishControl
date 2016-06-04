package ru.control;

import java.io.IOException;
import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.InetAddress;
import java.net.SocketTimeoutException;
import java.util.Scanner;

/**
 * class ThreadSender runs sender's part
 * sends commands which have to be performed
 * then receives the result
 */
//TODO: make constructor, receiving IP
public class ThreadSender extends Thread {
    public void run() {
        //TODO: reduce msg's buffer size
        byte[] msg = new byte[65235];
        DatagramSocket ds = null;
        DatagramPacket dp;
        try {
            //temporary have to type them manually until detecting LAN ips doesn't work
            InetAddress otherAddress = InetAddress.getByName("192.168.1.120");
            InetAddress myAddress = InetAddress.getByName("192.168.1.127");
            ds = new DatagramSocket(6666, myAddress);
            ds.setBroadcast(true);
            ds.setReuseAddress(true);
            Scanner sc = new Scanner(System.in);
            while (true) {
                try {
                    System.out.println("Input command to send");
                    String message = sc.nextLine();
                    dp = new DatagramPacket(message.getBytes(), message.length(), otherAddress, 55555);
                    ds.send(dp);
                    System.out.println("sent!");
                    String text;
                    char a = ' ';
                    //because of uncut cbuf in receiver's class I can't detect, when to stop receiving answers 45
                    while (a != '>') {
                        dp = new DatagramPacket(msg, msg.length);
                        ds.setSoTimeout(2000);
                        ds.receive(dp);
                        text = new String(msg, 0, dp.getLength());
                        System.out.println(text);
                        int n = text.length() - 1;
                        //finding first non-empty character
                        while((a = text.charAt(n)) == '\u0000') {
                            n--;
                        }
                    }
                } catch (SocketTimeoutException r) {
                    System.out.println("Didn't receive answer");
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
