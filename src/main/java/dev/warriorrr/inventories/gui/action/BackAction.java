package dev.warriorrr.inventories.gui.action;

import dev.warriorrr.inventories.gui.MenuHistory;
import dev.warriorrr.inventories.gui.MenuInventory;
import org.bukkit.entity.Player;
import org.bukkit.event.inventory.InventoryClickEvent;

public class BackAction implements ClickAction {

    @Override
    public void onClick(MenuInventory inventory, InventoryClickEvent event) {
        MenuHistory.back((Player) event.getWhoClicked());
    }
}
