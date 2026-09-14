package com.r4men.game_knight.gui.screen;

import com.mojang.blaze3d.platform.InputConstants;
import com.r4men.game_knight.GKConfig;
import com.r4men.game_knight.GameKnight;
import com.r4men.game_knight.engine.chess.Board;
import com.r4men.game_knight.gui.GKScreen;
import com.r4men.game_knight.gui.widget.GKSelectableButton;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiGraphicsExtractor;
import net.minecraft.client.input.KeyEvent;
import net.minecraft.client.input.MouseButtonEvent;
import net.minecraft.client.renderer.RenderPipelines;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.Identifier;
import org.jetbrains.annotations.NotNull;

import java.util.List;
import java.util.Map;

import static com.r4men.game_knight.GameKnightClient.FLIP_BOARD;

public class ChessSetupScreen extends GKScreen {
    private static final Identifier BACKGROUND = GameKnight.id("backgrounds/17px");

    private GKSelectableButton button1;
    private GKSelectableButton button2;
    private GKSelectableButton button3;

    public ChessSetupScreen(Component title) {
        super(title, 256, 256);

        this.titleLabelY = -1000000;
    }

    @Override
    protected void init() {
        super.init();

        this.button1 = this.addRenderableWidget(
                new GKSelectableButton(
                        this.leftPos + 5,
                        this.topPos + 5,
                        79,
                        60
                )
        );

        this.button2 = this.addRenderableWidget(
                new GKSelectableButton(
                        this.leftPos + 88,
                        this.topPos + 5,
                        80,
                        60
                )
        );

        this.button3 = this.addRenderableWidget(
                new GKSelectableButton(
                        this.leftPos + 172,
                        this.topPos + 5,
                        79,
                        60
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
    }


    // TODO Do I need this?
    private void extractLabels(@NotNull GuiGraphicsExtractor graphics, int mouseX, int mouseY) {

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
        GKSelectableButton.State state1 = button1.getState();
        GKSelectableButton.State state2 = button1.getState();
        GKSelectableButton.State state3 = button1.getState();

        if (button1.isHoveredOrFocused()) {
            if (state1 == GKSelectableButton.State.NORMAL) {
                button1.setState(GKSelectableButton.State.HOVERED);
            }
        } else {
            if (state1 != GKSelectableButton.State.SELECTED) {
                button1.setState(GKSelectableButton.State.NORMAL);
            }
        }

        if (button2.isHoveredOrFocused()) {
            if (state2 == GKSelectableButton.State.NORMAL) {
                button2.setState(GKSelectableButton.State.HOVERED);
            }
        } else {
            if (state2 != GKSelectableButton.State.SELECTED) {
                button2.setState(GKSelectableButton.State.NORMAL);
            }
        }

        if (button3.isHoveredOrFocused()) {
            if (state3 == GKSelectableButton.State.NORMAL) {
                button3.setState(GKSelectableButton.State.HOVERED);
            }
        } else {
            if (state3 != GKSelectableButton.State.SELECTED) {
                button3.setState(GKSelectableButton.State.NORMAL);
            }
        }

        super.mouseMoved(x, y);
    }

    @Override
    public boolean mouseClicked(@NotNull MouseButtonEvent event, boolean doubleClick) {
        if (button1.isHovered()) {
            button1.setState(GKSelectableButton.State.SELECTED);
        }

        if (button2.isHovered()) {
            button2.setState(GKSelectableButton.State.SELECTED);
        }

        if (button3.isHovered()) {
            button3.setState(GKSelectableButton.State.SELECTED);
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
