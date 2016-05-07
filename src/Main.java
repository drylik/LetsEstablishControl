import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.InetAddress;
import java.util.Scanner;

/**
 * Created by Андрей on 05.05.2016.
 */
public class Main {

    public static void main(String[] args) {
        //Thread sender = new ThreadSender();
        //Thread receiver = new ThreadReceiver();
        //sender.start();
        //receiver.start();
        //TODO: find way of getting ips only from lan
        ProcessBuilder builder = new ProcessBuilder("cmd.exe", "/c", "arp -a");
        builder.redirectErrorStream(true);
        try {
            Process proc = builder.start();
            BufferedReader br = new BufferedReader(new InputStreamReader(proc.getInputStream()));
            String line = br.readLine();
            InetAddress address;
            for ( ; line != null; line = br.readLine()) {
                String[] parts = line.split(" ");
                for (String part: parts) {
                    if (validIP(part)) {
                        address = InetAddress.getByName(part);
                        if (address.isReachable(5000)) {
                            System.out.println(line);
                            break;
                        }
                    }
                }
            }
        } catch (IOException e) {
            System.err.println(e.getMessage());
        }

        System.out.println("DONE!");
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
