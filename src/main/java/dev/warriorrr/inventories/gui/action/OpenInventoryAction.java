package dev.warriorrr.inventories.gui.action;

import dev.warriorrr.inventories.Inventories;
import dev.warriorrr.inventories.gui.MenuInventory;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.plugin.java.JavaPlugin;
import org.jetbrains.annotations.NotNull;

import java.util.function.Supplier;

public class OpenInventoryAction implements ClickAction {
    private final Supplier<MenuInventory> supplier;
    private final boolean silent;

    public OpenInventoryAction(@NotNull Supplier<MenuInventory> supplier, boolean silent) {
        this.supplier = supplier;
        this.silent = silent;
    }

    @Override
    public void onClick(MenuInventory inventory, InventoryClickEvent event) {
        final JavaPlugin plugin = Inventories.getInstance().getPlugin();

        plugin.getServer().getAsyncScheduler().runNow(plugin, task -> {
            final MenuInventory built = supplier.get();

            if (silent) {
                built.openSilent(event.getWhoClicked());
            } else {
                built.open(event.getWhoClicked());
            }
        });
    }
}
