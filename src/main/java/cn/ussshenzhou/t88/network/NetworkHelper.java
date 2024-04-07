package cn.ussshenzhou.t88.network;

import com.mojang.logging.LogUtils;
import net.minecraft.network.protocol.common.custom.CustomPacketPayload;
import net.minecraft.server.level.ServerPlayer;
import net.neoforged.neoforge.network.PacketDistributor;
import sun.misc.Unsafe;

import javax.annotation.Nullable;
import java.lang.reflect.Field;
import java.lang.reflect.Modifier;
import java.util.HashMap;

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

    public static @Nullable <MSG, T extends CustomPacketPayload> CustomPacketPayload convert(MSG packet) {
        var proxyClass = ORIGINAL_PROXY_CLASS.get(packet.getClass());
        if (proxyClass == null) {
            LogUtils.getLogger().error("Cannot find the proxy class for {}.", packet);
            return null;
        }
        try {
            //noinspection unchecked
            T proxy = (T) UNSAFE.allocateInstance(proxyClass);
            for (Field field : packet.getClass().getDeclaredFields()) {
                if (Modifier.isFinal(field.getModifiers())) {
                    try {
                        field.setAccessible(true);
                        field.set(proxy, field.get(packet));
                    } catch (IllegalAccessException ignored) {
                    }
                } else {
                    field.setAccessible(true);
                    field.set(proxy, field.get(packet));
                }
            }
            return proxy;
        } catch (InstantiationException | IllegalAccessException e) {
            LogUtils.getLogger().error("This should not happen. This packet will be abandoned.");
            LogUtils.getLogger().error(e.getMessage());
            return null;
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
        if (packet instanceof CustomPacketPayload c) {
            PacketDistributor.SERVER.noArg().send(c);
        } else {
            var p = convert(packet);
            if (p != null) {
                PacketDistributor.SERVER.noArg().send(p);
            }
        }
    }

    public static <MSG> void sendToPlayer(ServerPlayer target, MSG packet) {
        if (packet instanceof CustomPacketPayload c) {
            PacketDistributor.PLAYER.with(target).send(c);
        } else {
            var p = convert(packet);
            if (p != null) {
                PacketDistributor.PLAYER.with(target).send(p);
            }
        }
    }

    public static <MSG> void sendTo(PacketDistributor.PacketTarget target, MSG packet) {
        if (packet instanceof CustomPacketPayload c) {
            target.send(c);
        } else {
            var p = convert(packet);
            if (p != null) {
                target.send(p);
            }
        }
    }
}
