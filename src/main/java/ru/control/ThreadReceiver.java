package ru.control;

import java.io.*;
import java.net.*;
import java.nio.charset.StandardCharsets;

/**
 * class ThreadReceiver runs receiver's part
 * receives commands, performs them
 * and then sends the result back
 */
public class ThreadReceiver extends Thread {
    //TODO: divide run method
    //TODO: establish sending answer
    //TODO: reduce cbuf size
    public void run() {
        byte[] msg = new byte[2500];
        DatagramSocket ds = null;
        DatagramPacket dp;
        try {
            InetAddress myAddress = InetAddress.getByName("192.168.1.120");
            InetAddress otherAddress = InetAddress.getByName("192.168.1.127");
            ds = new DatagramSocket(55555, myAddress);
            ds.setBroadcast(true);
            ds.setReuseAddress(true);
            Process proc = Runtime.getRuntime().exec("cmd /k");
            BufferedWriter bw = new BufferedWriter(new OutputStreamWriter(proc.getOutputStream(), "cp866"));
            BufferedReader br = new BufferedReader(new InputStreamReader(proc.getInputStream(), "cp866"));
            while(true) {
                dp = new DatagramPacket(msg, msg.length);
                ds.receive(dp);
                String text = new String(msg, 0, dp.getLength());
                bw.write(text);
                bw.newLine();
                bw.flush();

                char[] cbuf = new char[65235];
                for (int n = br.read(cbuf); ; n = br.read(cbuf)) {
                    System.out.println(cbuf);
                    byte[] byteMsg = new String(cbuf).getBytes(StandardCharsets.UTF_8);
                    dp = new DatagramPacket(byteMsg, n*2, otherAddress, 6666);
                    ds.send(dp);
                    //n != -1 doesn't work
                    if (cbuf[n - 1] == '>') {
                        break;
                    }
                }
            }
        } catch (IOException e) {
            System.err.println(e.getMessage());
        } finally {
            if (ds != null) {
                ds.close();
            }
            System.exit(-1);
        }
    }
}