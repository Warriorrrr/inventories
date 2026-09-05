package dev.warriorrr.inventories.gui.frame;

import dev.warriorrr.inventories.gui.MenuItem;
import io.papermc.paper.datacomponent.DataComponentTypes;
import io.papermc.paper.datacomponent.item.TooltipDisplay;
import net.kyori.adventure.text.Component;
import org.bukkit.Material;
import org.bukkit.inventory.ItemStack;

/**
 * A class holding utility methods for constructing a {@link Framer}.
 */
public class Framing {
    private Framing() {}

    @SuppressWarnings("UnstableApiUsage")
    private static final ItemStack BACKGROUND_GLASS = MenuItem.builder(Material.GRAY_STAINED_GLASS_PANE).name(Component.empty()).mutateItem(item -> item.setData(DataComponentTypes.TOOLTIP_DISPLAY, TooltipDisplay.tooltipDisplay().hideTooltip(true).build())).build().itemStack();

    private static final Framer DEFAULT_FRAMER = fillEmptySpace(BACKGROUND_GLASS);
    private static final Framer NOTHING = (menu, inventory) -> {};

    /**
     * {@return a default framer that fills empty space with an opinionated item}
     */
    public static Framer defaultFramer() {
        return DEFAULT_FRAMER;
    }

    /**
     * {@return a framer that fills in empty slots with the given item}
     * @param filler The item to full the background with
     */
    public static Framer fillEmptySpace(final ItemStack filler) {
        if (filler.isEmpty()) {
            return blank();
        }

        return new Framer.Simple() {
            @Override
            ItemStack frame(ItemStack currentItem) {
                return currentItem.isEmpty() ? filler : currentItem;
            }
        };
    }

    /**
     * {@return a blank no-op framer}
     */
    public static Framer blank() {
        return NOTHING;
    }
}
