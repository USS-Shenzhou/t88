package cn.ussshenzhou.t88.networkanalyzer;

import com.mojang.logging.LogUtils;
import io.netty.buffer.Unpooled;
import net.minecraft.network.ConnectionProtocol;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.network.protocol.Packet;
import net.minecraft.network.protocol.PacketFlow;
import net.minecraft.network.protocol.common.ClientboundCustomPayloadPacket;
import net.minecraft.network.protocol.common.ServerboundCustomPayloadPacket;
import net.minecraft.network.protocol.common.custom.CustomPacketPayload;
import net.minecraft.resources.ResourceLocation;
import net.neoforged.fml.ModContainer;
import net.neoforged.fml.ModList;
import net.neoforged.fml.javafmlmod.FMLModContainer;
import net.neoforged.neoforge.network.connection.ConnectionType;
import net.neoforged.neoforge.network.registration.NetworkRegistry;
import net.neoforged.neoforgespi.language.ModFileScanData;

import java.lang.reflect.Field;
import java.lang.reflect.InvocationTargetException;
import java.util.Arrays;
import java.util.Map;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ConcurrentHashMap;

/**
 * @author USS_Shenzhou
 */
public class NetworkWatcher {

    //modId : classname - size
    public static final ConcurrentHashMap<SenderInfo, SizeAndTimes> SENT = new ConcurrentHashMap<>();
    public static final ConcurrentHashMap<SenderInfo, SizeAndTimes> RECEIVED = new ConcurrentHashMap<>();
    public static final ConcurrentHashMap<Class<?>, String> MOD_ID_CACHE = new ConcurrentHashMap<>();

    public static void record(Packet<?> packet, TR dir) {
        CompletableFuture.runAsync(() -> {
            var map = dir == TR.T ? SENT : RECEIVED;
            int size;
            if (packet instanceof ServerboundCustomPayloadPacket p) {
                size = getCustomPacketSize(p);
            } else if (packet instanceof ClientboundCustomPayloadPacket p) {
                size = getCustomPacketSize(p);
            } else {
                size = getPacketSizeFromWrite(packet);
            }
            recordInternal(map, getSenderInfo(packet), size);
        });
    }

    private static int getPacketSizeFromWrite(Packet<?> packet) {
        try {
            var method = packet.getClass().getDeclaredMethod("write", FriendlyByteBuf.class);
            method.setAccessible(true);
            var buf = new RegistryFriendlyByteBuf(Unpooled.buffer(), null, ConnectionType.NEOFORGE);
            method.invoke(packet, buf);
            int size = buf.writerIndex();
            buf.release();
            return size;
        } catch (NoSuchMethodException | IllegalAccessException | InvocationTargetException ignored) {
        }
        return 0;
    }

    private static int getCustomPacketSize(ServerboundCustomPayloadPacket packet) {
        return getSizeFromCustomPacketPayload(packet.payload());
    }

    private static int getCustomPacketSize(ClientboundCustomPayloadPacket packet) {
        return getSizeFromCustomPacketPayload(packet.payload());
    }

    @SuppressWarnings("UnstableApiUsage")
    private static int getSizeFromCustomPacketPayload(CustomPacketPayload payload) {
        var buf = new RegistryFriendlyByteBuf(Unpooled.buffer(), null, ConnectionType.NEOFORGE);
        var codec = NetworkRegistry.getCodec(payload.type().id(), ConnectionProtocol.PLAY, PacketFlow.SERVERBOUND);
        if (codec == null) {
            codec = NetworkRegistry.getCodec(payload.type().id(), ConnectionProtocol.PLAY, PacketFlow.CLIENTBOUND);
        }
        if (codec == null) {
            return 0;
        }
        net.minecraft.network.codec.StreamCodec<? super FriendlyByteBuf, ? extends CustomPacketPayload> finalCodec = codec;
        Arrays.stream(codec.getClass().getMethods()).filter(method -> "encode".equals(method.getName())).findAny().ifPresent(method -> {
            method.setAccessible(true);
            try {
                method.invoke(finalCodec, buf, payload);
            } catch (IllegalAccessException | InvocationTargetException e) {
                LogUtils.getLogger().warn(e.getMessage());
            }
        });
        int size = buf.writerIndex();
        buf.release();
        return size;
    }

    public static SenderInfo getSenderInfo(Packet<?> packet) {
        String clazz;
        if (packet instanceof ClientboundCustomPayloadPacket p) {
            clazz = p.payload().getClass().getSimpleName();
        } else if (packet instanceof ServerboundCustomPayloadPacket p) {
            clazz = p.payload().getClass().getSimpleName();
        } else {
            var s = packet.getClass().getName().split("\\.");
            clazz = s[s.length - 1];
        }
        return new SenderInfo(findModId(packet), clazz);
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
                } catch (NoSuchFieldException | IllegalAccessException ignored) {
                    LogUtils.getLogger().warn(ignored.getMessage());
                }
            }
        }
        return "unknown";
    }

    private static void recordInternal(Map<SenderInfo, SizeAndTimes> map, SenderInfo key, int size) {
        map.compute(key, ((senderInfo, sizeAndTimes) -> sizeAndTimes == null ? new SizeAndTimes(size) : sizeAndTimes.increaseSize(size)));
    }

    protected static void clear() {
        SENT.clear();
        RECEIVED.clear();
    }

    //TODO
    public static final ConcurrentHashMap<ResourceLocation, Integer> FROM_SERVER_SENT = new ConcurrentHashMap<>();
    public static final ConcurrentHashMap<ResourceLocation, Integer> FROM_SERVER_RECEIVED = new ConcurrentHashMap<>();
    public static boolean fromServerUpdated = false;

    public enum TR {
        T, R
    }
}
