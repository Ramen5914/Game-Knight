package games.ramen.game_knight.engine.chess.helper;

import games.ramen.game_knight.engine.chess.Board;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

class MoveGeneratorTest {
    @Test
    void perftInitialPosition() {
        Board board = new Board("rnbqkbnr/pppppppp/8/8/8/8/PPPPPPPP/RNBQKBNR w KQkq - 0 1");

        assertEquals(1, MoveGenerator.perft(board, 0));
        assertEquals(20, MoveGenerator.perft(board, 1));
        assertEquals(400, MoveGenerator.perft(board, 2));
        assertEquals(8902, MoveGenerator.perft(board, 3));
        assertEquals(197281, MoveGenerator.perft(board, 4));
        assertEquals(4865609, MoveGenerator.perft(board, 5));
    }

    @Test
    void perftKiwipete() {
        Board board = new Board("r3k2r/p1ppqpb1/bn2pnp1/3PN3/1p2P3/2N2Q1p/PPPBBPPP/R3K2R w KQkq - 0 1");

        assertEquals(48, MoveGenerator.perft(board, 1));
        assertEquals(2039, MoveGenerator.perft(board, 2));
        assertEquals(97862, MoveGenerator.perft(board, 3));
        assertEquals(4085603, MoveGenerator.perft(board, 4));
        assertEquals(193690690, MoveGenerator.perft(board, 5));
        assertEquals(8031647685L, MoveGenerator.perft(board, 6));
    }
}
