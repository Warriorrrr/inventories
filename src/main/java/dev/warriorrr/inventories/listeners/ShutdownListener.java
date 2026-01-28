package dev.warriorrr.inventories.listeners;

import dev.warriorrr.inventories.Inventories;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.server.PluginDisableEvent;

public class ShutdownListener implements Listener {
    private final Inventories instance;

    public ShutdownListener(Inventories instance) {
        this.instance = instance;
    }

    @EventHandler
    public void onPluginDisable(final PluginDisableEvent event) {
        if (event.getPlugin().equals(instance.getPlugin())) {
            instance.disable();
        }
    }
}
