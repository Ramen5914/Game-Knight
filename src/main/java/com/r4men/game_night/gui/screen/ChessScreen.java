package com.r4men.game_night.gui.screen;

import com.llamalad7.mixinextras.lib.apache.commons.tuple.Pair;
import com.r4men.game_night.GNConfig;
import com.r4men.game_night.GameNight;
import com.r4men.game_night.gui.menu.ChessMenu;
import net.minecraft.client.gui.GuiGraphicsExtractor;
import net.minecraft.client.gui.screens.inventory.AbstractContainerScreen;
import net.minecraft.client.input.KeyEvent;
import net.minecraft.client.input.MouseButtonEvent;
import net.minecraft.client.renderer.RenderPipelines;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.Identifier;
import net.minecraft.world.entity.Interaction;
import net.minecraft.world.entity.player.Inventory;
import org.jspecify.annotations.NonNull;

import java.util.Map;

public class ChessScreen extends AbstractContainerScreen<ChessMenu> {
    private final Identifier BOARD = GameNight.getIdentifier("textures/gui/container/chess.png");

    private Pair<Integer, Integer> clickedSquare = null;

    private final Map<Character, Identifier> PIECE_SPRITES = Map.ofEntries(
            Map.entry('b', GameNight.getIdentifier("container/chess/black_bishop")),
            Map.entry('k', GameNight.getIdentifier("container/chess/black_king")),
            Map.entry('n', GameNight.getIdentifier("container/chess/black_knight")),
            Map.entry('p', GameNight.getIdentifier("container/chess/black_pawn")),
            Map.entry('q', GameNight.getIdentifier("container/chess/black_queen")),
            Map.entry('r', GameNight.getIdentifier("container/chess/black_rook")),
            Map.entry('B', GameNight.getIdentifier("container/chess/white_bishop")),
            Map.entry('K', GameNight.getIdentifier("container/chess/white_king")),
            Map.entry('N', GameNight.getIdentifier("container/chess/white_knight")),
            Map.entry('P', GameNight.getIdentifier("container/chess/white_pawn")),
            Map.entry('Q', GameNight.getIdentifier("container/chess/white_queen")),
            Map.entry('R', GameNight.getIdentifier("container/chess/white_rook"))
    );

    public ChessScreen(ChessMenu menu, Inventory inventory, Component title) {
        super(menu, inventory, title, 256, 256);

        this.titleLabelY = -1000000;
        this.inventoryLabelY = -1000000;
    }

    @Override
    protected void init() {
        super.init();
    }

    @Override
    public void extractBackground(@NonNull GuiGraphicsExtractor graphics, int mouseX, int mouseY, float a) {
        super.extractBackground(graphics, mouseX, mouseY, a);

        graphics.blit(BOARD, leftPos, topPos, leftPos + imageWidth, topPos + imageHeight, 0, 1, 0, 1);
    }

    @Override
    public void extractRenderState(@NonNull GuiGraphicsExtractor graphics, int mouseX, int mouseY, float a) {
        super.extractRenderState(graphics, mouseX, mouseY, a);

        String[] pieces = menu.getSimplifiedFen().split("/");

        if (clickedSquare != null) {
            if (pieces[clickedSquare.getRight()].charAt(clickedSquare.getLeft()) != ' ') {
                int x0 = this.leftPos + clickedSquare.getLeft() * (imageWidth / 8);
                int y0 = this.topPos + clickedSquare.getRight() * (imageHeight / 8);

                graphics.fill(x0, y0, x0 +imageWidth / 8, y0 + imageHeight / 8, 0xA01FABFF);
            }
        }

        for (int y = 0; y < 8; y++) {
            for (int x = 0; x < 8; x++) {
                Identifier piece = PIECE_SPRITES.get(pieces[y].charAt(x));

                if (piece != null) {
                    graphics.blitSprite(RenderPipelines.GUI_TEXTURED, piece, leftPos + (imageWidth/8 * x), topPos + (imageHeight/8 * y), imageWidth/8, imageHeight/8);
                }
            }
        }
    }

    @Override
    protected void extractLabels(@NonNull GuiGraphicsExtractor graphics, int xm, int ym) {
        super.extractLabels(graphics, xm, ym);

        if (GNConfig.SHOW_COORDINATES.get()) {
            for (int i = 0; i < 8; i++) {
                int color;

                if (i % 2 == 0) {
                    color = 0xFFFFFFFF;
                } else {
                    color = 0xFF000000;
                }

                graphics.text(this.font, Character.toString(97 + i), 1 + (i * (this.imageWidth / 8)), this.imageHeight - this.font.lineHeight + 1, color, false);
                graphics.text(this.font, Character.toString(56 - i), this.imageHeight - this.font.width(Character.toString(56 - i)), 1 + (i * (this.imageWidth / 8)), color, false);
            }
        }
    }

    @Override
    protected void extractTooltip(@NonNull GuiGraphicsExtractor graphics, int mouseX, int mouseY) {
        super.extractTooltip(graphics, mouseX, mouseY);
    }

    @Override
    public boolean keyPressed(@NonNull KeyEvent event) {

        return super.keyPressed(event);
    }

    @Override
    public boolean keyReleased(@NonNull KeyEvent event) {

        return super.keyReleased(event);
    }

    @Override
    protected boolean isHovering(int left, int top, int w, int h, double xm, double ym) {
        return super.isHovering(left, top, w, h, xm, ym);
    }

    @Override
    public void mouseMoved(double x, double y) {

        super.mouseMoved(x, y);
    }

    @Override
    public boolean mouseClicked(@NonNull MouseButtonEvent event, boolean doubleClick) {
        if (clickedOnBoard(event.x(), event.y())) {
            this.clickedSquare = getClickedSquare(event.x(), event.y());
        } else {
            this.clickedSquare = null;
        }

        return super.mouseClicked(event, doubleClick);
    }

    private Pair<Integer, Integer> getClickedSquare(double x, double y) {
        int file = (int) ((x - leftPos) / (imageWidth / 8f));
        int rank = (int) ((y - topPos) / (imageHeight / 8f));

        Pair<Integer, Integer> square = Pair.of(file, rank);

        if (square.equals(this.clickedSquare)) {
            return null;
        } else {
            return square;
        }

    }

    private boolean clickedOnBoard(double x, double y) {
        return x >= leftPos && x <= leftPos + imageWidth && y >= topPos && y <= topPos + imageHeight;
    }

    @Override
    public boolean mouseReleased(@NonNull MouseButtonEvent event) {
        return super.mouseReleased(event);
    }

    @Override
    public boolean mouseScrolled(double x, double y, double scrollX, double scrollY) {
        return super.mouseScrolled(x, y, scrollX, scrollY);
    }

    @Override
    public boolean mouseDragged(@NonNull MouseButtonEvent event, double dx, double dy) {

        return super.mouseDragged(event, dx, dy);
    }
}
