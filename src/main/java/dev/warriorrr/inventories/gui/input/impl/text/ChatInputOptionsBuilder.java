package dev.warriorrr.inventories.gui.input.impl.text;

import dev.warriorrr.inventories.gui.input.InputOptionsBuilder;
import dev.warriorrr.inventories.gui.input.PlayerInput;
import dev.warriorrr.inventories.gui.input.response.InputResponse;
import net.kyori.adventure.text.Component;
import org.bukkit.entity.Player;

import java.time.Duration;
import java.util.ArrayList;
import java.util.List;
import java.util.function.Consumer;
import java.util.function.Function;

public class ChatInputOptionsBuilder implements InputOptionsBuilder {
    protected Function<PlayerInput, List<InputResponse>> inputFunction;
    protected List<Component> startMessages = new ArrayList<>();
    protected Duration timeout = Duration.ofSeconds(60);

    protected Consumer<Player> onCancel = player -> {};
    protected Consumer<Player> onTimeout = player -> {};

    protected ChatInputOptionsBuilder() {}

    @Override
    public ChatInputOptionsBuilder title(Component title) {
        this.startMessages.add(title);
        return this;
    }

    /**
     * Adds a new message to be sent in chat when the input session starts.
     *
     * @param message A line to send then the input session starts.
     * @return {@code this}
     */
    public ChatInputOptionsBuilder addStartMessage(final Component message) {
        this.startMessages.add(message);
        return this;
    }

    public ChatInputOptionsBuilder addStartMessage(final int index, final Component message) {
        this.startMessages.add(index, message);
        return this;
    }

    @Override
    public ChatInputOptionsBuilder onInput(Function<PlayerInput, List<InputResponse>> inputFunction) {
        this.inputFunction = inputFunction;
        return this;
    }

    public ChatInputOptionsBuilder timeout(final Duration timeout) {
        this.timeout = timeout;
        return this;
    }

    public ChatInputOptionsBuilder onTimeout(final Consumer<Player> onTimeout) {
        this.onTimeout = onTimeout;
        return this;
    }

    public ChatInputOptionsBuilder onCancel(final Consumer<Player> onCancel) {
        this.onCancel = onCancel;
        return this;
    }

    public Duration timeout() {
        return this.timeout;
    }
}
