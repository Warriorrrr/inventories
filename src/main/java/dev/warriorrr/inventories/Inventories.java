package dev.warriorrr.inventories;

import com.google.common.base.Preconditions;
import dev.warriorrr.inventories.gui.MenuHistory;
import dev.warriorrr.inventories.gui.MenuInventory;
import dev.warriorrr.inventories.gui.input.BuiltinInputMethods;
import dev.warriorrr.inventories.gui.input.InputMethodRegistry;
import dev.warriorrr.inventories.gui.input.InputMethodKey;
import dev.warriorrr.inventories.gui.input.InputOptionsBuilder;
import dev.warriorrr.inventories.gui.input.UserInputMethod;
import dev.warriorrr.inventories.gui.input.impl.sign.SignInputMethod;
import dev.warriorrr.inventories.gui.input.impl.text.ChatInputMethod;
import dev.warriorrr.inventories.listeners.InventoryListener;
import dev.warriorrr.inventories.listeners.PlayerListener;
import dev.warriorrr.inventories.listeners.ShutdownListener;
import dev.warriorrr.inventories.utils.MenuScheduler;
import net.kyori.adventure.key.Key;
import org.bukkit.entity.Player;
import org.bukkit.event.HandlerList;
import org.bukkit.event.Listener;
import org.bukkit.plugin.java.JavaPlugin;
import org.jetbrains.annotations.ApiStatus;

import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

public class Inventories {
    private static Inventories INSTANCE;

    private final JavaPlugin plugin;
    private final MenuScheduler scheduler;
    private final List<Listener> listeners = new ArrayList<>();
    private final InputMethodRegistry inputMethodRegistry;

    private Inventories(final JavaPlugin plugin, final Map<Key, UserInputMethod<?>> inputMethods) {
        this.plugin = plugin;
        this.scheduler = new MenuScheduler(plugin);
        this.inputMethodRegistry = new InputMethodRegistry(inputMethods);

        INSTANCE = this;

        listeners.addAll(List.of(new PlayerListener(), new InventoryListener(), new ShutdownListener(this)));

        for (final UserInputMethod<?> method : inputMethods.values()) {
            if (method instanceof Listener listener) {
                listeners.add(listener);
            }
        }

        listeners.forEach(listener -> plugin.getServer().getPluginManager().registerEvents(listener, plugin));
    }

    public void disable() {
        for (final Player player : plugin.getServer().getOnlinePlayers()) {
            if (player.getOpenInventory().getTopInventory().getHolder(false) instanceof MenuInventory) {
                player.closeInventory();
            }
        }

        listeners.forEach(HandlerList::unregisterAll);
        listeners.clear();

        MenuHistory.clearAllHistory();
        this.inputMethodRegistry.shutdown();
    }

    public InputMethodRegistry inputMethodRegistry() {
        return this.inputMethodRegistry;
    }

    public MenuScheduler getScheduler() {
        return scheduler;
    }

    public JavaPlugin getPlugin() {
        return plugin;
    }

    @ApiStatus.Internal
    public static Inventories getInstance() {
        return INSTANCE;
    }

    public static Builder forPlugin(final JavaPlugin plugin) {
        return new Builder(plugin);
    }

    public static class Builder {
        private final JavaPlugin plugin;
        private final Map<Key, UserInputMethod<?>> inputMethods = new LinkedHashMap<>();

        protected Builder(final JavaPlugin plugin) {
            this.plugin = plugin;

            addInputMethod(BuiltinInputMethods.SIGN, new SignInputMethod(plugin));
            addInputMethod(BuiltinInputMethods.CHAT, new ChatInputMethod(plugin));
        }

        public <T extends InputOptionsBuilder> Builder addInputMethod(final InputMethodKey<T> key, final UserInputMethod<T> method) {
            this.inputMethods.put(key.key(), method);
            return this;
        }

        /**
         * Removes the previously registered input method with the given key. By default, there are already builtin input methods that can be removed by this method.
         *
         * @param key The key for the method to remove.
         * @return {@code this}
         * @see BuiltinInputMethods#keys()
         */
        public Builder removeInputMethod(final Key key) {
            this.inputMethods.remove(key);
            return this;
        }

        /**
         * {@return a new Inventories instance based on the provided options}
         * @throws IllegalStateException if no input methods are registered.
         */
        public Inventories build() {
            Preconditions.checkState(!this.inputMethods.isEmpty(), "at least 1 input method is required!");

            return new Inventories(this.plugin, this.inputMethods);
        }
    }
}
