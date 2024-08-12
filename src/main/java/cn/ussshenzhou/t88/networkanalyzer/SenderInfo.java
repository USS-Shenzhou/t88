package cn.ussshenzhou.t88.networkanalyzer;

import io.netty.buffer.ByteBuf;
import net.minecraft.network.codec.ByteBufCodecs;
import net.minecraft.network.codec.StreamCodec;

/**
 * @author USS_Shenzhou
 */
public record SenderInfo(String modId, String clazz) {

    public static final StreamCodec<ByteBuf, SenderInfo> STREAM_CODEC = StreamCodec.composite(
            ByteBufCodecs.STRING_UTF8,
            SenderInfo::modId,
            ByteBufCodecs.STRING_UTF8,
            SenderInfo::clazz,
            SenderInfo::new
    );

    @Override
    public boolean equals(Object obj) {
        if (obj == this) {
            return true;
        }
        if (obj instanceof SenderInfo other) {
            return modId.equals(other.modId) && clazz.equals(other.clazz);
        }
        return false;
    }

    @Override
    public int hashCode() {
        return modId.hashCode() + clazz.hashCode();
    }
}
