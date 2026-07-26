package com.r4men.game_night.engine.chess.helper;

import com.r4men.game_night.engine.chess.Board;
import com.r4men.game_night.engine.chess.type.Direction;
import com.r4men.game_night.engine.chess.type.Move;
import com.r4men.game_night.engine.chess.type.Piece;
import oshi.util.tuples.Triplet;

public final class MoveParser {
    public static Move fromUci(Board board, String move) {
        if (!move.matches("[a-hA-H][1-8][a-hA-H][1-8][qrbnQBRN]?")) {
            throw new IllegalArgumentException("Invalid move string: " + move);
        }

        Triplet<Integer, Integer, Piece.PieceType> parsed = Util.convertUciTo8x8Move(move);
        int from8x8 = parsed.getA();
        int to8x8 = parsed.getB();

        if (from8x8 == to8x8) {
            return null;
        } else {
            int from10x12 = Util.convert8x8to10x12(from8x8);
            int to10x12 = Util.convert8x8to10x12(to8x8);
            Piece.PieceType promotionPiece = parsed.getC();

            Piece fromPiece = board.getPieceAt10x12(from10x12);
            Piece toPiece = board.getPieceAt10x12(to10x12);

            Move.Flag flag = Move.Flag.QUIET_MOVE_FLAG;
            Piece capturedPiece = Piece.EMPTY;

            if (fromPiece == Piece.EMPTY) {
                return null;
            } else if (fromPiece.getColor() == toPiece.getColor()) {
                if (fromPiece.isKing() && toPiece.isRook()) {
                    if (from8x8 - to8x8 == 4) {
                        flag = Move.Flag.QUEEN_CASTLE_FLAG;
                    } else if (from8x8 - to8x8 == -3) {
                        flag = Move.Flag.KING_CASTLE_FLAG;
                    } else {
                        return null;
                    }
                }
            } else {
                if (fromPiece.isPawn()) {
                    if (Math.abs(to8x8 - from8x8) == Direction.D8X8.N.toInt() * 2) {
                        flag = Move.Flag.DOUBLE_PAWN_PUSH_FLAG;
                    } else if (to10x12 == board.getEnPassantSquare10x12()) {
                        capturedPiece = board.getPieceAt10x12(to10x12);
                        flag = Move.Flag.EN_PASSANT_CAPTURE_FLAG;
                    } else if (fromPiece.getColor().opposite() == toPiece.getColor()) {
                        capturedPiece = board.getPieceAt10x12(to10x12);

                        if (promotionPiece != null) {
                            flag = switch (promotionPiece) {
                                case KNIGHT -> Move.Flag.KNIGHT_PROMOTION_CAPTURE_FLAG;
                                case BISHOP -> Move.Flag.BISHOP_PROMOTION_CAPTURE_FLAG;
                                case ROOK -> Move.Flag.ROOK_PROMOTION_CAPTURE_FLAG;
                                case QUEEN -> Move.Flag.QUEEN_PROMOTION_CAPTURE_FLAG;
                                default ->
                                        throw new IllegalArgumentException("Invalid promotion piece: " + promotionPiece);
                            };
                        } else {
                            flag = Move.Flag.CAPTURES_FLAG;
                        }
                    } else if (promotionPiece != null) {
                        flag = switch (promotionPiece) {
                            case KNIGHT -> Move.Flag.KNIGHT_PROMOTION_FLAG;
                            case BISHOP -> Move.Flag.BISHOP_PROMOTION_FLAG;
                            case ROOK -> Move.Flag.ROOK_PROMOTION_FLAG;
                            case QUEEN -> Move.Flag.QUEEN_PROMOTION_FLAG;
                            default -> throw new IllegalArgumentException("Invalid promotion piece: " + promotionPiece);
                        };
                    }
                } else {
                    if (fromPiece.getColor().opposite() == toPiece.getColor()) {
                        flag = Move.Flag.CAPTURES_FLAG;
                    }
                }
            }

            return new Move(from8x8, to8x8, flag, board.getEnPassantSquare10x12(), board.getCastlingRights(), board.getHalfmoveClock(), capturedPiece);
        }
    }
}
