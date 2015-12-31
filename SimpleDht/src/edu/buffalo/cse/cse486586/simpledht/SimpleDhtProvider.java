package edu.buffalo.cse.cse486586.simpledht;

import android.content.ContentProvider;
import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.MatrixCursor;
import android.net.Uri;
import android.telephony.TelephonyManager;
import android.util.Log;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.math.BigInteger;
import java.net.InetAddress;
import java.net.ServerSocket;
import java.net.Socket;
import java.net.UnknownHostException;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.Formatter;
import java.util.Map.Entry;
import java.util.concurrent.ConcurrentHashMap;

/** A simple chord content provider */
public class SimpleDhtProvider extends ContentProvider {
    private static final String TAG = SimpleDhtProvider.class.getSimpleName();
    // Ports
    private static final int SERVER_PORT = 10000;
    private static final int JOIN_PORT = 11108;
    // Column names
    private static final String FIRST_COL_NAME = "key";
    private static final String SECOND_COL_NAME = "value";
    // Message constants
    private static final String NONE = null;
    private static final int NO_PORT = 0;
    private static final ConcurrentHashMap<String, String> NO_MAP = null;
    private static final byte NO_TYPE = -1;
    private static final byte JOIN = 0;
    private static final byte ACCEPT = 1;
    private static final byte SET_SUCCESSOR = 2;
    private static final byte INSERT = 3;
    private static final byte QUERY = 4;
    private static final byte GET_ALL = 5;
    private static final byte DELETE = 6;
    
    // Holds key value pairs
    private static ConcurrentHashMap<String, String> sMap = new ConcurrentHashMap<String, String>();
    // The node id derived from the emulator port
    private String mNodeId;
    // Predecessor id
    private String mPredId;
    // Successor id
    private String mSuccId;
    // Redirection port for this process
    private int mPort;
    // Redirection port for the predecessor
    private int mPred;
    // Redirection port for the successor
    private int mSucc;
    // Contains needed data after we finish blocking
    private Message mMessage = new Message(NONE, NONE, NO_PORT, NO_PORT, NO_TYPE, NO_MAP);

    @Override
    public int delete(Uri uri, String selection, String[] selectionArgs) {
        delete(selection, mPort);
        return 0;
    }

    @Override
    public String getType(Uri uri) {
        return null;
    }

    @Override
    public Uri insert(Uri uri, ContentValues values) {
        insert(values.getAsString(FIRST_COL_NAME), values.getAsString(SECOND_COL_NAME));
        return uri;
    }

    @Override
    public boolean onCreate() {
        TelephonyManager tel = (TelephonyManager)this.getContext().getSystemService(
                Context.TELEPHONY_SERVICE);
        String portStr = tel.getLine1Number().substring(tel.getLine1Number().length() - 4);
        mPort = Integer.parseInt(portStr) * 2;
        
        try {
            mNodeId = genHash(portStr);
        } catch (NoSuchAlgorithmException e) {
            Log.e(TAG, "Can't generate the node id:\n" + e.getMessage());
            return false;
        }
        
        try {
            ServerSocket serverSocket = new ServerSocket(SERVER_PORT);
            server(serverSocket);
        } catch (IOException e) {
            Log.e(TAG, "Can't create a ServerSocket:\n" + e.getMessage());
            return false;
        }
        
        mPredId = mNodeId;
        mSuccId = mNodeId;
        mPred = mPort;
        mSucc = mPort;
        
        if (mPort != JOIN_PORT) client(JOIN_PORT, mNodeId, NONE, mPort, mPort, JOIN, NO_MAP);
        
        return true;
    }

    @Override
    public Cursor query(Uri uri, String[] projection, String selection, String[] selectionArgs,
            String sortOrder) {
        if (selection.equals("*") || selection.equals("@")) {
            return getAll(selection, new ConcurrentHashMap<String, String>(), mPort, true);
        } else {
            return query(selection, mPort, true);
        }
    }

    @Override
    public int update(Uri uri, ContentValues values, String selection, String[] selectionArgs) {
        return 0;
    }

    /**
     * Generates a SHA1 hash.
     * 
     * @param input - the string the encode
     * @return SHA1 hash
     * @throws NoSuchAlgorithmException
     */
    private String genHash(String input) throws NoSuchAlgorithmException {
        MessageDigest sha1 = MessageDigest.getInstance("SHA-1");
        byte[] sha1Hash = sha1.digest(input.getBytes());
        Formatter formatter = new Formatter();
        for (byte b : sha1Hash) {
            formatter.format("%02x", b);
        }
        return formatter.toString();
    }
    
    /**
     * The server thread - serves chord operations.
     * 
     * @param s - the socket to accept on
     */
    private void server(final ServerSocket s) {
        new Thread(new Runnable() {
            public void run() {
                ServerSocket serverSocket = s;
                
                try {
                    while(true) {
                        Socket socket = serverSocket.accept();
                        ObjectInputStream objectInputStream = 
                                new ObjectInputStream(socket.getInputStream());
                        Message message = (Message) objectInputStream.readObject();
                        
                        switch (message.type) {
                            case JOIN:
                                join(message);
                                break;
                            case ACCEPT:
                                mPredId = message.key;
                                mSuccId = message.value;
                                mPred = message.pred;
                                mSucc = message.succ;
                                break;
                            case SET_SUCCESSOR:
                                mSuccId = message.key;
                                mSucc = message.succ;
                                break;
                            case INSERT:
                                insert(message.key, message.value);
                                break;
                            case QUERY:
                                if (mPort == message.pred) {
                                    mMessage.value = message.value;
                                    synchronized(mMessage) {
                                        mMessage.notify();
                                    }
                                } else {
                                    query(message.key, message.pred, false);
                                }
                                break;
                            case GET_ALL:
                                if (mPort == message.pred) {
                                    mMessage.map = message.map;
                                    synchronized(mMessage) {
                                        mMessage.notify();
                                    }
                                } else {
                                    getAll(message.key, message.map, message.pred, false);
                                }
                                break;
                            case DELETE:
                                delete(message.key, message.pred);
                                break;
                            default:
                                Log.e(TAG, "Unknown message type: " + message.type);
                        }
                    }
                } catch (UnknownHostException e) {
                    Log.e(TAG, "ServerTask UnknownHostException");
                } catch (IOException e) {
                    Log.e(TAG, "ServerTask socket IOException:\n" + e.getMessage());
                } catch (ClassNotFoundException e) {
                    Log.e(TAG, "ServerTask ObjectInputStream ClassNotFoundException");
                }
            }
        }).start();
    }
    
    /**
     * Performs the operations necessary to finalize a join request.
     * 
     * @param message - a join message
     */
    private void join(Message message) {
        if (check(message.key)) {
            if (mPort == mPred) {
                client(message.pred, mPredId, mSuccId, mPred, mSucc, ACCEPT, NO_MAP);
                mPredId = message.key;
                mSuccId = message.key;
                mPred = message.pred;
                mSucc = message.succ;
            } else {
                client(mPred, message.key, NONE, NO_PORT, message.succ, SET_SUCCESSOR, NO_MAP);
                client(message.pred, mPredId, mNodeId, mPred, mPort, ACCEPT, NO_MAP);
                mPredId = message.key;
                mPred = message.pred;
            }
        } else {
            client(mSucc, message.key, message.value, message.pred, message.succ, message.type,
                    message.map);
        }
    }
    
    /**
     * Performs the operations necessary to finalize an insert.
     * 
     * @param key - SHA1 hash
     * @param value - the actual value to insert
     */
    private void insert(String key, String value) {
        try {
            String hash = genHash(key);
            if (check(hash)) {
                sMap.put(key, value);
            } else {
                client(mSucc, key, value, NO_PORT, NO_PORT, INSERT, NO_MAP);
            }
        } catch (NoSuchAlgorithmException e) {
            Log.e(TAG, "Can't generate insert hash:\n" + e.getMessage());
        }   
    }
    
    /**
     * Performs the operations necessary to finalize a delete.
     * 
     * @param key - SHA1 hash
     * @param origin - origin node
     */
    private void delete(String key, int origin) {
        if (key.equals("@") || key.equals("*")) {
            sMap.clear();
            if (key.equals("*") && mSucc != origin) {
                client(mSucc, key, NONE, origin, NO_PORT, DELETE, NO_MAP);
            }
        } else {     
            try {
                String hash = genHash(key);
                if (check(hash)) {
                    sMap.remove(key);
                } else {
                    client(mSucc, key, NONE, origin, NO_PORT, DELETE, NO_MAP);
                }
            } catch (NoSuchAlgorithmException e) {
                Log.e(TAG, "Can't generate delete hash:\n" + e.getMessage());
            }
        }
    }
    
    /**
     * Performs the operations necessary to get all entries.
     * 
     * @param key - "@" or "*"
     * @param map - a map to hold the entries
     * @param origin - origin node
     * @param wait - true if we need to wait for a return value, otherwise false
     * @return Cursor containing all the entries
     */
    private Cursor getAll(String key, ConcurrentHashMap<String, String> map, int origin,
            boolean wait) {
        MatrixCursor cursor = new MatrixCursor(new String[]{FIRST_COL_NAME, SECOND_COL_NAME});
        
        if (key.equals("@") || mPort == mSucc) {
            for(Entry<String, String> entry : sMap.entrySet()) {
                cursor.addRow(new String[]{entry.getKey(), entry.getValue()});
            }
            return cursor;
        } else {
            map.putAll(sMap);
            client(mSucc, key, NONE, origin, NO_PORT, GET_ALL, map);
            if (wait) {
                synchronized(mMessage) {
                    try {
                        mMessage.wait();
                    } catch (InterruptedException e) {
                        Log.e(TAG, "Get all interrupted");
                    }
                }
                for(Entry<String, String> entry : mMessage.map.entrySet()) {
                    cursor.addRow(new String[]{entry.getKey(), entry.getValue()});
                }
                mMessage.map = NO_MAP;
            }
        }
        
        return cursor;
    }
    
    /**
     * Performs the operations necessary to query.
     * 
     * @param key - a regular (non-hash) key value
     * @param origin - the origin node
     * @param wait - true if we need to wait for a return value, otherwise false
     * @return Cursor containing the key value pair we queried
     */
    private Cursor query(String key, int origin, boolean wait) {
        MatrixCursor cursor = new MatrixCursor(new String[]{FIRST_COL_NAME, SECOND_COL_NAME});
        
        try {
            String hash = genHash(key);
            if (check(hash)) {
                if (mPort == origin) {
                    cursor.addRow(new String[]{key, sMap.get(key)});
                } else {
                    client(origin, NONE, sMap.get(key), origin, NO_PORT, QUERY, NO_MAP);
                }
                return cursor;
            } else {
                client(mSucc, key, NONE, origin, NO_PORT, QUERY, NO_MAP);
                if (wait) {
                    synchronized(mMessage) {
                        try {
                            mMessage.wait();
                        } catch (InterruptedException e) {
                            Log.e(TAG, "Query interrupted");
                        }
                    }
                    cursor.addRow(new String[]{key, mMessage.value});
                    mMessage.value = NONE;
                }
            }
        } catch (NoSuchAlgorithmException e) {
            Log.e(TAG, "Can't generate query key:\n" + e.getMessage());
        }
        
        return cursor;
    }
    
    /**
     * Checks to see if the current node manages the given key.
     * 
     * @param key - SHA1 key
     * @return true if this node is the one managing the key, otherwise false
     */
    private boolean check(String key) {
        BigInteger pred = new BigInteger(mPredId, 16);
        BigInteger myid = new BigInteger(mNodeId, 16);
        if (myid.compareTo(pred) == 0) return true;
        BigInteger id = new BigInteger(key, 16);
        if (pred.compareTo(myid) == 1) {
            if (id.compareTo(pred) == 1 && id.compareTo(myid) == 1) return true;
            if (id.compareTo(pred) == -1 && id.compareTo(myid) <= 0) return true;
        }
        if (id.compareTo(pred) == 1 && id.compareTo(myid) < 1) return true;
        return false;
    }
    
    /**
     * The client thread - sends chord requests.
     * 
     * @param dest - destination node
     * @param key - a key (meaning may differ depending on type)
     * @param value - a value (meaning may differ depending on type)
     * @param pred - predecessor node (meaning may differ depending on type)
     * @param succ - successor node (meaning may differ depending on type)
     * @param type - message type
     * @param cursor - a map which will eventually become the returned cursor
     */
    private void client(final int dest, final String key, final String value, final int pred,
            final int succ, final byte type, final ConcurrentHashMap<String, String> cursor) {
        new Thread(new Runnable() {
            public void run() {
                try {
                    Socket socket = 
                            new Socket(InetAddress.getByAddress(new byte[] {10, 0, 2, 2}), dest);
                    ObjectOutputStream objectOutputStream = new ObjectOutputStream(
                            socket.getOutputStream());
                    Message message = new Message(key, value, pred, succ, type, cursor);
                    objectOutputStream.writeObject(message);
                    objectOutputStream.close();
                    socket.close();
                } catch (UnknownHostException e) {
                    Log.e(TAG, "Send UnknownHostException");
                } catch (IOException e) {
                    Log.e(TAG, "Send socket IOException:\n" + e.getMessage());
                }
            }
        }).start();
    }
}
