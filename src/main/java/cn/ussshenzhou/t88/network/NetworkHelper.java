package cn.ussshenzhou.t88.network;

import com.mojang.logging.LogUtils;
import net.minecraft.server.level.ServerPlayer;
import net.minecraftforge.network.PacketDistributor;
import net.minecraftforge.network.simple.SimpleChannel;

import java.util.HashMap;
import java.util.Locale;

/**
 * @author USS_Shenzhou
 */
public class NetworkHelper {
    private static HashMap<String, SimpleChannel> channels = new HashMap<>();

    public static SimpleChannel getChannel(Class<?> packetClass) {
        return getChannel(classNameToResLocName(packetClass));
    }

    private static SimpleChannel getChannel(String channelName) {
        return channels.get(channelName);
    }

    public static void addChannel(String channelName, SimpleChannel channel) {
        channels.put(channelName, channel);
    }

    public static String classNameToResLocName(Class<?> clazz) {
        return classNameToResLocName(clazz.getSimpleName());
    }

    protected static String classNameToResLocName(String s){
        return s.toLowerCase(Locale.ENGLISH).replaceAll("\\$","_");
    }

    public static <MSG> void sendToServer(MSG packet) {
        SimpleChannel channel = getChannel(packet.getClass());
        if (channel != null) {
            getChannel(packet.getClass()).sendToServer(packet);
        } else {
            LogUtils.getLogger().error("Cannot find channel for {}.", packet);
        }
    }

    public static <MSG> void sendToPlayer(ServerPlayer target, MSG packet) {
        SimpleChannel channel = getChannel(packet.getClass());
        if (channel != null) {
            getChannel(packet.getClass()).send(PacketDistributor.PLAYER.with(() -> target), packet);
        } else {
            LogUtils.getLogger().error("Cannot find channel for {}.", packet);
        }
    }

    public static <MSG> void sendTo(PacketDistributor.PacketTarget target, MSG packet) {
        SimpleChannel channel = getChannel(packet.getClass());
        if (channel != null) {
            getChannel(packet.getClass()).send(target, packet);
        } else {
            LogUtils.getLogger().error("Cannot find channel for {}.", packet);
        }
    }
}
