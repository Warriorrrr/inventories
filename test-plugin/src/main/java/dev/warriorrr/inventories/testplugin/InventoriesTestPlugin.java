package dev.warriorrr.inventories.testplugin;

import com.mojang.brigadier.Command;
import dev.warriorrr.inventories.Inventories;
import dev.warriorrr.inventories.event.input.StartAwaitingInputEvent;
import dev.warriorrr.inventories.gui.MenuInventory;
import dev.warriorrr.inventories.gui.MenuItem;
import dev.warriorrr.inventories.gui.action.ClickAction;
import dev.warriorrr.inventories.gui.input.BuiltinInputMethods;
import dev.warriorrr.inventories.gui.input.TextLength;
import dev.warriorrr.inventories.gui.input.impl.text.ChatInputBackend;
import dev.warriorrr.inventories.gui.input.impl.text.ChatInputOptionsBuilder;
import dev.warriorrr.inventories.gui.input.response.InputResponse;
import io.papermc.paper.command.brigadier.Commands;
import io.papermc.paper.plugin.lifecycle.event.types.LifecycleEvents;
import net.kyori.adventure.text.Component;
import org.bukkit.Material;
import org.bukkit.block.BlockType;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.plugin.java.JavaPlugin;

import java.util.Collections;

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
                                .action(ClickAction.userInput(BuiltinInputMethods.CHAT, builder -> builder
                                        .title(Component.text("Enter how much you wish to offer:"))
                                        .onInput((input -> {
                                            ctx.getSource().getSender().sendPlainMessage("You entered: " + input.getText());
                                            return Collections.singletonList(InputResponse.finish());
                                        }))
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
        if (event.getInputBackend() instanceof ChatInputBackend) {
            ((ChatInputOptionsBuilder) event.getOptionsBuilder()).addStartMessage(Component.text("Enter in chat:"));
        }
    }
}
