package cn.ussshenzhou.t88.mixin;

import cn.ussshenzhou.t88.networkanalyzer.NetworkWatcher;
import com.llamalad7.mixinextras.sugar.Local;
import io.netty.buffer.ByteBuf;
import io.netty.channel.ChannelHandlerContext;
import net.minecraft.network.PacketDecoder;
import net.minecraft.network.protocol.Packet;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

import java.util.List;

/**
 * @author USS_Shenzhou
 */
@Mixin(PacketDecoder.class)
public class PacketDecoderMixin {

    @Inject(method = "decode", at = @At(value = "INVOKE", target = "Lnet/minecraft/util/profiling/jfr/JvmProfiler;onPacketReceived(Lnet/minecraft/network/ConnectionProtocol;Lnet/minecraft/network/protocol/PacketType;Ljava/net/SocketAddress;I)V"))
    private void t88RecordReceive(ChannelHandlerContext context, ByteBuf in, List<Object> out, CallbackInfo ci, @Local int size, @Local Packet<?> packet) {
        NetworkWatcher.record(packet, NetworkWatcher.TR.R, size);
    }
}
