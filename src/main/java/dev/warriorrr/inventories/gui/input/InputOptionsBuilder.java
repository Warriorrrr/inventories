package dev.warriorrr.inventories.gui.input;

import dev.warriorrr.inventories.gui.input.response.InputResponse;
import net.kyori.adventure.text.Component;

import java.util.List;
import java.util.function.Function;

public interface InputOptionsBuilder {
    InputOptionsBuilder title(final Component title);

    InputOptionsBuilder onInput(final Function<PlayerInput, List<InputResponse>> inputFunction);
}
