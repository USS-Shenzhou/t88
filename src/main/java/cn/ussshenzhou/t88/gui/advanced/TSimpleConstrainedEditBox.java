package cn.ussshenzhou.t88.gui.advanced;

import com.mojang.brigadier.CommandDispatcher;
import com.mojang.brigadier.StringReader;
import com.mojang.brigadier.arguments.ArgumentType;
import com.mojang.brigadier.exceptions.CommandSyntaxException;

/**
 * @author USS_Shenzhou
 */
public class TSimpleConstrainedEditBox extends TConstrainedEditBox {
    private ArgumentType<?> argument;
    private boolean justSimpleText = false;

    public TSimpleConstrainedEditBox(ArgumentType<?> argument) {
        super();
        this.argument = argument;
    }

    public TSimpleConstrainedEditBox(ArgumentType<?> argument, boolean justSimpleText) {
        this.argument = argument;
        this.justSimpleText = justSimpleText;
    }

    @Override
    public void checkAndThrow(String value) throws CommandSyntaxException {
        StringReader stringReader = new StringReader(value);
        argument.parse(stringReader);
        if (!justSimpleText && stringReader.peek() != CommandDispatcher.ARGUMENT_SEPARATOR_CHAR) {
            throw CommandSyntaxException.BUILT_IN_EXCEPTIONS.dispatcherExpectedArgumentSeparator().createWithContext(stringReader);
        }
    }

    public ArgumentType<?> getArgument() {
        return argument;
    }

    public void setArgument(ArgumentType<?> argument) {
        this.argument = argument;
    }
}
