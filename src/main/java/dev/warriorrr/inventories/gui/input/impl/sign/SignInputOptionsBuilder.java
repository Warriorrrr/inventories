package dev.warriorrr.inventories.gui.input.impl.sign;

import com.google.common.base.Preconditions;
import dev.warriorrr.inventories.gui.input.InputOptionsBuilder;
import dev.warriorrr.inventories.gui.input.PlayerInput;
import dev.warriorrr.inventories.gui.input.response.InputResponse;
import net.kyori.adventure.text.Component;
import org.bukkit.block.BlockType;
import org.bukkit.block.data.type.HangingSign;
import org.bukkit.block.data.type.Sign;
import org.jetbrains.annotations.Range;

import java.util.ArrayList;
import java.util.List;
import java.util.function.Function;

public class SignInputOptionsBuilder implements InputOptionsBuilder {
    protected BlockType.Typed<?> signType = BlockType.OAK_SIGN;
    protected List<Component> lines = new ArrayList<>();
    protected Function<PlayerInput, List<InputResponse>> inputFunction;

    protected SignInputOptionsBuilder() {
        for (int i = 0; i < 4; i++) {
            this.lines.add(Component.empty());
        }

        this.lines.set(1, Component.text("^^^^^^^^^^^"));
    }

    @Override
    public SignInputOptionsBuilder title(Component title) {
        this.lines.set(2, title);
        return this;
    }

    public SignInputOptionsBuilder line(final @Range(from = 0, to = 3) int line, final Component content) {
        Preconditions.checkArgument(line >= 0 && line < 4, "line must be [0-3], got %s", line);
        this.lines.set(line, content);
        return this;
    }

    @Override
    public SignInputOptionsBuilder onInput(Function<PlayerInput, List<InputResponse>> inputFunction) {
        this.inputFunction = inputFunction;
        return this;
    }

    public SignInputOptionsBuilder signType(final BlockType.Typed<Sign> signType) {
        this.signType = signType;
        return this;
    }

    public SignInputOptionsBuilder hangingSignType(final BlockType.Typed<HangingSign> signType) {
        this.signType = signType;
        return this;
    }
}
