package dev.warriorrr.inventories;

import dev.warriorrr.inventories.gui.MenuHistory;
import dev.warriorrr.inventories.gui.MenuInventory;
import dev.warriorrr.inventories.gui.input.UserInputBackend;
import dev.warriorrr.inventories.listeners.InventoryListener;
import dev.warriorrr.inventories.listeners.PlayerListener;
import dev.warriorrr.inventories.listeners.ShutdownListener;
import dev.warriorrr.inventories.utils.MenuScheduler;
import org.bukkit.entity.Player;
import org.bukkit.event.HandlerList;
import org.bukkit.event.Listener;
import org.bukkit.plugin.java.JavaPlugin;
import org.jetbrains.annotations.ApiStatus;

import java.util.ArrayList;
import java.util.List;

public class Inventories {
    private static Inventories INSTANCE;

    private final JavaPlugin plugin;
    private final MenuScheduler scheduler;
    private final List<Listener> listeners = new ArrayList<>();
    private UserInputBackend userInputBackend;

    private Inventories(final JavaPlugin plugin) {
        this.plugin = plugin;
        this.scheduler = new MenuScheduler(plugin);

        INSTANCE = this;

        listeners.addAll(List.of(new PlayerListener(), new InventoryListener(), new ShutdownListener(this)));

        this.userInputBackend = UserInputBackend.selectBackend(plugin);
        if (this.userInputBackend instanceof Listener listener) {
            listeners.add(listener);
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
        this.userInputBackend.disable();
    }

    public static Builder forPlugin(final JavaPlugin plugin) {
        return new Builder(plugin);
    }

    public UserInputBackend getUserInputBackend() {
        return userInputBackend;
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

    public static class Builder {
        private final JavaPlugin plugin;

        protected Builder(final JavaPlugin plugin) {
            this.plugin = plugin;
        }

        public Inventories build() {
            return new Inventories(this.plugin);
        }
    }
}
