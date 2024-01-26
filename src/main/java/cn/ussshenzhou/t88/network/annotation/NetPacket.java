package cn.ussshenzhou.t88.network.annotation;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * Annotate to generate registrations for a custom play-stage network packet/payload during RegisterPayloadHandlerEvent.
 * <p>Annotated class must have a proper constructor (for you to create and send the packet),
 * a {@link Decoder} (constructor) method, a {@link Encoder} method, a {@link ClientHandler} and a {@link ServerHandler} method.
 * @author USS_Shenzhou
 */
@Target(ElementType.TYPE)
@Retention(RetentionPolicy.SOURCE)
public @interface NetPacket {
    String modid() default "t88";
}