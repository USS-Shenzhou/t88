package cn.ussshenzhou.t88.network.annotation;

import net.neoforged.neoforge.network.handling.PlayPayloadContext;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * Mark a method as a client-side handler of a packet.
 * <p>Marked method must take a {@link PlayPayloadContext} as parameter.
 * @author USS_Shenzhou
 */
@Target(ElementType.METHOD)
@Retention(RetentionPolicy.SOURCE)
public @interface ServerHandler {
}
