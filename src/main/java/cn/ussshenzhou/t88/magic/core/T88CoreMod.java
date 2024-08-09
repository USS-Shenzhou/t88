package cn.ussshenzhou.t88.magic.core;

import com.mojang.logging.LogUtils;
import cpw.mods.modlauncher.api.ITransformer;
import net.neoforged.neoforgespi.coremod.ICoreMod;

import java.util.List;

/**
 * @author USS_Shenzhou
 */
public class T88CoreMod implements ICoreMod {
    @Override
    public Iterable<? extends ITransformer<?>> getTransformers() {
        return List.of(
                new MutableRecordTransformer()
        );
    }
}
