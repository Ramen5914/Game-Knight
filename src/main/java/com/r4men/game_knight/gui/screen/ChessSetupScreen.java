package com.r4men.game_knight.gui.screen;

import com.r4men.game_knight.GameKnight;
import com.r4men.game_knight.gui.GKScreen;
import com.r4men.game_knight.gui.widget.GKButton;
import com.r4men.game_knight.gui.widget.GKSelectableButton;
import net.minecraft.client.gui.GuiGraphicsExtractor;
import net.minecraft.client.gui.components.Checkbox;
import net.minecraft.client.input.KeyEvent;
import net.minecraft.client.input.MouseButtonEvent;
import net.minecraft.client.renderer.RenderPipelines;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.Identifier;
import org.jetbrains.annotations.NotNull;

public class ChessSetupScreen extends GKScreen {
    private static final Identifier BACKGROUND = GameKnight.id("backgrounds/17px");
    private static final Identifier LICHESS_LOGO = GameKnight.id("icons/white_lichess");

    private GKSelectableButton select1;
    private GKSelectableButton select2;
    private GKSelectableButton select3;

    private GKButton completeButton;

    private Checkbox checkbox1;

    public ChessSetupScreen(Component title) {
        super(title, 256, 256);

        this.titleLabelY = -1000000;
    }

    @Override
    protected void init() {
        super.init();

        this.checkbox1 = this.addRenderableWidget(
                new Checkbox(80, 80, 1000, Component.literal("Test"), this.font, false, Checkbox.OnValueChange.NOP)
        );

        this.select1 = this.addRenderableWidget(
                new GKSelectableButton(
                        this.leftPos + 5,
                        this.topPos + 5,
                        79,
                        60
                )
        );

        this.select1.setState(GKSelectableButton.SelectState.SELECTED);

        this.select2 = this.addRenderableWidget(
                new GKSelectableButton(
                        this.leftPos + 88,
                        this.topPos + 5,
                        80,
                        60
                )
        );

        this.select3 = this.addRenderableWidget(
                new GKSelectableButton(
                        this.leftPos + 172,
                        this.topPos + 5,
                        79,
                        60
                )
        );

        this.completeButton = this.addRenderableWidget(
                new GKButton(
                        this.leftPos + 42,
                        this.topPos + 225,
                        180,
                        24
                )
        );
    }

    @Override
    public void extractBackground(@NotNull GuiGraphicsExtractor graphics, int mouseX, int mouseY, float a) {
        super.extractBackground(graphics, mouseX, mouseY, a);

        graphics.blitSprite(RenderPipelines.GUI_TEXTURED, BACKGROUND, this.leftPos, this.topPos, this.imageWidth, this.imageHeight, 0xFF222222);
    }

    @Override
    public void extractRenderState(@NotNull GuiGraphicsExtractor graphics, int mouseX, int mouseY, float a) {
        super.extractRenderState(graphics, mouseX, mouseY, a);

        this.extractLabels(graphics, mouseX, mouseY);
    }


    // TODO Do I need this?
    private void extractLabels(@NotNull GuiGraphicsExtractor graphics, int mouseX, int mouseY) {
        graphics.text(this.font, Component.literal("Lichess"), this.leftPos + 26, this.topPos + 20, 0xFFFFFFFF, false);

        graphics.blitSprite(RenderPipelines.GUI_TEXTURED, LICHESS_LOGO, this.leftPos + 38, this.topPos + 35, 16, 16, 0xFFFFFFFF);
    }

    // TODO Do I need this?
    @Override
    public boolean keyPressed(@NotNull KeyEvent event) {
        return super.keyPressed(event);
    }

    // TODO Do I need this?
    @Override
    public boolean keyReleased(@NotNull KeyEvent event) {
        return super.keyReleased(event);
    }

    // TODO Do I need this?
    @Override
    public void mouseMoved(double x, double y) {
        GKSelectableButton.SelectState selectState1 = select1.getState();
        GKSelectableButton.SelectState selectState2 = select2.getState();
        GKSelectableButton.SelectState selectState3 = select3.getState();

        GKButton.ButtonState buttonState1 = completeButton.getState();

        if (select1.isHoveredOrFocused()) {
            if (selectState1 == GKSelectableButton.SelectState.NORMAL) {
                select1.setState(GKSelectableButton.SelectState.HOVERED);
            }
        } else {
            if (selectState1 != GKSelectableButton.SelectState.SELECTED) {
                select1.setState(GKSelectableButton.SelectState.NORMAL);
            }
        }

        if (select2.isHoveredOrFocused()) {
            if (selectState2 == GKSelectableButton.SelectState.NORMAL) {
                select2.setState(GKSelectableButton.SelectState.HOVERED);
            }
        } else {
            if (selectState2 != GKSelectableButton.SelectState.SELECTED) {
                select2.setState(GKSelectableButton.SelectState.NORMAL);
            }
        }

        if (select3.isHoveredOrFocused()) {
            if (selectState3 == GKSelectableButton.SelectState.NORMAL) {
                select3.setState(GKSelectableButton.SelectState.HOVERED);
            }
        } else {
            if (selectState3 != GKSelectableButton.SelectState.SELECTED) {
                select3.setState(GKSelectableButton.SelectState.NORMAL);
            }
        }

        if (completeButton.isHoveredOrFocused()) {
            completeButton.setState(GKButton.ButtonState.HOVERED);
        } else {
            completeButton.setState(GKButton.ButtonState.NORMAL);
        }

        super.mouseMoved(x, y);
    }

    @Override
    public boolean mouseClicked(@NotNull MouseButtonEvent event, boolean doubleClick) {
        if (select1.isHovered()) {
            select1.setState(GKSelectableButton.SelectState.SELECTED);
            select2.setState(GKSelectableButton.SelectState.NORMAL);
            select3.setState(GKSelectableButton.SelectState.NORMAL);
        } else if (select2.isHovered()) {
            select1.setState(GKSelectableButton.SelectState.NORMAL);
            select2.setState(GKSelectableButton.SelectState.SELECTED);
            select3.setState(GKSelectableButton.SelectState.NORMAL);
        } else if (select3.isHovered()) {
            select1.setState(GKSelectableButton.SelectState.NORMAL);
            select2.setState(GKSelectableButton.SelectState.NORMAL);
            select3.setState(GKSelectableButton.SelectState.SELECTED);
        } else if (completeButton.isHovered()) {
            completeButton.setState(GKButton.ButtonState.CLICKED);

            try {
                wait(500);
            } catch (InterruptedException e) {
                throw new RuntimeException(e);
            }

            completeButton.setState(GKButton.ButtonState.NORMAL);
        }

        return super.mouseClicked(event, doubleClick);
    }

    // TODO Do I need this?
    @Override
    public boolean mouseReleased(@NotNull MouseButtonEvent event) {
        return super.mouseReleased(event);
    }

    // TODO Do I need this?
    @Override
    public boolean mouseScrolled(double x, double y, double scrollX, double scrollY) {
        return super.mouseScrolled(x, y, scrollX, scrollY);
    }

    // TODO Do I need this?
    @Override
    public boolean mouseDragged(@NotNull MouseButtonEvent event, double dx, double dy) {
        return super.mouseDragged(event, dx, dy);
    }

    @Override
    public boolean isPauseScreen() {
        return false;
    }
}
