package dev.warriorrr.inventories.gui.input.impl.dialog;

import io.papermc.paper.dialog.DialogResponseView;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.ApiStatus;
import org.jspecify.annotations.Nullable;

public record DialogResponse(Player player, @ApiStatus.Internal DialogResponseView responseView) {
    public @Nullable String getText(final String key) {
        return this.responseView.getText(key);
    }

    public @Nullable Boolean getBoolean(final String key) {
        return this.responseView.getBoolean(key);
    }

    public @Nullable Float getFloat(final String key) {
        return this.responseView.getFloat(key);
    }
}
