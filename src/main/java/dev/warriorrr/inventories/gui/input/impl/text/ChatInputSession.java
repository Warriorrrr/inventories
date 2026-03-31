package dev.warriorrr.inventories.gui.input.impl.text;

import dev.warriorrr.inventories.gui.MenuInventory;
import dev.warriorrr.inventories.gui.input.PlayerInput;
import dev.warriorrr.inventories.gui.input.response.InputResponse;
import io.papermc.paper.threadedregions.scheduler.ScheduledTask;
import org.bukkit.entity.Player;
import org.jspecify.annotations.Nullable;
import java.util.List;
import java.util.function.Consumer;
import java.util.function.Function;

public class ChatInputSession {
    private final MenuInventory currentInventory;
    private final Function<PlayerInput, List<InputResponse>> inputFunction;
    private ScheduledTask timeoutTask;
    private final Consumer<Player> onCancel;

    public ChatInputSession(final MenuInventory currentInventory, final Function<PlayerInput, List<InputResponse>> inputFunction, final Consumer<Player> onCancel) {
        this.currentInventory = currentInventory;
        this.inputFunction = inputFunction;
        this.onCancel = onCancel;
    }

    public MenuInventory currentInventory() {
        return this.currentInventory;
    }

    public Function<PlayerInput, List<InputResponse>> inputFunction() {
        return this.inputFunction;
    }

    @Nullable
    public ScheduledTask timeoutTask() {
        return this.timeoutTask;
    }

    public Consumer<Player> onCancel() {
        return this.onCancel;
    }

    public void timeoutTask(@Nullable ScheduledTask task) {
        this.timeoutTask = task;
    }
}
