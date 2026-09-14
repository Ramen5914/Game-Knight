package com.r4men.game_knight.gui.widget;

import com.r4men.game_knight.GameKnight;
import net.minecraft.client.gui.GuiGraphicsExtractor;
import net.minecraft.client.gui.components.AbstractWidget;
import net.minecraft.client.gui.narration.NarrationElementOutput;
import net.minecraft.client.renderer.RenderPipelines;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.Identifier;
import org.jspecify.annotations.NonNull;

public class GKSelectableButton extends AbstractWidget {
    private static final Identifier NORMAL = GameKnight.id("buttons/12px_base");
    private static final Identifier HOVERED = GameKnight.id("buttons/12px_hover");
    private static final Identifier SELECTED = GameKnight.id("buttons/12px_select");

    private State state = State.NORMAL;

    public GKSelectableButton(int x, int y, int width, int height) {
        super(x, y, width, height, Component.empty());
    }

    public void setState(State state) {
        this.state = state;
    }

    public State getState() {
        return this.state;
    }

    @Override
    protected void extractWidgetRenderState(@NonNull GuiGraphicsExtractor graphics, int mouseX, int mouseY, float a) {
        Identifier sprite = switch (this.state) {
            case NORMAL -> NORMAL;
            case HOVERED -> HOVERED;
            case SELECTED -> SELECTED;
        };

        graphics.blitSprite(
                RenderPipelines.GUI_TEXTURED,
                sprite,
                this.getX(),
                this.getY(),
                this.getWidth(),
                this.getHeight()
        );
    }

    @Override
    protected void updateWidgetNarration(@NonNull NarrationElementOutput output) {

    }

    public enum State {
        NORMAL, HOVERED, SELECTED
    }
}
