package cn.ussshenzhou.t88.networkanalyzer;

import io.netty.buffer.ByteBuf;
import net.minecraft.network.codec.ByteBufCodecs;
import net.minecraft.network.codec.StreamCodec;

import java.util.concurrent.atomic.AtomicInteger;

/**
 * @author USS_Shenzhou
 */
public class SizeAndTimes {
    private final AtomicInteger size = new AtomicInteger(0);
    private final AtomicInteger times = new AtomicInteger(0);

    public static final StreamCodec<ByteBuf, SizeAndTimes> STREAM_CODEC = StreamCodec.composite(
            ByteBufCodecs.VAR_INT,
            s -> s.size.get(),
            ByteBufCodecs.VAR_INT,
            s -> s.times.get(),
            SizeAndTimes::new
    );


    private SizeAndTimes(int size, int times) {
        this.size.set(size);
        this.times.set(times);
    }

    public SizeAndTimes(int size) {
        super();
        increaseSize(size);
    }

    public int getSize() {
        return size.get();
    }

    public int getTimes() {
        return times.get();
    }

    public SizeAndTimes increaseSize(int size) {
        this.size.addAndGet(size);
        times.incrementAndGet();
        return this;
    }
}
