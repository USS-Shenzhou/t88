package cn.ussshenzhou.t88.mixin.compat;

import cn.ussshenzhou.t88.render.fixedblockentity.SectionBufferRenderTypeHelper;
import net.caffeinemc.mods.sodium.client.render.chunk.terrain.DefaultTerrainRenderPasses;
import net.caffeinemc.mods.sodium.client.render.chunk.terrain.TerrainRenderPass;
import net.caffeinemc.mods.sodium.client.render.chunk.terrain.material.Material;
import net.caffeinemc.mods.sodium.client.render.chunk.terrain.material.parameters.AlphaCutoffParameter;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Mutable;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

import java.util.Arrays;
import java.util.stream.Stream;

/**
 * @author USS_Shenzhou
 */
@Mixin(DefaultTerrainRenderPasses.class)
public abstract class SodiumDefaultTerrainRenderPassesMixin {

    @Shadow
    @Final
    @Mutable
    public static TerrainRenderPass[] ALL;

    @Inject(method = "<clinit>", at = @At("TAIL"), require = 0, remap = false)
    private static void t88ExtendChunkBufferRenderType(CallbackInfo ci) {
        ALL = Stream.concat(
                        SectionBufferRenderTypeHelper.T88_ADDITIONAL_CHUNK_BUFFER_RENDER_TYPES.stream()
                                .map(renderType -> {
                                    var p = new TerrainRenderPass(renderType, true, false);
                                    SectionBufferRenderTypeHelper.Sodium.SODIUM_MATERIALS.put(renderType, new Material(p, AlphaCutoffParameter.ZERO, false));
                                    SectionBufferRenderTypeHelper.Sodium.SODIUM_TERRAIN_RENDER_PASSES.put(renderType, p);
                                    return p;
                                }),
                        Arrays.stream(ALL))
                .toArray(TerrainRenderPass[]::new);
    }
}
