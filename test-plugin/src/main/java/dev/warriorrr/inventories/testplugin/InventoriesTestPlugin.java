package dev.warriorrr.inventories.testplugin;

import com.mojang.brigadier.Command;
import dev.warriorrr.inventories.Inventories;
import dev.warriorrr.inventories.event.input.StartAwaitingInputEvent;
import dev.warriorrr.inventories.gui.MenuInventory;
import dev.warriorrr.inventories.gui.MenuItem;
import dev.warriorrr.inventories.gui.action.ClickAction;
import dev.warriorrr.inventories.gui.input.BuiltinInputMethods;
import dev.warriorrr.inventories.gui.input.impl.text.ChatInputMethod;
import dev.warriorrr.inventories.gui.input.impl.text.ChatInputOptionsBuilder;
import dev.warriorrr.inventories.gui.input.response.InputResponse;
import io.papermc.paper.command.brigadier.Commands;
import io.papermc.paper.dialog.Dialog;
import io.papermc.paper.plugin.lifecycle.event.types.LifecycleEvents;
import io.papermc.paper.registry.data.dialog.ActionButton;
import io.papermc.paper.registry.data.dialog.DialogBase;
import io.papermc.paper.registry.data.dialog.action.DialogAction;
import io.papermc.paper.registry.data.dialog.input.DialogInput;
import io.papermc.paper.registry.data.dialog.type.DialogType;
import net.kyori.adventure.key.Key;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.format.TextColor;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.plugin.java.JavaPlugin;

import java.util.Collections;
import java.util.List;

public class InventoriesTestPlugin extends JavaPlugin implements Listener {
    @Override
    public void onEnable() {
        final Inventories instance = Inventories.forPlugin(this).build();
        getServer().getPluginManager().registerEvents(this, this);

        getLifecycleManager().registerEventHandler(LifecycleEvents.COMMANDS, event -> {
            event.registrar().register(Commands.literal("open").executes(ctx -> {
                MenuInventory.builder()
                        .title(Component.text("hi"))
                        .rows(3)
                        .addItem(MenuItem.builder(Material.RED_WOOL)
                                .action(ClickAction.userInput(BuiltinInputMethods.DIALOG, b -> b
                                        /*.dialog(Dialog.create(builder -> builder.empty()
                                                .type(DialogType.confirmation(
                                                        ActionButton.create(
                                                                Component.text("Confirm", TextColor.color(0xAEFFC1)),
                                                                Component.text("Click to confirm your input."),
                                                                100,
                                                                DialogAction.customClick(Key.key("inventories:confirm"), null)
                                                        ),
                                                        ActionButton.create(
                                                                Component.text("Discard", TextColor.color(0xFFA0B1)),
                                                                Component.text("Click to discard your input."),
                                                                100,
                                                                null
                                                        )
                                                ))
                                                .base(DialogBase.builder(Component.text("this is my dialog")).inputs(List.of(
                                                        DialogInput.text("text", Component.text("enter text value here")).build(),
                                                        DialogInput.numberRange("number", Component.text("number input"), 1, 100).build()
                                                )).build())))
                                        .clickHandler(Key.key("inventories:confirm"), input -> {
                                            input.player().sendMessage("text input: " + input.getText("text"));
                                            input.player().sendMessage("number input: " + input.getFloat("number"));
                                            return List.of(InputResponse.finish());
                                        })*/
                                        .title(Component.text("Your input:"))
                                        .onInput(input -> {
                                            ctx.getSource().getSender().sendPlainMessage("You sent: " + input.getText());
                                            return Collections.singletonList(InputResponse.finish());
                                        })
                                )).build())
                        .addItem(MenuItem.builder(Material.ACACIA_BOAT)
                                .name(Component.text("pagination with limited rows"))
                                .slot(1)
                                .action(ClickAction.openInventory(() -> {
                                    final MenuInventory.PaginatorBuilder builder = MenuInventory.paginator();
                                    builder.maxRows(2);
                                    for (int i = 0; i < 100; i++) {
                                        builder.addItem(MenuItem.builder(Material.DIRT).name(Component.text(i)).build());
                                    }
                                    return builder.build();
                                }))
                                .build())
                        .build().open((Player) ctx.getSource().getSender());
                return Command.SINGLE_SUCCESS;
            }).build());
        });
    }

    @EventHandler
    public void on(StartAwaitingInputEvent event) {
        if (event.getInputMethod() instanceof ChatInputMethod) {
            ((ChatInputOptionsBuilder) event.getOptionsBuilder()).addStartMessage(Component.text("Enter in chat:"));
        }
    }
}
