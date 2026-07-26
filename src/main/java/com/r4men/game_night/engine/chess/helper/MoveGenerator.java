package com.r4men.game_night.engine.chess.helper;

import com.r4men.game_night.engine.chess.Board;
import com.r4men.game_night.engine.chess.type.Direction;
import com.r4men.game_night.engine.chess.type.Move;
import com.r4men.game_night.engine.chess.type.Piece;
import oshi.util.tuples.Pair;

import java.util.ArrayList;
import java.util.List;

public final class MoveGenerator {
    public static long perft(Board board, int depth) {
        if (depth == 0) return 1L;

        Pair<List<Move>, Integer> pair = generateLegalMoves(board);

        long nodes = 0;
        for (int i = 0; i < pair.getB(); i++) {
            MoveApplier.makeMove(board, pair.getA().get(i), false);
            nodes += perft(board, depth - 1);
            MoveApplier.undoMove(board, pair.getA().get(i));
        }

        return nodes;
    }

    public static Pair<List<Move>, Integer> generateLegalMoves(Board board) {
        List<Move> moveList = new ArrayList<>(256);

        long occ = board.getBitBoard(board.getPlayerToMove(), Piece.PieceType.OCC);

        for (int s8x8 = 0; s8x8 < 64; s8x8++) {
            if ((occ & (1L << s8x8)) != 0) {
                int from10x12 = Util.convert8x8to10x12(s8x8);

                generateMovesForPiece(moveList, board, from10x12, board.getPieceAt10x12(from10x12));
            }
        }

        return new Pair<>(moveList, moveList.size());
    }

    private static void generateMovesForPiece(List<Move> moveList, Board board, int from10x12, Piece piece) {
        switch (piece.getPieceType()) {
            case PAWN -> generatePawnMoves(moveList, board, from10x12, piece);
            case KNIGHT -> generateKnightMoves(moveList, board, from10x12, piece);
            case BISHOP -> generateBishopMoves(moveList, board, from10x12, piece);
            case ROOK -> generateRookMoves(moveList, board, from10x12, piece);
            case QUEEN -> generateQueenMoves(moveList, board, from10x12, piece);
            case KING -> generateKingMoves(moveList, board, from10x12, piece);
            default -> throw new IllegalArgumentException("Invalid piece type: " + piece.getPieceType());
        };
    }

    static int generateKingMoves(List<Move> moveList, Board board, int from10x12, Piece piece) {
        int count = 0;

        int from8x8 = Util.convert10x12to8x8(from10x12);
        int enPassantSquare10x12 = board.getEnPassantSquare10x12();
        int castlingRights = board.getCastlingRights();
        int halfmoveClock = board.getHalfmoveClock();
        Piece.Color oppositeColor = piece.getColor().opposite();

        for (Direction.D10X12 direction : Direction.D10X12.values()) {
            int to10x12 = from10x12 + direction.toInt();
            Piece capturedPiece = board.getPieceAt10x12(to10x12);

            if (capturedPiece.isOffBoard()) continue;

            int to8x8 = Util.convert10x12to8x8(to10x12);

            Move.Flag flag;
            if (capturedPiece.isEmpty()) {
                flag = Move.Flag.QUIET_MOVE_FLAG;
            } else if (capturedPiece.matchesColor(oppositeColor)) {
                flag = Move.Flag.CAPTURES_FLAG;
            } else {
                continue;
            }

            moveList.add(new Move(from8x8, to8x8, flag, enPassantSquare10x12, castlingRights, halfmoveClock, capturedPiece));
            count++;
        }

        if (piece.isWhite() && ((castlingRights & 0b0011) != 0)) {
            if ((castlingRights & 0b0001) == 0b0001) {
                if (Util.isPathClear10x12(board, from10x12, 0x1C)) {
                    moveList.add(new Move(from8x8, 7, Move.Flag.KING_CASTLE_FLAG, enPassantSquare10x12, castlingRights, halfmoveClock, Piece.EMPTY));
                    count++;
                }
            }
            if ((castlingRights & 0b0010) == 0b0010) {
                if (Util.isPathClear10x12(board, from10x12, 0x15)) {
                    moveList.add(new Move(from8x8, 0, Move.Flag.QUEEN_CASTLE_FLAG, enPassantSquare10x12, castlingRights, halfmoveClock, Piece.EMPTY));
                    count++;
                }
            }
        } else if (piece.isBlack() && ((castlingRights & 0b1100) != 0)) {
            if ((castlingRights & 0b0100) == 0b0100) {
                moveList.add(new Move(from8x8, 63, Move.Flag.KING_CASTLE_FLAG, enPassantSquare10x12, castlingRights, halfmoveClock, Piece.EMPTY));
                count++;
            }
            if ((castlingRights & 0b1000) == 0b1000) {
                moveList.add(new Move(from8x8, 56, Move.Flag.QUEEN_CASTLE_FLAG, enPassantSquare10x12, castlingRights, halfmoveClock, Piece.EMPTY));
                count++;
            }
        }

        return count;
    }

    private static void generateQueenMoves(List<Move> moveList, Board board, int from10x12, Piece piece) {

    }

    private static void generateRookMoves(List<Move> moveList, Board board, int from10x12, Piece piece) {

    }

    private static void generateBishopMoves(List<Move> moveList, Board board, int from10x12, Piece piece) {

    }

    private static void generateKnightMoves(List<Move> moveList, Board board, int from10x12, Piece piece) {

    }

    private static void generatePawnMoves(List<Move> moveList, Board board, int from10x12, Piece piece) {

    }
}
