package cn.ussshenzhou.t88.gui.util;

import cn.ussshenzhou.t88.gui.widegt.TSlider;
import cn.ussshenzhou.t88.mixin.AbstractSelectionListAccessor;
import cn.ussshenzhou.t88.mixin.EditBoxAccessor;
import cn.ussshenzhou.t88.mixin.SliderButtonAccessor;
import net.minecraft.client.OptionInstance;
import net.minecraft.client.gui.components.AbstractSelectionList;
import net.minecraft.client.gui.components.EditBox;

/**
 * @author Tony Yu
 */
public class AccessorProxy {
    public static class EditBoxProxy {
        public static int getDisplayPos(EditBox that) {
            return ((EditBoxAccessor) that).getDisplayPos();
        }

        public static void setDisplayPos(EditBox that, int pos) {
            ((EditBoxAccessor) that).setDisplayPos(pos);
        }

        public static boolean isEditBoxEdible(EditBox that) {
            return ((EditBoxAccessor) that).isIsEditable();
        }
    }


    @SuppressWarnings("unchecked")
    public static class AbstractSelectionListProxy {
        public static <E extends AbstractSelectionList.Entry<E>> void setHovered(AbstractSelectionList<E> that, E hovered) {
            ((AbstractSelectionListAccessor<E>) that).setHovered(hovered);
        }

        public static <E extends AbstractSelectionList.Entry<E>> boolean isRenderHeader(AbstractSelectionList<E> that) {
            return ((AbstractSelectionListAccessor<E>) that).isRenderHeader();
        }


        public static <E extends AbstractSelectionList.Entry<E>> boolean isRenderBackground(AbstractSelectionList<E> that) {
            return ((AbstractSelectionListAccessor<E>) that).isRenderBackground();
        }
    }

    public static class SliderProxy {
        public static void setOption(TSlider that, OptionInstance<?> option) {
            ((SliderButtonAccessor) that).setOption(option);
        }

        public static void setToolTip(TSlider that, OptionInstance.TooltipSupplier<?> tooltipSupplier) {
            ((SliderButtonAccessor) that).setTooltipSupplier(tooltipSupplier);
        }
    }
}
