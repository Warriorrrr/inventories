package dev.warriorrr.inventories.gui.input.impl.text;

import dev.warriorrr.inventories.gui.MenuHistory;
import dev.warriorrr.inventories.gui.MenuInventory;
import dev.warriorrr.inventories.gui.input.TextLength;
import dev.warriorrr.inventories.gui.input.TextLengths;
import dev.warriorrr.inventories.gui.input.response.Finish;
import dev.warriorrr.inventories.gui.input.response.InputResponse;
import dev.warriorrr.inventories.gui.input.PlayerInput;
import dev.warriorrr.inventories.gui.input.UserInputMethod;
import dev.warriorrr.inventories.gui.input.response.Nothing;
import dev.warriorrr.inventories.gui.input.response.OpenPreviousMenu;
import dev.warriorrr.inventories.gui.input.response.ReOpen;
import dev.warriorrr.inventories.gui.input.response.ErrorMessage;
import io.papermc.paper.event.player.AsyncChatEvent;
import io.papermc.paper.threadedregions.scheduler.ScheduledTask;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.serializer.plain.PlainTextComponentSerializer;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerQuitEvent;
import org.bukkit.plugin.java.JavaPlugin;

import java.time.Duration;
import java.util.Collection;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.UUID;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.TimeUnit;

public class ChatInputMethod implements UserInputMethod<ChatInputOptionsBuilder>, Listener {
    private static final Duration INPUT_TIMEOUT = Duration.ofSeconds(60);
    private static final Collection<String> CANCEL_PHRASES = Set.of("q", "quit", "cancel", "stop");

    private final JavaPlugin plugin;
    private final Map<UUID, ChatInputSession> sessions = new ConcurrentHashMap<>();

    public ChatInputMethod(JavaPlugin plugin) {
        this.plugin = plugin;
    }

    @Override
    public void startAwaitingInput(final Player player, final MenuInventory currentInventory, final ChatInputOptionsBuilder options) {
        final ChatInputSession session = new ChatInputSession(currentInventory, options.inputFunction, options.onCancel);
        final UUID uuid = player.getUniqueId();

        cancelSession(uuid);
        sessions.put(uuid, session);

        player.closeInventory();

        for (final Component message : options.startMessages) {
            player.sendMessage(message);
        }

        session.timeoutTask(plugin.getServer().getAsyncScheduler().runDelayed(plugin, task -> {
            cancelSession(uuid);

            final Player p = plugin.getServer().getPlayer(uuid);
            if (p != null) {
                options.onTimeout.accept(p);
            }
        }, options.timeout.toMillis(), TimeUnit.MILLISECONDS));
    }

    @Override
    public TextLength maximumTextLength() {
        return TextLengths.LONG;
    }

    @Override
    public ChatInputOptionsBuilder newOptionsBuilder() {
        return new ChatInputOptionsBuilder();
    }

    @Override
    public void disable() {
        for (final ChatInputSession session : sessions.values()) {
            final ScheduledTask timeoutTask = session.timeoutTask();

            if (timeoutTask != null) {
                timeoutTask.cancel();
            }
        }

        sessions.clear();
    }

    @EventHandler(priority = EventPriority.LOWEST, ignoreCancelled = true)
    public void listenForInput(AsyncChatEvent event) {
        final Player player = event.getPlayer();

        final ChatInputSession session = sessions.get(player.getUniqueId());
        if (session == null)
            return;

        event.setCancelled(true);

        final String plain = PlainTextComponentSerializer.plainText().serialize(event.originalMessage());
        if (CANCEL_PHRASES.contains(plain)) {
            cancelSession(player.getUniqueId());
            session.onCancel().accept(player);
            return;
        }

        final List<InputResponse> responses = session.inputFunction().apply(new PlayerInput(plain));

        for (final InputResponse response : responses) {
            switch (response) {
                case Finish finish -> cancelSession(player.getUniqueId());
                case Nothing nothing -> {}
                case ReOpen reOpen -> {
                    MenuHistory.reOpen(player, reOpen.supplier());
                    cancelSession(player.getUniqueId());
                }
                case ErrorMessage errorMessage -> player.sendMessage(errorMessage.error());
                case OpenPreviousMenu ignored -> {
                    cancelSession(player.getUniqueId());
                    MenuHistory.last(player);
                }
                default -> throw new IllegalArgumentException("Unimplemented input response type " + response.getClass());
            }
        }
    }

    @EventHandler
    public void invalidateSession(PlayerQuitEvent event) {
        cancelSession(event.getPlayer().getUniqueId());
    }

    private void cancelSession(UUID uuid) {
        final ChatInputSession session = sessions.remove(uuid);
        if (session != null && session.timeoutTask() != null) {
            session.timeoutTask().cancel();
        }
    }
}
