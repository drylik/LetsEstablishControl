import java.io.*;
import java.net.InetAddress;
import java.nio.CharBuffer;
import java.nio.charset.Charset;
import java.util.HashSet;
import java.util.Scanner;
import java.util.Set;

/**
 * Created by Андрей on 05.05.2016.
 */
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
        Set<InetAddress> addressSet = getLanIPs(numbers);
        System.out.println("IPs list: " + addressSet);
        //Thread sender = new ThreadSender();
        //Thread receiver = new ThreadReceiver();
        //sender.start();
        //receiver.start();
        System.out.println("STARTED!");
        /*while(true) {

        }*/
    }

    //TODO: find a better way of getting ips only from lAN
    /**
     * reading results from arp -a
     * dividing it's lines by spaces
     * and checking if divided part is an IP which consist necessary first numbers
     * if it is then checking if it is reachable
     * if it is then pushing it into a set
     */
    private static Set<InetAddress> getLanIPs(int[] numbers) {
        Set<InetAddress> addressesSet = new HashSet<>();
        ProcessBuilder builder = new ProcessBuilder("cmd.exe", "/c", "arp -a");
        builder.redirectErrorStream(true);
        try {
            Process proc = builder.start();
            BufferedReader br = new BufferedReader(new InputStreamReader(proc.getInputStream()));
            InetAddress address;
            String line = br.readLine();
            while (line != null) {
                String[] parts = line.split(" ");
                for (String part : parts) {
                    if (validIP(part, numbers)) {
                        address = InetAddress.getByName(part);
                        if (address.isReachable(5000)) {
                            addressesSet.add(address);
                            break;
                        }
                    }
                }
                line = br.readLine();
            }
        } catch (IOException e) {
            System.err.println(e.getMessage());
        }
        return addressesSet;
    }

    private static boolean validIP(String ip, int[] numbers) {
        try {
            if ( ip == null || ip.isEmpty() ) {
                return false;
            }

            String[] parts = ip.split( "\\." );
            if ( parts.length != 4 ) {
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
