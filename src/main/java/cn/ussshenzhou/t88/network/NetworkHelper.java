package cn.ussshenzhou.t88.network;

import cn.ussshenzhou.t88.magic.MagicHelper;
import com.mojang.logging.LogUtils;
import net.minecraft.network.protocol.common.custom.CustomPacketPayload;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.level.ChunkPos;
import net.neoforged.neoforge.client.network.ClientPacketDistributor;
import net.neoforged.neoforge.network.PacketDistributor;

import javax.annotation.Nullable;
import java.lang.reflect.Field;
import java.lang.reflect.InvocationTargetException;
import java.util.HashMap;

/**
 * @author USS_Shenzhou
 */
public class NetworkHelper {
    private static final HashMap<Class<?>, Class<? extends CustomPacketPayload>> ORIGINAL_PROXY_CLASS = new HashMap<>();

    public static @Nullable <MSG, T extends CustomPacketPayload> CustomPacketPayload convert(MSG packet) {
        var proxyClass = ORIGINAL_PROXY_CLASS.get(packet.getClass());
        if (proxyClass == null) {
            LogUtils.getLogger().error("Cannot find the proxy class for {}.", packet);
            return null;
        }
        try {
            //noinspection unchecked
            T proxy = (T) MagicHelper.UNSAFE.allocateInstance(proxyClass);
            for (Field field : packet.getClass().getDeclaredFields()) {
                field.setAccessible(true);
                if (proxyClass.isRecord()) {
                    Field proxyField = proxyClass.getDeclaredField(field.getName());
                    //noinspection unchecked
                    proxy = (T) MagicHelper.set((Record) proxy, proxyField, field.get(packet));
                } else {
                    field.set(proxy, field.get(packet));
                }
            }
            return proxy;
        } catch (InstantiationException | IllegalAccessException | NoSuchFieldException | InvocationTargetException e) {
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
            ClientPacketDistributor.sendToServer(c);
        } else {
            var p = convert(packet);
            if (p != null) {
                ClientPacketDistributor.sendToServer(p);
            }
        }
    }

    public static <MSG> void sendToPlayer(ServerPlayer target, MSG packet) {
        if (packet instanceof CustomPacketPayload c) {
            PacketDistributor.sendToPlayer(target, c);
        } else {
            var p = convert(packet);
            if (p != null) {
                PacketDistributor.sendToPlayer(target, p);
            }
        }
    }

    public static <MSG> void sendToPlayersInDimension(ServerLevel level, MSG packet) {
        if (packet instanceof CustomPacketPayload c) {
            PacketDistributor.sendToPlayersInDimension(level, c);
        } else {
            var p = convert(packet);
            if (p != null) {
                PacketDistributor.sendToPlayersInDimension(level, p);
            }
        }
    }

    public static <MSG> void sendToPlayersNear(ServerLevel level, @Nullable ServerPlayer excluded, double x, double y, double z, double radius, MSG packet) {
        if (packet instanceof CustomPacketPayload c) {
            PacketDistributor.sendToPlayersNear(level, excluded, x, y, z, radius, c);
        } else {
            var p = convert(packet);
            if (p != null) {
                PacketDistributor.sendToPlayersNear(level, excluded, x, y, z, radius, p);
            }
        }
    }

    public static <MSG> void sendToAllPlayers(MSG packet) {
        if (packet instanceof CustomPacketPayload c) {
            PacketDistributor.sendToAllPlayers(c);
        } else {
            var p = convert(packet);
            if (p != null) {
                PacketDistributor.sendToAllPlayers(p);
            }
        }
    }

    public static <MSG> void sendToPlayersTrackingEntity(Entity entity, MSG packet) {
        if (packet instanceof CustomPacketPayload c) {
            PacketDistributor.sendToPlayersTrackingEntity(entity, c);
        } else {
            var p = convert(packet);
            if (p != null) {
                PacketDistributor.sendToPlayersTrackingEntity(entity, p);
            }
        }
    }

    public static <MSG> void sendToPlayersTrackingEntityAndSelf(Entity entity, MSG packet) {
        if (packet instanceof CustomPacketPayload c) {
            PacketDistributor.sendToPlayersTrackingEntityAndSelf(entity, c);
        } else {
            var p = convert(packet);
            if (p != null) {
                PacketDistributor.sendToPlayersTrackingEntityAndSelf(entity, p);
            }
        }
    }

    public static <MSG> void sendToPlayersTrackingChunk(ServerLevel level, ChunkPos chunkPos, MSG packet) {
        if (packet instanceof CustomPacketPayload c) {
            PacketDistributor.sendToPlayersTrackingChunk(level, chunkPos, c);
        } else {
            var p = convert(packet);
            if (p != null) {
                PacketDistributor.sendToPlayersTrackingChunk(level, chunkPos, p);
            }
        }
    }
}
