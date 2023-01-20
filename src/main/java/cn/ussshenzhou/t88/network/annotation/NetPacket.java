package cn.ussshenzhou.t88.network.annotation;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * Annotate to generate a registration class for network registration during FMLCommonSetupEvent.
 *
 * <p>Annotated class should have a proper constructor (for you to create and send the packet),
 * a {@link Decoder} (constructor) method, a {@link Encoder} method, and a {@link Consumer} method.
 *
 * @author USS_Shenzhou
 */
@Target(ElementType.TYPE)
@Retention(RetentionPolicy.SOURCE)
public @interface NetPacket {
    String version() default "1.0";
}