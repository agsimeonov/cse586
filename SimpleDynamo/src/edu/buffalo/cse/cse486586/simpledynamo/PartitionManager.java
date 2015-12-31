package edu.buffalo.cse.cse486586.simpledynamo;

import java.util.Vector;

/** Manages partitions for simple dynamo */
public class PartitionManager {
    private Pair mHead = null;
    private int mSize = 0;
    
    /**
     * Adds a node to be managed (circular linked list).
     * 
     * @param nodeId - the node id
     * @param port - the node port
     */
    public void add(String nodeId, int port) {
        Pair pair = new Pair(nodeId, port, null, null);
        
        if (mSize == 0) {
            pair.mPrev = pair;
            pair.mNext = pair;
            mHead = pair;
        } else if (mSize == 1) {
            pair.mPrev = mHead;
            pair.mNext = mHead;
            mHead.mPrev = pair;
            mHead.mNext = pair;
            if (pair.compareTo(mHead) <= 0) mHead = pair; 
        } else {
            Pair curr = mHead;
            for (int i = 0; i < mSize; i = i + 1) {
                if (pair.compareTo(curr) <= 0) {
                    pair.mPrev = curr.mPrev;
                    pair.mNext = curr;
                    curr.mPrev.mNext = pair;
                    curr.mPrev = pair;
                    if (curr == mHead) mHead = pair;
                    break;
                } else if (curr.mNext == mHead) {
                    pair.mPrev = curr;
                    pair.mNext = mHead;
                    curr.mNext = pair;
                    mHead.mPrev = pair;
                    break;
                } else {
                    curr = curr.mNext;
                }
            }
        }
        mSize = mSize + 1;
    }
    
    /**
     * Getter for number of nodes in the manager.
     * 
     * @return the number of nodes in the manager.
     */
    public int getSize() {
        return mSize;
    }
    
    /**
     * Get port based on hash key.
     * 
     * @param hashKey - the hash key.
     * @return the port
     */
    public int getPort(String hashKey) {
        return getPair(hashKey).mPort;
    }
    
    /**
     * Gets a query port based on hash key.
     * 
     * @param hashKey = the hash key.
     * @return the port
     */
    public int getQueryPort(String hashKey) {
        Pair pair = getPair(hashKey);
        for (int i = 0; i < SimpleDynamoProvider.N-1; i = i + 1) {
            pair = pair.mNext;
        }
        return pair.mPort;
    }
    
    /**
     * Gets all the ports in the manager.
     * 
     * @return a vector containing all the ports
     */
    public Vector<Integer> getAllPorts() {
        Vector<Integer> v = new Vector<Integer>();
        Pair curr = mHead;
        for (int i = 0; i < mSize; i = i + 1) {
            v.add(curr.mPort);
            curr = curr.mNext;
        }
        return v;
    }
    
    /**
     * Gets the next port.
     * 
     * @param port - current port
     * @return next port
     */
    public int nextPort(int port) {
        Pair pair = getPair(port);
        if (pair != null) return pair.mNext.mPort;
        return 0;
    }
    
    /**
     * Gets the previous port.
     * 
     * @param port - current port
     * @return previous port
     */
    public int prevPort(int port) {
        Pair pair = getPair(port);
        if (pair != null) return pair.mPrev.mPort;
        return 0;
    }
    
    /**
     * Gets a Pair managing a hash key.
     * 
     * @param hashKey a hash key
     * @return the Pair.
     */
    private Pair getPair(String hashKey) {
        Pair curr = mHead;
        for (int i = 0; i < mSize; i = i + 1) {
            if (hashKey.compareTo(curr.mId) <= 0) return curr;
            curr = curr.mNext;
        }
        return curr;
    }
    
    /**
     * Gets a Pair based on port.
     * 
     * @param port - Pari's port
     * @return the Pair
     */
    private Pair getPair(int port) {
        Pair curr = mHead;
        for (int i = 0; i < mSize; i = i + 1) {
            if (curr.mPort == port) return curr;
            curr = curr.mNext;
        }
        return null;
    }
    
    /** Node Id, Port pair. */
    class Pair implements Comparable<Pair> {
        private String mId;
        private int mPort;
        private Pair mPrev;
        private Pair mNext;
        
        /**
         * Node Id, Port pair.
         * 
         * @param id - the node id
         * @param port - port
         * @param prev - previous node
         * @param next - next node
         */
        Pair(String id, int port, Pair prev, Pair next) {
            mId = id;
            mPort = port;
            mNext = next;
        }

        @Override
        public int compareTo(Pair another) {
            return this.mId.compareTo(another.mId);
        }
    }
}
