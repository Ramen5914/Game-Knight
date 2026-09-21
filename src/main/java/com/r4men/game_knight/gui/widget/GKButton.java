package com.r4men.game_knight.gui.widget;

import com.r4men.game_knight.GameKnight;
import net.minecraft.client.gui.GuiGraphicsExtractor;
import net.minecraft.client.gui.components.AbstractWidget;
import net.minecraft.client.gui.narration.NarrationElementOutput;
import net.minecraft.client.renderer.RenderPipelines;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.Identifier;
import org.jspecify.annotations.NonNull;

public class GKButton extends AbstractWidget {
    private static final Identifier BACKGROUND = GameKnight.id("backgrounds/8px");

    private ButtonState buttonState = ButtonState.NORMAL;

    public GKButton(int x, int y, int width, int height) {
        super(x, y, width, height, Component.empty());
    }

    public void setState(ButtonState buttonState) {
        this.buttonState = buttonState;
    }

    public ButtonState getState() {
        return buttonState;
    }

    @Override
    protected void extractWidgetRenderState(@NonNull GuiGraphicsExtractor graphics, int mouseX, int mouseY, float a) {
        int color = switch (this.buttonState) {
            case NORMAL -> 0xFFA74FFF;
            case HOVERED -> 0xFF632F96;
            case CLICKED -> 0xFF46216B;
        };

        graphics.blitSprite(
                RenderPipelines.GUI_TEXTURED,
                BACKGROUND,
                this.getX(),
                this.getY(),
                this.getWidth(),
                this.getHeight(),
                color
        );
    }

    @Override
    protected void updateWidgetNarration(NarrationElementOutput output) {

    }

    public enum ButtonState {
        NORMAL, HOVERED, CLICKED
    }
}
