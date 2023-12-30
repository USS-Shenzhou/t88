package cn.ussshenzhou.t88.analyzer.back;

import io.netty.buffer.Unpooled;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.network.protocol.Packet;
import net.minecraft.resources.ResourceLocation;
import net.neoforged.neoforge.network.ICustomPacket;
import org.spongepowered.asm.mixin.Unique;

import java.util.Map;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ConcurrentHashMap;

/**
 * @author USS_Shenzhou
 */
public class NetworkWatcher {

    public static final Map<ResourceLocation, Integer> SENT = new ConcurrentHashMap<>();
    public static final Map<ResourceLocation, Integer> RECEIVED = new ConcurrentHashMap<>();

    public static void record(Packet<?> pPacket, TR dir) {
        CompletableFuture.runAsync(() -> {
            var map = dir == TR.T ? SENT : RECEIVED;
            if (pPacket instanceof ICustomPacket<?> customPacket) {
                recordInternal(map, customPacket.getName(), customPacket.getInternalData() == null ? 0 : customPacket.getInternalData().readableBytes());
            } else {
                var buf = new FriendlyByteBuf(Unpooled.buffer());
                pPacket.write(buf);
                recordInternal(map, new ResourceLocation("minecraft", pPacket.getClass().getSimpleName().toLowerCase()), buf.readableBytes());
            }
        });
    }

    private static void recordInternal(Map<ResourceLocation, Integer> map, ResourceLocation key, int size) {
        map.compute(key, ((resourceLocation, integer) -> integer == null ? size : integer + size));
    }

    protected static void clear() {
        SENT.clear();
        RECEIVED.clear();
    }

    public static final Map<ResourceLocation, Integer> FROM_SERVER_SENT = new ConcurrentHashMap<>();
    public static final Map<ResourceLocation, Integer> FROM_SERVER_RECEIVED = new ConcurrentHashMap<>();
    public static boolean fromServerUpdated = false;

    public enum TR {
        T, R
    }
}
