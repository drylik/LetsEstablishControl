package ru.control;

import java.net.InetAddress;

/**
 * Interface for users
 * no matter whether sender or receiver
 */
public abstract class User extends Thread {
    protected InetAddress myAddress;
    //ports
    protected static final int UDP_COMMANDS_PORT = 55555;
    protected static final int UDP_ANSWERS_PORT = 6666;

    protected boolean more = true;

    //sync
    protected final Object gotTextMonitor = new Object();
    //
    public abstract void setTextToSend(String textToSend);
    public abstract StringBuilder getAnswerText();
}