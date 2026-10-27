package games.ramen.game_knight.cli;

import games.ramen.game_knight.engine.chess.Board;
import games.ramen.game_knight.engine.chess.helper.MoveApplier;
import games.ramen.game_knight.engine.chess.helper.MoveGenerator;
import games.ramen.game_knight.engine.chess.helper.MoveParser;
import games.ramen.game_knight.engine.chess.type.Move;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public final class PerftreeMain {
    private PerftreeMain() {
    }

    static void main(String[] args) {
        if (args.length < 2) {
            throw new IllegalArgumentException("Usage: java -jar game-knight-perftree.jar <depth> <fen> [moves]");
        }

        int depth = parseDepth(args[0]);
        Board board = new Board(args[1]);

        if (args.length >= 3 && !args[2].isBlank()) {
            for (String uci : String.join(" ", Arrays.copyOfRange(args, 2, args.length)).trim().split("\\s+")) {
                Move move = MoveParser.fromUci(board, uci);
                if (move == null || !games.ramen.game_knight.engine.chess.helper.MoveValidator.isMoveValid(board, move)) {
                    throw new IllegalArgumentException("Invalid move '" + uci + "' for position " + board.toFen());
                }
                MoveApplier.makeMove(board, move, false);
            }
        }

        printDivide(board, depth);
    }

    private static int parseDepth(String value) {
        try {
            int depth = Integer.parseInt(value);
            if (depth < 1) {
                throw new IllegalArgumentException("Depth must be at least 1");
            }
            return depth;
        } catch (NumberFormatException exception) {
            throw new IllegalArgumentException("Depth must be an integer: " + value, exception);
        }
    }

    private static void printDivide(Board board, int depth) {
        List<Move> moves = new ArrayList<>(256);
        MoveGenerator.generateLegalMoves(moves, board);

        long total = 0;
        for (Move move : moves) {
            MoveApplier.makeMove(board, move, false);
            long count = MoveGenerator.perft(board, depth - 1);
            MoveApplier.undoMove(board, move);

            System.out.printf("%s %d%n", move.toUci(), count);
            total += count;
        }

        System.out.printf("%n%d%n", total);
    }
}
