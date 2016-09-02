package ru.control;

import java.io.*;
import java.net.*;
import java.nio.charset.StandardCharsets;

/**
 * class Receiver runs receiver's part
 * receives commands, performs them
 * and then sends the result back
 */
public class Receiver extends User {
    private InetAddress myAddress;
    public Receiver(InetAddress myAddress) {
        this.myAddress = myAddress;
    }
    //TODO: divide run method
    //TODO: reduce cbuf size
    public void run() {
        byte[] msg = new byte[2500];
        DatagramSocket ds = null;
        DatagramPacket dp;
        InetAddress otherAddress;
        try {
            ds = new DatagramSocket(UDP_COMMANDS_PORT, myAddress);
            ds.setBroadcast(true);
            ds.setReuseAddress(true);
            Process proc = Runtime.getRuntime().exec("cmd /k");
            BufferedWriter bw = new BufferedWriter(new OutputStreamWriter(proc.getOutputStream(), "cp866"));
            BufferedReader br = new BufferedReader(new InputStreamReader(proc.getInputStream(), "cp866"));
            while(true) {
                dp = new DatagramPacket(msg, msg.length);
                ds.receive(dp);
                otherAddress = dp.getAddress();
                String text = new String(msg, 0, dp.getLength());
                bw.write(text);
                bw.newLine();
                bw.flush();

                char[] cbuf = new char[65235];
                for (int n = br.read(cbuf); ; n = br.read(cbuf)) {
                    System.out.println(cbuf);
                    byte[] byteMsg = new String(cbuf).getBytes(StandardCharsets.UTF_8);
                    dp = new DatagramPacket(byteMsg, n*2, otherAddress, UDP_ANSWERS_PORT);
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

    @Override
    public void setTextToSend(String textToSend) {

    }
}
