package cn.ussshenzhou.t88.gui.widegt;

import cn.ussshenzhou.t88.gui.event.TWidgetContentUpdatedEvent;
import cn.ussshenzhou.t88.gui.util.ITranslatable;
import net.minecraft.client.Minecraft;
import net.minecraft.locale.Language;
import net.minecraft.network.chat.Component;
import net.minecraft.util.Mth;
import net.minecraftforge.common.MinecraftForge;

import javax.annotation.Nullable;
import java.util.Collection;
import java.util.LinkedList;
import java.util.function.Consumer;
import java.util.function.Predicate;

/**
 * @author USS_Shenzhou
 */
public class TCycleButton<E> extends TButton {
    LinkedList<Entry> values = new LinkedList<>();
    int cycleIndex = 0;

    public TCycleButton() {
        super(Component.empty());
        this.setOnPress(pButton -> cycleOnce(1));
    }

    public TCycleButton(Collection<Entry> entries) {
        this();
        entries.forEach(this::addElement);
    }

    public void addElement(E e) {
        addElement(new Entry(e));
    }

    public void addElement(E e, Consumer<TCycleButton<E>> consumer) {
        addElement(new Entry(e, consumer));
    }

    public void addElement(Entry e) {
        int index = values.indexOf(e);
        if (index >= 0) {
            this.values.remove(index);
        }
        values.add(e);
        if ("".equals(getMessage().getString())) {
            this.setMessage(e.getNarration());
        }
    }

    public void removeElement(Entry e) {
        this.values.remove(e);
    }

    public void removeElement(E e) {
        //this.values.removeIf(entry -> entry.content.toString().equals(e.toString()));
        this.removeElement(new Entry(e));
    }

    public void removeElement(Predicate<Entry> predicate) {
        this.values.stream().filter(predicate).findFirst().ifPresent(this::removeElement);
    }

    public int getSelectedIndex() {
        return cycleIndex;
    }

    public LinkedList<Entry> getValues() {
        return values;
    }

    public @Nullable Entry getSelected() {
        if (values.isEmpty()) {
            return null;
        }
        return values.get(cycleIndex);
    }

    public void select(int index) throws IndexOutOfBoundsException {
        //if (index > 0 && index <= values.size() - 1) {
        index = Mth.clamp(index, 0, values.size() - 1);
        this.cycleIndex = index;
        this.setMessage(values.get(cycleIndex).getNarration());
        Consumer<TCycleButton<E>> c = values.get(cycleIndex).onSwitched;
        if (c != null) {
            c.accept(this);
        }
        MinecraftForge.EVENT_BUS.post(new TWidgetContentUpdatedEvent(this));
    }

    public void select(Entry entry) {
        select(values.indexOf(entry));
    }

    public void select(E content) {
        select(values.indexOf(new Entry(content)));
    }

    public void select(Predicate<Entry> predicate) {
        for (int i = 0; i < values.size(); i++) {
            if (predicate.test(values.get(i))) {
                select(i);
                return;
            }
        }
    }

    @Override
    public boolean mouseClicked(double pMouseX, double pMouseY, int pButton) {
        if (isInRange(pMouseX, pMouseY)) {
            if (pButton == 1) {
                this.playDownSound(Minecraft.getInstance().getSoundManager());
                cycleOnce(-1);
                return true;
            }
        }
        return super.mouseClicked(pMouseX, pMouseY, pButton);
    }

    protected void cycleOnce(int i) {
        if (!values.isEmpty()) {
            this.cycleIndex = cycleIndex + i;
            if (cycleIndex < 0) {
                cycleIndex = values.size() - 1;
            } else if (cycleIndex > values.size() - 1) {
                cycleIndex = 0;
            }
            this.select(cycleIndex);
        } else {
            this.cycleIndex = 0;
            this.setMessage(Component.empty());
            MinecraftForge.EVENT_BUS.post(new TWidgetContentUpdatedEvent(this));
        }
    }

    public class Entry {
        E content;
        Consumer<TCycleButton<E>> onSwitched;

        public Entry(E content) {
            this.content = content;
            this.onSwitched = null;
        }

        public Entry(E content, Consumer<TCycleButton<E>> onSwitched) {
            this.content = content;
            this.onSwitched = onSwitched;
        }

        public Component getNarration() {
            Language language = Language.getInstance();
            String s;
            if (content instanceof ITranslatable translatable) {
                s = translatable.translateKey();
            } else {
                s = content.toString();
            }
            if (language.has(s)) {
                return Component.translatable(s);
            } else {
                return Component.literal(s);
            }
        }

        @Override
        public boolean equals(Object obj) {
            if (obj == this) {
                return true;
            }
            if (obj instanceof TCycleButton<?>.Entry entry) {
                return entry.content.toString().equals(this.content.toString());
            }
            return false;
        }

        public E getContent() {
            return content;
        }

        public void setContent(E content) {
            this.content = content;
        }

        public Consumer<TCycleButton<E>> getOnSwitched() {
            return onSwitched;
        }

        public void setOnSwitched(Consumer<TCycleButton<E>> onSwitched) {
            this.onSwitched = onSwitched;
        }
    }
}
