package cn.ussshenzhou.t88.network;

import net.minecraftforge.network.simple.SimpleChannel;

import java.util.HashMap;
import java.util.Locale;

/**
 * @author USS_Shenzhou
 */
public class PacketProxy {
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
}
