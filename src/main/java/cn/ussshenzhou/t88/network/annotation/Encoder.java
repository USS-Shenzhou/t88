package cn.ussshenzhou.t88.network.annotation;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * Mark a method as a combiner of a byte buffer from a packet's fields to send.
 * <p>Marked method should take {@link net.minecraft.network.FriendlyByteBuf} as parameter.
 * @author USS_Shenzhou
 */
@Target(ElementType.METHOD)
@Retention(RetentionPolicy.SOURCE)
public @interface Encoder {
}
