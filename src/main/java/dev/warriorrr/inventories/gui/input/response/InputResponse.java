package dev.warriorrr.inventories.gui.input.response;

import dev.warriorrr.inventories.gui.MenuInventory;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.format.NamedTextColor;
import java.util.function.Supplier;

/**
 * Different actions for responding to user input.
 */
public sealed interface InputResponse permits ErrorMessage, Finish, Nothing, OpenPreviousMenu, ReOpen {
    static InputResponse reOpen(Supplier<MenuInventory> supplier) {
        return new ReOpen(supplier);
    }

    static InputResponse errorMessage(Component error) {
        return new ErrorMessage(error.colorIfAbsent(NamedTextColor.DARK_RED));
    }

    static InputResponse doNothing() {
        return Nothing.INSTANCE;
    }

    static InputResponse finish() {
        return Finish.INSTANCE;
    }

    static InputResponse openPreviousMenu() {
        return OpenPreviousMenu.INSTANCE;
    }
}
