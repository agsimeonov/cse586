package edu.buffalo.cse.cse486586.simpledht;

import java.io.Serializable;
import java.util.concurrent.ConcurrentHashMap;

/** A simple chord message. */
public class Message implements Serializable {
    private static final long serialVersionUID = 6423880349476098101L;
    public String key;
    public String value;
    public int pred;
    public int succ;
    public byte type;
    public ConcurrentHashMap<String, String> map = null;
    
    /** 
     * Initializes the message.
     * 
     * @param k - key
     * @param v - value
     * @param p - predecessor
     * @param s - successor
     * @param t - type
     * @param m - map
     */
    public Message(String k, String v, int p, int s, byte t, ConcurrentHashMap<String, String> m) {
        key = k;
        value = v;
        pred = p;
        succ = s;
        type = t;
        map = m;
    }
}
