package xyz.qreaj.virtualizer.engine;


import java.util.Deque;
import java.util.HashMap;
import java.util.LinkedList;

public class StackEngine {
    private final HashMap<Short, Object> stackmap = new HashMap<>();
    private final Deque<Object> stack = new LinkedList<>();

    public void store(final Object obj) {
        stack.addLast(obj);
    }

    public Object top() {
        if (stack.isEmpty()) throw new IllegalArgumentException("File is damaged invalid [s]!");
        return stack.removeLast();
    }

    public boolean empty() {
        return stack.isEmpty();
    }

    public void store(short index,byte var) {
        stackmap.put(index,var);
    }
    public void store(short index,char var) {
        stackmap.put(index,var);
    }
    public void store(short index,double var) {
        stackmap.put(index,var);
    }
    public void store(short index,float var) {
        stackmap.put(index,var);
    }
    public void store(short index,short  var) {
        stackmap.put(index,var);
    }
    public void store(short index,int var) {
        stackmap.put(index,var);
    }
    public void store(short index,long var) {
        stackmap.put(index,var);
    }
    public void store(short index,Object var) {
        stackmap.put(index,var);
    }
    public void store(short index,String var) {
        stackmap.put(index,var);
    }

    public byte loadByte(short index) {
        return (byte) stackmap.get(index);
    }
    public char loadChar(short index) {
        return (char) stackmap.get(index);
    }
    public double loadDouble(short index) {
        return (double) stackmap.get(index);
    }
    public float loadFloat(short index) {
        return (float) stackmap.get(index);
    }
    public int loadInt(short index) {
        return (int) stackmap.get(index);
    }
    public long loadLong(short index) {
        return (long) stackmap.get(index);
    }
    public Object loadObject(short index) {
        return stackmap.get(index);
    }
    public String loadString(short index) {
        return (String) stackmap.get(index);
    }
}