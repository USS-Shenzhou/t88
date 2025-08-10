package cn.ussshenzhou.t88.util;

import java.util.List;
import java.util.function.Predicate;

/**
 * @author USS_Shenzhou
 */
public class Util {

    public static <T> T findBefore(List<T> list, int i, Predicate<T> predicate) {
        for (int j = i - 1; j >= 0; j--) {
            T element = list.get(j);
            if (predicate.test(element)) {
                return element;
            }
        }
        return null;
    }
}
