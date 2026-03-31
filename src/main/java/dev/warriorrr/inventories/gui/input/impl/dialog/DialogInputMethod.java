package dev.warriorrr.inventories.gui.input.impl.dialog;

import com.destroystokyo.paper.event.player.PlayerConnectionCloseEvent;
import dev.warriorrr.inventories.gui.MenuHistory;
import dev.warriorrr.inventories.gui.MenuInventory;
import dev.warriorrr.inventories.gui.input.PlayerInput;
import dev.warriorrr.inventories.gui.input.TextLength;
import dev.warriorrr.inventories.gui.input.UserInputMethod;
import dev.warriorrr.inventories.gui.input.response.ErrorMessage;
import dev.warriorrr.inventories.gui.input.response.Finish;
import dev.warriorrr.inventories.gui.input.response.InputResponse;
import dev.warriorrr.inventories.gui.input.response.Nothing;
import dev.warriorrr.inventories.gui.input.response.OpenPreviousMenu;
import dev.warriorrr.inventories.gui.input.response.ReOpen;
import io.papermc.paper.connection.PlayerGameConnection;
import io.papermc.paper.dialog.Dialog;
import io.papermc.paper.event.player.PlayerCustomClickEvent;
import io.papermc.paper.registry.data.dialog.ActionButton;
import io.papermc.paper.registry.data.dialog.DialogBase;
import io.papermc.paper.registry.data.dialog.action.DialogAction;
import io.papermc.paper.registry.data.dialog.input.DialogInput;
import io.papermc.paper.registry.data.dialog.type.DialogType;
import net.kyori.adventure.dialog.DialogLike;
import net.kyori.adventure.key.Key;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.format.TextColor;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;

import java.util.List;
import java.util.Map;
import java.util.SequencedCollection;
import java.util.UUID;
import java.util.concurrent.ConcurrentHashMap;
import java.util.function.Function;

@SuppressWarnings("UnstableApiUsage")
public class DialogInputMethod implements UserInputMethod<DialogInputOptionsBuilder>, Listener {
    private final Map<UUID, DialogInputSession> sessions = new ConcurrentHashMap<>();

    @Override
    public void startAwaitingInput(Player player, MenuInventory currentInventory, DialogInputOptionsBuilder options) {
        DialogLike dialog = options.dialog;
        if (dialog == null) {
            final Key confirmKey = Key.key("inventories:user_input/confirm");

            dialog = Dialog.create(builder -> builder.empty()
                    .base(DialogBase.builder(options.title)
                            .inputs(List.of(
                                    DialogInput.text("input", Component.text("Input")).maxLength(1024).build()
                            )).build())
                    .type(DialogType.confirmation(
                            // thank you paper docs
                            ActionButton.create(
                                    Component.text("Confirm", TextColor.color(0xAEFFC1)),
                                    Component.text("Click to confirm your input."),
                                    100,
                                    DialogAction.customClick(confirmKey, null)
                            ),
                            ActionButton.create(
                                    Component.text("Discard", TextColor.color(0xFFA0B1)),
                                    Component.text("Click to discard your input."),
                                    100,
                                    null
                            )
                    )));

            if (options.inputFunction != null) {
                options.clickHandlers.put(confirmKey, response -> options.inputFunction.apply(new PlayerInput(response.getText("input"))));
            }
        }

        final DialogInputSession session = new DialogInputSession(options.clickHandlers);
        sessions.put(player.getUniqueId(), session);

        player.showDialog(dialog);
    }

    @Override
    public TextLength maximumTextLength() {
        return TextLength.ofAtLeast(1024);
    }

    @Override
    public DialogInputOptionsBuilder newOptionsBuilder() {
        return new DialogInputOptionsBuilder();
    }

    @EventHandler
    public void handleDialog(final PlayerCustomClickEvent event) {
        if (!(event.getCommonConnection() instanceof PlayerGameConnection conn)) {
            return;
        }
        final Player player = conn.getPlayer();

        final DialogInputSession session = sessions.get(player.getUniqueId());
        if (session == null) {
            return;
        }

        final Function<DialogResponse, SequencedCollection<InputResponse>> inputFunction = session.clickHandlers().get(event.getIdentifier().key());
        if (inputFunction == null) {
            return;
        }

        final SequencedCollection<InputResponse> responses = inputFunction.apply(new DialogResponse(conn.getPlayer(), event.getDialogResponseView()));
        for (final InputResponse response : responses) {
            switch (response) {
                case Finish ignored -> {
                    cancel(player);
                    player.closeDialog();
                    player.closeInventory();
                }
                case Nothing ignored -> {}
                case ReOpen reOpen -> {
                    MenuHistory.reOpen(player, reOpen.supplier());
                    cancel(player);
                }
                case ErrorMessage errorMessage -> player.sendMessage(errorMessage.error());
                case OpenPreviousMenu ignored -> {
                    cancel(player);
                    MenuHistory.last(player);
                }
                default -> throw new IllegalArgumentException("Unimplemented input response type " + response.getClass());
            }
        }
    }

    @Override
    public void disable() {
        sessions.clear();
    }

    @EventHandler
    public void onDisconnect(final PlayerConnectionCloseEvent event) {
        sessions.remove(event.getPlayerUniqueId());
    }

    private void cancel(final Player player) {
        this.sessions.remove(player.getUniqueId());
    }

    private record DialogInputSession(Map<Key, Function<DialogResponse, SequencedCollection<InputResponse>>> clickHandlers) {
        public DialogInputSession {
            clickHandlers = Map.copyOf(clickHandlers);
        }
    }
}
