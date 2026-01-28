package dev.warriorrr.inventories.gui.input.impl.sign;

import dev.warriorrr.inventories.gui.MenuHistory;
import dev.warriorrr.inventories.gui.MenuInventory;
import dev.warriorrr.inventories.gui.input.PlayerInput;
import dev.warriorrr.inventories.gui.input.UserInputBackend;
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
import org.bukkit.block.BlockType;
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
import java.util.UUID;
import java.util.concurrent.ConcurrentHashMap;
import java.util.function.Function;

@SuppressWarnings("UnstableApiUsage")
public class SignInputBackend implements UserInputBackend, Listener {
    private final JavaPlugin plugin;
    private final Map<Location, SignInputSession> sessionsByLocation = new ConcurrentHashMap<>();
    private final Map<UUID, SignInputSession> sessionsByPlayer = new ConcurrentHashMap<>();

    public SignInputBackend(final JavaPlugin plugin) {
        this.plugin = plugin;
    }

    @Override
    public void startAwaitingInput(Player player, MenuInventory currentInventory, Component title, Function<PlayerInput, List<InputResponse>> inputFunction) {
        final Location location = player.getLocation().add(player.getLocation().getDirection().multiply(-3)).getBlock().getLocation(); // this places the sign somewhere behind the player

        final BlockData originalData = location.getBlock().getBlockData();
        final BlockData signBlockData = BlockType.OAK_SIGN.createBlockData();

        final Sign sign = (Sign) signBlockData.createBlockState();
        sign.getSide(Side.FRONT).line(1, Component.text("^^^^^^^^^^^"));
        sign.getSide(Side.FRONT).line(2, title);

        player.closeInventory();

        player.sendBlockChange(location, signBlockData);
        player.sendBlockUpdate(location, sign);
        player.openVirtualSign(location, Side.FRONT);

        final SignInputSession session = new SignInputSession(location, originalData, inputFunction);
        sessionsByLocation.put(location, session);
        sessionsByPlayer.put(player.getUniqueId(), session);
    }

    @EventHandler(priority = EventPriority.LOW, ignoreCancelled = true)
    public void onUncheckedSignChange(UncheckedSignChangeEvent event) {
        final SignInputSession session = sessionsByLocation.get(event.getEditedBlockPosition().toLocation(event.getPlayer().getWorld()));
        if (session == null) {
            return;
        }

        event.setCancelled(true);
        if (event.getSide() != Side.FRONT) {
            return;
        }

        final Player player = event.getPlayer();

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

        sessionsByLocation.clear();
        sessionsByPlayer.clear();
    }

    @EventHandler
    public void cancelSession(PlayerQuitEvent event) {
        cancel(event.getPlayer(), false);
    }

    @EventHandler
    public void cancelSession(PlayerInputEvent event) {
        // players typically don't send input events with an open sign, so
        cancel(event.getPlayer(), true);
    }

    private void cancel(final Player player, boolean resend) {
        final SignInputSession session = sessionsByPlayer.remove(player.getUniqueId());
        if (session == null) {
            return;
        }

        sessionsByLocation.remove(session.signPosition());

        if (resend) {
            player.getScheduler().run(plugin, task -> player.sendBlockChange(session.signPosition(), session.originalBlockData()), null);
        }
    }

    public record SignInputSession(Location signPosition, BlockData originalBlockData,
                                   Function<PlayerInput, List<InputResponse>> inputFunction) {
    }
}
