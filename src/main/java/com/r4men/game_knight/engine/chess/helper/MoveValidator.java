package com.r4men.game_knight.engine.chess.helper;

import com.r4men.game_knight.engine.chess.Board;
import com.r4men.game_knight.engine.chess.type.Direction;
import com.r4men.game_knight.engine.chess.type.Move;
import com.r4men.game_knight.engine.chess.type.Piece;

import java.util.Objects;

public final class MoveValidator {
    // TODO move check test to the end of make move
    public static boolean isMoveValid(Board board, Move move) {
        int from10x12 = move.getFrom10x12();
        int to10x12 = move.getTo10x12();
        Piece movedPiece = board.getPieceAt10x12(from10x12);
        Piece destinationPiece = board.getPieceAt10x12(to10x12);

        if (from10x12 == to10x12 || !movedPiece.matchesColor(board.getPlayerToMove()) || destinationPiece.isOffBoard() || destinationPiece != move.capturedPiece()) {
            return false;
        }

        return isValidMovementForPiece(board, movedPiece, move);
    }

    static boolean isValidMovementForPiece(Board board, Piece movedPiece, Move move) {
        Piece.Color color = movedPiece.getColor();

        return switch (movedPiece.getPieceType()) {
            case PAWN -> isValidPawnMove(board, move, color);
            case KNIGHT -> isValidKnightMove(board, move, color);
            case BISHOP -> isValidBishopMove(board, move, color);
            case ROOK -> isValidRookMove(board, move, color);
            case QUEEN -> isValidQueenMove(board, move, color);
            case KING -> isValidKingMove(board, move, color);
            default -> false;
        };
    }

    static boolean isValidPawnMove(Board board, Move move, Piece.Color color) {
        int from10x12 = move.getFrom10x12();
        int to10x12 = move.getTo10x12();

        Piece destinationPiece = board.getPieceAt10x12(to10x12);
        Direction.D10X12 direction = color == Piece.Color.WHITE ? Direction.D10X12.N : Direction.D10X12.S;

        if (move.isCapture()) {
            int east = from10x12 + direction.toInt() + Direction.D10X12.E.toInt();
            int west = from10x12 + direction.toInt() + Direction.D10X12.W.toInt();

            if (!(to10x12 == east || to10x12 == west)) {
                return false;
            }

            if (move.isEnPassantCapture()) {
                return to10x12 == board.getEnPassantSquare10x12() && destinationPiece == move.capturedPiece();
            } else {
                return destinationPiece.getColor() == color.opposite();
            }
        } else {
            if (move.isDoublePawnPush()) {
                return Util.getRankDiff10x12(from10x12, to10x12) == 2 && Util.isPathClear10x12(board, from10x12, to10x12);
            }

            return Util.getRankDiff10x12(from10x12, to10x12) == 1 && Util.getFileDiff10x12(from10x12, to10x12) == 0 && destinationPiece.isEmpty();
        }
    }

    static boolean isValidKnightMove(Board board, Move move, Piece.Color color) {
        int from = move.getFrom10x12();
        int to = move.getTo10x12();

        if (board.getPieceAt10x12(to).getColor() == color) {
            return false;
        }

        int rankDiff = Util.getRankDiff10x12(from, to);
        int fileDiff = Util.getFileDiff10x12(from, to);

        return Math.min(rankDiff, fileDiff) == 1 && rankDiff + fileDiff == 3;
    }

    static boolean isValidBishopMove(Board board, Move move, Piece.Color color) {
        int from = move.getFrom10x12();
        int to = move.getTo10x12();

        boolean isDiagonal = Objects.requireNonNull(Util.getD10X12FromSquares(from, to)).isDiagonal();
        if (isDiagonal && Util.isPathClear10x12(board, from, to)) {
            if (move.isCapture()) {
                return board.getPieceAt10x12(to).getColor() == color.opposite();
            } else {
                return board.getPieceAt10x12(to).isEmpty();
            }
        } else {
            return false;
        }
    }

    static boolean isValidRookMove(Board board, Move move, Piece.Color color) {
        int from = move.getFrom10x12();
        int to = move.getTo10x12();

        boolean isOrthogonal = Objects.requireNonNull(Util.getD10X12FromSquares(from, to)).isOrthogonal();
        if (isOrthogonal && Util.isPathClear10x12(board, from, to)) {
            if (move.isCapture()) {
                return board.getPieceAt10x12(to).getColor() == color.opposite();
            } else {
                return board.getPieceAt10x12(to).isEmpty();
            }
        } else {
            return false;
        }
    }

    static boolean isValidQueenMove(Board board, Move move, Piece.Color color) {
        return isValidBishopMove(board, move, color) || isValidRookMove(board, move, color);
    }

    static boolean isValidKingMove(Board board, Move move, Piece.Color color) {
        int from = move.getFrom10x12();
        int to = move.getTo10x12();

        if (move.isCastle()) {
            // TODO
            return false;
        } else {
            int rankDiff = Util.getRankDiff10x12(from, to);
            int fileDiff = Util.getFileDiff10x12(from, to);

            if (Math.max(rankDiff, fileDiff) != 1) {
                return false;
            }

            if (move.isCapture()) {
                return board.getPieceAt10x12(to).getColor() == color.opposite();
            } else {
                return board.getPieceAt10x12(to).isEmpty();
            }
        }
    }
}
