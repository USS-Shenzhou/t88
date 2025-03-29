package cn.ussshenzhou.t88.networkanalyzer;

import cn.ussshenzhou.t88.config.ConfigHelper;
import com.mojang.logging.LogUtils;
import net.minecraft.network.protocol.Packet;
import net.minecraft.network.protocol.common.ClientboundCustomPayloadPacket;
import net.minecraft.network.protocol.common.ServerboundCustomPayloadPacket;
import net.minecraft.network.protocol.common.custom.CustomPacketPayload;
import net.minecraft.resources.ResourceLocation;
import net.neoforged.fml.ModContainer;
import net.neoforged.fml.ModList;
import net.neoforged.fml.javafmlmod.FMLModContainer;
import net.neoforged.neoforgespi.language.ModFileScanData;

import java.lang.reflect.Field;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ConcurrentHashMap;

/**
 * @author USS_Shenzhou
 */
public class NetworkWatcher {

    public static final Object NULL = new Object();
    //modId : classname - size
    public static final DelayedMap<SenderInfo, SizeAndTimes> SENT = new DelayedMap<>();
    public static final DelayedMap<SenderInfo, SizeAndTimes> RECEIVED = new DelayedMap<>();
    public static final ConcurrentHashMap<Class<?>, String> MOD_ID_CACHE = new ConcurrentHashMap<>();
    public static final ConcurrentHashMap<ResourceLocation, Object> SIZE_BLACKLIST = new ConcurrentHashMap<>() {{
        ConfigHelper.getConfigRead(NetworkWatcherBlacklist.class).blacklist.forEach(resourceLocation -> put(resourceLocation, NULL));
    }};
    public static final ConcurrentHashMap<ResourceLocation, Object> MOD_ID_BLACKLIST = new ConcurrentHashMap<>();

    public static void record(Packet<?> packet, TR dir, int size) {
        CompletableFuture.runAsync(() -> {
            var map = dir == TR.T ? SENT : RECEIVED;
            recordInternal(map, getSenderInfo(packet), size);
        });
    }

    public static SenderInfo getSenderInfo(Packet<?> packet) {
        String name;
        if (packet instanceof ClientboundCustomPayloadPacket p) {
            name = p.payload().type().id().toString();
        } else if (packet instanceof ServerboundCustomPayloadPacket p) {
            name = p.payload().type().id().toString();
        } else {
            name = packet.type().id().toString();
        }
        return new SenderInfo(findModId(packet),name );
    }

    public static String findModId(Packet<?> packet) {
        if (packet instanceof ClientboundCustomPayloadPacket p) {
            return MOD_ID_CACHE.computeIfAbsent(p.payload().getClass(), c -> searchModId(p.payload()));
        } else if (packet instanceof ServerboundCustomPayloadPacket p) {
            return MOD_ID_CACHE.computeIfAbsent(p.payload().getClass(), c -> searchModId(p.payload()));
        }
        return "minecraft";
    }

    public static String searchModId(CustomPacketPayload payload) {
        if (MOD_ID_BLACKLIST.containsKey(payload.type().id())) {
            return "unknown";
        }
        var clazz = payload.getClass();
        for (ModContainer mod : ModList.get().getSortedMods()) {
            if (mod instanceof FMLModContainer) {
                try {
                    Field s = FMLModContainer.class.getDeclaredField("scanResults");
                    s.setAccessible(true);
                    ModFileScanData scanResults = (ModFileScanData) s.get(mod);
                    for (ModFileScanData.ClassData classData : scanResults.getClasses()) {
                        if (classData.clazz().getClassName().equals(clazz.getName())) {
                            return mod.getModId();
                        }
                    }
                } catch (NoSuchFieldException | IllegalAccessException e) {
                    LogUtils.getLogger().warn("Failed to get the modID of CustomPacketPayload<{}>. It will do no harm.", payload.type().id());
                    LogUtils.getLogger().error("<{}> has been added to a temp blacklist to prevent filling the log.", payload.type().id());
                    LogUtils.getLogger().warn(e.getMessage());
                }
            }
        }
        return "unknown";
    }

    private static void recordInternal(DelayedMap<SenderInfo, SizeAndTimes> map, SenderInfo key, int size) {
        map.get().compute(key, ((senderInfo, sizeAndTimes) -> sizeAndTimes == null ? new SizeAndTimes(size) : sizeAndTimes.increaseSize(size)));
    }

    protected static void clear() {
        SENT.switchAndClear();
        RECEIVED.switchAndClear();
    }

    public static final ConcurrentHashMap<SenderInfo, SizeAndTimes> SERVER_SENT = new ConcurrentHashMap<>();
    public static final ConcurrentHashMap<SenderInfo, SizeAndTimes> SERVER_RECEIVED = new ConcurrentHashMap<>();

    public enum TR {
        T, R
    }

    public static class DelayedMap<K, V> {
        private final ConcurrentHashMap<K, V> a = new ConcurrentHashMap<>(), b = new ConcurrentHashMap<>();
        private volatile boolean usingA = true;

        public ConcurrentHashMap<K, V> get() {
            return usingA ? a : b;
        }

        public void switchAndClear() {
            //noinspection NonAtomicOperationOnVolatileField
            usingA = !usingA;
            (usingA ? a : b).clear();
        }
    }
}
