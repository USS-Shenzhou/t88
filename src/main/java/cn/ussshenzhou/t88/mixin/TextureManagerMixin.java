package cn.ussshenzhou.t88.mixin;

import cn.ussshenzhou.t88.T88;
import cn.ussshenzhou.t88.config.ConfigHelper;
import cn.ussshenzhou.t88.util.T88Config;
import net.minecraft.client.renderer.texture.AbstractTexture;
import net.minecraft.client.renderer.texture.ReloadableTexture;
import net.minecraft.client.renderer.texture.SimpleTexture;
import net.minecraft.client.renderer.texture.TextureManager;
import net.minecraft.resources.ResourceLocation;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

import java.util.Map;

/**
 * @author USS_Shenzhou
 */
@Mixin(TextureManager.class)
public abstract class TextureManagerMixin {

    @Shadow
    @Final
    private Map<ResourceLocation, AbstractTexture> byPath;

    @Shadow
    public abstract void registerAndLoad(ResourceLocation textureId, ReloadableTexture texture);

    @Inject(method = "getTexture", at = @At("HEAD"), cancellable = true)
    public void t88ReplaceStyle(ResourceLocation originalLocation, CallbackInfoReturnable<AbstractTexture> cir) {
        if (!ResourceLocation.DEFAULT_NAMESPACE.equals(originalLocation.getNamespace()) || (ConfigHelper.getConfigRead(T88Config.class) != null && !ConfigHelper.getConfigRead(T88Config.class).replaceStyle)) {
            return;
        }
        var replacedLocation = ResourceLocation.fromNamespaceAndPath(T88.MOD_ID, originalLocation.getPath().replace("/gui/", "/t88_style/"));
        if (this.byPath.containsKey(replacedLocation)) {
            AbstractTexture abstracttexture = this.byPath.get(replacedLocation);
            if (abstracttexture != null) {
                cir.setReturnValue(abstracttexture);
            } else {
                SimpleTexture simpletexture = new SimpleTexture(replacedLocation);
                this.registerAndLoad(replacedLocation, simpletexture);
                cir.setReturnValue(simpletexture);
            }
        }
    }
}
