package com.example.mainuser.car_finder;
import java.nio.BufferOverflowException;
import java.nio.BufferUnderflowException;

/**
 * Created by mainuser on 4/15/17.
 */

public class RingBuffer<T>{
    private T[] buffer;

    private int tail;

    private int head;


    public RingBuffer(int n) {
        buffer = (T[]) new Object[n];
        tail = 0;
        head = 0;
    }

    public void add(T toAdd) {
        buffer[head++] = toAdd;
        head = head % buffer.length;
    }

    public T[] get(int n) {
        T t = null;
        T[] tList = null;
        for(int i = n-1; i > 0; i--) {
            t = (T) buffer[tail];
            tail = tail % buffer.length;
        }
        return t;
    }

    public String toString() {
        return "RingBuffer(size=" + buffer.length + ", head=" + head + ", tail=" + tail + ")";
    }
}
