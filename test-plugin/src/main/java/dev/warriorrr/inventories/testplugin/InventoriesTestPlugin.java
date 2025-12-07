package dev.warriorrr.inventories.testplugin;

import com.mojang.brigadier.Command;
import dev.warriorrr.inventories.Inventories;
import dev.warriorrr.inventories.gui.MenuInventory;
import dev.warriorrr.inventories.gui.MenuItem;
import dev.warriorrr.inventories.gui.action.ClickAction;
import dev.warriorrr.inventories.gui.input.response.InputResponse;
import io.papermc.paper.command.brigadier.Commands;
import io.papermc.paper.plugin.lifecycle.event.types.LifecycleEvents;
import net.kyori.adventure.text.Component;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.plugin.java.JavaPlugin;

public class InventoriesTestPlugin extends JavaPlugin {
    @Override
    public void onEnable() {
        final Inventories instance = Inventories.forPlugin(this).build();

        getLifecycleManager().registerEventHandler(LifecycleEvents.COMMANDS, event -> {
            event.registrar().register(Commands.literal("open").executes(ctx -> {
                MenuInventory.builder()
                        .title(Component.text("hi"))
                        .rows(3)
                        .addItem(MenuItem.builder(Material.RED_WOOL)
                                .action(ClickAction.userInput(Component.text("enter your text"), input -> {
                                    ctx.getSource().getSender().sendPlainMessage("You entered: " + input.getText());
                                    return InputResponse.finish();
                                })).build())
                        .build().open((Player) ctx.getSource().getSender());
                return Command.SINGLE_SUCCESS;
            }).build());
        });
    }
}
