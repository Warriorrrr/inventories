package dev.warriorrr.inventories.gui.input.impl.dialog;

import dev.warriorrr.inventories.gui.input.InputOptionsBuilder;
import dev.warriorrr.inventories.gui.input.PlayerInput;
import dev.warriorrr.inventories.gui.input.response.InputResponse;
import net.kyori.adventure.dialog.DialogLike;
import net.kyori.adventure.key.Key;
import net.kyori.adventure.text.Component;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.SequencedCollection;
import java.util.function.Function;
import java.util.function.Supplier;

public class DialogInputOptionsBuilder implements InputOptionsBuilder {
    protected Component title = Component.empty();
    protected Function<PlayerInput, List<InputResponse>> inputFunction;
    protected DialogLike dialog;
    protected Map<Key, Function<DialogResponse, SequencedCollection<InputResponse>>> clickHandlers = new HashMap<>();

    protected DialogInputOptionsBuilder() {}

    @Override
    public DialogInputOptionsBuilder title(Component title) {
        this.title = title;
        return this;
    }

    @Override
    public DialogInputOptionsBuilder onInput(Function<PlayerInput, List<InputResponse>> inputFunction) {
        this.inputFunction = inputFunction;
        return this;
    }

    public DialogInputOptionsBuilder dialog(final DialogLike dialog) {
        this.dialog = dialog;
        return this;
    }

    public DialogInputOptionsBuilder dialog(final Supplier<DialogLike> dialog) {
        this.dialog = dialog.get();
        return this;
    }

    public DialogInputOptionsBuilder clickHandler(final Key key, final Function<DialogResponse, SequencedCollection<InputResponse>> inputFunction) {
        this.clickHandlers.put(key, inputFunction);
        return this;
    }
}
