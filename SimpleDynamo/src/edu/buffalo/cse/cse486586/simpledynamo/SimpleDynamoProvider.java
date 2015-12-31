package edu.buffalo.cse.cse486586.simpledynamo;

import android.content.ContentProvider;
import android.content.ContentValues;
import android.content.Context;
import android.content.SharedPreferences;
import android.database.Cursor;
import android.database.MatrixCursor;
import android.net.Uri;
import android.telephony.TelephonyManager;
import android.util.Log;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.net.InetAddress;
import java.net.ServerSocket;
import java.net.Socket;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.Formatter;
import java.util.Map.Entry;
import java.util.Vector;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.atomic.AtomicInteger;

/** A simple Dynamo provider. */
public class SimpleDynamoProvider extends ContentProvider {
    private static final String TAG = SimpleDynamoProvider.class.getSimpleName();
    // The replication degree
    public static final int N = 3;
    // Ports
    private static final int SERVER_PORT = 10000;
    // Column names
    private static final String FIRST_COL_NAME = "key";
    private static final String SECOND_COL_NAME = "value";
    // Message types
    private static final byte INSERT = 0;
    private static final byte QUERY = 1;
    private static final byte QUERY_ALL = 2;
    private static final byte DELETE = 3;
    private static final byte SUCCESS = 4;
    private static final byte RECOVER = 5;
    private static final byte SENDBACK = 6;
    
    // Manages IDs and ports
    private static PartitionManager sPartitionManager = new PartitionManager();
    // Holds key value pairs 0 - this node, 1 last node, 2 - second to last node
    private static Vector<ConcurrentHashMap<String,String>> sStore = 
            new Vector<ConcurrentHashMap<String,String>>(N);
    // Contains query data - used for wait notify
    private static ConcurrentHashMap<String,String> sMap = new ConcurrentHashMap<String, String>();
    // Used for query all wait notify
    private static AtomicInteger sReceived = new AtomicInteger(0);
    // Used for server side insert query wait notify
    private static ConcurrentHashMap<String, Object> sWaitSet = 
            new ConcurrentHashMap<String, Object>();
    // Used for recovery
    private static boolean sRecover = false;
    // Redirection port for this process
    private int mPort;

    @Override
    public int delete(Uri uri, String selection, String[] selectionArgs) {
        // Wait to recover
        if (sRecover) {
            synchronized (sReceived) {
                try {
                    Log.i(TAG, "WAIT TO RECOVER");
                    sReceived.wait();
                    Log.i(TAG, "RECOVERED");
                } catch (InterruptedException e) {
                    Log.e(TAG, "Recover interrupted");
                }
            }
        }
        
        ConcurrentHashMap<String, String> map = new ConcurrentHashMap<String, String>();
        map.put(selection, "");
        
        try {
            int port = sPartitionManager.getPort(genHash(selection));
            
            if (port == mPort) {
                sStore.get(0).remove(selection);
                client(sPartitionManager.nextPort(port), DELETE, (byte)1, map);
            } else {
                client(port, DELETE, (byte)0, map);
            }
        } catch (NoSuchAlgorithmException e) {
            Log.e(TAG, "Can't generate the hash key:\n" + e.getMessage());
        }
        return 0;
    }

    @Override
    public String getType(Uri uri) {
        return null;
    }

    @Override
    public Uri insert(Uri uri, ContentValues values) {
        // Wait to recover
        if (sRecover) {
            synchronized (sReceived) {
                try {
                    Log.i(TAG, "WAIT TO RECOVER");
                    sReceived.wait();
                    Log.i(TAG, "RECOVERED");
                } catch (InterruptedException e) {
                    Log.e(TAG, "Recover interrupted");
                }
            }
        }
        
        ConcurrentHashMap<String, String> map = new ConcurrentHashMap<String, String>();
        map.put(values.getAsString(FIRST_COL_NAME), values.getAsString(SECOND_COL_NAME));
        
        try {
            int port = sPartitionManager.getPort(genHash(values.getAsString(FIRST_COL_NAME)));
            
            if (port == mPort) {
                sStore.get(0).putAll(map);
                client(sPartitionManager.nextPort(port), INSERT, (byte)1, map);
            } else {
                client(port, INSERT, (byte)0, map);
            }
        } catch (NoSuchAlgorithmException e) {
            Log.e(TAG, "Can't generate the hash key:\n" + e.getMessage());
        }
        return uri;
    }

    @Override
    public boolean onCreate() {
        TelephonyManager tel = (TelephonyManager)this.getContext().getSystemService(
                Context.TELEPHONY_SERVICE);
        String portStr = tel.getLine1Number().substring(tel.getLine1Number().length() - 4);
        mPort = Integer.parseInt(portStr) * 2;
        
        // Set up the node port structure used to find ports for partitions
        try {
            sPartitionManager.add(genHash("5554"), 11108);
            sPartitionManager.add(genHash("5556"), 11112);
            sPartitionManager.add(genHash("5558"), 11116);
            sPartitionManager.add(genHash("5560"), 11120);
            sPartitionManager.add(genHash("5562"), 11124);
        } catch (NoSuchAlgorithmException e) {
            Log.e(TAG, "Can't set up the partition manager:\n" + e.getMessage());
            return false;
        }
        
        // Set up the data store
        for (int i = 0; i < sStore.capacity(); i = i + 1) {
            sStore.add(i, new ConcurrentHashMap<String, String>());
        }
        
        // Check if this is an initial start up or failure recovery
        SharedPreferences failCheck = this.getContext().getSharedPreferences("failCheck", 0);
        if (failCheck.getBoolean("initialStartUp", true)) {
            failCheck.edit().putBoolean("initialStartUp", false).commit(); 
        } else {
            sRecover = true;
        }
        
        // Set up the server socket
        try {
            ServerSocket serverSocket = new ServerSocket(SERVER_PORT);
            server(serverSocket);
        } catch (IOException e) {
            Log.e(TAG, "Can't create a ServerSocket:\n" + e.getMessage());
            return false;
        }
        
        // Recover from failure
        if (sRecover) {
            // Get 1
            int port = sPartitionManager.nextPort(mPort);
            client(port, RECOVER, 2, null);
            // Get 0
            port = sPartitionManager.nextPort(port);
            client(port, RECOVER, 2, null);
            // Get 2
            port = sPartitionManager.prevPort(mPort);
            client(port, RECOVER, 1, null);
        }
        
        return true;
    }

    @Override
    public Cursor query(Uri uri, String[] projection, String selection,
            String[] selectionArgs, String sortOrder) {
        // Wait to recover
        if (sRecover) {
            synchronized (sReceived) {
                try {
                    Log.i(TAG, "WAIT TO RECOVER");
                    sReceived.wait();
                    Log.i(TAG, "RECOVERED");
                } catch (InterruptedException e) {
                    Log.e(TAG, "Recover interrupted");
                }
            }
        }
        
        Log.i(TAG, "INIT QUERY: " + selection);
        synchronized (this) {
            Log.i(TAG, "COMMENSE QUERY: " + selection);
            MatrixCursor cursor = new MatrixCursor(new String[] {FIRST_COL_NAME, SECOND_COL_NAME});
            ConcurrentHashMap<String, String> map = null;
            int port = 0;

            if (selection.equals("@")) {
                // Query @
                for (int i = 0; i < N; i = i + 1) {
                    for (Entry<String, String> entry : sStore.get(i).entrySet()) {
                        cursor.addRow(new String[] {entry.getKey(), entry.getValue()});
                    }
                }
            } else if (selection.equalsIgnoreCase("*")) {
                // Query *
                sMap.clear();
                Vector<Integer> ports = sPartitionManager.getAllPorts();

                for (Integer p : ports) {
                    if (p == mPort) {
                        sMap.putAll(sStore.get(N - 1));
                        sReceived.incrementAndGet();
                    } else {
                        client(p, QUERY_ALL, N - 1, null);
                    }
                }

                synchronized (sReceived) {
                    try {
                        sReceived.wait();
                    } catch (InterruptedException e) {
                        Log.e(TAG, "Query all interrupted");
                    }
                }

                for (Entry<String, String> entry : sMap.entrySet()) {
                    cursor.addRow(new String[] {entry.getKey(), entry.getValue()});
                }
            } else {
                // Query
                sMap.clear();

                try {
                    port = sPartitionManager.getQueryPort(genHash(selection));
                } catch (NoSuchAlgorithmException e) {
                    Log.e(TAG, "Can't generate the hash key:\n" + e.getMessage());
                }

                if (port == mPort) {
                    String value = sStore.get(N - 1).get(selection);
                    if (value == null) {
                        Object object = sWaitSet.get(selection);
                        if (object == null) {
                            object = new Object();
                            sWaitSet.put(selection, object);
                        }
                        synchronized (object) {
                            try {
                                Log.i(TAG, "WAITING FOR " + selection);
                                object.wait();
                                Log.i(TAG, "READY");
                            } catch (InterruptedException e) {
                                Log.e(TAG, "Key wait interrupted");
                            }
                        }
                        value = sStore.get(N - 1).get(selection);
                    }
                    Log.i(TAG, "KEY: " + selection + "\nVAL: " + value);
                    cursor.addRow(new String[] {selection, value});
                } else {
                    map = new ConcurrentHashMap<String, String>();
                    map.put(selection, "");
                    client(port, QUERY, N - 1, map);

                    synchronized (sMap) {
                        try {
                            sMap.wait();
                        } catch (InterruptedException e) {
                            Log.e(TAG, "Query interrupted");
                        }
                    }
                    String[] row = null;
                    for (Entry<String, String> entry : sMap.entrySet()) {
                        row = new String[] {entry.getKey(), entry.getValue()};
                        cursor.addRow(row);
                    }
                    if (row == null) {
                        Log.i(TAG, "NULL Value detected");
                    } else {
                        Log.i(TAG, "KEY: " + row[0] + "\nVAL: " + row[1]);
                    }
                }
            }

            return cursor;
        }
    }

    @Override
    public int update(Uri uri, ContentValues values, String selection, String[] selectionArgs) {
        return 0;
    }

    @SuppressWarnings("resource")
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
     * Server Socket which spawns servling threads.
     * 
     * @param ss - the server socket to listen on.
     */
    private void server(final ServerSocket ss) {
        new Thread(new Runnable() {
            public void run() {
                ServerSocket serverSocket = ss;
                
                while(true) {
                    try {
                        Socket s = serverSocket.accept();
                        servling(s);
                    } catch (IOException e) {
                        Log.e(TAG, "server socket IOException");
                        Log.e(TAG, Log.getStackTraceString(e));
                    }
                }
            }
        }).start();
    }
    
    /**
     * Servlings like zerglings are spawned when needed.
     * 
     * @param s - a socket to serve a request on.
     */
    private void servling(final Socket s) {
        new Thread(new Runnable() {
            public void run() {
                try {
                    // Create output stream and flush before opening input stream on other end
                    ObjectOutputStream o = new ObjectOutputStream(s.getOutputStream());
                    o.flush();
                    
                    // Handle request
                    ObjectInputStream ois = new ObjectInputStream(s.getInputStream());
                    Message m = (Message) ois.readObject();
                    
                    // Wait to recover
                    if (sRecover) {
                        synchronized (sReceived) {
                            try {
                                Log.i(TAG, "WAIT TO RECOVER");
                                sReceived.wait();
                                Log.i(TAG, "RECOVERED");
                            } catch (InterruptedException e) {
                                Log.e(TAG, "Recover interrupted");
                            }
                        }
                    }
                    
                    if (m.type == INSERT) {
                        sStore.get(m.index).putAll(m.map);
                        Log.i(TAG, "INDEX: " + m.index + " INSERTED: "
                                + m.map.keySet().iterator().next());
                        if (sWaitSet.size() > 0) {
                            String key = m.map.keySet().iterator().next();
                            Object object = sWaitSet.get(key);
                            if (object != null) {
                                sWaitSet.remove(key);
                                synchronized (object) {
                                    Log.i(TAG, "NOTIFY");
                                    object.notifyAll();
                                }
                            }
                        }
                        if (m.propagate) {
                            int port = sPartitionManager.nextPort(mPort);
                            int index = m.index + 1;
                            client(port, m.type, index, m.map);
                        }
                        if (m.skipped) {
                            int port = sPartitionManager.prevPort(mPort);
                            int index = m.index - 1;
                            Log.i(TAG, "SENDBACK SKIPPED: " + m.map.keySet().iterator().next());
                            client(port, SENDBACK, index, m.map);
                        }
                        // Send response
                        o.writeObject(new Message(SUCCESS, 0, false, null));
                    } else if (m.type == QUERY) {
                        String key = m.map.keySet().iterator().next();
                        String value = sStore.get(m.index).get(key);
                        if (value == null) {
                            Object object = sWaitSet.get(key);
                            if (object == null) {
                                object = new Object();
                                sWaitSet.put(key, object);
                            }
                            synchronized (object) {
                                try {
                                    Log.i(TAG, "WAITING FOR " + key);
                                    object.wait();
                                    Log.i(TAG, "READY");
                                } catch (InterruptedException e) {
                                    Log.e(TAG, "Key wait interrupted");
                                }
                            }
                            value = sStore.get(m.index).get(key);
                        }
                        m.map.put(key, value);
                        // Send response
                        o.writeObject(m);
                    } else if (m.type == QUERY_ALL) {
                        m.map = sStore.get(m.index);
                        // Send response
                        o.writeObject(m);
                    } else if (m.type == DELETE) {
                        String key = m.map.keySet().iterator().next();
                        sStore.get(m.index).remove(key);
                        if (m.propagate) {
                            int port = sPartitionManager.nextPort(mPort);
                            int index = m.index + 1;
                            client(port, m.type, index, m.map);
                        }
                        // Send response
                        o.writeObject(new Message(SUCCESS, 0, false, null));
                    } else {
                        // RECOVER
                        m.map = sStore.get(m.index);
                        Log.i(TAG, "RECOVERING NODE REQUESTED INDEX: " + m.index);
                        // Send response
                        o.writeObject(m);
                    }
                    
                    o.close();
                } catch (ClassNotFoundException e) {
                    Log.e(TAG, "servling ObjectInputStream ClassNotFoundException");
                } catch (IOException e) {
                    Log.e(TAG, "servling socket IOException");
                    Log.e(TAG, Log.getStackTraceString(e));
                }
            }
        }).start();
    }
    
    /**
     * A thread which routes and processes requests.
     * 
     * @param port - destination port.
     * @param type - request type.
     * @param index - intended replica.
     * @param map - a map which holds key value pairs that may need to be transmitted.
     */
    private void client(final int port, final byte type, final int index, 
            final ConcurrentHashMap<String, String> map) {
        Thread t = new Thread(new Runnable() {
            public void run() {
                if(type == QUERY)
                    Log.i(TAG, "QUERY: " + map.keySet().iterator().next());
                else if(type == INSERT)
                    Log.i(TAG, "INSERT: " + map.keySet().iterator().next());
                
                if (type == SENDBACK) {
                    if (tryClient(port, INSERT, index, map, false)) {
                        Log.i(TAG, "SENDBACK SUCCESS");
                    } else {
                        Log.i(TAG, "SENDBACK FAILED");
                    }
                } else if (!tryClient(port, type, index, map, false)) {
                    Log.e(TAG, "Node at " + port + " has failed!");
                    
                    if (type == QUERY || type == QUERY_ALL) {
                        int newPort = sPartitionManager.prevPort(port);
                        int newIndex = index - 1;
                        
                        if(!tryClient(newPort, type, newIndex, map, false)) {
                            Log.e(TAG, "Second node at" + port + " has failed (query)!");
                        }
                    } else if (type == INSERT || type == DELETE) {
                       if (index != N-1) {
                            int newPort = sPartitionManager.nextPort(port);
                            int newIndex = index + 1;
                            
                            if(!tryClient(newPort, type, newIndex, map, true)) {
                                Log.e(TAG, "Second node at " + port + " has failed (insert)!");
                            }
                        }
                    } else {
                        // RECOVER
                        Log.e(TAG, "Recover from" + port + " has failed!");
                    }
                }
            }
        });
        
        t.start();
        
        if (type == INSERT || type == DELETE) {
            try {
                t.join();
            } catch (InterruptedException e) {
                Log.e(TAG, "Failed to join on chain client");
            }
        }
    }
    
    /**
     * The client for requests.
     * 
     * @param port - destination port.
     * @param type - request type.
     * @param index - intended replica.
     * @param map - a map which holds key value pairs that may need to be transmitted.
     * @param skipped - set if this is a skip over request.
     * @return
     */
    private boolean tryClient(final int port, final byte type, final int index, 
            final ConcurrentHashMap<String, String> map, boolean skipped) {
        try {
            Socket s = new Socket(InetAddress.getByAddress(new byte[] {10, 0, 2, 2}), port);
            s.setSoTimeout(10000);
            ObjectOutputStream o = null;
            ObjectInputStream ois = null;
            boolean propagate = false;
            
            // Set up chain rules and message
            if ((type == INSERT || type == DELETE) && index < N-1) propagate = true;
            
            // Send request
            Message m = new Message(type, index, propagate, map);
            m.skipped = skipped;
            
            try {
                o = new ObjectOutputStream(s.getOutputStream()); 
                o.writeObject(m);
            } catch (IOException e) {
                Log.e(TAG, "client write " + mPort + " to " + port + " failed!");
                s.close();
                return false;
            }
            
            // Handle response
            try {
                ois = new ObjectInputStream(s.getInputStream());;
                m = (Message) ois.readObject();
            } catch (IOException e) {
                Log.e(TAG, "client " + mPort + " read from " + port + " failed!");
                s.close();
                return false;
            }
            
            o.close();                    
            s.close();
            
            if (type == QUERY) {
                sMap.putAll(m.map);
                synchronized (sMap) {
                    sMap.notify();
                }
            } else if (type == QUERY_ALL) {
                sMap.putAll(m.map);
                if (sReceived.incrementAndGet() == sPartitionManager.getSize()) {
                    sReceived.set(0);
                    synchronized (sReceived) {
                        sReceived.notify();
                    }
                }
            } else if (type == RECOVER) {
                if (port == sPartitionManager.nextPort(mPort)) {
                    sStore.get(1).putAll(m.map);
                } else if (port == sPartitionManager.prevPort(mPort)) {
                    sStore.get(2).putAll(m.map);
                } else {
                    sStore.get(0).putAll(m.map);
                }
                
                if (sReceived.incrementAndGet() == N) {
                    sRecover = false;
                    sReceived.set(0);
                    synchronized (sReceived) {
                        Log.i(TAG, "NOTIFY ALL RECOVERED");
                        sReceived.notifyAll();
                    }
                }
            }
        } catch (ClassNotFoundException e) {
            Log.e(TAG, "client ObjectInputStream ClassNotFoundException");
            return false;
        } catch (IOException e) {
            Log.e(TAG, "client socket IOException");
            Log.e(TAG, Log.getStackTraceString(e));
            return false;
        }
        
        return true;
    }
}
