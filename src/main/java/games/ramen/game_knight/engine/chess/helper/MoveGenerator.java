package games.ramen.game_knight.engine.chess.helper;

import games.ramen.game_knight.engine.chess.Board;
import games.ramen.game_knight.engine.chess.type.Castling;
import games.ramen.game_knight.engine.chess.type.Direction;
import games.ramen.game_knight.engine.chess.type.Move;
import games.ramen.game_knight.engine.chess.type.Piece;
import org.jetbrains.annotations.NotNull;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public final class MoveGenerator {
    public static long perft(Board board, int depth) {
        List<Move> moveList = new ArrayList<>(256);
        int nMoves, i;
        long nodes = 0;

        if (depth == 0) return 1;

        nMoves = generateLegalMoves(moveList, board);

        if (depth == 1) return nMoves;

        for (i = 0; i < nMoves; i++) {
            MoveApplier.makeMove(board, moveList.get(i), false);
            nodes += perft(board, depth - 1);
            MoveApplier.undoMove(board, moveList.get(i));
        }

        return nodes;
    }

    public static int generateLegalMoves(List<Move> moveList, Board board) {
        int count = 0;
        long occ = board.getBitBoard(board.getPlayerToMove(), Piece.PieceType.OCC);

        for (int s8x8 = 0; s8x8 < 64; s8x8++) {
            if ((occ & (1L << s8x8)) != 0) {
                int from10x12 = Util.convert8x8to10x12(s8x8);

                count += generateMovesForPiece(moveList, board, from10x12, board.getPieceAt10x12(from10x12));
            }
        }

        return count;
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

            if (isLegalMove(moveList, board, new Move(from8x8, to8x8, flag, enPassantSquare10x12, castlingRights, halfmoveClock, capturedPiece))) count++;
        }

        if (color == Piece.Color.WHITE && ((castlingRights & 0b0011) != 0)) {
            if ((castlingRights & 0b0001) == 0b0001) {
                if (Util.isCastlingPathClear10x12(board, Castling.WHITE_SHORT)) {
                    moveList.add(new Move(from8x8, 6, Move.Flag.KING_CASTLE_FLAG, enPassantSquare10x12, castlingRights, halfmoveClock, Piece.EMPTY));
                    count++;
                }
            }
            if ((castlingRights & 0b0010) == 0b0010) {
                if (Util.isCastlingPathClear10x12(board, Castling.WHITE_LONG)) {
                    moveList.add(new Move(from8x8, 2, Move.Flag.QUEEN_CASTLE_FLAG, enPassantSquare10x12, castlingRights, halfmoveClock, Piece.EMPTY));
                    count++;
                }
            }
        } else if (color == Piece.Color.BLACK && ((castlingRights & 0b1100) != 0)) {
            if ((castlingRights & 0b0100) == 0b0100) {
                if (Util.isCastlingPathClear10x12(board, Castling.BLACK_SHORT)) {
                    moveList.add(new Move(from8x8, 62, Move.Flag.KING_CASTLE_FLAG, enPassantSquare10x12, castlingRights, halfmoveClock, Piece.EMPTY));
                    count++;
                }
            }
            if ((castlingRights & 0b1000) == 0b1000) {
                if (Util.isCastlingPathClear10x12(board, Castling.BLACK_LONG)) {
                    moveList.add(new Move(from8x8, 58, Move.Flag.QUEEN_CASTLE_FLAG, enPassantSquare10x12, castlingRights, halfmoveClock, Piece.EMPTY));
                    count++;
                }
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
                if (isLegalMove(moveList, board,
                        new Move(from8x8, Util.convert10x12to8x8(to10x12), Move.Flag.QUIET_MOVE_FLAG, enPassantSquare10x12, castlingRights, halfmoveClock, Piece.EMPTY)
                )) count++;

                to10x12 += direction.toInt();
            }

            if (board.getPieceAt10x12(to10x12).matchesColor(oppositeColor)) {
                if (isLegalMove(moveList, board,
                        new Move(from8x8, Util.convert10x12to8x8(to10x12), Move.Flag.CAPTURES_FLAG, enPassantSquare10x12, castlingRights, halfmoveClock, board.getPieceAt10x12(to10x12))
                )) count++;
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
                if (isLegalMove(moveList, board,
                        new Move(from8x8, Util.convert10x12to8x8(to10x12), Move.Flag.QUIET_MOVE_FLAG, enPassantSquare10x12, castlingRights, halfmoveClock, Piece.EMPTY)
                )) count++;

                to10x12 += direction.toInt();
            }

            if (board.getPieceAt10x12(to10x12).matchesColor(oppositeColor)) {
                if (isLegalMove(moveList, board,
                        new Move(from8x8, Util.convert10x12to8x8(to10x12), Move.Flag.CAPTURES_FLAG, enPassantSquare10x12, castlingRights, halfmoveClock, board.getPieceAt10x12(to10x12))
                )) count++;
            }
        }

        return count;
    }

    private static int generateKnightMoves(List<Move> moveList, Board board, int from10x12, Piece.Color color) {
        int count = 0;
        int from8x8 = Util.convert10x12to8x8(from10x12);

        int enPassantSquare10x12 = board.getEnPassantSquare10x12();
        int castlingRights = board.getCastlingRights();
        int halfmoveClock = board.getHalfmoveClock();

        final int N = Direction.D10X12.N.toInt();
        final int E = Direction.D10X12.E.toInt();
        final int S = Direction.D10X12.S.toInt();
        final int W = Direction.D10X12.W.toInt();
        List<Integer> spots = List.of(
                from10x12 + N + N + W,
                from10x12 + N + N + E,
                from10x12 + E + E + N,
                from10x12 + E + E + S,
                from10x12 + S + S + E,
                from10x12 + S + S + W,
                from10x12 + W + W + S,
                from10x12 + W + W + N
        );

        for (int to10x12 : spots) {
            if (Util.isIntOffBoard10x12(to10x12)) continue;

            final int to8x8 = Util.convert10x12to8x8(to10x12);

            Piece capturedPiece = board.getPieceAt10x12(to10x12);
            if (capturedPiece.isEmpty()) {
                if (isLegalMove(moveList, board,
                        new Move(from8x8, to8x8, Move.Flag.QUIET_MOVE_FLAG, enPassantSquare10x12, castlingRights, halfmoveClock, Piece.EMPTY)
                )) count++;
            } else if (!capturedPiece.isOffBoard() && capturedPiece.matchesColor(color.opposite())) {
                if (isLegalMove(moveList, board,
                        new Move(from8x8, to8x8, Move.Flag.CAPTURES_FLAG, enPassantSquare10x12, castlingRights, halfmoveClock, capturedPiece)
                )) count++;
            }
        }

        return count;
    }

    private static int generatePawnMoves(List<Move> moveList, Board board, int from10x12, Piece piece) {
        int count = 0;
        int from8x8 = Util.convert10x12to8x8(from10x12);

        int enPassantSquare10x12 = board.getEnPassantSquare10x12();
        int castlingRights = board.getCastlingRights();
        int halfmoveClock = board.getHalfmoveClock();
        int promotionRank = piece.isWhite() ? 7 : 0;
        int pawnDirection10x12 = piece.isWhite() ? Direction.D10X12.N.toInt() : Direction.D10X12.S.toInt();
        int pawnDirection8x8 = piece.isWhite() ? Direction.D8X8.N.toInt() : -Direction.D8X8.N.toInt();

        if (board.getPieceAt10x12(from10x12 + pawnDirection10x12).isEmpty()) {
            int to8x8 = from8x8 + pawnDirection8x8;
            if (to8x8 >> 3 == promotionRank) {
                count += generatePromotionMoves(moveList, board, from8x8, to8x8, false, Piece.EMPTY,
                        enPassantSquare10x12, castlingRights, halfmoveClock);
            } else if (isLegalMove(moveList, board, new Move(from8x8, to8x8, Move.Flag.QUIET_MOVE_FLAG,
                    enPassantSquare10x12, castlingRights, halfmoveClock, Piece.EMPTY))) {
                count++;
            }

            if (to8x8 >> 3 != promotionRank
                    && board.getPieceAt10x12(from10x12 + pawnDirection10x12 * 2).isEmpty()
                    && Util.getRank10x12(from10x12) == (piece.isWhite() ? 1 : 6)
                    && isLegalMove(moveList, board, new Move(from8x8, from8x8 + pawnDirection8x8 * 2,
                    Move.Flag.DOUBLE_PAWN_PUSH_FLAG, enPassantSquare10x12, castlingRights, halfmoveClock, Piece.EMPTY))) {
                count++;
            }

        }

        int[] captureDirections10x12 = {Direction.D10X12.E.toInt(), Direction.D10X12.W.toInt()};
        int[] captureDirections8x8 = {Direction.D8X8.E.toInt(), Direction.D8X8.W.toInt()};
        for (int i = 0; i < captureDirections10x12.length; i++) {
            int to10x12 = from10x12 + pawnDirection10x12 + captureDirections10x12[i];
            boolean isEnPassant = to10x12 == enPassantSquare10x12;
            Piece capturedPiece = board.getPieceAt10x12(to10x12);

            if (capturedPiece.matchesColor(piece.getColor().opposite()) || isEnPassant) {
                int to8x8 = from8x8 + pawnDirection8x8 + captureDirections8x8[i];
                if (to8x8 >> 3 == promotionRank) {
                    count += generatePromotionMoves(moveList, board, from8x8, to8x8, true, capturedPiece,
                            enPassantSquare10x12, castlingRights, halfmoveClock);
                } else if (isLegalMove(moveList, board, new Move(from8x8, to8x8,
                        isEnPassant ? Move.Flag.EN_PASSANT_CAPTURE_FLAG : Move.Flag.CAPTURES_FLAG,
                        enPassantSquare10x12, castlingRights, halfmoveClock, capturedPiece))) {
                    count++;
                }
            }
        }

        return count;
    }

    private static int generatePromotionMoves(List<Move> moveList, Board board, int from8x8, int to8x8,
                                              boolean capture, Piece capturedPiece, int enPassantSquare10x12,
                                              int castlingRights, int halfmoveClock) {
        int count = 0;
        Move.Flag[] flags = capture
                ? new Move.Flag[]{Move.Flag.KNIGHT_PROMOTION_CAPTURE_FLAG, Move.Flag.BISHOP_PROMOTION_CAPTURE_FLAG,
                Move.Flag.ROOK_PROMOTION_CAPTURE_FLAG, Move.Flag.QUEEN_PROMOTION_CAPTURE_FLAG}
                : new Move.Flag[]{Move.Flag.KNIGHT_PROMOTION_FLAG, Move.Flag.BISHOP_PROMOTION_FLAG,
                Move.Flag.ROOK_PROMOTION_FLAG, Move.Flag.QUEEN_PROMOTION_FLAG};

        for (Move.Flag flag : flags) {
            if (isLegalMove(moveList, board, new Move(from8x8, to8x8, flag, enPassantSquare10x12,
                    castlingRights, halfmoveClock, capturedPiece))) {
                count++;
            }
        }

        return count;
    }

    private static boolean isLegalMove(List<Move> moveList, Board board, Move move) {
        MoveApplier.makeMove(board, move, true);
        if (!AttackDetector.isKingInCheck(board, board.getPlayerToMove())) {
            moveList.add(move);
            MoveApplier.undoMove(board, move, false);
            return true;
        }

        MoveApplier.undoMove(board, move, false);
        return false;
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
