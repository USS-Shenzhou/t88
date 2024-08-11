package cn.ussshenzhou.t88.magic.core;

import com.mojang.logging.LogUtils;
import cpw.mods.modlauncher.api.ITransformer;
import net.neoforged.neoforgespi.coremod.ICoreMod;

import java.lang.reflect.Field;
import java.util.List;

/**
 * @author USS_Shenzhou
 * @deprecated Wait for FML guys to develop a improved java-based one. Before that, use {@link sun.misc.Unsafe#objectFieldOffset(Field)} instead.
 */
@Deprecated
public class T88CoreMod implements ICoreMod {
    @Override
    public Iterable<? extends ITransformer<?>> getTransformers() {
        return List.of(
                new MutableRecordTransformer()
        );
    }
}
