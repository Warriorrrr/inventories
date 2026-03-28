package dev.warriorrr.inventories;

import com.google.common.base.Preconditions;
import dev.warriorrr.inventories.gui.MenuHistory;
import dev.warriorrr.inventories.gui.MenuInventory;
import dev.warriorrr.inventories.gui.input.BuiltinInputMethods;
import dev.warriorrr.inventories.gui.input.InputBackendRegistry;
import dev.warriorrr.inventories.gui.input.UserInputBackend;
import dev.warriorrr.inventories.gui.input.impl.sign.SignInputBackend;
import dev.warriorrr.inventories.gui.input.impl.text.TextInputBackend;
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
    private final InputBackendRegistry inputBackendRegistry;

    private Inventories(final JavaPlugin plugin, final Map<Key, UserInputBackend> inputBackends) {
        this.plugin = plugin;
        this.scheduler = new MenuScheduler(plugin);
        this.inputBackendRegistry = new InputBackendRegistry(inputBackends);

        INSTANCE = this;

        listeners.addAll(List.of(new PlayerListener(), new InventoryListener(), new ShutdownListener(this)));

        for (final UserInputBackend backend : inputBackends.values()) {
            if (backend instanceof Listener listener) {
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
        this.inputBackendRegistry.shutdown();
    }

    public InputBackendRegistry inputBackendRegistry() {
        return this.inputBackendRegistry;
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
        private final Map<Key, UserInputBackend> inputBackends = new LinkedHashMap<>();

        protected Builder(final JavaPlugin plugin) {
            this.plugin = plugin;

            addInputBackend(BuiltinInputMethods.SIGN, new SignInputBackend(plugin));
            addInputBackend(BuiltinInputMethods.CHAT, new TextInputBackend(plugin));
        }

        public Builder addInputBackend(final Key key, final UserInputBackend backend) {
            this.inputBackends.put(key, backend);
            return this;
        }

        /**
         * Removes the previously registered input backend with the given key. By default, there are already builtin input backends that can be removed by this method.
         *
         * @param key The key for the backend to remove.
         * @return {@code this}
         * @see BuiltinInputMethods#keys()
         */
        public Builder removeInputBackend(final Key key) {
            this.inputBackends.remove(key);
            return this;
        }

        /**
         * {@return a new Inventories instance based on the provided options}
         * @throws IllegalStateException if no input backends are registered.
         */
        public Inventories build() {
            Preconditions.checkState(!this.inputBackends.isEmpty(), "at least 1 input backend is required!");

            return new Inventories(this.plugin, this.inputBackends);
        }
    }
}
