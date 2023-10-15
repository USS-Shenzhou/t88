package cn.ussshenzhou.t88.util;

import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.math.Axis;
import net.minecraft.client.renderer.LevelRenderer;
import net.minecraft.client.renderer.block.model.BakedQuad;
import net.minecraft.client.renderer.block.model.ItemOverrides;
import net.minecraft.client.renderer.texture.TextureAtlasSprite;
import net.minecraft.client.resources.model.BakedModel;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.util.RandomSource;
import net.minecraft.world.level.BlockAndTintGetter;
import net.minecraft.world.level.block.state.BlockState;
import org.jetbrains.annotations.Nullable;

import java.util.List;

/**
 * @author USS_Shenzhou
 */
public class RenderUtil {

    public static int getPackedLight(int sourceLight, BlockAndTintGetter level, BlockPos pos) {
        return getPackedLight(sourceLight, LevelRenderer.getLightColor(level, pos));
    }

    public static int getPackedLight(int sourceLight, int packedLight) {
        return (packedLight & 0b00000000_00000000_00000000_11110000) > sourceLight ? packedLight : (packedLight & 0b00000000_11110000_00000000_00000000 | sourceLight << 4);
    }

    public static void rotateAroundBlockCenter(Direction direction, PoseStack poseStack) {
        switch (direction) {
            case NORTH -> {
            }
            case SOUTH -> poseStack.rotateAround(Axis.YP.rotation((float) Math.PI), 0.5f, 0.5f, 0.5f);
            case EAST -> poseStack.rotateAround(Axis.YP.rotation((float) -Math.PI / 2), 0.5f, 0.5f, 0.5f);
            case WEST -> poseStack.rotateAround(Axis.YP.rotation((float) Math.PI / 2), 0.5f, 0.5f, 0.5f);
            case UP -> poseStack.rotateAround(Axis.XP.rotation((float) -Math.PI / 2), 0.5f, 0.5f, 0.5f);
            case DOWN -> poseStack.rotateAround(Axis.XP.rotation((float) Math.PI / 2), 0.5f, 0.5f, 0.5f);
        }
    }

    public static BakedModel simpleFromBakedQuads(List<BakedQuad> quads, BakedModel origin) {
        return new BakedModel() {
            @Override
            public List<BakedQuad> getQuads(@Nullable BlockState pState, @Nullable Direction pDirection, RandomSource pRandom) {
                return quads;
            }

            @Override
            public boolean useAmbientOcclusion() {
                return origin.useAmbientOcclusion();
            }

            @Override
            public boolean isGui3d() {
                return origin.isGui3d();
            }

            @Override
            public boolean usesBlockLight() {
                return origin.usesBlockLight();
            }

            @Override
            public boolean isCustomRenderer() {
                return origin.isCustomRenderer();
            }

            @Override
            public TextureAtlasSprite getParticleIcon() {
                return origin.getParticleIcon();
            }

            @Override
            public ItemOverrides getOverrides() {
                return origin.getOverrides();
            }
        };
    }

}
