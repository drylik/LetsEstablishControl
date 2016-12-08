package ru.control;

import org.apache.logging.log4j.Level;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.net.InetAddress;
import java.net.UnknownHostException;
import java.util.Scanner;
import java.util.Set;

class Main {

    private static final Logger log = LogManager.getLogger(Main.class.getName());

    public static void main(String[] args) {
        Scanner sc = new Scanner(System.in);
        Set<InetAddress> addressSet;
        try {
            addressSet = getAddress();
            String answer;
            User user;
            System.out.println("\nAre you Sender or Receiver? (s for sender / r for receiver)");
            do {
                answer = sc.nextLine();
                if (!(answer.equals("s") || answer.equals("r"))) {
                    System.out.println("Wrong answer, try again");
                }
            } while (!(answer.equals("s") || answer.equals("r")));
            switch (answer) {
                case "s":
                    String otherAddress;
                    do {
                        System.out.println("Enter other address: ");
                        otherAddress = sc.nextLine();
                    } while (!IPUtils.validIP(otherAddress, null));
                    user = new Sender(addressSet.iterator().next(), InetAddress.getByName(otherAddress), System.out);
                    System.out.println("STARTED!");
                    user.start();
                    while (true) {
                        System.out.println("Input command to send");
                        String message = sc.nextLine();
                        user.setTextToSend(message);
                    }
                case "r":
                    user = new Receiver(addressSet.iterator().next(), System.out);
                    System.out.println("STARTED!");
                    user.start();
                    while (true) {

                    }
                default:
                    break;
            }
        } catch (UnknownHostException e) {
            log.log(Level.ERROR, "Error, during InetAddress initialization", e);
        }
    }

    private static Set<InetAddress> getAddress() throws UnknownHostException {
        InetAddress ia = InetAddress.getByName("");
        assert ia != null;
        System.out.println(ia.getHostAddress());

        Set<InetAddress> addressSet = IPUtils.getLanIPs();
        System.out.println("IPs list: " + addressSet);
        return addressSet;
    }

}
