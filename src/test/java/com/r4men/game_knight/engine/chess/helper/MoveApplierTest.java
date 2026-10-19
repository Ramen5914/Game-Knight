package com.r4men.game_knight.engine.chess.helper;

import com.r4men.game_knight.engine.chess.Board;
import com.r4men.game_knight.engine.chess.type.Piece;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;

class MoveApplierTest {
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
}
