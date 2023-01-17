package cn.ussshenzhou.t88.network;

import net.minecraftforge.network.simple.SimpleChannel;

/**
 * @author USS_Shenzhou
 */
@SuppressWarnings("AlibabaClassNamingShouldBeCamel")
public abstract class AbstractNetworkPacket {
    public static SimpleChannel CHANNEL;
    protected static int id = 0;

    public static int nextId() {
        return id++;
    }
}
