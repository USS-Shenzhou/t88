package cn.ussshenzhou.t88.networkanalyzer;

import io.netty.buffer.ByteBuf;
import net.minecraft.network.codec.ByteBufCodecs;
import net.minecraft.network.codec.StreamCodec;

/**
 * @author USS_Shenzhou
 */
public class SizeAndTimes {
    private int size, times;

    public static final StreamCodec<ByteBuf, SizeAndTimes> STREAM_CODEC = StreamCodec.composite(
            ByteBufCodecs.VAR_INT,
            s -> s.size,
            ByteBufCodecs.VAR_INT,
            s -> s.times,
            SizeAndTimes::new
    );

    private SizeAndTimes(int size, int times) {
        this.size = size;
        this.times = times;
    }

    public SizeAndTimes(int size) {
        super();
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
