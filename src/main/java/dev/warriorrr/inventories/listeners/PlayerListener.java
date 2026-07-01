package dev.warriorrr.inventories.listeners;

import com.destroystokyo.paper.event.player.PlayerConnectionCloseEvent;
import dev.warriorrr.inventories.gui.MenuHistory;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;

public class PlayerListener implements Listener {

    @EventHandler(priority = EventPriority.MONITOR)
    public void clearMenuHistory(PlayerConnectionCloseEvent event) {
        MenuHistory.clearHistory(event.getPlayerUniqueId());
    }
}
