package com.r4men.game_night.gui.screen;

import com.mojang.blaze3d.platform.InputConstants;
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
import net.minecraft.world.entity.player.Inventory;
import org.jetbrains.annotations.NotNull;

import java.util.List;
import java.util.Map;

import static com.r4men.game_night.GameNightClient.FLIP_BOARD;


public class ChessScreen extends AbstractContainerScreen<ChessMenu> {
    private final Identifier BOARD = GameNight.getIdentifier("textures/gui/container/chess.png");
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
    private int clickedSquare = -1;
    private List<Integer> legalMoves = null;

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
    public void extractBackground(@NotNull GuiGraphicsExtractor graphics, int mouseX, int mouseY, float a) {
        super.extractBackground(graphics, mouseX, mouseY, a);

        graphics.blit(BOARD, leftPos, topPos, leftPos + imageWidth, topPos + imageHeight, 0, 1, 0, 1);
    }

    @Override
    public void extractRenderState(@NotNull GuiGraphicsExtractor graphics, int mouseX, int mouseY, float a) {
        super.extractRenderState(graphics, mouseX, mouseY, a);

        String[] pieces = menu.getSimplifiedFen().split("/");

        if (clickedSquare >= 0) {
            if (pieces[7 - clickedSquare / 8].charAt(clickedSquare % 8) != ' ') {
                int x0 = this.leftPos + (clickedSquare % 8) * (imageWidth / 8);
                int y0 = this.topPos + (7 - clickedSquare / 8) * (imageHeight / 8);

                graphics.fill(x0, y0, x0 + imageWidth / 8, y0 + imageHeight / 8, GNConfig.SELECT_COLOR.get());

                for (int move : legalMoves) {
                    int width = imageWidth / 8 / 3;
                    int height = imageHeight / 8 / 3;

                    int x1 = this.leftPos + (move % 8) * (imageWidth / 8) + (imageWidth / 8 / 2) - width / 2;
                    int y1 = this.topPos + (7 - (move / 8)) * (imageHeight / 8) + (imageHeight / 8 / 2) - height / 2;

                    graphics.fill(x1, y1, x1 + width, y1 + height, GNConfig.SELECT_COLOR.get());
                }
            }
        }

        for (int y = 0; y < 8; y++) {
            for (int x = 0; x < 8; x++) {
                Identifier piece = PIECE_SPRITES.get(pieces[y].charAt(x));

                if (piece != null) {
                    graphics.blitSprite(RenderPipelines.GUI_TEXTURED, piece, leftPos + (imageWidth / 8 * x), topPos + (imageHeight / 8 * y), imageWidth / 8, imageHeight / 8);
                }
            }
        }
    }

    @Override
    protected void extractLabels(@NotNull GuiGraphicsExtractor graphics, int xm, int ym) {
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
    protected void extractTooltip(@NotNull GuiGraphicsExtractor graphics, int mouseX, int mouseY) {
        super.extractTooltip(graphics, mouseX, mouseY);
    }

    @Override
    public boolean keyPressed(@NotNull KeyEvent event) {
        if (FLIP_BOARD.get().isActiveAndMatches(InputConstants.getKey(event))) {
            GameNight.LOGGER.info("Flipping board");

            return true;
        }

        return super.keyPressed(event);
    }

    @Override
    public boolean keyReleased(@NotNull KeyEvent event) {

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
    public boolean mouseClicked(@NotNull MouseButtonEvent event, boolean doubleClick) {
        if (this.clickedSquare >= 0 && this.legalMoves != null && clickedOnBoard(event.x(), event.y())) {
            int targetSquare = getClickedSquare(event.x(), event.y());

            if (this.legalMoves.contains(targetSquare)) {
                GameNight.LOGGER.info("Moving piece from {} to {}", this.clickedSquare, targetSquare);

                menu.makeMove(this.clickedSquare, targetSquare);

                this.clickedSquare = -1;
                this.legalMoves = null;
                return super.mouseClicked(event, doubleClick);
            }
        }

        if (clickedOnBoard(event.x(), event.y())) {
            this.clickedSquare = getClickedSquare(event.x(), event.y());

            if (this.clickedSquare >= 0) {
                GameNight.LOGGER.info("Clicked square: {}", this.clickedSquare);

                this.legalMoves = menu.getLegalMovesForPiece(this.clickedSquare);
            }
        } else {
            this.clickedSquare = -1;
            this.legalMoves = null;
        }

        return super.mouseClicked(event, doubleClick);
    }

    private int getClickedSquare(double x, double y) {
        int file = (int) ((x - leftPos) / (imageWidth / 8f));
        int rank = 7 - (int) ((y - topPos) / (imageHeight / 8f));

        return file + rank * 8;
    }

    private boolean clickedOnBoard(double x, double y) {
        return x >= leftPos && x <= leftPos + imageWidth && y >= topPos && y <= topPos + imageHeight;
    }

    @Override
    public boolean mouseReleased(@NotNull MouseButtonEvent event) {
        return super.mouseReleased(event);
    }

    @Override
    public boolean mouseScrolled(double x, double y, double scrollX, double scrollY) {
        return super.mouseScrolled(x, y, scrollX, scrollY);
    }

    @Override
    public boolean mouseDragged(@NotNull MouseButtonEvent event, double dx, double dy) {

        return super.mouseDragged(event, dx, dy);
    }
}
