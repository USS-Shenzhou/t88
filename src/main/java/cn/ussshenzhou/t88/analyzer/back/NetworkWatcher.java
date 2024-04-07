package cn.ussshenzhou.t88.analyzer.back;

import com.mojang.logging.LogUtils;
import io.netty.buffer.Unpooled;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.network.protocol.Packet;
import net.minecraft.network.protocol.common.ClientboundCustomPayloadPacket;
import net.minecraft.network.protocol.common.ServerboundCustomPayloadPacket;
import net.minecraft.network.protocol.common.custom.CustomPacketPayload;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.util.Tuple;
import net.neoforged.fml.ModContainer;
import net.neoforged.fml.ModList;
import net.neoforged.fml.javafmlmod.FMLModContainer;
import net.neoforged.neoforgespi.language.ModFileScanData;

import java.lang.reflect.Field;
import java.util.Map;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ConcurrentHashMap;

/**
 * @author USS_Shenzhou
 */
public class NetworkWatcher {

    //modId - classname - size
    public static final Map<Tuple<String, String>, Integer> SENT = new ConcurrentHashMap<>();
    public static final Map<Tuple<String, String>, Integer> RECEIVED = new ConcurrentHashMap<>();
    public static final Map<Class<?>, String> MOD_ID_CACHE = new ConcurrentHashMap<>();

    public static void record(Packet<?> packet, TR dir) {
        CompletableFuture.runAsync(() -> {
            var map = dir == TR.T ? SENT : RECEIVED;
            var buf = new FriendlyByteBuf(Unpooled.buffer());
            packet.write(buf);
            recordInternal(map, new Tuple<>(findModId(packet), packet.getClass().getSimpleName()), buf.readableBytes());
        });
    }

    public static String findModId(Packet<?> packet) {
        if (packet instanceof ClientboundCustomPayloadPacket p) {
            return MOD_ID_CACHE.computeIfAbsent(packet.getClass(), c -> searchModId(p.payload()));
        } else if (packet instanceof ServerboundCustomPayloadPacket p) {
            return MOD_ID_CACHE.computeIfAbsent(packet.getClass(), c -> searchModId(p.payload()));
        }
        return "minecraft";
    }

    public static String searchModId(CustomPacketPayload payload) {
        var clazz = payload.getClass();
        for (ModContainer mod : ModList.get().getSortedMods()) {
            try {
                Field s = FMLModContainer.class.getDeclaredField("scanResults");
                s.setAccessible(true);
                ModFileScanData scanResults = (ModFileScanData) s.get(mod);
                for (ModFileScanData.ClassData classData : scanResults.getClasses()) {
                    LogUtils.getLogger().warn("{}", classData);
                    if (classData.clazz().getClassName().equals(clazz.getName())) {
                        return mod.getModId();
                    }
                }
            } catch (NoSuchFieldException | IllegalAccessException ignored) {
            }
        }
        return "unknown";
    }

    private static void recordInternal(Map<Tuple<String, String>, Integer> map, Tuple<String, String> key, int size) {
        map.compute(key, ((resourceLocation, integer) -> integer == null ? size : integer + size));
    }

    protected static void clear() {
        SENT.clear();
        RECEIVED.clear();
    }

    //TODO
    public static final Map<ResourceLocation, Integer> FROM_SERVER_SENT = new ConcurrentHashMap<>();
    public static final Map<ResourceLocation, Integer> FROM_SERVER_RECEIVED = new ConcurrentHashMap<>();
    public static boolean fromServerUpdated = false;

    public enum TR {
        T, R
    }
}
