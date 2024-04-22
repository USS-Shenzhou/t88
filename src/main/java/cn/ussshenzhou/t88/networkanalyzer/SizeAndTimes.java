package cn.ussshenzhou.t88.networkanalyzer;

/**
 * @author USS_Shenzhou
 */
public class SizeAndTimes {
    private int size, times;

    public SizeAndTimes(int size) {
        increaseSize(size);
    }

    public int getSize() {
        return size;
    }

    public int getTimes() {
        return times;
    }

    public SizeAndTimes increaseSize(int size) {
        this.size += size;
        times++;
        return this;
    }
}
