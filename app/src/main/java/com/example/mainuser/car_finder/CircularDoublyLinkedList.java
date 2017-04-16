package com.example.mainuser.car_finder;

/**
 * Created by mainuser on 4/15/17.
 */

/**
 *  Java Program to Implement Circular Doubly Linked List
 */

/*  Class Node  */

class Node<T> {
    protected T data;
    protected Node<T> next, prev;

    /* Constructor */

    public Node() {
        next = null;
        prev = null;
        data = (T) new Object();
    }

    /* Constructor */

    public Node(T d, Node<T> n, Node<T> p) {
        data = d;
        next = n;
        prev = p;
    }

    /* Function to set link to next node */

    public void setLinkNext(Node<T> n)
    {
        this.next = n;
    }

    /* Function to set link to previous node */

    public void setLinkPrev(Node<T> p)
    {
        this.prev = p;
    }

    /* Funtion to get link to next node */

    public Node<T> getLinkNext() {
        return next;
    }

    /* Function to get link to previous node */

    public Node<T> getLinkPrev()
    {
        return prev;
    }

    /* Function to set data to node */

    public void setData(T d)
    {
        this.data = d;
    }

    /* Function to get data from node */

    public T getData()
    {
        return data;
    }
}

/* Class linkedList */

class CircularDoublyLinkedList<T> {
    protected Node<T> start, end;
    public int size;

    /* Constructor */

    public CircularDoublyLinkedList() {
        start = null;
        end = null;
        size = 0;
    }

    /* Function to check if list is empty */

    public boolean isEmpty() {
        return start == null;
    }

    /* Function to get size of list */

    public int getSize() {
        return size;
    }

    /* Function to insert element at beginning */

    public void insertAtStart(T val) {
        Node<T> nptr = new Node(val, null, null);

        if (start == null) {
            nptr.setLinkNext(nptr);
            nptr.setLinkPrev(nptr);
            start = nptr;
            end = start;
        } else {
            nptr.setLinkPrev(end);
            end.setLinkNext(nptr);
            start.setLinkPrev(nptr);
            nptr.setLinkNext(start);
            start = nptr;
        }

        size++;
    }

    /*Function to insert element at end */

    public void insertAtEnd(T val) {
        Node<T> nptr = new Node<>(val, null, null);

        if (start == null) {
            nptr.setLinkNext(nptr);
            nptr.setLinkPrev(nptr);
            start = nptr;
            end = start;
        } else {
            nptr.setLinkPrev(end);
            end.setLinkNext(nptr);
            start.setLinkPrev(nptr);
            nptr.setLinkNext(start);
            end = nptr;
        }

        size++;
    }

    /* Function to insert element at position */

    public void insertAtPos(T val, int pos) {
        Node<T> nptr = new Node(val, null, null);
        if (pos == 1) {
            insertAtStart(val);
            return;
        }

        Node ptr = start;

        for (int i = 2; i <= size; i++) {
            if (i == pos) {
                Node<T> tmp = ptr.getLinkNext();
                ptr.setLinkNext(nptr);
                nptr.setLinkPrev(ptr);
                nptr.setLinkNext(tmp);
                tmp.setLinkPrev(nptr);
            }

            ptr = ptr.getLinkNext();
        }

        size++;
    }

    public Node<T> getStartNode() {
        return start;
    }

    public Node<T> getEndNode() {
        return end;
    }

    public void overwriteNode(T val) {
        end = end.getLinkNext();
        start = start.getLinkNext();
        end.setData(val);
    }

    public Node<T> getNodeAtPos(int pos) {
        if(size == 0) {
            return null;
        }

        if(size == 1) {
            return start;
        }

        Node<T> ptr = start;

        for(int i = 1; i <= size; i++) {
            if(i == pos) {
                return ptr;
            }

            ptr = ptr.getLinkNext();
        }

        return null;
    }

    /* Function to delete node at position  */

    public void deleteAtPos(int pos) {
        if (pos == 1) {
            if (size == 1) {
                start = null;
                end = null;
                size = 0;

                return;
            }

            start = start.getLinkNext();
            start.setLinkPrev(end);
            end.setLinkNext(start);
            size--;

            return;
        }

        if (pos == size) {
            end = end.getLinkPrev();
            end.setLinkNext(start);
            start.setLinkPrev(end);
            size--;
        }

        Node<T> ptr = start.getLinkNext();

        for (int i = 2; i <= size; i++) {
            if (i == pos) {
                Node<T> p = ptr.getLinkPrev();
                Node<T> n = ptr.getLinkNext();

                p.setLinkNext(n);
                n.setLinkPrev(p);

                size--;

                return;
            }

            ptr = ptr.getLinkNext();
        }
    }
}