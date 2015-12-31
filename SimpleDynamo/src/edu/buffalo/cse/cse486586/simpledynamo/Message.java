package edu.buffalo.cse.cse486586.simpledynamo;

import java.io.Serializable;
import java.util.concurrent.ConcurrentHashMap;

/** The message to be transmitted */
public class Message implements Serializable {
    private static final long serialVersionUID = 5146901630442283632L;
    public byte type;
    public int index;
    public boolean propagate;
    public boolean skipped = false;
    public ConcurrentHashMap<String, String> map;
    
    /**
     * The message to be transmitted.
     * 
     * @param t - type
     * @param i - index
     * @param p - port
     * @param m - map
     */
    public Message(byte t, int i, boolean p, ConcurrentHashMap<String, String> m) {
        type = t;
        index = i;
        propagate = p;
        map = m;
    }
}
