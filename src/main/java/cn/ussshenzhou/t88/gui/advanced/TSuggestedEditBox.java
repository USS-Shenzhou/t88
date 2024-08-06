package cn.ussshenzhou.t88.gui.advanced;

import cn.ussshenzhou.t88.gui.util.HorizontalAlignment;
import cn.ussshenzhou.t88.gui.widegt.TPanel;
import cn.ussshenzhou.t88.gui.widegt.TSelectList;
import com.mojang.brigadier.CommandDispatcher;
import com.mojang.brigadier.ParseResults;
import com.mojang.brigadier.suggestion.Suggestion;
import com.mojang.brigadier.suggestion.Suggestions;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.commands.CommandSourceStack;
import org.lwjgl.glfw.GLFW;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ExecutionException;
import java.util.function.Consumer;

/**
 * @author USS_Shenzhou
 */
public class TSuggestedEditBox extends TPanel {
    private final TCommandConstrainedEditBox editBox;
    private final TSelectList<String> suggestionList;

    public TSuggestedEditBox(Consumer<CommandDispatcher<CommandSourceStack>> consumer) {
        super();
        editBox = new TCommandConstrainedEditBox(consumer) {
            @Override
            public void setFocused(boolean pIsFocused) {
                super.setFocused(pIsFocused);
                suggestionList.setVisibleT(pIsFocused);
            }
        };
        editBox.addResponder(s -> {
            this.updateSuggestion(s);
            //editBox.check(s);
        });
        suggestionList = new TSelectList<>(12, 0) {
            @Override
            public boolean isFocused() {
                return super.isFocused() || editBox.isFocused();
            }
        };
        suggestionList.setBackground(0xff000000);
        suggestionList.setHorizontalAlignment(HorizontalAlignment.LEFT);
        suggestionList.setSelectedForeGround(0xfffcfc00);
        this.add(editBox);
        this.add(suggestionList);
    }

    public TSuggestedEditBox(CommandDispatcher<CommandSourceStack> dispatcher) {
        this(d -> {
        });
        editBox.setDispatcher(dispatcher);
    }

    @Override
    public void tickT() {
        super.tickT();
        if (this.isOutOfParentScrollContainerScissor()) {
            suggestionList.setVisibleT(false);
        }
    }

    @Override
    public void layout() {
        editBox.setBounds(0, 0, width, height);
        super.layout();
    }

    @Override
    protected void renderChildren(GuiGraphics guigraphics, int pMouseX, int pMouseY, float pPartialTick) {
        if (editBox.isVisible()) {
            editBox.render(guigraphics, pMouseX, pMouseY, pPartialTick);
        }
    }

    @Override
    public void renderTop(GuiGraphics guigraphics, int pMouseX, int pMouseY, float pPartialTick) {
        if (suggestionList.isVisibleT()) {
            suggestionList.render(guigraphics, pMouseX, pMouseY, pPartialTick);
        }
        super.renderTop(guigraphics, pMouseX, pMouseY, pPartialTick);
    }

    public void updateSuggestion(String value) {
        CommandSourceStack sourceStack = Minecraft.getInstance().player.createCommandSourceStack();
        ParseResults<CommandSourceStack> parseResults = editBox.dispatcher.parse(value, sourceStack);
        try {
            CompletableFuture<Suggestions> suggestions = editBox.dispatcher.getCompletionSuggestions(parseResults, editBox.getCursorPosition());
            suggestions.thenRun(() -> {
                if (suggestions.isDone()) {
                    try {
                        List<Suggestion> list = suggestions.get().getList();
                        updateSuggestionList(list);
                    } catch (InterruptedException | ExecutionException ignored) {
                    }
                }
            });
        } catch (NullPointerException ignored) {
        }
    }

    private void updateSuggestionList(List<Suggestion> list) {
        suggestionList.clearElement();
        if (!list.isEmpty()) {
            var font = Minecraft.getInstance().font;
            suggestionList.setVisibleT(true);
            List<String> texts = new ArrayList<>();
            int w = 0;
            for (Suggestion suggestion : list) {
                texts.add(suggestion.getText());
                w = Math.max(w, font.width(suggestion.getText()));
            }
            suggestionList.addElement(texts);
            if (Minecraft.getInstance().screen != null) {
                int listY;
                int s = (int) this.getParentScroll().y;
                int h = //ideal
                        texts.size() * suggestionList.getItemHeight() + 4;
                if (y - s <= Minecraft.getInstance().screen.height / 2) {
                    //start from upper half to screen bottom
                    listY = y + height + 1;
                    h = Math.min(h, Minecraft.getInstance().screen.height - listY + s);
                } else {
                    //start from lower half to screen top
                    listY = Math.max(0, y - texts.size() * suggestionList.getItemHeight() - 4);
                    h = Math.min(h, this.getYT() - s - 1);
                }
                w = w + suggestionList.getScrollbarGap() + TSelectList.SCROLLBAR_WIDTH + 2;
                suggestionList.setAbsBounds(calculateSuggestionX(w), listY, w, h);
                //choose first
                if (suggestionList.getSelected() == null) {
                    suggestionList.setSelected(0);
                }
            }
        } else {
            suggestionList.setVisibleT(false);
        }
    }

    private int calculateSuggestionX(int l) {
        int i = editBox.getCurrentWordBeginX() - 1;
        int w = Minecraft.getInstance().screen.width - 5;
        if (i + l > w) {
            return w - l;
        } else {
            return i;
        }
    }

    public void applySuggestion() {
        TSelectList<String>.Entry e = suggestionList.getSelected();
        if (e != null) {
            String suggestion = suggestionList.getSelected().getContent();
            String s = editBox.getValue();
            if ("[".equals(suggestion) || "]".equals(suggestion) || "{".equals(suggestion) || "}".equals(suggestion)) {
                editBox.setValue(s.substring(0, editBox.getCursorPosition()) + suggestion + s.substring(editBox.getCursorPosition()));
                return;
            }
            if (s.isEmpty()) {
                editBox.setValue(suggestion);
            } else {
                int a = findLastSplitter(s);
                int b = s.indexOf(" ", editBox.getCursorPosition());
                if (a == -1) {
                    editBox.setValue(b == -1 ? suggestion : suggestion + s.substring(b));
                } else if (b == -1) {
                    editBox.setValue(s.substring(0, a + 1) + suggestion);
                } else {
                    editBox.setValue(s.substring(0, a + 1) + suggestion + s.substring(b));
                }
                editBox.moveCursorTo(a + 1 + suggestion.length(), true);
            }
        }
    }

    private int findLastSplitter(String s) {
        String[] splitters = {" ", ",", "[", "{"};
        int i = -1, j = 0;
        while (i == -1) {
            try {
                i = s.lastIndexOf(splitters[j], editBox.getCursorPosition());
                j++;
            } catch (IndexOutOfBoundsException ignored) {
                break;
            }
        }
        return i;
    }

    public TCommandConstrainedEditBox getEditBox() {
        return editBox;
    }

    public TSelectList<String> getSuggestionList() {
        return suggestionList;
    }

    @Override
    public boolean keyPressed(int pKeyCode, int pScanCode, int pModifiers) {
        if (editBox.isFocused()) {
            if (pKeyCode == GLFW.GLFW_KEY_TAB || pKeyCode == GLFW.GLFW_KEY_ENTER) {
                applySuggestion();
                return true;
            }
            return super.keyPressed(pKeyCode, pScanCode, pModifiers);
        }
        return false;
    }
}
