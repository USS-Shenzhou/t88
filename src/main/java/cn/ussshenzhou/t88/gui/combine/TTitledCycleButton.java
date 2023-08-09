package cn.ussshenzhou.t88.gui.combine;

import org.joml.Vector2i;
import cn.ussshenzhou.t88.gui.widegt.TButton;
import cn.ussshenzhou.t88.gui.widegt.TCycleButton;
import net.minecraft.network.chat.Component;

import java.util.Collection;
import java.util.function.Consumer;

/**
 * @author USS_Shenzhou
 */
public class TTitledCycleButton<E> extends TTitledComponent<TCycleButton<E>> {
    public TTitledCycleButton(Component titleText) {
        super(titleText, new TCycleButton<>());
    }

    public TTitledCycleButton(Component titleText, Collection<TCycleButton<E>.Entry> entries) {
        super(titleText, new TCycleButton<E>(entries));
    }

    @Override
    public Vector2i getPreferredSize() {
        return new Vector2i(
                TButton.RECOMMEND_SIZE.x,
                title.getHeight() + TButton.RECOMMEND_SIZE.y
        );
    }

    public void addElement(E e) {
        getComponent().addElement(e);
    }

    public void addElement(E e, Consumer<TCycleButton<E>> consumer) {
        getComponent().addElement(e, consumer);
    }

    public void addElement(TCycleButton<E>.Entry e) {
        getComponent().addElement(e);
    }

    public void removeElement(E e) {
        getComponent().removeElement(e);
    }


}
