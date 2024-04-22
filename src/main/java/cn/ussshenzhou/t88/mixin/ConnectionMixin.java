package cn.ussshenzhou.t88.mixin;

import cn.ussshenzhou.t88.networkanalyzer.NetworkWatcher;
import io.netty.channel.ChannelHandlerContext;
import net.minecraft.network.Connection;
import net.minecraft.network.PacketSendListener;
import net.minecraft.network.protocol.Packet;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

/**
 * @author USS_Shenzhou
 */
@Mixin(Connection.class)
public class ConnectionMixin {

    @Inject(method = "sendPacket", at = @At("HEAD"))
    private void t88RecordSentByteSize(Packet<?> pPacket, PacketSendListener pSendListener, boolean flush, CallbackInfo ci) {
        NetworkWatcher.record(pPacket, NetworkWatcher.TR.T);
    }

    @Inject(method = "channelRead0(Lio/netty/channel/ChannelHandlerContext;Lnet/minecraft/network/protocol/Packet;)V", at = @At("HEAD"))
    private void t88RecordReceivedByteSize(ChannelHandlerContext pContext, Packet<?> pPacket, CallbackInfo ci) {
        NetworkWatcher.record(pPacket, NetworkWatcher.TR.R);
    }
}
