package com.r4men.game_knight.engine.chess.helper;

import com.r4men.game_knight.engine.chess.Board;
import com.r4men.game_knight.engine.chess.type.Direction;
import com.r4men.game_knight.engine.chess.type.Move;
import com.r4men.game_knight.engine.chess.type.Piece;
import org.jetbrains.annotations.NotNull;
import oshi.util.tuples.Pair;

import java.util.ArrayList;
import java.util.Arrays;
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

    private static int generateMovesForPiece(List<Move> moveList, Board board, int from10x12, Piece piece) {
        Piece.Color color = piece.getColor();

        return switch (piece.getPieceType()) {
            case PAWN -> generatePawnMoves(moveList, board, from10x12, piece);
            case KNIGHT -> generateKnightMoves(moveList, board, from10x12, color);
            case BISHOP -> generateBishopMoves(moveList, board, from10x12, color);
            case ROOK -> generateRookMoves(moveList, board, from10x12, color);
            case QUEEN -> generateQueenMoves(moveList, board, from10x12, color);
            case KING -> generateKingMoves(moveList, board, from10x12, color);
            default -> throw new IllegalArgumentException("Invalid piece type: " + piece.getPieceType());
        };
    }

    static int generateKingMoves(List<Move> moveList, Board board, int from10x12, Piece.Color color) {
        int count = 0;

        int from8x8 = Util.convert10x12to8x8(from10x12);
        int enPassantSquare10x12 = board.getEnPassantSquare10x12();
        int castlingRights = board.getCastlingRights();
        int halfmoveClock = board.getHalfmoveClock();
        Piece.Color oppositeColor = color.opposite();

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

        if (color == Piece.Color.WHITE && ((castlingRights & 0b0011) != 0)) {
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
        } else if (color == Piece.Color.BLACK && ((castlingRights & 0b1100) != 0)) {
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

    private static int generateQueenMoves(List<Move> moveList, Board board, int from10x12, Piece.Color color) {
        int count = 0;

        count += generateBishopMoves(moveList, board, from10x12, color);
        count += generateRookMoves(moveList, board, from10x12, color);

        return count;
    }

    private static int generateRookMoves(List<Move> moveList, Board board, int from10x12, Piece.Color color) {
        int count = 0;
        int from8x8 = Util.convert10x12to8x8(from10x12);

        int enPassantSquare10x12 = board.getEnPassantSquare10x12();
        int castlingRights = board.getCastlingRights();
        int halfmoveClock = board.getHalfmoveClock();
        Piece.Color oppositeColor = color.opposite();

        for (Direction.D10X12 direction : Arrays.stream(Direction.D10X12.values()).filter(Direction.D10X12::isOrthogonal).toList()) {
            int to10x12 = from10x12 + direction.toInt();
            while (board.getPieceAt10x12(to10x12).isEmpty()) {
                Move move = new Move(from8x8, Util.convert10x12to8x8(to10x12), Move.Flag.QUIET_MOVE_FLAG, enPassantSquare10x12, castlingRights, halfmoveClock, Piece.EMPTY);

                MoveApplier.makeMove(board, move, true);
                if (!AttackDetector.isKingInCheck(board, board.getPlayerToMove())) {
                    moveList.add(move);
                    count++;
                }
                MoveApplier.undoMove(board, move);

                to10x12 += direction.toInt();
            }

            if (board.getPieceAt10x12(to10x12).matchesColor(oppositeColor)) {
                moveList.add(new Move(from8x8, Util.convert10x12to8x8(to10x12), Move.Flag.CAPTURES_FLAG, enPassantSquare10x12, castlingRights, halfmoveClock, board.getPieceAt10x12(to10x12)));
                count++;
            }
        }

        return count;
    }

    private static int generateBishopMoves(List<Move> moveList, Board board, int from10x12, Piece.Color color) {
        int count = 0;
        int from8x8 = Util.convert10x12to8x8(from10x12);

        int enPassantSquare10x12 = board.getEnPassantSquare10x12();
        int castlingRights = board.getCastlingRights();
        int halfmoveClock = board.getHalfmoveClock();
        Piece.Color oppositeColor = color.opposite();

        for (Direction.D10X12 direction : Arrays.stream(Direction.D10X12.values()).filter(Direction.D10X12::isDiagonal).toList()) {
            int to10x12 = from10x12 + direction.toInt();
            while (board.getPieceAt10x12(to10x12).isEmpty()) {
                Move move = new Move(from8x8, Util.convert10x12to8x8(to10x12), Move.Flag.QUIET_MOVE_FLAG, enPassantSquare10x12, castlingRights, halfmoveClock, Piece.EMPTY);

                MoveApplier.makeMove(board, move, true);
                if (!AttackDetector.isKingInCheck(board, board.getPlayerToMove())) {
                    moveList.add(move);
                    count++;
                }
                MoveApplier.undoMove(board, move);

                to10x12 += direction.toInt();
            }

            if (board.getPieceAt10x12(to10x12).matchesColor(oppositeColor)) {
                moveList.add(new Move(from8x8, Util.convert10x12to8x8(to10x12), Move.Flag.CAPTURES_FLAG, enPassantSquare10x12, castlingRights, halfmoveClock, board.getPieceAt10x12(to10x12)));
                count++;
            }
        }

        return count;
    }

    private static int generateKnightMoves(List<Move> moveList, Board board, int from10x12, Piece.Color color) {
        int count = 0;

        return count;
    }

    private static int generatePawnMoves(List<Move> moveList, Board board, int from10x12, Piece piece) {
        int count = 0;

        int from8x8 = Util.convert10x12to8x8(from10x12);

        if (piece.isWhite()) {
            if (board.getPieceAt10x12(from10x12 + Direction.D10X12.N.toInt()).isEmpty()) {
                moveList.add(new Move(from8x8, from8x8 + Direction.D8X8.N.toInt(), Move.Flag.QUIET_MOVE_FLAG, board.getEnPassantSquare10x12(), board.getCastlingRights(), board.getHalfmoveClock(), Piece.EMPTY));
                count++;

                if (board.getPieceAt10x12(from10x12 + Direction.D10X12.N.toInt() * 2).isEmpty() && Util.getRank10x12(from10x12) == 1) {
                    moveList.add(new Move(from8x8, from8x8 + Direction.D8X8.N.toInt() * 2, Move.Flag.DOUBLE_PAWN_PUSH_FLAG, board.getEnPassantSquare10x12(), board.getCastlingRights(), board.getHalfmoveClock(), Piece.EMPTY));
                    count++;
                }
            }

        } else {
            if (board.getPieceAt10x12(from10x12 - Direction.D10X12.N.toInt()).isEmpty()) {
                moveList.add(new Move(from8x8, from8x8 - Direction.D8X8.N.toInt(), Move.Flag.QUIET_MOVE_FLAG, board.getEnPassantSquare10x12(), board.getCastlingRights(), board.getHalfmoveClock(), Piece.EMPTY));
                count++;

                if (board.getPieceAt10x12(from10x12 - Direction.D10X12.N.toInt() * 2).isEmpty() && Util.getRank10x12(from10x12) == 6) {
                    moveList.add(new Move(from8x8, from8x8 - Direction.D8X8.N.toInt() * 2, Move.Flag.DOUBLE_PAWN_PUSH_FLAG, board.getEnPassantSquare10x12(), board.getCastlingRights(), board.getHalfmoveClock(), Piece.EMPTY));
                    count++;
                }
            }
        }

        return count;
    }

    public static @NotNull List<Integer> generate8x8MovesForPiece(Board board, int s8x8) {
        int from10x12 = Util.convert8x8to10x12(s8x8);
        Piece piece = board.getPieceAt10x12(from10x12);

        if (piece.isEmpty()) return List.of();

        List<Move> moveList = new ArrayList<>(256);
        generateMovesForPiece(moveList, board, from10x12, piece);

        List<Integer> moves = new ArrayList<>(moveList.size());
        for (Move move : moveList) {
            moves.add(move.getTo8x8());
        }

        return moves;
    }
}
