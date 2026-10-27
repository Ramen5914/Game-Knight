package games.ramen.game_knight.engine.chess.helper;

import games.ramen.game_knight.engine.chess.Board;
import games.ramen.game_knight.engine.chess.helper.Util;
import games.ramen.game_knight.engine.chess.type.Move;
import games.ramen.game_knight.engine.chess.type.Piece;
import org.junit.jupiter.api.Test;

import java.util.ArrayList;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;

class MoveApplierTest {
    @Test
    void castlingMovesKingTwoSquaresAndRelocatesRook() {
        Board white = new Board("r3k2r/8/8/8/8/8/8/R3K2R w KQkq - 0 1");
        white.makeMove("e1g1");

        assertEquals(Piece.WHITE_KING, white.getPieceAt10x12(Util.convert8x8to10x12(6)));
        assertEquals(Piece.WHITE_ROOK, white.getPieceAt10x12(Util.convert8x8to10x12(5)));
        assertEquals(Piece.EMPTY, white.getPieceAt10x12(Util.convert8x8to10x12(7)));

        Board black = new Board("r3k2r/8/8/8/8/8/8/R3K2R b KQkq - 0 1");
        black.makeMove("e8c8");

        assertEquals(Piece.BLACK_KING, black.getPieceAt10x12(Util.convert8x8to10x12(58)));
        assertEquals(Piece.BLACK_ROOK, black.getPieceAt10x12(Util.convert8x8to10x12(59)));
        assertEquals(Piece.EMPTY, black.getPieceAt10x12(Util.convert8x8to10x12(56)));
    }

    @Test
    void doublePawnPushDoesNotRemoveAdjacentEnPassantPawn() {
        Board board = new Board("7k/8/8/8/3p4/8/4P3/K7 w - - 0 1");

        board.makeMove("e2e4");

        assertEquals(Piece.BLACK_PAWN, board.getPieceAt10x12(Util.convert8x8to10x12(27)));
        assertEquals(Util.convert8x8to10x12(20), board.getEnPassantSquare10x12());
    }

    @Test
    void blackDoublePawnPushRemainsAvailableAfterOpeningSequence() {
        Board board = new Board("rnbqkbnr/pppppppp/8/8/8/8/PPPPPPPP/RNBQKBNR w KQkq - 0 1");

        board.makeMove("e2e4");
        board.makeMove("d7d5");
        board.makeMove("e4e5");
        board.makeMove("f7f5");

        assertEquals(Piece.BLACK_PAWN, board.getPieceAt10x12(Util.convert8x8to10x12(37)));
        assertEquals(Util.convert8x8to10x12(45), board.getEnPassantSquare10x12());
    }

    @Test
    void enPassantCaptureRemovesThePawnPassedOver() {
        Board board = new Board("rnbqkbnr/pppppppp/8/8/8/8/PPPPPPPP/RNBQKBNR w KQkq - 0 1");

        board.makeMove("e2e4");
        board.makeMove("d7d5");
        board.makeMove("e4e5");
        board.makeMove("f7f5");
        board.makeMove("e5f6");

        assertEquals(Piece.WHITE_PAWN, board.getPieceAt10x12(Util.convert8x8to10x12(45)));
        assertEquals(Piece.EMPTY, board.getPieceAt10x12(Util.convert8x8to10x12(37)));
    }

    @Test
    void promotionReplacesPawnWithSelectedPiece() {
        Board board = new Board("4k3/P7/8/8/8/8/8/4K3 w - - 0 1");
        List<Move> moves = new ArrayList<>();
        MoveGenerator.generateLegalMoves(moves, board);

        MoveApplier.makeMove(board, moves.stream().filter(Move::isQueenPromotion).findFirst().orElseThrow(), false);

        assertEquals(Piece.WHITE_QUEEN, board.getPieceAt10x12(Util.convert8x8to10x12(56)));
    }

    @Test
    void movingRookRemovesItsCastlingRight() {
        Board board = new Board("r3k2r/8/8/8/8/8/8/R3K2R w KQkq - 0 1");

        board.makeMove("a1b1");

        assertEquals(0b1101, board.getCastlingRights());
    }
}
