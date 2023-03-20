package cn.ussshenzhou.t88.network;

import cn.ussshenzhou.t88.network.annotation.Consumer;
import cn.ussshenzhou.t88.network.annotation.Decoder;
import cn.ussshenzhou.t88.network.annotation.Encoder;
import cn.ussshenzhou.t88.network.annotation.NetPacket;

import javax.annotation.processing.AbstractProcessor;
import javax.annotation.processing.RoundEnvironment;
import javax.annotation.processing.SupportedAnnotationTypes;
import javax.annotation.processing.SupportedSourceVersion;
import javax.lang.model.SourceVersion;
import javax.lang.model.element.Element;
import javax.lang.model.element.ElementKind;
import javax.lang.model.element.TypeElement;
import javax.tools.Diagnostic;
import javax.tools.JavaFileObject;
import java.io.IOException;
import java.io.PrintWriter;
import java.util.Set;

/**
 * @author USS_Shenzhou
 */
@SupportedAnnotationTypes({"cn.ussshenzhou.t88.network.annotation.NetPacket", "cn.ussshenzhou.t88.network.annotation.Encoder", "cn.ussshenzhou.t88.network.annotation.Decoder", "cn.ussshenzhou.t88.network.annotation.Consumer",})
@SupportedSourceVersion(SourceVersion.RELEASE_17)
public class NetworkAnnotationProcessor extends AbstractProcessor {
    @Override
    public boolean process(Set<? extends TypeElement> annotations, RoundEnvironment roundEnv) {
        Set<? extends Element> sources = roundEnv.getElementsAnnotatedWith(NetPacket.class);
        if (sources.isEmpty()) {
            return true;
        }
        try {
            TypeElement anySourceClass = (TypeElement) sources.toArray()[0];
            String anySourceClassName = anySourceClass.getQualifiedName().toString();
            String packageName = anySourceClassName.substring(0, anySourceClassName.lastIndexOf(".")) + ".generated";
            JavaFileObject registry = processingEnv.getFiler().createSourceFile("t88.ModNetworkRegistry", anySourceClass);
            PrintWriter registryWriter = new PrintWriter(registry.openWriter());
            registryWriter.println(String.format("""
                    // Automatically generated by cn.ussshenzhou.t88.network.NetworkAnnotationProcessor
                    package %s;
                        
                    import net.minecraft.resources.ResourceLocation;
                    import net.minecraftforge.eventbus.api.SubscribeEvent;
                    import net.minecraftforge.fml.common.Mod;
                    import net.minecraftforge.fml.event.lifecycle.FMLCommonSetupEvent;
                    import net.minecraftforge.network.NetworkRegistry;
                    import net.minecraftforge.network.simple.SimpleChannel;
                    import cn.ussshenzhou.t88.network.PacketProxy;
                    """, packageName));
            for (Element e : sources) {
                TypeElement sourceClass = (TypeElement) e;
                registryWriter.println("import " + sourceClass.getQualifiedName() + ";");
            }
            registryWriter.println("""
                                        
                    @Mod.EventBusSubscriber(bus = Mod.EventBusSubscriber.Bus.MOD)
                    public class ModNetworkRegistry {
                        @SubscribeEvent
                        public static void onCommonSetup(FMLCommonSetupEvent event) {
                            event.enqueueWork(() -> {
                    """);

            for (Element e : sources) {
                TypeElement sourceClass = (TypeElement) e;
                String sourceClassName = sourceClass.getSimpleName().toString();
                NetPacket netPacket = sourceClass.getAnnotation(NetPacket.class);
                registryWriter.println(String.format("""
                                            SimpleChannel channel%1$s = NetworkRegistry.newSimpleChannel(
                                                    new ResourceLocation("%2$s"),
                                                    () -> "%3$s",
                                                    (version) -> version.equals("%3$s"),
                                                    (version) -> version.equals("%3$s")
                                            );
                                """,
                        sourceClassName,
                        PacketProxy.classNameToResLocName(sourceClassName) + "_t88_generated",
                        netPacket.version()
                ));
                String encoderMethod = null;
                String decoderMethod = null;
                String consumerMethod = null;
                for (Element element : sourceClass.getEnclosedElements()) {
                    if (element.getAnnotation(Encoder.class) != null) {
                        encoderMethod = element.getSimpleName().toString();
                    } else if (element.getAnnotation(Decoder.class) != null) {
                        if (element.getKind() == ElementKind.CONSTRUCTOR) {
                            decoderMethod = "new";
                        } else {
                            decoderMethod = element.getSimpleName().toString();
                        }
                    } else if (element.getAnnotation(Consumer.class) != null) {
                        consumerMethod = element.getSimpleName().toString();
                    }
                }
                if (encoderMethod == null || decoderMethod == null || consumerMethod == null) {
                    processingEnv.getMessager().printMessage(Diagnostic.Kind.ERROR,
                            "Failed to get valid method name. NetworkPacket class = " + sourceClassName
                                    + ", encoderMethod = " + encoderMethod
                                    + ", decoderMethod = " + decoderMethod
                                    + ", consumerMethod = " + consumerMethod
                    );
                }
                registryWriter.println(String.format("""
                                            channel%1$s.messageBuilder(%1$s.class, getId())
                                                    .encoder(%1$s::%2$s)
                                                    .decoder(%1$s::%3$s)
                                                    .consumerMainThread(%1$s::%4$s)
                                                    .add();
                                            PacketProxy.addChannel("%5$s", channel%1$s);
                                """,
                        sourceClassName,
                        encoderMethod,
                        decoderMethod,
                        consumerMethod,
                        PacketProxy.classNameToResLocName(sourceClassName)
                ));

            }
            registryWriter.println("""
                            });
                        }
                                                
                        static int id = 0;
                                                
                        public static int getId() {
                            return id++;
                        }
                    }""");
            registryWriter.close();
        } catch (IOException ex) {
            processingEnv.getMessager().printMessage(Diagnostic.Kind.ERROR, ex.getMessage());
        }
        return true;
    }

}
