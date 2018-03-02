package com.androidplot.util;

import java.util.HashMap;
import java.util.List;

public class ZHash<KeyType, ValueType> implements ZIndexable<KeyType> {
    private HashMap<KeyType, ValueType> hash = new HashMap();
    private ZLinkedList<KeyType> zlist = new ZLinkedList();

    public int size() {
        return this.zlist.size();
    }

    public ValueType get(KeyType key) {
        return this.hash.get(key);
    }

    public List<KeyType> getKeysAsList() {
        return this.zlist;
    }

    public synchronized void addToTop(KeyType key, ValueType value) {
        if (this.hash.containsKey(key)) {
            this.hash.put(key, value);
        } else {
            this.hash.put(key, value);
            this.zlist.addToTop(key);
        }
    }

    public synchronized void addToBottom(KeyType key, ValueType value) {
        if (this.hash.containsKey(key)) {
            this.hash.put(key, value);
        } else {
            this.hash.put(key, value);
            this.zlist.addToBottom(key);
        }
    }

    public synchronized boolean moveToTop(KeyType element) {
        boolean moveToTop;
        if (this.hash.containsKey(element)) {
            moveToTop = this.zlist.moveToTop(element);
        } else {
            moveToTop = false;
        }
        return moveToTop;
    }

    public synchronized boolean moveAbove(KeyType objectToMove, KeyType reference) {
        boolean moveAbove;
        if (objectToMove == reference) {
            throw new IllegalArgumentException("Illegal argument to moveAbove(A, B); A cannot be equal to B.");
        } else if (this.hash.containsKey(reference) && this.hash.containsKey(objectToMove)) {
            moveAbove = this.zlist.moveAbove(objectToMove, reference);
        } else {
            moveAbove = false;
        }
        return moveAbove;
    }

    public synchronized boolean moveBeneath(KeyType objectToMove, KeyType reference) {
        boolean moveBeneath;
        if (objectToMove == reference) {
            throw new IllegalArgumentException("Illegal argument to moveBeaneath(A, B); A cannot be equal to B.");
        } else if (this.hash.containsKey(reference) && this.hash.containsKey(objectToMove)) {
            moveBeneath = this.zlist.moveBeneath(objectToMove, reference);
        } else {
            moveBeneath = false;
        }
        return moveBeneath;
    }

    public synchronized boolean moveToBottom(KeyType key) {
        boolean moveToBottom;
        if (this.hash.containsKey(key)) {
            moveToBottom = this.zlist.moveToBottom(key);
        } else {
            moveToBottom = false;
        }
        return moveToBottom;
    }

    public synchronized boolean moveUp(KeyType key) {
        boolean moveUp;
        if (this.hash.containsKey(key)) {
            moveUp = this.zlist.moveUp(key);
        } else {
            moveUp = false;
        }
        return moveUp;
    }

    public synchronized boolean moveDown(KeyType key) {
        boolean moveDown;
        if (this.hash.containsKey(key)) {
            moveDown = this.zlist.moveDown(key);
        } else {
            moveDown = false;
        }
        return moveDown;
    }

    public List<KeyType> elements() {
        return this.zlist;
    }

    public List<KeyType> keys() {
        return elements();
    }

    public synchronized boolean remove(KeyType key) {
        boolean z;
        if (this.hash.containsKey(key)) {
            this.hash.remove(key);
            this.zlist.remove(key);
            z = true;
        } else {
            z = false;
        }
        return z;
    }
}
