package cn.ussshenzhou.t88.gui.widegt;

import java.util.function.Consumer;

/**
 * @author Tony Yu
 */
public interface TResponder<T> {
    void respond(T value);

    void addResponder(Consumer<T> responder);

    void clearResponders();
}
