package com.r4men.game_night.gui.screen;

import com.mojang.blaze3d.platform.InputConstants;
import com.r4men.game_night.GNConfig;
import com.r4men.game_night.GameNight;
import com.r4men.game_night.engine.chess.Board;
import com.r4men.game_night.gui.GNScreen;
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

import static com.r4men.game_night.GameNightClient.FLIP_BOARD;

public class ChessSetupScreen extends GNScreen {
    private static final Identifier NAME_BADGE = GameNight.id("badges/name_badge");
    private static final Identifier BOARD = GameNight.id("textures/gui/container/chess.png");
    private final Board board;
    private final Map<Character, Identifier> PIECE_SPRITES = Map.ofEntries(
            Map.entry('b', GameNight.id("container/chess/black_bishop")),
            Map.entry('k', GameNight.id("container/chess/black_king")),
            Map.entry('n', GameNight.id("container/chess/black_knight")),
            Map.entry('p', GameNight.id("container/chess/black_pawn")),
            Map.entry('q', GameNight.id("container/chess/black_queen")),
            Map.entry('r', GameNight.id("container/chess/black_rook")),
            Map.entry('B', GameNight.id("container/chess/white_bishop")),
            Map.entry('K', GameNight.id("container/chess/white_king")),
            Map.entry('N', GameNight.id("container/chess/white_knight")),
            Map.entry('P', GameNight.id("container/chess/white_pawn")),
            Map.entry('Q', GameNight.id("container/chess/white_queen")),
            Map.entry('R', GameNight.id("container/chess/white_rook"))
    );
    private boolean viewingBoardAsWhite;
    private int clickedSquare = -1;
    private List<Integer> legalMoves = null;
    private String whitePlayer;
    private String blackPlayer;

    public ChessSetupScreen(Component title, String fen, String whitePlayer, String blackPlayer) {
        super(title, 256, 256);

        this.titleLabelY = -1000000;

        this.whitePlayer = whitePlayer;
        this.blackPlayer = blackPlayer;
        this.board = new Board(fen);

        assert Minecraft.getInstance().player != null;
        viewingBoardAsWhite = !Minecraft.getInstance().player.getName().toString().equals(this.blackPlayer);
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

        String[] pieces = board.toSimpleFen().split("/");

        if (clickedSquare >= 0) {
            if (pieces[7 - clickedSquare / 8].charAt(clickedSquare % 8) != ' ') {
                int x0 = this.leftPos + (clickedSquare % 8) * (imageWidth / 8);
                int y0 = this.topPos + (7 - clickedSquare / 8) * (imageHeight / 8);

                graphics.fill(x0, y0, x0 + imageWidth / 8, y0 + imageHeight / 8, GNConfig.SELECT_COLOR.get());

                if (legalMoves != null) {
                    for (int move : legalMoves) {
                        int width = imageWidth / 8 / 3;
                        int height = imageHeight / 8 / 3;

                        int x1 = this.leftPos + (move % 8) * (imageWidth / 8) + (imageWidth / 8 / 2) - width / 2;
                        int y1 = this.topPos + (7 - (move / 8)) * (imageHeight / 8) + (imageHeight / 8 / 2) - height / 2;

                        graphics.fill(x1, y1, x1 + width, y1 + height, GNConfig.SELECT_COLOR.get());
                    }
                }
            }
        }

        for (int y = 0; y < 8; y++) {
            for (int x = 0; x < 8; x++) {
                int x1 = x;
                int y1 = y;
                if (!viewingBoardAsWhite) {
                    x1 = 7 - x;
                    y1 = 7 - y;
                }

                Identifier piece = PIECE_SPRITES.get(pieces[y].charAt(x));

                if (piece != null) {
                    graphics.blitSprite(RenderPipelines.GUI_TEXTURED, piece, leftPos + (imageWidth / 8 * x1), topPos + (imageHeight / 8 * y1), imageWidth / 8, imageHeight / 8);
                }
            }
        }

        this.extractLabels(graphics, mouseX, mouseY);

    }

    private void extractLabels(@NotNull GuiGraphicsExtractor graphics, int mouseX, int mouseY) {
        if (GNConfig.SHOW_COORDINATES.get()) {
            for (int i = 0; i < 8; i++) {
                int color;

                if (i % 2 == 0) {
                    color = 0xFFFFFFFF;
                } else {
                    color = 0xFF000000;
                }

                if (viewingBoardAsWhite) {
                    graphics.text(this.font, Character.toString(97 + i), this.leftPos + 1 + (i * (this.imageWidth / 8)), this.topPos + this.imageHeight - this.font.lineHeight + 1, color, false);
                    graphics.text(this.font, Character.toString(56 - i), this.leftPos + this.imageHeight - this.font.width(Character.toString(56 - i)), this.topPos + 1 + (i * (this.imageWidth / 8)), color, false);
                } else {
                    graphics.text(this.font, Character.toString(104 - i), this.leftPos + 1 + (i * (this.imageWidth / 8)), this.topPos + this.imageHeight - this.font.lineHeight + 1, color, false);
                    graphics.text(this.font, Character.toString(49 + i), this.leftPos + this.imageHeight - this.font.width(Character.toString(56 - i)), this.topPos + 1 + (i * (this.imageWidth / 8)), color, false);
                }
            }
        }


        String topPlayer;
        String bottomPlayer;
        if (viewingBoardAsWhite) {
            topPlayer = blackPlayer;
            bottomPlayer = whitePlayer;
        } else {
            topPlayer = whitePlayer;
            bottomPlayer = blackPlayer;
        }

        // Top Player
        graphics.blitSprite(RenderPipelines.GUI_TEXTURED, NAME_BADGE, this.leftPos + 1, this.topPos - this.font.lineHeight - 13, 12 + font.width(topPlayer), 20);
        graphics.text(this.font, topPlayer, this.leftPos + 8, this.topPos - this.font.lineHeight - 7, 0xFF000000, false);


        // Bottom Player
        graphics.blitSprite(RenderPipelines.GUI_TEXTURED, NAME_BADGE, this.leftPos + 1, this.topPos + imageHeight + this.font.lineHeight - 7, 12 + font.width(topPlayer), 20);
        graphics.text(this.font, bottomPlayer, this.leftPos + 8, this.topPos + this.imageHeight + this.font.lineHeight - 1, 0xFF000000, false);
    }

    @Override
    public boolean keyPressed(@NotNull KeyEvent event) {
        if (FLIP_BOARD.get().isActiveAndMatches(InputConstants.getKey(event))) {
            return flipBoard();
        }

        return super.keyPressed(event);
    }

    private boolean flipBoard() {
        viewingBoardAsWhite = !viewingBoardAsWhite;

        if (clickedSquare > 0) {
            clickedSquare = 63 - clickedSquare;
            this.legalMoves = board.generate8x8MovesForPiece(this.clickedSquare);
        }

        return true;
    }

    @Override
    public boolean keyReleased(@NotNull KeyEvent event) {

        return super.keyReleased(event);
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

                board.makeMove(this.clickedSquare, targetSquare);

                this.clickedSquare = -1;
                this.legalMoves = null;
                return super.mouseClicked(event, doubleClick);
            }
        }

        if (clickedOnBoard(event.x(), event.y())) {
            this.clickedSquare = getClickedSquare(event.x(), event.y());

            if (this.clickedSquare >= 0) {
                GameNight.LOGGER.info("Clicked square: {}", this.clickedSquare);

                setLegalMoves();
            }
        } else {
            this.clickedSquare = -1;
            this.legalMoves = null;
        }

        return super.mouseClicked(event, doubleClick);
    }

    private void setLegalMoves() {
        this.legalMoves = board.generate8x8MovesForPiece(this.clickedSquare);
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

    @Override
    public boolean isPauseScreen() {
        return false;
    }

    public void setPlayers(String whitePlayer, String blackPlayer) {
        this.whitePlayer = whitePlayer;
        this.blackPlayer = blackPlayer;
    }
}
