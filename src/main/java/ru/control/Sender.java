package ru.control;

import java.io.IOException;
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

    private String textToSend;
    private StringBuilder answerText;

    public Sender(InetAddress myAddress) {
        this.myAddress = myAddress;
        this.textToSend = "";
        this.answerText = new StringBuilder();
    }
    public void run() {
        //TODO: reduce msg's buffer size
        byte[] msg = new byte[65235];
        DatagramSocket ds = null;
        DatagramPacket dp;
        try {
            //temporary have to type them manually until detecting LAN ips doesn't work
            InetAddress otherAddress = InetAddress.getByName("192.168.1.120");
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
                            this.answerText.append(text);
                            int n = text.length() - 1;
                            //finding last non-empty character
                            while ((a = text.charAt(n)) == '\u0000') {
                                n--;
                            }
                        }
                    } catch (SocketTimeoutException r) {
                        this.answerText.append("Didn't receive answer.\n");
                    } catch (InterruptedException e) {
                        System.err.println(e.getMessage());
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
        synchronized (gotTextMonitor) {
            this.textToSend = textToSend;
            gotTextMonitor.notify();
        }
    }

    @Override
    public StringBuilder getAnswerText() {
        return answerText;
    }
}
