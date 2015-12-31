package edu.buffalo.cse.cse486586.groupmessenger;

import java.io.Serializable;

/** The message to be sent. */
public class Message implements Serializable {
    private static final long serialVersionUID = -805186578442161410L;
    public static final int NO_ORDER = Integer.MIN_VALUE;
    public String message;
    public String portOrigin;
    public int order = NO_ORDER;
    
    public Message(String msg, String port) {
        message = msg;
        portOrigin = port;
    }
}
