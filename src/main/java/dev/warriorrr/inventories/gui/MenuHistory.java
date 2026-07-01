package dev.warriorrr.inventories.gui;

import dev.warriorrr.inventories.Inventories;
import org.bukkit.entity.Player;

import java.util.Collections;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.UUID;
import java.util.concurrent.ConcurrentHashMap;
import java.util.function.Supplier;

/**
 * Used for tracking what menus a player has opened in their current 'session'.
 */
public class MenuHistory {
    private static final Map<UUID, List<Supplier<MenuInventory>>> HISTORY_MAP = new ConcurrentHashMap<>();

    public static void clearHistory(UUID uuid) {
        HISTORY_MAP.remove(uuid);
    }

    public static void addHistory(UUID uuid, MenuInventory menuInventory) {
        addHistory(uuid, () -> menuInventory);
    }

    public static void addHistory(UUID uuid, Supplier<MenuInventory> menuInventory) {
        HISTORY_MAP.computeIfAbsent(uuid, k -> Collections.synchronizedList(new LinkedList<>())).add(menuInventory);
    }

    public static void clearAllHistory() {
        HISTORY_MAP.clear();
    }

    /**
     * Uses history to open the previous menu (if possible).
     * @param player The player to open the previous menu for.
     */
    public static void back(Player player) {
        List<Supplier<MenuInventory>> history = HISTORY_MAP.get(player.getUniqueId());

        // Player has no history or not enough history
        if (history == null || history.size() < 2)
            return;

        // Remove the current inventory from history
        history.removeLast();

        // Open the last inventory without adding to history.
        history.getLast().get().openSilent(player);
    }

    /**
     * Re-opens the last menu for this player.
     * @param player The player to open the last menu for.
     */
    public static void last(Player player) {
        List<Supplier<MenuInventory>> history = HISTORY_MAP.get(player.getUniqueId());

        if (history == null || history.isEmpty())
            return;

        history.getLast().get().openSilent(player);
    }

    public static void pop(UUID uuid) {
        List<?> history = HISTORY_MAP.get(uuid);

        if (history == null || history.isEmpty())
            return;

        history.removeLast();
    }

    public static void reOpen(Player player, Supplier<MenuInventory> supplier) {
        Inventories.getInstance().getScheduler().scheduleAsync(player.getUniqueId(), () -> {
            pop(player.getUniqueId());
            addHistory(player.getUniqueId(), supplier);

            supplier.get().openSilent(player);
        });
    }
}
