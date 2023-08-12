package cn.ussshenzhou.t88.analyzer.back;

import java.util.HashMap;
import java.util.HashSet;
import java.util.LinkedList;

/**
 * @author USS_Shenzhou
 */
public class T88AnalyzerClient {
    @SuppressWarnings("rawtypes")
    public static final HashMap<String, Recorder> RECORDERS = new HashMap<>();

    @SuppressWarnings("unchecked")
    public static <T extends Number & Comparable<T>> void record(String mark, T value) {
        RECORDERS.compute(mark, (key, recorder) -> {
            if (recorder == null) {
                var r = new Recorder<T>();
                return r.record(value);
            } else {
                return recorder.record(value);
            }
        });
    }
}
