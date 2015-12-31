package edu.buffalo.cse.cse486586.groupmessenger;

import android.content.ContentResolver;

import android.app.Activity;
import android.content.ContentValues;
import android.content.Context;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.telephony.TelephonyManager;
import android.text.method.ScrollingMovementMethod;
import android.util.Log;
import android.view.Menu;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.EditText;
import android.widget.TextView;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.net.InetAddress;
import java.net.ServerSocket;
import java.net.Socket;
import java.net.UnknownHostException;
import java.util.concurrent.LinkedBlockingQueue;

/**
 * GroupMessengerActivity is the main Activity for the assignment.
 * 
 * @author stevko
 *
 */
public class GroupMessengerActivity extends Activity {
    private static final String TAG = GroupMessengerActivity.class.getSimpleName();
    private static final String REMOTE_PORT0 = "11108";
    private static final String REMOTE_PORT1 = "11112";
    private static final String REMOTE_PORT2 = "11116";
    private static final String REMOTE_PORT3 = "11120";
    private static final String REMOTE_PORT4 = "11124";
    private static final String SEQUENCER_PORT = REMOTE_PORT0;
    private static final int SERVER_PORT = 10000;
    
    // The port this process uses
    private String mPort;
    // The content resolver
    private ContentResolver mContentResolver;
    // Unique URI for our content provider
    private Uri mUri;
    // Will hold the key value pair
    private ContentValues mCv;
    // TextView
    private TextView mTv;
    // Holdback queue
    private LinkedBlockingQueue<String> mQueue = new LinkedBlockingQueue<String>();
    // Order number used by the sequencer
    private int mOrder = 0;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_group_messenger);
        
        mContentResolver = getContentResolver();
        Uri.Builder uriBuilder = new Uri.Builder();
        uriBuilder.authority("edu.buffalo.cse.cse486586.groupmessenger.provider");
        uriBuilder.scheme("content");
        mUri = uriBuilder.build();
        
        TelephonyManager tel = (TelephonyManager) this.getSystemService(Context.TELEPHONY_SERVICE);
        String portStr = tel.getLine1Number().substring(tel.getLine1Number().length() - 4);
        mPort = String.valueOf((Integer.parseInt(portStr) * 2));
        
        try {
            ServerSocket serverSocket = new ServerSocket(SERVER_PORT);
            new ServerTask().executeOnExecutor(AsyncTask.THREAD_POOL_EXECUTOR, serverSocket);
        } catch (IOException e) {
            Log.e(TAG, "Can't create a ServerSocket");
            return;
        }
        
        /*
         * Use the TextView to display your messages. Though there is no grading component
         * on how you display the messages, if you implement it, it'll make your debugging easier.
         */
        mTv = (TextView) findViewById(R.id.textView1);
        mTv.setMovementMethod(new ScrollingMovementMethod());
        
        /*
         * Registers OnPTestClickListener for "button1" in the layout, which is the "PTest" button.
         * OnPTestClickListener demonstrates how to access a ContentProvider.
         */
        findViewById(R.id.button1).setOnClickListener(
                new OnPTestClickListener(mTv, getContentResolver()));
        
        /*
         * TODO: You need to register and implement an OnClickListener for the "Send" button.
         * In your implementation you need to get the message from the input box (EditText)
         * and send it to other AVDs in a total-causal order.
         */
        final EditText editText = (EditText) findViewById(R.id.editText1);
        
        findViewById(R.id.button4).setOnClickListener(new OnClickListener() {

            @Override
            public void onClick(View view) {
                String msg = editText.getText().toString();
                if (msg.equals("")) return;
                editText.setText("");
                mQueue.add(msg);
                
                if (mQueue.size() == 1) {
                    new ClientTask().executeOnExecutor(AsyncTask.THREAD_POOL_EXECUTOR, msg, mPort,
                            REMOTE_PORT0);
                    new ClientTask().executeOnExecutor(AsyncTask.THREAD_POOL_EXECUTOR, msg, mPort,
                            REMOTE_PORT1);
                    new ClientTask().executeOnExecutor(AsyncTask.THREAD_POOL_EXECUTOR, msg, mPort,
                            REMOTE_PORT2);
                    new ClientTask().executeOnExecutor(AsyncTask.THREAD_POOL_EXECUTOR, msg, mPort,
                            REMOTE_PORT3);
                    new ClientTask().executeOnExecutor(AsyncTask.THREAD_POOL_EXECUTOR, msg, mPort,
                            REMOTE_PORT4);
                }
            }
        });
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.activity_group_messenger, menu);
        return true;
    }
    
    /** Performs server and sequencer operations */
    private class ServerTask extends AsyncTask<ServerSocket, Message, Void> {

        @Override
        protected Void doInBackground(ServerSocket... sockets) {
            ServerSocket serverSocket = sockets[0];
            
            try {
                while(true) {
                    Socket socket = serverSocket.accept();
                    ObjectInputStream objectInputStream = 
                            new ObjectInputStream(socket.getInputStream());
                    Message msg = (Message) objectInputStream.readObject();
                    if (!mPort.equals(SEQUENCER_PORT) && msg.order == Message.NO_ORDER) continue;
                    if (msg.order == Message.NO_ORDER) {
                        msg.order = mOrder;
                        send(msg, REMOTE_PORT0);
                        send(msg, REMOTE_PORT1);
                        send(msg, REMOTE_PORT2);
                        send(msg, REMOTE_PORT3);
                        send(msg, REMOTE_PORT4);
                        mOrder = mOrder + 1;
                    } else {
                        mCv = new ContentValues();
                        mCv.put(GroupMessengerProvider.FIRST_COL_NAME, Integer.toString(msg.order));
                        mCv.put(GroupMessengerProvider.SECOND_COL_NAME, msg.message);
                        mContentResolver.insert(mUri, mCv);
                        this.publishProgress(msg);
                        if (msg.portOrigin.equals(mPort)) {
                            mQueue.remove();
                            if (!mQueue.isEmpty()) {
                                Message message = new Message(mQueue.peek(), mPort);
                                send(message, REMOTE_PORT0);
                                send(message, REMOTE_PORT1);
                                send(message, REMOTE_PORT2);
                                send(message, REMOTE_PORT3);
                                send(message, REMOTE_PORT4);
                            }
                        }
                    }
                }
            } catch (UnknownHostException e) {
                Log.e(TAG, "ServerTask UnknownHostException");
            } catch (IOException e) {
                Log.e(TAG, "ServerTask socket IOException");
            } catch (ClassNotFoundException e) {
                Log.e(TAG, "ServerTask ObjectInputStream ClassNotFoundException");
            }
            
            return null;
        }
        
        protected void onProgressUpdate(Message... msgs) {
            mTv.append("[" + Integer.toString(msgs[0].order) + "][" + msgs[0].portOrigin + "] "
                    + msgs[0].message + "\n");
        }
        
        private void send(Message msg, String port) {
            for (int i = 0; i < 2; i++) {
                try {
                    Socket socket = new Socket(InetAddress.getByAddress(new byte[] {10, 0, 2, 2}), 
                            Integer.parseInt(port));
                    ObjectOutputStream objectOutputStream = new ObjectOutputStream(
                            socket.getOutputStream());
                    objectOutputStream.writeObject(msg);
                    objectOutputStream.flush();
                    objectOutputStream.close();
                    socket.close();
                    break;
                } catch (UnknownHostException e) {
                    Log.e(TAG, "ServerTask send UnknownHostException");
                } catch (IOException e) {
                    Log.e(TAG, "ServerTask send socket IOException");
                    Log.e(TAG, e.getMessage());
                }
            }
        }
    }
    
    /** Performs client operations */
    private class ClientTask extends AsyncTask<String, Void, Void> {

        @Override
        protected Void doInBackground(String... msgs) {
            try {
                Socket socket = new Socket(InetAddress.getByAddress(new byte[] {10, 0, 2, 2}), 
                        Integer.parseInt(msgs[2]));
                ObjectOutputStream objectOutputStream = new ObjectOutputStream(
                        socket.getOutputStream());
                Message msg = new Message(msgs[0], msgs[1]);
                objectOutputStream.writeObject(msg);
                objectOutputStream.flush();
                objectOutputStream.close();
                socket.close();
            } catch (UnknownHostException e) {
                Log.e(TAG, "ClientTask UnknownHostException");
            } catch (IOException e) {
                Log.e(TAG, "ClientTask socket IOException");
                Log.e(TAG, e.getMessage());
            }
            
            return null;
        }
    }
}
