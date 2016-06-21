package ru.control;

import java.io.*;
import java.net.InetAddress;
import java.net.NetworkInterface;
import java.nio.CharBuffer;
import java.nio.charset.Charset;
import java.util.Enumeration;
import java.util.HashSet;
import java.util.Scanner;
import java.util.Set;

public class Main {

    public static void main(String[] args) {
        //creating a kind of mask consisting of first 3 numbers
        /*Scanner sc = new Scanner(System.in);
        System.out.println("Input first 3 numbers of IP: ");
        int[] numbers = new int[3];
        for (int i = 0; i < 3; i++) {
            numbers[i] = sc.nextInt();
        }*/
        int[] numbers = {192, 168, 1};  //default for my LAN
        Set<InetAddress> addressSet = getLanIPs();
        System.out.println("IPs list: " + addressSet);
        //Thread sender = new ru.control.ThreadSender();
        //Thread receiver = new ru.control.ThreadReceiver();
        //sender.start();
        //receiver.start();
        System.out.println("STARTED!");
        /*while(true) {

        }*/
    }

    //TODO: fix arp, because it doesn't show ips connected after me
    //TODO: with network interface detect LAN ips
    //TODO: make method parsing string ip to int array
    /**
     * detecting computers LAN IP and pushes it to set
     * reading results from arp -a
     * dividing it's lines by spaces
     * and checking if divided part is an IP which consist necessary first numbers
     * if it is then pinging it
     * if it pings then pushing it into a set
     */
    private static Set<InetAddress> getLanIPs() {
        int[] numbers = null;
        Set<InetAddress> addressesSet = new HashSet<>();
        ProcessBuilder arpBuilder = new ProcessBuilder("arp", "-a");
        ProcessBuilder pingBuilder;
        Process proc;
        arpBuilder.redirectErrorStream(true);
        try {
            Enumeration<NetworkInterface> nics = NetworkInterface.getNetworkInterfaces();
            while (nics.hasMoreElements()) {
                NetworkInterface nic = nics.nextElement();
                Enumeration<InetAddress> addrs = nic.getInetAddresses();
                while (addrs.hasMoreElements()) {
                    InetAddress addr = addrs.nextElement();
                    if (nic.getName().contains("wlan") && addr.getHostAddress().matches("[0-9]*\\.[0-9]*\\.[0-9]*\\.[0-9]*")) {
                        System.out.println("my LAN IP is " + addr.getHostAddress());
                        addressesSet.add(addr);
                        //TODO: convert byte[] to int[]
                        //numbers = addr.getAddress();
                    }
                }
            }
            proc = arpBuilder.start();
            BufferedReader br = new BufferedReader(new InputStreamReader(proc.getInputStream(), "cp866"));
            String line = br.readLine();
            while (line != null) {
                String[] parts = line.split(" ");
                for (String part : parts) {
                    if (validIP(part, numbers)) {
                        pingBuilder = new ProcessBuilder("ping", "-n", "1", part);
                        proc = pingBuilder.start();
                        if (proc.waitFor() == 0) {
                            addressesSet.add(InetAddress.getByName(part));
                            break;
                        }
                    }
                }
                line = br.readLine();
            }
        } catch (Exception e) {
            System.err.println(e.getMessage());
        }
        return addressesSet;
    }

    private static boolean validIP(String ip, int[] numbers) {
        try {
            if (ip == null || ip.isEmpty()) {
                return false;
            }

            String[] parts = ip.split( "\\." );
            if (parts.length != 4) {
                return false;
            }

            for (int i = 0; i < 3; i++) {
                if (Byte.parseByte(parts[i]) != numbers[i]) {
                    return false;
                }
            }
            int lastNum = Integer.parseInt(parts[3]);
            return !ip.endsWith(".") && (lastNum > 0 && lastNum < 255);

        } catch (NumberFormatException nfe) {
            return false;
        }
    }
}
