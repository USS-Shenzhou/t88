package cn.ussshenzhou.t88.gui.widegt;

import cn.ussshenzhou.t88.gui.screen.TScreen;
import cn.ussshenzhou.t88.gui.util.MouseHelper;
import cn.ussshenzhou.t88.gui.util.VanillaWidget2TComponentHelper;
import cn.ussshenzhou.t88.gui.util.Vec2i;
import com.mojang.serialization.Codec;
import net.minecraft.client.Minecraft;
import net.minecraft.client.OptionInstance;
import net.minecraft.client.Options;
import net.minecraft.client.gui.components.Tooltip;
import net.minecraft.client.gui.screens.Screen;
import net.minecraft.network.chat.Component;
import net.minecraft.util.Mth;

import javax.annotation.Nullable;
import java.util.LinkedList;
import java.util.Optional;
import java.util.function.BiFunction;
import java.util.function.Consumer;

/**
 * @author USS_Shenzhou
 */
public class TSlider extends OptionInstance.OptionInstanceSliderButton<Double> implements TWidget, TResponder<Double> {
    protected boolean visible = true;
    TComponent parent = null;
    TScreen parentScreen = null;
    double min, max;
    protected final LinkedList<Consumer<Double>> responders = new LinkedList<>();

    @Deprecated
    public TSlider(double minValue, double maxValue, float pSteps, Component tipText) {
        this(tipText.getString(), minValue, maxValue, false, null);
    }

    public TSlider(String title, double minValue, double maxValue) {
        this(title, minValue, maxValue, true, null);
    }

    public TSlider(String title, double minValue, double maxValue, boolean showValueInTitle, @Nullable Component tipText) {
        super(Minecraft.getInstance().options, 0, 0, 0, 0,
                new OptionInstance<>(title,
                        value -> null,
                        (pCaption, value) -> showValueInTitle ?
                                Options.genericValueLabel(pCaption, Component.literal(String.format("%.2f", value))) :
                                Component.literal(title),
                        new DoubleRange(minValue, maxValue),
                        maxValue,
                        d -> {
                        }
                ),
                new DoubleRange(minValue, maxValue),
                value -> tipText == null ? null : Tooltip.create(tipText),
                d -> {
                }
        );
        //AccessorProxy.SliderProxy.setOption(this, progressOption);
        this.updateMessage();
        this.min = minValue;
        this.max = maxValue;
    }

    public TSlider(String title, double minValue, double maxValue, BiFunction<Component, Double, Component> getLabelFromCaptionAndValue, @Nullable Component tipText) {
        super(Minecraft.getInstance().options, 0, 0, 0, 0,
                new OptionInstance<>(title,
                        value -> null,
                        getLabelFromCaptionAndValue::apply,
                        new DoubleRange(minValue, maxValue),
                        maxValue,
                        d -> {
                        }
                ),
                new DoubleRange(minValue, maxValue),
                value -> tipText == null ? null : Tooltip.create(tipText),
                d -> {
                }
        );
        //AccessorProxy.SliderProxy.setOption(this, progressOption);
        this.updateMessage();
        this.min = minValue;
        this.max = maxValue;
    }

    public double relToAbsValueLinear(double relativeValue) {
        return relativeValue * (max - min) + min;
    }

    public double absToRelValueLinear(double absoluteValue) {
        return (absoluteValue - min) / (max - min);
    }

    @Deprecated
    public void setValue(double value) {
        this.value = Mth.clamp(value, min, max);
        applyValue();
    }

    public void setRelValue(double relativeValue) {
        this.value = Mth.clamp(relativeValue, 0, 1);
        applyValue();
    }

    public void setAbsValue(double absoluteValue) {
        this.value = absToRelValueLinear(absoluteValue);
        applyValue();
    }

    @Deprecated
    public void setValueWithoutRespond(double value) {
        this.value = Mth.clamp(value, min, max);
        applyValue(false);
    }

    public void setRelValueWithoutRespond(double relativeValue) {
        this.value = Mth.clamp(relativeValue, 0, 1);
        this.updateMessage();
    }

    public void setAbsValueWithoutRespond(double absoluteValue) {
        this.value = absToRelValueLinear(absoluteValue);
        this.updateMessage();
    }

    @Deprecated
    public double getValue() {
        return value;
    }

    public double getRelValue() {
        return value;
    }

    public double getAbsValue() {
        return relToAbsValueLinear(value);
    }

    @Override
    protected void applyValue() {
        applyValue(true);
    }

    protected void applyValue(boolean respond) {
        if (respond) {
            respond(value);
        }
        super.applyValue();
        super.updateMessage();
    }

    @Deprecated
    public void addValue(double delta) {
        setValue(value + delta);
    }

    protected float factor0 = 0.2f;
    protected float factor1 = 0.1f;
    protected float factor2 = 0.05f;
    protected float factor3 = 0.01f;

    @Override
    public boolean mouseScrolled(double pMouseX, double pMouseY, double pDelta) {
        if (isInRange(pMouseX, pMouseY, 2, 2)) {
            if (Screen.hasControlDown()) {
                if (Screen.hasShiftDown()) {
                    //TODO
                }

            } else if (Screen.hasShiftDown()) {

            } else {

            }
        }
        return false;
    }

    @Override
    public boolean mouseDragged(double pMouseX, double pMouseY, int pButton, double pDragX, double pDragY) {
        if (isInRange(pMouseX, pMouseY, 2, 2)) {
            return super.mouseDragged(pMouseX, pMouseY, pButton, pDragX, pDragY);
        }
        return false;
    }

    @Override
    public boolean keyPressed(int pKeyCode, int pScanCode, int pModifiers) {
        if (isInRange(MouseHelper.getMouseX(), MouseHelper.getMouseY(), 2, 2)) {
            return super.keyPressed(pKeyCode, pScanCode, pModifiers);
        }
        return false;
    }

    @Override
    public boolean isVisibleT() {
        return visible;
    }

    @Override
    public void setVisibleT(boolean visible) {
        this.visible = visible;
    }

    @Override
    public void setBounds(int x, int y, int width, int height) {
        VanillaWidget2TComponentHelper.setBounds(x, y, width, height, this);
    }

    @Override
    public void setAbsBounds(int x, int y, int width, int height) {
        this.x = x;
        this.y = y;
        this.width = width;
        this.height = height;
    }

    @Override
    public void setParent(TComponent parent) {
        this.parent = parent;
    }

    @Nullable
    @Override
    public TComponent getParent() {
        return parent;
    }

    @Override
    public void setParentScreen(@Nullable TScreen parentScreen) {
        this.parentScreen = parentScreen;
    }

    @Nullable
    @Override
    public TScreen getParentScreen() {
        return parentScreen;
    }

    @Override
    public int getXT() {
        return x;
    }

    @Override
    public int getYT() {
        return y;
    }

    @Override
    public Vec2i getPreferredSize() {
        return new Vec2i(width, 20);
    }

    @Override
    public Vec2i getSize() {
        return new Vec2i(width, height);
    }

    @Override
    public void tickT() {
    }

    @Override
    public void respond(Double value) {
        responders.forEach(consumer -> consumer.accept(value));
    }

    @Override
    public void addResponder(Consumer<Double> responder) {
        responders.add(responder);
    }

    @Override
    public void clearResponders() {
        responders.clear();
    }

    @Override
    public void onRelease(double pMouseX, double pMouseY) {
        if (isInRange(pMouseX, pMouseY)) {
            super.onRelease(pMouseX, pMouseY);
        }
    }

    private static class DoubleRange implements OptionInstance.SliderableValueSet<Double> {
        double minInclusive;
        double maxInclusive;

        public DoubleRange(double minInclusive, double maxInclusive) {
            this.minInclusive = minInclusive;
            this.maxInclusive = maxInclusive;
        }

        @Override
        public double toSliderValue(Double value) {
            return Mth.map(value, minInclusive, maxInclusive, 0, 1);
        }

        @Override
        public Double fromSliderValue(double percentage) {
            return Mth.map(percentage, 0, 1, minInclusive, maxInclusive);
        }

        @Override
        public Optional<Double> validateValue(Double value) {
            return value >= minInclusive && value <= maxInclusive ? Optional.of(value) : Optional.empty();
        }

        @Override
        public Codec<Double> codec() {
            return Codec.DOUBLE;
        }
    }
}
