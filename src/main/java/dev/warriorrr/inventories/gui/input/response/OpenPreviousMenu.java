package dev.warriorrr.inventories.gui.input.response;

public record OpenPreviousMenu() implements InputResponse {
    static final OpenPreviousMenu INSTANCE = new OpenPreviousMenu();
}
