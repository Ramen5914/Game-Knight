package games.ramen.game_knight.engine.chess.helper;

import games.ramen.game_knight.engine.chess.Board;
import games.ramen.game_knight.engine.chess.type.Move;
import org.junit.jupiter.api.Test;

import java.util.ArrayList;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

class MoveGeneratorTest {
    @Test
    void generatesStandardCastlingDestinations() {
        Board board = new Board("r3k2r/8/8/8/8/8/8/R3K2R w KQkq - 0 1");

        assertTrue(board.generate8x8MovesForPiece(4).containsAll(java.util.List.of(2, 6)));
    }

    @Test
    void generatesAllPromotionMoves() {
        Board board = new Board("4k3/P7/8/8/8/8/6p1/4K3 w - - 0 1");
        List<Move> moves = new ArrayList<>();

        MoveGenerator.generateLegalMoves(moves, board);
        List<Move> pawnMoves = moves.stream().filter(move -> move.getFrom8x8() == 48).toList();
        assertEquals(4, pawnMoves.size());
        assertTrue(pawnMoves.stream().allMatch(move -> move.isPromotion() && move.getTo8x8() == 56));

        board = new Board("4k3/8/8/8/8/8/6p1/4K3 b - - 0 1");
        moves.clear();

        MoveGenerator.generateLegalMoves(moves, board);
        pawnMoves = moves.stream().filter(move -> move.getFrom8x8() == 14).toList();
        assertEquals(4, pawnMoves.size());
        assertTrue(pawnMoves.stream().allMatch(move -> move.isPromotion() && move.getTo8x8() == 6));
    }

    @Test
    void generatesPromotionCaptures() {
        Board board = new Board("1r2k3/P7/8/8/8/8/8/4K3 w - - 0 1");
        List<Move> moves = new ArrayList<>();

        MoveGenerator.generateLegalMoves(moves, board);
        List<Move> pawnMoves = moves.stream().filter(move -> move.getFrom8x8() == 48).toList();
        assertEquals(8, pawnMoves.size());
        assertEquals(4, pawnMoves.stream().filter(move -> move.getTo8x8() == 57 && move.isPromotion()).count());
        assertEquals(4, pawnMoves.stream().filter(move -> move.getTo8x8() == 56 && move.isPromotion()).count());
    }

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
    }

    @Test
    void perftPos3() {
        Board board = new Board("8/2p5/3p4/KP5r/1R3p1k/8/4P1P1/8 w - - 0 1");

        assertEquals(14, MoveGenerator.perft(board, 1));
        assertEquals(191, MoveGenerator.perft(board, 2));
        assertEquals(2812, MoveGenerator.perft(board, 3));
        assertEquals(43238, MoveGenerator.perft(board, 4));
        assertEquals(674624, MoveGenerator.perft(board, 5));
        assertEquals(11030083, MoveGenerator.perft(board, 6));
    }

    @Test
    void perftPos4() {
        Board board1 = new Board("r3k2r/Pppp1ppp/1b3nbN/nP6/BBP1P3/q4N2/Pp1P2PP/R2Q1RK1 w kq - 0 1");

        assertEquals(6, MoveGenerator.perft(board1, 1));
        assertEquals(264, MoveGenerator.perft(board1, 2));
        assertEquals(9467, MoveGenerator.perft(board1, 3));
        assertEquals(422333, MoveGenerator.perft(board1, 4));
        assertEquals(15833292, MoveGenerator.perft(board1, 5));

        // Mirrored
        Board board2 = new Board("r2q1rk1/pP1p2pp/Q4n2/bbp1p3/Np6/1B3NBn/pPPP1PPP/R3K2R b KQ - 0 1");
        assertEquals(6, MoveGenerator.perft(board2, 1));
        assertEquals(264, MoveGenerator.perft(board2, 2));
        assertEquals(9467, MoveGenerator.perft(board2, 3));
        assertEquals(422333, MoveGenerator.perft(board2, 4));
        assertEquals(15833292, MoveGenerator.perft(board2, 5));
    }

    @Test
    void perftPos5() {
        Board board = new Board("rnbq1k1r/pp1Pbppp/2p5/8/2B5/8/PPP1NnPP/RNBQK2R w KQ - 1 8");

        assertEquals(44, MoveGenerator.perft(board, 1));
        assertEquals(1486, MoveGenerator.perft(board, 2));
        assertEquals(62379, MoveGenerator.perft(board, 3));
        assertEquals(2103487, MoveGenerator.perft(board, 4));
        assertEquals(89941194, MoveGenerator.perft(board, 5));
    }

    @Test
    void perftPos6() {
        Board board = new Board("r4rk1/1pp1qppp/p1np1n2/2b1p1B1/2B1P1b1/P1NP1N2/1PP1QPPP/R4RK1 w - - 0 10");

        assertEquals(1, MoveGenerator.perft(board, 0));
        assertEquals(46, MoveGenerator.perft(board, 1));
        assertEquals(2079, MoveGenerator.perft(board, 2));
        assertEquals(89890, MoveGenerator.perft(board, 3));
        assertEquals(3894594, MoveGenerator.perft(board, 4));
        assertEquals(164075551, MoveGenerator.perft(board, 5));
    }
}
