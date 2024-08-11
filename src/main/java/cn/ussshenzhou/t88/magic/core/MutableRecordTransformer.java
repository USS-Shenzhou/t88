package cn.ussshenzhou.t88.magic.core;

import cn.ussshenzhou.t88.magic.MutableDataComponent;
import cn.ussshenzhou.t88.magic.MutableRecord;
import com.mojang.logging.LogUtils;
import cpw.mods.modlauncher.api.ITransformer;
import cpw.mods.modlauncher.api.ITransformerVotingContext;
import cpw.mods.modlauncher.api.TargetType;
import cpw.mods.modlauncher.api.TransformerVoteResult;
import net.neoforged.fml.loading.FMLLoader;
import net.neoforged.fml.loading.moddiscovery.ModFile;
import net.neoforged.fml.loading.moddiscovery.ModFileInfo;
import org.jetbrains.annotations.NotNull;
import org.objectweb.asm.Opcodes;
import org.objectweb.asm.Type;
import org.objectweb.asm.tree.ClassNode;

import java.lang.annotation.ElementType;
import java.lang.reflect.Field;
import java.util.Set;
import java.util.stream.Collectors;
import java.util.stream.Stream;

/**
 * @author USS_Shenzhou
 * @deprecated Wait for FML guys to develop a improved java-based one. Before that, use {@link sun.misc.Unsafe#objectFieldOffset(Field)} instead.
 */
@Deprecated
public class MutableRecordTransformer implements ITransformer<ClassNode> {
    @Override
    public @NotNull ClassNode transform(ClassNode input, ITransformerVotingContext context) {
        input.fields.forEach(field -> {
            if ((field.access & Opcodes.ACC_FINAL) != 0) {
                field.access &= ~Opcodes.ACC_FINAL;
            }
        });
        return input;
    }

    @Override
    public @NotNull TransformerVoteResult castVote(ITransformerVotingContext context) {
        return TransformerVoteResult.YES;
    }

    @SuppressWarnings("UnstableApiUsage")
    @Override
    public @NotNull Set<Target<ClassNode>> targets() {
        var modFiles = FMLLoader.getLoadingModList().getModFiles();
        var mutableDataComponentType = Type.getType(MutableDataComponent.class);
        return Stream.concat(
                modFiles.stream()
                        .map(ModFileInfo::getFile)
                        .map(ModFile::getScanResult)
                        .flatMap(modFileScanData -> modFileScanData.getAnnotatedBy(MutableRecord.class, ElementType.TYPE))
                        .map(annotationData -> Target.targetClass(annotationData.clazz().getClassName())),
                modFiles.stream()
                        .map(ModFileInfo::getFile)
                        .map(ModFile::getScanResult)
                        .flatMap(modFileScanData -> modFileScanData.getClasses().stream())
                        .filter(classData -> classData.interfaces().contains(mutableDataComponentType))
                        .map(classData -> Target.targetClass(classData.clazz().getClassName()))
        ).collect(Collectors.toSet());
    }

    @Override
    public @NotNull TargetType<ClassNode> getTargetType() {
        return TargetType.CLASS;
    }
}
