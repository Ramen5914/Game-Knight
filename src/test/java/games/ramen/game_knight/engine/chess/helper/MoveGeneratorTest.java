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
}
