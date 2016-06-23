package ru.control;

import java.io.*;
import java.net.*;
import java.util.Enumeration;
import java.util.HashSet;
import java.util.Scanner;
import java.util.Set;

public class Main {

    public static void main(String[] args) throws UnknownHostException {
        Scanner sc = new Scanner(System.in);
        Set<InetAddress> addressSet = getLanIPs();
        System.out.println("IPs list: " + addressSet);
        System.out.println(Inet4Address.getLocalHost());
        System.out.println("\nAre you Sender or Receiver? (s for sender / r for receiver)");
        String answer;
        do {
            answer = sc.nextLine();
            if (answer.equals("s")) {
                ThreadSender sender = new ThreadSender(addressSet.iterator().next());
                sender.start();
            } else if (answer.equals("r")) {
                ThreadReceiver receiver = new ThreadReceiver(addressSet.iterator().next());
                receiver.start();
            } else {
                System.out.println("Wrong answer, try again");
            }
        } while (!(answer.equals("s") || answer.equals("r")));
        System.out.println("STARTED!");
        while(true) {

        }
    }

    //TODO: getting user's LAN ip doesn't work
    //TODO: replace arp, because it doesn't show ips connected after me
    /**
     * detecting computers LAN IP and pushes it to set
     * reading results from arp -a
     * dividing it's lines by spaces
     * and checking if divided part is an IP which consist necessary first numbers
     * if it is then pinging it
     * if it pings then pushing it into a set
     * @return Set of IPs. The first one is user's
     */
    private static Set<InetAddress> getLanIPs() {
        int[] numbers = null;
        Set<InetAddress> addressesSet = new HashSet<>();
        ProcessBuilder arpBuilder = new ProcessBuilder("arp", "-a");
        ProcessBuilder pingBuilder;
        Process proc;
        arpBuilder.redirectErrorStream(true);
        try {
            addressesSet.add(getMyLanIP());
            numbers = getIPNumbers(addressesSet.iterator().next().getHostAddress());
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


    private static InetAddress getMyLanIP() {
        Enumeration<NetworkInterface> nics;
        NetworkInterface nic;
        Enumeration<InetAddress> addrs;
        InetAddress addr;
        try {
            nics = NetworkInterface.getNetworkInterfaces();
            while (nics.hasMoreElements()) {
                nic = nics.nextElement();
                addrs = nic.getInetAddresses();
                while (addrs.hasMoreElements()) {
                    addr = addrs.nextElement();
                    //TODO: there has to be appropriate condition to detect the necessary IP address
                    if (addr instanceof Inet4Address && addr.isSiteLocalAddress()) {
                        System.out.println(nic.getName() + " " + addr.getHostName() + " " + addr.getHostAddress());
                        //return addr;
                    }
                }
            }
        } catch (SocketException e) {
            System.err.println(e.getMessage());
        }
        return null;
    }

    /**
     * getting first 3 numbers of IP
     * @param ip IP address
     * @return int array consisting of 3 first numbers of IP
     */
    private static int[] getIPNumbers(String ip) {
        String[] split = ip.split("\\.");
        int[] numbers = new int[3];
        for (int i = 0; i < 3; i++) {
            numbers[i] = Integer.parseInt(split[i]);
        }
        return numbers;
    }

    /**
     * method checks if IP is appropriate
     * @param ip checking IP
     * @param numbers numbers to be checked
     * @return
     */
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
                if (Integer.parseInt(parts[i]) != numbers[i]) {
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
