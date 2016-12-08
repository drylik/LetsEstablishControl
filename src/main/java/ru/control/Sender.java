package ru.control;

import org.apache.logging.log4j.Level;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.io.IOException;
import java.io.OutputStream;
import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.InetAddress;
import java.net.SocketTimeoutException;

/**
 * class Sender runs sender's part
 * sends commands which have to be performed
 * then receives the result
 */
//TODO: make choice of IPs
public class Sender extends User {

    private static final Logger log = LogManager.getLogger(Sender.class.getName());

    private String textToSend;

    public Sender(InetAddress myAddress, InetAddress otherAddress, OutputStream out) {
        this.myAddress = myAddress;
        this.otherAddress = otherAddress;
        this.textToSend = "";
        this.out = out;
    }
    public void run() {
        //TODO: reduce msg's buffer size
        byte[] msg = new byte[65235];
        DatagramSocket ds = null;
        DatagramPacket dp;
        try {
            ds = new DatagramSocket(UDP_ANSWERS_PORT, myAddress);
            ds.setBroadcast(true);
            ds.setReuseAddress(true);
            while (more) {
                synchronized (gotTextMonitor) {
                    try {
                        //waiting for text to send
                        gotTextMonitor.wait();
                        dp = new DatagramPacket(textToSend.getBytes(), textToSend.length(), otherAddress, UDP_COMMANDS_PORT);
                        ds.send(dp);
                        String text;
                        char a = ' ';
                        //because of uncut cbuf in receiver's class I can't detect, when to stop receiving answers 45
                        while (a != '>') {
                            dp = new DatagramPacket(msg, msg.length);
                            ds.setSoTimeout(2000);
                            ds.receive(dp);
                            text = new String(msg, 0, dp.getLength());
                            out.write(text.getBytes());
                            int n = text.length() - 1;
                            //finding last non-empty character
                            while ((a = text.charAt(n)) == '\u0000') {
                                n--;
                            }
                        }
                    } catch (SocketTimeoutException e) {
                        out.write("Didn't receive any answer".getBytes());
                        log.log(Level.INFO, "Didn't receive any answer", e);
                    } catch (InterruptedException e) {
                        log.log(Level.ERROR, "Error during waiting for text", e);
                    }
                }
            }
        } catch (IOException e) {
            log.log(Level.ERROR, "Error during sending text", e);
        } finally {
            if (ds != null) {
                ds.close();
            }
            System.exit(-1);
        }
    }

    @Override
    public void setTextToSend(String textToSend) {
        synchronized (gotTextMonitor) {
            this.textToSend = textToSend;
            gotTextMonitor.notify();
        }
    }
}
