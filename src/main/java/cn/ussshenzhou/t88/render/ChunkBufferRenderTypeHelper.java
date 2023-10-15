package cn.ussshenzhou.t88.render;

import com.mojang.logging.LogUtils;
import net.minecraft.client.renderer.RenderType;
import net.minecraftforge.client.event.RenderLevelStageEvent;
import net.minecraftforge.fml.ModList;
import net.minecraftforge.fml.javafmlmod.FMLModContainer;
import net.minecraftforge.forgespi.language.ModFileScanData;
import org.jetbrains.annotations.ApiStatus;
import org.objectweb.asm.Type;

import java.lang.reflect.Field;
import java.lang.reflect.Modifier;
import java.util.Comparator;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.PriorityQueue;

/**
 * @author USS_Shenzhou
 */
@ApiStatus.Internal
public class ChunkBufferRenderTypeHelper {
    private static final Type RENDER_TYPE = Type.getType(ChunkBufferRenderType.class);

    public static HashMap<RenderLevelStageEvent.Stage, PriorityQueue<RenderTypeWithPriority>> renderTypes = new HashMap<>();

    public static LinkedList<RenderType> scan() {
        LinkedList<RenderType> types = new LinkedList<>();
        if (ModList.get() == null) {
            LogUtils.getLogger().error("Who loaded RenderType early again?");
            LogUtils.getLogger().error("Just let the game crash. Report to mod's author if you can find the caller method from following stacktrace. Or you can report to USS_Shenzhou.");
        }
        ModList.get().forEachModInOrder(modContainer -> {
            if (modContainer instanceof FMLModContainer mod) {
                ModFileScanData scanResults;
                Class<?> modClass;
                try {
                    var s = FMLModContainer.class.getDeclaredField("scanResults");
                    s.setAccessible(true);
                    scanResults = (ModFileScanData) s.get(mod);
                    var m = FMLModContainer.class.getDeclaredField("modClass");
                    m.setAccessible(true);
                    modClass = (Class<?>) m.get(mod);
                } catch (IllegalAccessException | NoSuchFieldException e) {
                    LogUtils.getLogger().error("Failed to get scanResults.");
                    LogUtils.getLogger().error(e.getMessage());
                    return;
                }
                scanResults.getAnnotations().forEach(annotationData -> {
                    handleType(modClass, annotationData, types);
                });
            }
        });
        return types;
    }

    private static void handleType(Class<?> modClass, ModFileScanData.AnnotationData annotationData, LinkedList<RenderType> types) {
        if (annotationData.annotationType().equals(RENDER_TYPE)) {
            try {
                Class<?> c = Class.forName(annotationData.clazz().getClassName(), true, modClass.getClassLoader());
                Field f = c.getDeclaredField(annotationData.memberName());
                if (!Modifier.isStatic(f.getModifiers())) {
                    LogUtils.getLogger().error("Field {} with @ChunkBufferRenderType in class {} must be static.", annotationData.memberName(), annotationData.clazz().getClassName());
                    return;
                }
                f.setAccessible(true);
                Object o = f.get(null);
                if (o instanceof RenderType renderType) {
                    types.add(renderType);
                    ChunkBufferRenderType anno = f.getAnnotation(ChunkBufferRenderType.class);
                    renderTypes.computeIfAbsent(anno.value().get(), at -> new PriorityQueue<>(Comparator.comparingInt(r -> r.priority)))
                            .add(new RenderTypeWithPriority(anno.priority(), renderType));
                } else {
                    LogUtils.getLogger().error("Field {} with @ChunkBufferRenderType in class {} must extend RenderType.", annotationData.memberName(), annotationData.clazz().getClassName());
                }
            } catch (ClassNotFoundException | NoSuchFieldException | IllegalAccessException e) {
                LogUtils.getLogger().error("Failed to handle annotationData {}", annotationData);
                LogUtils.getLogger().error(e.getMessage());
            }
        }
    }

    public static class RenderTypeWithPriority {
        public final int priority;
        public final RenderType renderType;

        private RenderTypeWithPriority(int priority, RenderType renderType) {
            this.priority = priority;
            this.renderType = renderType;
        }
    }
}
