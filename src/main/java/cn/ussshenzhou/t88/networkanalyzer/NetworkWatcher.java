package cn.ussshenzhou.t88.networkanalyzer;

import cn.ussshenzhou.t88.config.ConfigHelper;
import com.google.common.collect.ImmutableMap;
import com.mojang.logging.LogUtils;
import io.netty.buffer.Unpooled;
import net.minecraft.client.Minecraft;
import net.minecraft.core.RegistryAccess;
import net.minecraft.network.ConnectionProtocol;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.network.protocol.Packet;
import net.minecraft.network.protocol.common.ClientboundCustomPayloadPacket;
import net.minecraft.network.protocol.common.ServerboundCustomPayloadPacket;
import net.minecraft.network.protocol.common.custom.CustomPacketPayload;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.MinecraftServer;
import net.neoforged.api.distmarker.Dist;
import net.neoforged.fml.LogicalSide;
import net.neoforged.fml.ModContainer;
import net.neoforged.fml.ModList;
import net.neoforged.fml.javafmlmod.FMLModContainer;
import net.neoforged.fml.loading.FMLEnvironment;
import net.neoforged.neoforge.common.util.LogicalSidedProvider;
import net.neoforged.neoforge.network.connection.ConnectionType;
import net.neoforged.neoforge.network.payload.*;
import net.neoforged.neoforge.network.registration.NetworkRegistry;
import net.neoforged.neoforge.network.registration.PayloadRegistration;
import net.neoforged.neoforgespi.language.ModFileScanData;
import org.checkerframework.checker.units.qual.N;
import org.jetbrains.annotations.Nullable;

import java.lang.reflect.Field;
import java.lang.reflect.InvocationTargetException;
import java.util.Arrays;
import java.util.HashSet;
import java.util.Map;
import java.util.Objects;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ConcurrentHashMap;

/**
 * @author USS_Shenzhou
 */
@SuppressWarnings("UnstableApiUsage")
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
            @SuppressWarnings("DataFlowIssue")
            var buf = new RegistryFriendlyByteBuf(Unpooled.buffer(), getRegistryAccess(), ConnectionType.NEOFORGE);
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

    /**
     * @see NetworkRegistry#BUILTIN_PAYLOADS
     */
    private static final Map<ResourceLocation, StreamCodec<FriendlyByteBuf, ? extends CustomPacketPayload>> BUILTIN_PAYLOADS = ImmutableMap.of(
            MinecraftRegisterPayload.ID, MinecraftRegisterPayload.STREAM_CODEC,
            MinecraftUnregisterPayload.ID, MinecraftUnregisterPayload.STREAM_CODEC,
            ModdedNetworkQueryPayload.ID, ModdedNetworkQueryPayload.STREAM_CODEC,
            ModdedNetworkPayload.ID, ModdedNetworkPayload.STREAM_CODEC,
            ModdedNetworkSetupFailedPayload.ID, ModdedNetworkSetupFailedPayload.STREAM_CODEC);

    private static final Map<ConnectionProtocol, Map<ResourceLocation, PayloadRegistration<?>>> PAYLOAD_REGISTRATIONS;

    static {
        try {
            var f = NetworkRegistry.class.getDeclaredField("PAYLOAD_REGISTRATIONS");
            f.setAccessible(true);
            //noinspection unchecked
            PAYLOAD_REGISTRATIONS = (Map<ConnectionProtocol, Map<ResourceLocation, PayloadRegistration<?>>>) f.get(null);
        } catch (NoSuchFieldException | IllegalAccessException e) {
            throw new RuntimeException(e);
        }
    }

    private static int getSizeFromCustomPacketPayload(CustomPacketPayload payload) {
        if (SIZE_BLACKLIST.containsKey(payload.type().id())) {
            return 0;
        }
        @SuppressWarnings("DataFlowIssue")
        var buf = new RegistryFriendlyByteBuf(Unpooled.buffer(), getRegistryAccess(), ConnectionType.NEOFORGE);
        StreamCodec<? super FriendlyByteBuf, ? extends CustomPacketPayload> codec = null;
        var reg = PAYLOAD_REGISTRATIONS.get(ConnectionProtocol.PLAY).get(payload.type().id());
        if (reg != null) {
            //noinspection unchecked
            codec = (StreamCodec<? super FriendlyByteBuf, ? extends CustomPacketPayload>) reg.codec();
        }
        if (codec == null) {
            codec = BUILTIN_PAYLOADS.get(payload.type().id());
        }
        if (codec == null) {
            return 0;
        }
        net.minecraft.network.codec.StreamCodec<? super FriendlyByteBuf, ? extends CustomPacketPayload> finalCodec = codec;
        Arrays.stream(codec.getClass().getMethods()).filter(method -> "encode".equals(method.getName())).findAny().ifPresent(method -> {
            method.setAccessible(true);
            try {
                method.invoke(finalCodec, buf, payload);
            } catch (Exception e) {
                SIZE_BLACKLIST.put(payload.type().id(), NULL);
                LogUtils.getLogger().error("Failed trying getting the size of CustomPacketPayload<{}> by invoke <{} # {}>. It will do no harm.",
                        payload.type().id(), finalCodec, method);
                LogUtils.getLogger().error("<{}> has been added to a temp blacklist to prevent filling the log.",
                        payload.type().id());
                LogUtils.getLogger().error(e.getMessage());
            }
        });
        int size = buf.writerIndex();
        buf.release();
        return size;
    }

    private static RegistryAccess getRegistryAccess() {
        if (FMLEnvironment.dist == Dist.CLIENT) {
            return Minecraft.getInstance().level == null ? null : Minecraft.getInstance().level.registryAccess();
        } else {
            return ((MinecraftServer) LogicalSidedProvider.WORKQUEUE.get(LogicalSide.SERVER)).registryAccess();
        }
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
