package cn.ussshenzhou.t88.network;

import cn.ussshenzhou.t88.T88;
import com.mojang.logging.LogUtils;
import net.minecraft.network.protocol.common.custom.CustomPacketPayload;
import net.minecraft.server.level.ServerPlayer;
import net.neoforged.neoforge.network.PacketDistributor;
import sun.misc.Unsafe;

import java.lang.reflect.Constructor;
import java.lang.reflect.Field;
import java.util.HashMap;
import java.util.Locale;

/**
 * @author USS_Shenzhou
 */
public class NetworkHelper {
    private static final HashMap<Class<?>, Class<? extends CustomPacketPayload>> ORIGINAL_PROXY_CLASS = new HashMap<>();
    private static final Unsafe UNSAFE = getUnsafe();

    private static Unsafe getUnsafe() {
        try {
            var field = Unsafe.class.getDeclaredField("theUnsafe");
            field.setAccessible(true);
            return (Unsafe) field.get(null);
        } catch (IllegalAccessException | NoSuchFieldException e) {
            LogUtils.getLogger().error("Failed to initialize UnSafe. This should not happen.");
            throw new RuntimeException(e);
        }
    }

    public static <MSG, T extends CustomPacketPayload> T convert(MSG packet) {
        var proxyClass = ORIGINAL_PROXY_CLASS.get(packet.getClass());
        if (proxyClass == null) {
            LogUtils.getLogger().error("Cannot find the proxy class for {}.", packet);
            return null;
        }
        try {
            //noinspection unchecked
            T proxy = (T) UNSAFE.allocateInstance(proxyClass);
            for (Field field : packet.getClass().getDeclaredFields()) {
                field.setAccessible(true);
                var proxyField = proxyClass.getDeclaredField(field.getName());
                proxyField.setAccessible(true);
                proxyField.set(proxy, field.get(packet));
            }
            return proxy;
        } catch (InstantiationException | NoSuchFieldException | IllegalAccessException e) {
            LogUtils.getLogger().error("This should not happen.");
            throw new RuntimeException(e);
        }
    }

    public static void register(Class<?> packet, Class<?> proxy) {
        try {
            //noinspection unchecked
            ORIGINAL_PROXY_CLASS.put(packet, (Class<? extends CustomPacketPayload>) proxy);
        } catch (ClassCastException e) {
            LogUtils.getLogger().error("T88 Failed to cast {}. This should not happen.", proxy);
            throw new RuntimeException(e);
        }
    }

    public static <MSG> void sendToServer(MSG packet) {
        PacketDistributor.SERVER.noArg().send(convert(packet));
    }

    public static <MSG> void sendToPlayer(ServerPlayer target, MSG packet) {
        PacketDistributor.PLAYER.with(target).send(convert(packet));
    }

    public static <MSG> void sendTo(PacketDistributor.PacketTarget target, MSG packet) {
        target.send(convert(packet));
    }
}
