package com.r4men.game_knight.engine.chess.helper;

import com.r4men.game_knight.engine.chess.Board;
import com.r4men.game_knight.engine.chess.type.Piece;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

class AttackDetectorTest {
    @Test
    void isSquareAttacked() {
        Board board = new Board("rnbqkbn1/ppp2pp1/4r3/3B3p/5P1P/3p4/PPP5/RNBQK1NR w KQq - 0 9");
        assertTrue(AttackDetector.isSquareAttacked(board, Util.convert8x8to10x12(4), Piece.Color.BLACK));


        board = new Board("rnbqkbn1/ppp2pp1/4r3/7p/2B1pP1P/3P4/PPP5/RNBQK1NR w KQq - 0 8");
        assertFalse(AttackDetector.isSquareAttacked(board, Util.convert8x8to10x12(4), Piece.Color.BLACK));
    }
}