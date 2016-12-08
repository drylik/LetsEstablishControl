package ru.control;

import java.io.OutputStream;
import java.net.InetAddress;

/**
 * Interface for users
 * no matter whether sender or receiver
 */
public abstract class User extends Thread {
    //ports
    protected static final int UDP_COMMANDS_PORT = 55555;
    protected static final int UDP_ANSWERS_PORT = 6666;

    protected boolean more = true;
    protected InetAddress myAddress;
    protected InetAddress otherAddress;
    protected OutputStream out;

    //sync
    protected final Object gotTextMonitor = new Object();
    //
    public abstract void setTextToSend(String textToSend);
}
