package cn.ussshenzhou.t88.gui.advanced;

import cn.ussshenzhou.t88.gui.container.TScrollContainer;
import cn.ussshenzhou.t88.gui.util.HorizontalAlignment;
import cn.ussshenzhou.t88.gui.util.LayoutHelper;
import cn.ussshenzhou.t88.gui.widegt.*;
import net.minecraft.network.chat.Component;
import net.minecraft.util.Tuple;
import org.joml.Vector2i;

import javax.annotation.Nullable;
import java.util.List;
import java.util.function.*;

/**
 * @author USS_Shenzhou
 */
public class TOptionsPanel extends TPanel {
    private final OptionContainer container;
    protected int gapBetweenOptions = 4;
    protected HorizontalAlignment titleHorizontalAlignment = HorizontalAlignment.RIGHT;

    public TOptionsPanel() {
        this.container = new OptionContainer();
        this.add(container);
    }

    public <T extends TWidget> Tuple<TOptionsPanel, T> addOption(Component title, T controller) {
        var option = new HorizontalTitledOption<>(title, controller);
        container.add(option);
        return new Tuple<>(this, controller);
    }

    public Tuple<TOptionsPanel, TSlider> addOptionSliderDouble(Component title, double minValue, double maxValue, @Nullable Component tipText, BiConsumer<TSlider, Double> setter) {
        var slider = new TSlider("", minValue, maxValue, true, tipText);
        slider.addResponder(d -> setter.accept(slider, d));
        return addOption(title, slider);
    }

    public Tuple<TOptionsPanel, TSlider> addOptionSliderDouble(Component title, double minValue, double maxValue, BiFunction<Component, Double, Component> textFromCaptionAndValue, @Nullable Component tipText, BiConsumer<TSlider, Double> setter) {
        var slider = new TSlider("", minValue, maxValue, textFromCaptionAndValue, tipText);
        slider.addResponder(d -> setter.accept(slider, d));
        return addOption(title, slider);
    }

    public Tuple<TOptionsPanel, TSlider> addOptionSliderDoubleInit(Component title, double minValue, double maxValue, @Nullable Component tipText, BiConsumer<TSlider, Double> setter, double absValue) {
        var r = addOptionSliderDouble(title, minValue, maxValue, tipText, setter);
        r.getB().setAbsValue(absValue);
        return r;
    }

    public Tuple<TOptionsPanel, TSlider> addOptionSliderDoubleInit(Component title, double minValue, double maxValue, BiFunction<Component, Double, Component> textFromCaptionAndValue, @Nullable Component tipText, BiConsumer<TSlider, Double> setter, double absValue) {
        var r = addOptionSliderDouble(title, minValue, maxValue, textFromCaptionAndValue, tipText, setter);
        r.getB().setAbsValue(absValue);
        return r;
    }

    public <E> Tuple<TOptionsPanel, TCycleButton<E>> addOptionCycleButton(Component title, List<E> entryTitles, List<Consumer<TCycleButton<E>>> entrySetters) {
        if (entryTitles.size() != entrySetters.size()) {
            throw new IllegalArgumentException(String.format("Collection %s with size %d must be the same size as collection %s with size %d.", entryTitles, entryTitles.size(), entrySetters, entrySetters.size()));
        }
        var option = new HorizontalTitledOption<>(title, new TCycleButton<E>());
        for (int i = 0; i < entryTitles.size(); i++) {
            option.controller.addElement(entryTitles.get(i), entrySetters.get(i));
        }
        container.add(option);
        return new Tuple<>(this, option.controller);
    }

    public <E> Tuple<TOptionsPanel, TCycleButton<E>> addOptionCycleButton(Component title, List<E> entryTitles, Function<E, Consumer<TCycleButton<E>>> entrySetterGenerator) {
        return addOptionCycleButton(title, entryTitles, entryTitles.stream().map(entrySetterGenerator).toList());
    }

    public <E> Tuple<TOptionsPanel, TCycleButton<E>> addOptionCycleButtonInit(Component title, List<E> entryTitles, List<Consumer<TCycleButton<E>>> entrySetters, Predicate<TCycleButton<E>.Entry> predicate) {
        var r = addOptionCycleButton(title, entryTitles, entrySetters);
        r.getB().select(predicate);
        return r;
    }

    public <E> Tuple<TOptionsPanel, TCycleButton<E>> addOptionCycleButtonInit(Component title, List<E> entryTitles, Function<E, Consumer<TCycleButton<E>>> entrySetterGenerator, Predicate<TCycleButton<E>.Entry> predicate) {
        var r = addOptionCycleButton(title, entryTitles, entrySetterGenerator);
        r.getB().select(predicate);
        return r;
    }

    public Tuple<TOptionsPanel, OptionSplitter> addOptionSplitter(Component title) {
        var splitter = new OptionSplitter(title);
        container.add(splitter);
        return new Tuple<>(this, splitter);
    }

    public void setGapBetweenOptions(int gapBetweenOptions) {
        this.gapBetweenOptions = gapBetweenOptions;
        this.getParentLazy().layout();
    }

    public void setTitleHorizontalAlignment(HorizontalAlignment horizontalAlignment) {
        this.titleHorizontalAlignment = horizontalAlignment;
        container.getChildren().stream()
                .filter(t -> t instanceof HorizontalTitledOption)
                .forEach(t -> ((HorizontalTitledOption<?>) t).title.setHorizontalAlignment(titleHorizontalAlignment));
        container.getChildren().stream()
                .filter(t -> t instanceof OptionSplitter)
                .forEach(t -> ((OptionSplitter) t).title.setHorizontalAlignment(titleHorizontalAlignment == HorizontalAlignment.CENTER ? HorizontalAlignment.CENTER : HorizontalAlignment.LEFT));
    }

    @Override
    public void layout() {
        container.setBounds(0, 0, width, height);
        super.layout();
    }

    private static class OptionContainer extends TScrollContainer {
        public OptionContainer() {
        }

        @Override
        public void layout() {
            int i = 0;
            int gapBetweenOptions = ((TOptionsPanel) this.getParentLazy()).gapBetweenOptions;
            for (TWidget tWidget : this.children) {
                if (i == 0) {
                    tWidget.setBounds(gapBetweenOptions, gapBetweenOptions, getUsableWidth() - 2 * gapBetweenOptions, tWidget.getPreferredSize().y);
                } else {
                    LayoutHelper.BBottomOfA(tWidget, gapBetweenOptions, this.children.get(i - 1));
                }
                i++;
            }
            super.layout();
        }
    }

    private static class HorizontalTitledOption<T extends TWidget> extends TPanel {
        protected final TLabel title;
        protected final T controller;

        public HorizontalTitledOption(Component title, T controller) {
            super();
            this.title = new TLabel(title);
            this.add(this.title);
            this.controller = controller;
            this.add(controller);
            this.title.setHorizontalAlignment(cn.ussshenzhou.t88.gui.util.HorizontalAlignment.RIGHT)
                    .setAutoScroll(false);
        }

        @Override
        public void layout() {
            int gapBetweenOptions = ((TOptionsPanel) this.getParentLazy().getParentLazy()).gapBetweenOptions;
            title.setBounds(0, 0, width / 2 - gapBetweenOptions, height);
            controller.setBounds(width / 2 + gapBetweenOptions, 0, width / 2 - gapBetweenOptions * 2, height);
            super.layout();
        }

        @Override
        public Vector2i getPreferredSize() {
            int actionY = controller.getPreferredSize().y;
            return new Vector2i(this.width, Math.max(actionY, 20));
        }

        public TLabel getTitle() {
            return title;
        }

        public T getController() {
            return controller;
        }
    }

    public static class OptionSplitter extends HorizontalTitledOption<TPanel> {
        public OptionSplitter(Component title) {
            super(title, new TPanel());
            this.title.setHorizontalAlignment(HorizontalAlignment.LEFT);
            this.setBackground(0x20ffffff);
        }
    }
}
