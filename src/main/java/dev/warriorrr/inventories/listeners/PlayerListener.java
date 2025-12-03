package dev.warriorrr.inventories.listeners;

import dev.warriorrr.inventories.gui.MenuHistory;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerQuitEvent;

public class PlayerListener implements Listener {

    @EventHandler(priority = EventPriority.MONITOR)
    public void clearMenuHistory(PlayerQuitEvent event) {
        MenuHistory.clearHistory(event.getPlayer().getUniqueId());
    }
}
