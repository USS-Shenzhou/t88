package cn.ussshenzhou.t88.mixin;

import net.minecraft.client.gui.components.EditBox;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.gen.Accessor;

/**
 * @author USS_Shenzhou
 */
@Mixin(EditBox.class)
public interface EditBoxAccessor {
    @Accessor
    int getDisplayPos();

    @Accessor
    boolean isIsEditable();

    @Accessor
    void setDisplayPos(int pos);

    @Accessor
    int getTextColor();
}
