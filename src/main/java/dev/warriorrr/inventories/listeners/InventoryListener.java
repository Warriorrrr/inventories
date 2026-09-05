package dev.warriorrr.inventories.listeners;

import dev.warriorrr.inventories.gui.MenuInventory;
import dev.warriorrr.inventories.gui.MenuItem;
import dev.warriorrr.inventories.gui.action.ClickAction;
import net.kyori.adventure.text.Component;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.event.inventory.InventoryCloseEvent;
import org.bukkit.event.inventory.InventoryDragEvent;
import org.bukkit.event.inventory.InventoryOpenEvent;
import org.bukkit.event.player.PlayerDropItemEvent;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;

import java.util.List;

public class InventoryListener implements Listener {
    @EventHandler
    public void onInventoryClick(final InventoryClickEvent event) {
        if (!(event.getInventory().getHolder(false) instanceof MenuInventory menu)) {
            return;
        }

        event.setCancelled(true);

        final Inventory clicked = event.getClickedInventory();
        if (clicked == null || !(clicked.getHolder(false) instanceof MenuInventory)) {
            return;
        }

        List<ClickAction> actions = menu.actions(event.getSlot());
        if (actions != null) {
            actions.forEach(action -> action.onClick(menu, event));
        }
    }

    @EventHandler
    public void onInventoryClose(final InventoryCloseEvent event) {
        if (!(event.getView().getTopInventory().getHolder(false) instanceof MenuInventory)) {
            return;
        }

        for (ItemStack item : event.getView().getBottomInventory()) {
            if (item != null && item.getPersistentDataContainer().has(MenuItem.PDC_KEY)) {
                item.setAmount(0);
            }
        }
    }

    @EventHandler(ignoreCancelled = true, priority = EventPriority.LOW)
    public void removeMenuItem(final PlayerDropItemEvent event) {
        final ItemStack itemStack = event.getItemDrop().getItemStack();

        if (itemStack.getPersistentDataContainer().has(MenuItem.PDC_KEY)) {
            event.getItemDrop().remove();
        }
    }

    @EventHandler(ignoreCancelled = true, priority = EventPriority.LOW)
    public void onInventoryDrag(final InventoryDragEvent event) {
        event.setCancelled(event.getInventory().getHolder(false) instanceof MenuInventory);
    }

    @EventHandler
    public void applyTitleOverride(final InventoryOpenEvent event) {
        if (event.getInventory().getHolder(false) instanceof MenuInventory holder) {
            final Component override = holder.titleOverride();

            if (override != null) {
                event.titleOverride(override);
                holder.titleOverride(null);
            }
        }
    }
}
