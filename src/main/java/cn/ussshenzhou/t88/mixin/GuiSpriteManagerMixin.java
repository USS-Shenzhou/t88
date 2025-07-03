package cn.ussshenzhou.t88.mixin;

import cn.ussshenzhou.t88.T88;
import com.llamalad7.mixinextras.sugar.Local;
import net.minecraft.client.gui.GuiSpriteManager;
import net.minecraft.client.renderer.texture.TextureAtlasSprite;
import net.minecraft.client.renderer.texture.TextureManager;
import net.minecraft.client.resources.TextureAtlasHolder;
import net.minecraft.resources.ResourceLocation;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

/**
 * @author USS_Shenzhou
 */
@Mixin(GuiSpriteManager.class)
public abstract class GuiSpriteManagerMixin extends TextureAtlasHolder {

    public GuiSpriteManagerMixin(TextureManager textureManager, ResourceLocation textureAtlasLocation, ResourceLocation atlasInfoLocation) {
        super(textureManager, textureAtlasLocation, atlasInfoLocation);
    }

    @Inject(method = "getSprite", at = @At("HEAD"), cancellable = true)
    private void t88ReplaceStyle(CallbackInfoReturnable<TextureAtlasSprite> cir, @Local(argsOnly = true) ResourceLocation originalLocation) {
        if (!ResourceLocation.DEFAULT_NAMESPACE.equals(originalLocation.getNamespace())) {
            return;
        }
        var path = originalLocation.getPath();
        var slashPos = path.lastIndexOf('/');
        var replacedLocation = ResourceLocation.fromNamespaceAndPath(T88.MOD_ID, path.substring(0, slashPos) + "/t88_style_" + path.substring(slashPos + 1));
        if (this.textureAtlas.texturesByName.containsKey(replacedLocation)) {
            cir.setReturnValue(super.getSprite(replacedLocation));
        }
    }
}
