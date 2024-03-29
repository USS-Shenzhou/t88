package cn.ussshenzhou.t88.analyzer.back;

import java.util.LinkedList;

/**
 * @author USS_Shenzhou
 */
public class Recorder<T extends Number & Comparable<T>> {
    private final LinkedList<T> recorded = new LinkedList<>();

    public Recorder<T> record(T value) {
        recorded.add(value);
        return this;
    }

    @SuppressWarnings("unchecked")
    public T[] getRecords() {
        return recorded.toArray(size -> (T[]) new Number[size]);
    }

    @Override
    public int hashCode() {
        return super.hashCode();
    }

    @Override
    public boolean equals(Object obj) {
        return super.equals(obj);
    }

}
