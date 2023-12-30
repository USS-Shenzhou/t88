package cn.ussshenzhou.t88.network.annotation;

import net.neoforged.neoforge.network.NetworkEvent;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * Mark a method as a handler of a packet.
 * <p>Marked method should take a Supplier of {@link NetworkEvent.Context} as parameter.
 * @author USS_Shenzhou
 */
@Target(ElementType.METHOD)
@Retention(RetentionPolicy.SOURCE)
public @interface Consumer {
}
