package dev.warriorrr.inventories.utils;

import org.bukkit.entity.HumanEntity;
import org.bukkit.plugin.java.JavaPlugin;

import java.util.Set;
import java.util.UUID;
import java.util.concurrent.ConcurrentHashMap;

public class MenuScheduler {
    private final JavaPlugin plugin;
    private final Set<UUID> runningTasks = ConcurrentHashMap.newKeySet();

    public MenuScheduler(final JavaPlugin plugin) {
        this.plugin = plugin;
    }

    public void scheduleAsync(HumanEntity entity, Runnable runnable) {
        scheduleAsync(entity.getUniqueId(), runnable);
    }

    public synchronized void scheduleAsync(UUID uuid, Runnable runnable) {
        // Prevent unwanted behavior by only allowing 1 async task per player at a time
        if (!runningTasks.add(uuid) || !plugin.isEnabled()) {
            return;
        }

        plugin.getServer().getAsyncScheduler().runNow(plugin, task -> {
            try {
                runnable.run();
            } finally {
                runningTasks.remove(uuid);
            }
        });
    }
}
