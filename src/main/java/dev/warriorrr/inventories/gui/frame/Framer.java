package dev.warriorrr.inventories.gui.frame;

import dev.warriorrr.inventories.gui.MenuInventory;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;

/**
 * An interface for customizing how items are framed within inventories.
 */
@FunctionalInterface
public interface Framer {
    /**
     * Called when a menu has been constructed and is in need of framing.
     *
     * @param menu The menu
     * @param inventory The inventory belonging to the menu
     */
    void frame(final MenuInventory menu, final Inventory inventory);

    /**
     * A simple framer implementation that exposes a method that's called for every item in the inventory.
     */
    abstract class Simple implements Framer {
        /**
         * Called for every slot in an inventory during framing.
         *
         * @param currentItem The current item being evaluated for framing.
         * @return The item to place at this current item's spot.
         */
        abstract ItemStack frame(final ItemStack currentItem);

        @Override
        public void frame(MenuInventory menu, Inventory inventory) {
            for (int i = 0; i < menu.size(); i++) {
                ItemStack stack = inventory.getItem(i);
                if (stack == null) {
                    stack = ItemStack.empty();
                }

                final ItemStack result = this.frame(stack);
                if (!result.equals(stack)) {
                    inventory.setItem(i, result);
                }
            }
        }
    }
}
