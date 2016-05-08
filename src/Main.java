import org.omg.CORBA.portable.*;

import java.io.*;

import java.net.InetAddress;
import java.util.HashSet;
import java.util.Scanner;
import java.util.Set;
import java.util.stream.Stream;

/**
 * Created by Андрей on 05.05.2016.
 */
public class Main {

    public static void main(String[] args) {
        //Thread sender = new ThreadSender();
        Thread receiver = new ThreadReceiver();
        //sender.start();
        //receiver.start();
        //Set<InetAddress> addressSet = getLanIPs();
        //System.out.println(addressSet);
        try {
            Scanner sc = new Scanner(System.in);
            String text = sc.nextLine();
            Process proc = Runtime.getRuntime().exec("cmd /k " + text);
            BufferedWriter bw = new BufferedWriter(new OutputStreamWriter(proc.getOutputStream()));
            BufferedReader br = new BufferedReader(new InputStreamReader(proc.getInputStream()));
            while (true) {
                for (String line = br.readLine(); line != null; ) {
                    System.out.println(line);
                    if (br.ready()) {
                        line = br.readLine();
                    }
                }

                text = sc.nextLine();
                bw.write(text);
                bw.newLine();
                bw.flush();
            }
        } catch (IOException e) {
            System.err.println(e.getMessage());
        }

        System.out.println("DONE!");
        /*while(true) {

        }*/
    }

    //TODO: find way of getting ips only from lan
    public static Set<InetAddress> getLanIPs() {
        Set<InetAddress> addressesSet = new HashSet<>();
        ProcessBuilder builder = new ProcessBuilder("cmd.exe", "/c", "arp -a");
        builder.redirectErrorStream(true);
        try {
            Process proc = builder.start();
            BufferedReader br = new BufferedReader(new InputStreamReader(proc.getInputStream()));
            String line = br.readLine();
            InetAddress address;
            for ( ; line != null; line = br.readLine()) {
                String[] parts = line.split(" ");
                for (String part : parts) {
                    if (validIP(part)) {
                        address = InetAddress.getByName(part);
                        if (address.isReachable(5000)) {
                            addressesSet.add(address);
                            break;
                        }
                    }
                }
            }
        } catch (IOException e) {
            System.err.println(e.getMessage());
        }
        return addressesSet;
    }

    //found on stackoverflow *shy*
    public static boolean validIP (String ip) {
        try {
            if ( ip == null || ip.isEmpty() ) {
                return false;
            }

            String[] parts = ip.split( "\\." );
            if ( parts.length != 4 ) {
                return false;
            }

            for ( String s : parts ) {
                int i = Integer.parseInt( s );
                if ( (i < 0) || (i > 255) ) {
                    return false;
                }
            }
            return !ip.endsWith(".");

        } catch (NumberFormatException nfe) {
            return false;
        }
    }
}
