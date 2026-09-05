package dev.warriorrr.inventories.gui;

import com.google.common.base.Preconditions;
import io.papermc.paper.datacomponent.DataComponentTypes;
import io.papermc.paper.datacomponent.item.ItemLore;
import io.papermc.paper.datacomponent.item.ResolvableProfile;
import net.kyori.adventure.text.Component;
import dev.warriorrr.inventories.gui.action.ClickAction;
import dev.warriorrr.inventories.gui.slot.Slot;
import dev.warriorrr.inventories.gui.slot.anchor.SlotAnchor;
import net.kyori.adventure.text.format.TextDecoration;
import org.bukkit.Material;
import org.bukkit.NamespacedKey;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.ItemType;
import org.bukkit.persistence.PersistentDataType;
import org.jetbrains.annotations.ApiStatus;
import org.jetbrains.annotations.NotNull;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import java.util.UUID;
import java.util.function.Consumer;
import java.util.function.Supplier;

@SuppressWarnings("UnstableApiUsage") // data component api
public class MenuItem {
    public static final @NotNull NamespacedKey PDC_KEY = Objects.requireNonNull(NamespacedKey.fromString("inventories:item"));

    private Slot slot;
    private final ItemStack itemStack;
    private final List<ClickAction> actions = new ArrayList<>(0);

    public MenuItem(@NotNull ItemStack itemStack, @NotNull Slot slot) {
        this.itemStack = itemStack;
        this.slot = slot;
    }

    public void slot(int slot) {
        this.slot = SlotAnchor.ofSlot(slot);
    }

    public void slot(@NotNull Slot slot) {
        this.slot = slot;
    }

    @NotNull
    public Slot slot() {
        return this.slot;
    }

    public ItemStack itemStack() {
        return this.itemStack.clone();
    }

    @NotNull
    public List<ClickAction> actions() {
        return this.actions;
    }

    public void addAction(@NotNull ClickAction action) {
        this.actions.add(action);
    }

    public void addActions(@NotNull List<ClickAction> actions) {
        this.actions.addAll(actions);
    }

    @ApiStatus.Obsolete(since = "1.1.4")
    public static Builder builder(@NotNull Material type) {
        final ItemType itemType = type.asItemType();
        Preconditions.checkArgument(itemType != null, "%s isn't an item", type.name());

        return new Builder(itemType);
    }

    public static Builder builder(@NotNull ItemType type) {
        return new Builder(type);
    }

    public static Builder builder(@NotNull Supplier<@NotNull ItemStack> itemSupplier) {
        return new Builder(itemSupplier);
    }

    public static Builder builder(@NotNull ItemStack itemStack) {
        return new Builder(() -> itemStack);
    }

    public Builder builder() {
        Builder builder = builder(itemStack)
                .slot(slot);

        final ItemLore lore = itemStack.getData(DataComponentTypes.LORE);
        if (lore != null) {
            builder.loreBuilder = ItemLore.lore().addLines(lore.lines());
        }

        for (ClickAction clickAction : this.actions) {
            builder.action(clickAction);
        }

        return builder;
    }

    public static class Builder {
        private final ItemStack itemStack;
        private ItemLore.Builder loreBuilder = null;
        private Slot slot = SlotAnchor.ofSlot(0);
        private final List<ClickAction> actions = new ArrayList<>(0);
        private Consumer<ItemStack> postBuildConsumer = null;

        private Builder(ItemType type) {
            this.itemStack = type.createItemStack();
        }

        private Builder(Supplier<ItemStack> itemSupplier) {
            this.itemStack = itemSupplier.get().clone();
        }

        public Builder name(@NotNull Component name) {
            this.itemStack.setData(DataComponentTypes.ITEM_NAME, name);
            return this;
        }

        public Builder size(int size) {
            this.itemStack.setAmount(size);
            return this;
        }

        public Builder action(@NotNull ClickAction action) {
            this.actions.add(action);
            return this;
        }

        public Builder clearActions() {
            this.actions.clear();
            return this;
        }

        public Builder slot(int slot) {
            this.slot = SlotAnchor.ofSlot(slot);
            return this;
        }

        public Builder slot(Slot slot) {
            this.slot = slot;
            return this;
        }

        public Builder withGlint() {
            this.itemStack.setData(DataComponentTypes.ENCHANTMENT_GLINT_OVERRIDE, true);
            return this;
        }

        public Builder withGlint(boolean glint) {
            this.itemStack.setData(DataComponentTypes.ENCHANTMENT_GLINT_OVERRIDE, glint);
            return this;
        }

        public Builder lore(@NotNull Component lore) {
            if (loreBuilder == null) {
                loreBuilder = ItemLore.lore();
            }

            loreBuilder.addLine(lore.decorationIfAbsent(TextDecoration.ITALIC, TextDecoration.State.FALSE));
            return this;
        }

        @SuppressWarnings("unchecked")
        public Builder lore(@NotNull Supplier<Object> supplier) {
            Object object = supplier.get();

            if (object instanceof Component component)
                return this.lore(component);
            else if (object instanceof List<?> list)
                return this.lore((List<Component>) list);

            throw new IllegalArgumentException("Invalid lore class type: " + object.getClass().getName());
        }

        public Builder lore(@NotNull List<Component> lore) {
            lore.forEach(this::lore);
            return this;
        }

        public Builder skullOwner(@NotNull UUID ownerUUID) {
            this.itemStack.setData(DataComponentTypes.PROFILE, ResolvableProfile.resolvableProfile().uuid(ownerUUID).build());
            return this;
        }

        /**
         * Allows setting an {@link ItemStack} consumer for further modifying the built item.
         *
         * @param itemMutator The item consumer to set.
         * @return {@code this}
         */
        public Builder mutateItem(Consumer<ItemStack> itemMutator) {
            this.postBuildConsumer = itemMutator;
            return this;
        }

        public MenuItem build() {
            itemStack.editPersistentDataContainer(pdc -> pdc.set(PDC_KEY, PersistentDataType.BYTE, (byte) 1));

            if (this.loreBuilder != null) {
                itemStack.setData(DataComponentTypes.LORE, this.loreBuilder.build());
            }

            if (postBuildConsumer != null) {
                postBuildConsumer.accept(itemStack);
            }

            MenuItem item = new MenuItem(itemStack, slot);
            item.addActions(this.actions);

            return item;
        }
    }
}
