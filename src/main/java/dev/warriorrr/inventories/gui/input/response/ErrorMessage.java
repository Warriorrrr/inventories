package dev.warriorrr.inventories.gui.input.response;

import net.kyori.adventure.text.Component;

public record ErrorMessage(Component error) implements InputResponse {
}
