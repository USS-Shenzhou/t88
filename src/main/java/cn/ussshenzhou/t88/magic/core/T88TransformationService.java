package cn.ussshenzhou.t88.magic.core;

import cpw.mods.modlauncher.api.IEnvironment;
import cpw.mods.modlauncher.api.ITransformationService;
import cpw.mods.modlauncher.api.ITransformer;
import cpw.mods.modlauncher.api.IncompatibleEnvironmentException;
import org.jetbrains.annotations.NotNull;

import java.lang.reflect.Field;
import java.util.List;
import java.util.Set;

/**
 * @author USS_Shenzhou
 * @deprecated Wait for FML guys to develop a improved java-based one. Before that, use {@link sun.misc.Unsafe#objectFieldOffset(Field)} instead.
 */
@Deprecated
public class T88TransformationService implements ITransformationService {
    @Override
    public @NotNull String name() {
        return "T88TransformationService";
    }

    @Override
    public void initialize(IEnvironment environment) {

    }

    @Override
    public void onLoad(IEnvironment env, Set<String> otherServices) throws IncompatibleEnvironmentException {

    }

    @Override
    public @NotNull List<? extends ITransformer<?>> transformers() {
        return List.of();
    }
}
