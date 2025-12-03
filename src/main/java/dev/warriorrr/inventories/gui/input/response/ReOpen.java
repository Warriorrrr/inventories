package dev.warriorrr.inventories.gui.input.response;

import dev.warriorrr.inventories.gui.MenuInventory;
import java.util.function.Supplier;

public record ReOpen(Supplier<MenuInventory> supplier) implements InputResponse {
}
