package cn.ussshenzhou.t88.network.annotation;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * Mark a method as a builder of a packet from received byte buffer.
 * <p>Marked method should take {@link net.minecraft.network.FriendlyByteBuf} as parameter.
 * @author USS_Shenzhou
 */
@Target({ElementType.CONSTRUCTOR, ElementType.METHOD})
@Retention(RetentionPolicy.SOURCE)
public @interface Decoder {

}
