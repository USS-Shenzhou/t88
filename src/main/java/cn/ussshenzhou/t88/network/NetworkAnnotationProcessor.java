package cn.ussshenzhou.t88.network;

import cn.ussshenzhou.t88.network.annotation.*;

import javax.annotation.Nullable;
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
import java.io.*;
import java.lang.annotation.Annotation;
import java.util.*;

/**
 * @author USS_Shenzhou
 */
@SupportedAnnotationTypes({"cn.ussshenzhou.t88.network.annotation.NetPacket", "cn.ussshenzhou.t88.network.annotation.Encoder",
        "cn.ussshenzhou.t88.network.annotation.Decoder", "cn.ussshenzhou.t88.network.annotation.ClientHandler", "cn.ussshenzhou.t88.network.annotation.ServerHandler"})
@SupportedSourceVersion(SourceVersion.RELEASE_17)
public class NetworkAnnotationProcessor extends AbstractProcessor {
    private static final String PROXY_CLASS_SUFFIX = "$Generated";
    private static final String GENERATED_PACKAGE_SUFFIX = ".generated";

    @Override
    public boolean process(Set<? extends TypeElement> annotations, RoundEnvironment roundEnv) {
        Set<? extends Element> sources = roundEnv.getElementsAnnotatedWith(NetPacket.class);
        if (sources.isEmpty()) {
            return true;
        }
        implementCustomPacketPayload(sources);
        generateRegistry(sources);
        return true;
    }

    private void implementCustomPacketPayload(Set<? extends Element> sources) {
        sources.forEach(element -> {
            try {
                TypeElement originalClass = (TypeElement) element;
                if (originalClass.getKind() == ElementKind.RECORD) {
                    implementForRecord(originalClass);
                } else if (originalClass.getKind() == ElementKind.CLASS) {
                    implementForTraditionalClass(originalClass);
                }
            } catch (IOException e) {
                processingEnv.getMessager().printMessage(Diagnostic.Kind.ERROR, "T88 #implementCustomPacketPayload Error:");
                processingEnv.getMessager().printMessage(Diagnostic.Kind.ERROR, e.getMessage());
            }
        });
    }

    private void implementForTraditionalClass(TypeElement originalClass) throws IOException {
        var originalClassName = originalClass.getSimpleName().toString();
        var packageName = processingEnv.getElementUtils().getPackageOf(originalClass).getQualifiedName().toString();
        JavaFileObject proxyClass = processingEnv.getFiler().createSourceFile("t88.packets." + originalClassName + PROXY_CLASS_SUFFIX, originalClass);
        NetPacket netPacket = originalClass.getAnnotation(NetPacket.class);
        String encoderName = getNamedOf(originalClass, Encoder.class);
        if (encoderName == null) {
            processingEnv.getMessager().printMessage(Diagnostic.Kind.ERROR,
                    "Failed to get valid @Encoder method name. @NetPacket class = " + originalClass
                            + ", @Encoder = " + encoderName
            );
            return;
        }
        PrintWriter registryWriter = new PrintWriter(proxyClass.openWriter());
        var proxyClassCode = String.format("""
                        // Automatically generated by cn.ussshenzhou.t88.network.NetworkAnnotationProcessor
                        package %1$s;
                                                
                        public class %2$s extends %3$s.%4$s implements net.minecraft.network.protocol.common.custom.CustomPacketPayload {
                                                
                            public static final net.minecraft.resources.ResourceLocation ID = net.minecraft.resources.ResourceLocation.fromNamespaceAndPath("%5$s", "%6$s");
                            public static final net.minecraft.network.protocol.common.custom.CustomPacketPayload.Type<%2$s> TYPE = new net.minecraft.network.protocol.common.custom.CustomPacketPayload.Type<>(ID);
                                                
                            public %2$s(net.minecraft.network.FriendlyByteBuf buf) {
                                super(buf);
                            }
                                                
                            @Override
                            public net.minecraft.network.protocol.common.custom.CustomPacketPayload.Type<? extends net.minecraft.network.protocol.common.custom.CustomPacketPayload> type() {
                                return TYPE;
                            }
                                                
                            public void write(net.minecraft.network.FriendlyByteBuf buf) {
                                super.%7$s(buf);
                            }
                                                
                        }
                        """,
                packageName + GENERATED_PACKAGE_SUFFIX,
                originalClassName + PROXY_CLASS_SUFFIX,
                packageName,
                originalClassName,
                netPacket.modid(),
                originalClassName.toLowerCase(),
                encoderName
        );
        registryWriter.println(proxyClassCode);
        registryWriter.close();
    }

    private void implementForRecord(TypeElement originalClass) throws IOException {
        var originalClassName = originalClass.getSimpleName().toString();
        var packageName = processingEnv.getElementUtils().getPackageOf(originalClass).getQualifiedName().toString();
        JavaFileObject proxyClass = processingEnv.getFiler().createSourceFile("t88.packets." + originalClassName + PROXY_CLASS_SUFFIX, originalClass);
        JavaFileObject originalRecord = processingEnv.getElementUtils().getFileObjectOf(originalClass);
        NetPacket netPacket = originalClass.getAnnotation(NetPacket.class);
        PrintWriter registryWriter = new PrintWriter(proxyClass.openWriter());
        StringBuilder originalRecordContent = new StringBuilder("""
                // Automatically generated by cn.ussshenzhou.t88.network.NetworkAnnotationProcessor
                """);
        try (InputStream inputStream = originalRecord.openInputStream();
             BufferedReader reader = new BufferedReader(new InputStreamReader(inputStream))) {
            String line;
            while ((line = reader.readLine()) != null) {
                if (line.startsWith("@NetPacket")){
                    continue;
                }
                originalRecordContent.append(line).append("\n");
            }
        }
        StringBuilder proxyRecordContent = new StringBuilder(originalRecordContent.toString()
                .replaceFirst(packageName, packageName + GENERATED_PACKAGE_SUFFIX)
                .replace(originalClassName, originalClassName + PROXY_CLASS_SUFFIX)
                .replace("@Codec","")
                .replace("@ClientHandler","")
                .replace("@ServerHandler","")
        );
        if (originalClass.getInterfaces().isEmpty()) {
            proxyRecordContent.insert(proxyRecordContent.indexOf(")") + 1, " implements net.minecraft.network.protocol.common.custom.CustomPacketPayload");
        } else {
            proxyRecordContent.insert(proxyRecordContent.indexOf("implements") + 10, " net.minecraft.network.protocol.common.custom.CustomPacketPayload,");
        }
        proxyRecordContent.insert(proxyRecordContent.lastIndexOf("}") - 1, String.format("""
                                                
                                                
                            public static final net.minecraft.resources.ResourceLocation ID = net.minecraft.resources.ResourceLocation.fromNamespaceAndPath("%1$s", "%2$s");
                            public static final net.minecraft.network.protocol.common.custom.CustomPacketPayload.Type<%3$s> TYPE = new net.minecraft.network.protocol.common.custom.CustomPacketPayload.Type<>(ID);
                                                
                            @Override
                            public net.minecraft.network.protocol.common.custom.CustomPacketPayload.Type<? extends net.minecraft.network.protocol.common.custom.CustomPacketPayload> type() {
                                return TYPE;
                            }
                        """,
                netPacket.modid(),
                originalClassName.toLowerCase(),
                originalClassName + PROXY_CLASS_SUFFIX
        ));
        registryWriter.println(proxyRecordContent);
        registryWriter.close();
    }

    private void generateRegistry(Set<? extends Element> sources) {
        try {
            TypeElement anySourceClass = (TypeElement) sources.toArray()[0];
            String anySourceClassName = anySourceClass.getQualifiedName().toString();
            String thisPackageName = anySourceClassName.substring(0, anySourceClassName.lastIndexOf(".")) + GENERATED_PACKAGE_SUFFIX;
            JavaFileObject registry = processingEnv.getFiler().createSourceFile("t88.ModNetworkRegistry", anySourceClass);
            PrintWriter registryWriter = new PrintWriter(registry.openWriter());
            registryWriter.println(String.format("""
                    // Automatically generated by cn.ussshenzhou.t88.network.NetworkAnnotationProcessor
                    package %s;
                                        
                    import com.mojang.logging.LogUtils;
                    import net.minecraft.network.codec.StreamCodec;
                    import net.neoforged.bus.api.SubscribeEvent;
                    import net.neoforged.fml.common.EventBusSubscriber;
                    import net.neoforged.fml.common.Mod;
                    import net.neoforged.neoforge.network.event.RegisterPayloadHandlersEvent;
                    import net.neoforged.neoforge.network.handling.DirectionalPayloadHandler;
                    import net.neoforged.neoforge.network.registration.PayloadRegistrar;
                    """, thisPackageName));
            sources.forEach(element -> {
                TypeElement sourceClass = (TypeElement) element;
                var proxyClassName = sourceClass.getSimpleName().toString() + PROXY_CLASS_SUFFIX;
                registryWriter.println("import " + processingEnv.getElementUtils().getPackageOf(sourceClass).getQualifiedName().toString() + GENERATED_PACKAGE_SUFFIX + "." + proxyClassName + ";");
            });
            registryWriter.println("""
                                        
                    @EventBusSubscriber(bus = EventBusSubscriber.Bus.MOD)
                    public class ModNetworkRegistry {
                        @SubscribeEvent
                        public static void networkPacketRegistry(RegisterPayloadHandlersEvent event) {
                    """);
            Map<String, LinkedList<TypeElement>> packetGroupedByModId = new HashMap<>();
            sources.forEach(element -> {
                TypeElement sourceClass = (TypeElement) element;
                NetPacket netPacket = sourceClass.getAnnotation(NetPacket.class);
                packetGroupedByModId.compute(netPacket.modid(), (modId, elementList) -> Objects.requireNonNullElseGet(elementList, LinkedList::new)).add(sourceClass);
            });
            packetGroupedByModId.forEach((modId, elementList) -> {
                var registrarName = "registrar$" + modId;
                registryWriter.println(String.format("""
                                final PayloadRegistrar %s = event.registrar("%s");
                        """, registrarName, modId));
                elementList.forEach(sourceClass -> {
                    var sourceClassName = sourceClass.getSimpleName().toString();
                    var proxyClassName = sourceClassName + PROXY_CLASS_SUFFIX;
                    var packageName = processingEnv.getElementUtils().getPackageOf(sourceClass).getQualifiedName().toString();
                    String clientHandlerName = getNamedOf(sourceClass, ClientHandler.class);
                    String serverHandlerName = getNamedOf(sourceClass, ServerHandler.class);
                    if (sourceClass.getKind() == ElementKind.RECORD) {
                        generateForRecord(sourceClass, registryWriter, proxyClassName, clientHandlerName, serverHandlerName, registrarName, packageName, sourceClassName);
                    } else if (sourceClass.getKind() == ElementKind.CLASS) {
                        generateForTraditionalClass(sourceClass, registryWriter, proxyClassName, clientHandlerName, serverHandlerName, registrarName, packageName, sourceClassName);
                    }
                });
            });
            registryWriter.println("""
                        }
                    }
                    """);
            registryWriter.close();
        } catch (IOException ex) {
            processingEnv.getMessager().printMessage(Diagnostic.Kind.ERROR, "T88 #generateRegistry Error:");
            processingEnv.getMessager().printMessage(Diagnostic.Kind.ERROR, ex.getMessage());
        }
    }

    private void generateForRecord(TypeElement sourceClass, PrintWriter registryWriter, String proxyClassName, String clientHandlerName, String serverHandlerName, String registrarName, String packageName, String sourceClassName) {
        String codecName = getNamedOf(sourceClass, Codec.class);
        if (codecName == null) {
            processingEnv.getMessager().printMessage(Diagnostic.Kind.ERROR,
                    "Failed to get valid codec name. @NetPacket class = " + sourceClassName
                            + ", @Codec = " + codecName
            );
            return;
        }
        registryWriter.println(String.format("""
                                %4$s.playBidirectional(%1$s.TYPE, %1$s.%5$s, new DirectionalPayloadHandler<>(
                                        (payload, context) -> %2$s,
                                        (payload, context) -> %3$s
                                ));
                                try {
                                    cn.ussshenzhou.t88.network.NetworkHelper.register(Class.forName("%6$s"), Class.forName("%7$s"));
                                } catch (ClassNotFoundException e) {
                                    LogUtils.getLogger().error("T88 failed to register < %6$s, %7$s >");
                                    throw new RuntimeException(e);
                                }
                        """,
                proxyClassName,
                clientHandlerName == null ? "{}" : String.format("payload.%s(context)", clientHandlerName),
                serverHandlerName == null ? "{}" : String.format("payload.%s(context)", serverHandlerName),
                registrarName,
                codecName,
                packageName + "." + sourceClassName,
                packageName + GENERATED_PACKAGE_SUFFIX + "." + proxyClassName
        ));
    }

    private void generateForTraditionalClass(TypeElement sourceClass, PrintWriter registryWriter, String proxyClassName, String clientHandlerName, String serverHandlerName, String registrarName, String packageName, String sourceClassName) {
        String decoderName = getNamedOf(sourceClass, Decoder.class);
        if (decoderName == null) {
            processingEnv.getMessager().printMessage(Diagnostic.Kind.ERROR,
                    "Failed to get valid decoder method name. @NetPacket class = " + sourceClassName
                            + ", @Decoder = " + decoderName
            );
            return;
        }
        if ("<init>".equals(decoderName)) {
            decoderName = "new";
        }
        registryWriter.println(String.format("""
                                %4$s.playBidirectional(%1$s.TYPE, StreamCodec.ofMember(%1$s::write,%1$s::%5$s), new DirectionalPayloadHandler<>(
                                        (payload, context) -> %2$s,
                                        (payload, context) -> %3$s
                                ));
                                try {
                                    cn.ussshenzhou.t88.network.NetworkHelper.register(Class.forName("%6$s"), Class.forName("%7$s"));
                                } catch (ClassNotFoundException e) {
                                    LogUtils.getLogger().error("T88 failed to register < %6$s, %7$s >");
                                    throw new RuntimeException(e);
                                }
                        """,
                proxyClassName,
                clientHandlerName == null ? "{}" : String.format("payload.%s(context)", clientHandlerName),
                serverHandlerName == null ? "{}" : String.format("payload.%s(context)", serverHandlerName),
                registrarName,
                decoderName,
                packageName + "." + sourceClassName,
                packageName + GENERATED_PACKAGE_SUFFIX + "." + proxyClassName
        ));
    }

    private @Nullable String getNamedOf(TypeElement clazz, Class<? extends Annotation> annotation) {
        for (Element e : clazz.getEnclosedElements()) {
            if (e.getAnnotation(annotation) != null) {
                return e.getSimpleName().toString();
            }
        }
        return null;
    }

}
