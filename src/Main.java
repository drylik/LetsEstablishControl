import java.io.IOException;
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

        //TODO: terrible way to get IPs list, need new one
        Scanner sc = new Scanner(System.in);
        System.out.print("Input start IP: ");
        Integer startIP = Integer.parseInt(sc.nextLine());
        try {
            InetAddress address;
            for (int i = startIP; i <= 200; i++) {
                address = InetAddress.getByName("192.168.1." + i);
                System.out.print(address.isReachable(5000) ? address.getHostName() + "\n" : "");
            }
        } catch (IOException e) {
            System.out.println(e.getMessage());
        }

        System.out.println("DONE!");
    }
}
