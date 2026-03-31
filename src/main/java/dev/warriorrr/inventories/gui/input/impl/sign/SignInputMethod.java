package dev.warriorrr.inventories.gui.input.impl.sign;

import dev.warriorrr.inventories.gui.MenuHistory;
import dev.warriorrr.inventories.gui.MenuInventory;
import dev.warriorrr.inventories.gui.input.PlayerInput;
import dev.warriorrr.inventories.gui.input.TextLength;
import dev.warriorrr.inventories.gui.input.TextLengths;
import dev.warriorrr.inventories.gui.input.UserInputMethod;
import dev.warriorrr.inventories.gui.input.response.ErrorMessage;
import dev.warriorrr.inventories.gui.input.response.Finish;
import dev.warriorrr.inventories.gui.input.response.InputResponse;
import dev.warriorrr.inventories.gui.input.response.Nothing;
import dev.warriorrr.inventories.gui.input.response.OpenPreviousMenu;
import dev.warriorrr.inventories.gui.input.response.ReOpen;
import io.papermc.paper.event.packet.UncheckedSignChangeEvent;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.serializer.plain.PlainTextComponentSerializer;
import org.bukkit.Location;
import org.bukkit.block.Sign;
import org.bukkit.block.data.BlockData;
import org.bukkit.block.sign.Side;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerInputEvent;
import org.bukkit.event.player.PlayerQuitEvent;
import org.bukkit.plugin.java.JavaPlugin;

import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.UUID;
import java.util.concurrent.ConcurrentHashMap;
import java.util.function.Function;

@SuppressWarnings("UnstableApiUsage")
public class SignInputMethod implements UserInputMethod<SignInputOptionsBuilder>, Listener {
    private final JavaPlugin plugin;
    private final Map<UUID, SignInputSession> sessionsByPlayer = new ConcurrentHashMap<>();

    public SignInputMethod(final JavaPlugin plugin) {
        this.plugin = plugin;
    }

    @Override
    public void startAwaitingInput(Player player, MenuInventory currentInventory, SignInputOptionsBuilder options) {
        final Location location = player.getLocation().add(player.getLocation().getDirection().multiply(-3)).getBlock().getLocation(); // this places the sign somewhere behind the player

        final BlockData originalData = location.getBlock().getBlockData();
        final BlockData signBlockData = options.signType.createBlockData();

        final Sign sign = (Sign) signBlockData.createBlockState();
        for (int i = 0; i < options.lines.size(); i++) {
            sign.getSide(Side.FRONT).line(i, Objects.requireNonNullElse(options.lines.get(i), Component.empty()));
        }

        player.closeInventory();

        player.sendBlockChange(location, signBlockData);
        player.sendBlockUpdate(location, sign);
        player.openVirtualSign(location, Side.FRONT);

        final SignInputSession session = new SignInputSession(location, originalData, options.inputFunction);
        sessionsByPlayer.put(player.getUniqueId(), session);
    }

    @Override
    public TextLength maximumTextLength() {
        // the maximum length for a sign is a bit complicated since it's dependent on the font and characters being used except for a hard limit of 50
        // the limit when using characters in the English alphabet is 15, so that'll be used here
        return TextLengths.SHORT;
    }

    @Override
    public SignInputOptionsBuilder newOptionsBuilder() {
        return new SignInputOptionsBuilder();
    }

    @EventHandler(priority = EventPriority.LOW, ignoreCancelled = true)
    public void onUncheckedSignChange(UncheckedSignChangeEvent event) {
        final Player player = event.getPlayer();
        final SignInputSession session = sessionsByPlayer.get(player.getUniqueId());
        if (session == null) {
            return;
        }

        event.setCancelled(true);
        if (event.getSide() != Side.FRONT) {
            return;
        }

        final String input = PlainTextComponentSerializer.plainText().serialize(event.lines().getFirst());
        final List<InputResponse> responses = session.inputFunction().apply(new PlayerInput(input));

        for (final InputResponse response : responses) {
            switch (response) {
                case Finish ignored -> cancel(player, true);
                case Nothing ignored -> {}
                case ReOpen reOpen -> {
                    MenuHistory.reOpen(player, reOpen.supplier());
                    cancel(player, true);
                }
                case ErrorMessage errorMessage -> player.sendMessage(errorMessage.error());
                case OpenPreviousMenu ignored -> {
                    cancel(player, true);
                    MenuHistory.last(player);
                }
                default -> throw new IllegalArgumentException("Unimplemented input response type " + response.getClass());
            }
        }
    }

    @Override
    public void disable() {
        for (final Player player : plugin.getServer().getOnlinePlayers()) {
            cancel(player, true);
        }

        sessionsByPlayer.clear();
    }

    @EventHandler
    public void cancelSession(PlayerQuitEvent event) {
        cancel(event.getPlayer(), false);
    }

    @EventHandler
    public void cancelSession(PlayerInputEvent event) {
        // players typically don't send input events with an open sign, so use the input event to clean up after ourselves
        cancel(event.getPlayer(), true);
    }

    private void cancel(final Player player, boolean resend) {
        final SignInputSession session = sessionsByPlayer.remove(player.getUniqueId());
        if (session == null) {
            return;
        }

        if (resend) {
            player.getScheduler().run(plugin, task -> player.sendBlockChange(session.signPosition(), session.originalBlockData()), null);
        }
    }

    public record SignInputSession(Location signPosition, BlockData originalBlockData,
                                   Function<PlayerInput, List<InputResponse>> inputFunction) {
    }
}
