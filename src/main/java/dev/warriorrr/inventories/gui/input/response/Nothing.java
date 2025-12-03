package dev.warriorrr.inventories.gui.input.response;

public record Nothing() implements InputResponse {
    static final Nothing INSTANCE = new Nothing();
}
