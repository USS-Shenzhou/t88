package cn.ussshenzhou.t88.render;

import com.mojang.blaze3d.vertex.DefaultVertexFormat;
import com.mojang.blaze3d.vertex.VertexFormat;
import net.minecraft.client.renderer.RenderStateShard;
import net.minecraft.client.renderer.RenderType;

/**
 * @author USS_Shenzhou
 */
public class ModRenderTypes extends RenderStateShard {

    @Deprecated
    public ModRenderTypes(String pName, Runnable pSetupState, Runnable pClearState) {
        super(pName, pSetupState, pClearState);
    }

    @SectionBufferRenderType
    public static final RenderType FILL_COLOR = RenderType.create("fill_color",
            DefaultVertexFormat.POSITION_COLOR_LIGHTMAP,
            VertexFormat.Mode.QUADS,
            2097152,
            true,
            false,
            RenderType.CompositeState.builder()
                    .setLightmapState(LIGHTMAP)
                    .setShaderState(POSITION_COLOR_LIGHTMAP_SHADER)
                    .createCompositeState(true)
    );
}
