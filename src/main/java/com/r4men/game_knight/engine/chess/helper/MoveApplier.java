package com.r4men.game_knight.engine.chess.helper;

import com.r4men.game_knight.engine.chess.Board;
import com.r4men.game_knight.engine.chess.type.Direction;
import com.r4men.game_knight.engine.chess.type.Move;
import com.r4men.game_knight.engine.chess.type.Piece;

public final class MoveApplier {
    public static void makeMove(Board board, Move move, boolean ignoreSelfCheck) {
        if (move == null) {
            return;
        }

        if (!MoveValidator.isMoveValid(board, move)) {
            return;
        }

        int from10x12 = move.getFrom10x12();
        int to10x12 = move.getTo10x12();
        Piece movedPiece = board.getPieceAt10x12(from10x12);

        if (movedPiece.isPawn() || move.isCapture()) {
            board.setHalfmoveClock(0);
        } else {
            board.incrementHalfmoveClock();
        }

        board.emptyPieceAt10x12(from10x12);
        board.emptyPieceAt10x12(to10x12);

        if (move.isQuiet() || move.isDoublePawnPush()) {
            board.setPieceAt10x12(to10x12, movedPiece);

            if (move.isDoublePawnPush()) {
                markEnPassantSquare(board, move);
            }
        } else if (move.isCapture()) {
            if (move.isPromotion()) {
                Piece.Color color = movedPiece.getColor();

                switch (color) {
                    case WHITE -> {
                        if (move.isQueenPromotion()) {
                            board.setPieceAt10x12(to10x12, Piece.WHITE_QUEEN);
                        } else if (move.isRookPromotion()) {
                            board.setPieceAt10x12(to10x12, Piece.WHITE_ROOK);
                        } else if (move.isBishopPromotion()) {
                            board.setPieceAt10x12(to10x12, Piece.WHITE_BISHOP);
                        } else if (move.isKnightPromotion()) {
                            board.setPieceAt10x12(to10x12, Piece.WHITE_KNIGHT);
                        }
                    }
                    case BLACK -> {
                        if (move.isQueenPromotion()) {
                            board.setPieceAt10x12(to10x12, Piece.BLACK_QUEEN);
                        } else if (move.isRookPromotion()) {
                            board.setPieceAt10x12(to10x12, Piece.BLACK_ROOK);
                        } else if (move.isBishopPromotion()) {
                            board.setPieceAt10x12(to10x12, Piece.BLACK_BISHOP);
                        } else if (move.isKnightPromotion()) {
                            board.setPieceAt10x12(to10x12, Piece.BLACK_KNIGHT);
                        }
                    }
                }
            } else {
                board.setPieceAt10x12(to10x12, movedPiece);

                if (move.isEnPassantCapture()) {
                    removeEnPassantPawn(board, move);
                }
            }
        } else if (move.isCastle()) {
            doCastling(board, move, movedPiece);
        } else if (move.isKingCastle()) {
            // For kingside castling, the move is encoded with 'to' == rook's original square
            // King moves to the appropriate square, and rook is moved from its original square
            if (movedPiece.isWhite()) {
                // King from e1 to g1
                board.setPieceAt10x12(6, movedPiece);
//                        emptySquare10x12(from);
                // Rook moves to f1 (which is 'to')
                board.setPieceAt10x12(5, Piece.WHITE_ROOK);
                board.emptyPieceAt10x12(7);
            } else {
                // King from e8 to g8
                board.setPieceAt10x12(62, movedPiece);
//                        emptySquare10x12(from);
                // Rook moves to f8 (which is 'to')
                board.setPieceAt10x12(61, Piece.BLACK_ROOK);
                board.emptyPieceAt10x12(63);
            }
        } else if (move.isQueenCastle()) {
            // For queenside castling, the move is encoded with 'to' == rook's original square
            // King moves to the appropriate square, and rook is moved from its original square
            if (movedPiece.isWhite()) {
                // King from e1 to c1
                board.setPieceAt10x12(0x17, movedPiece);
//                        emptySquare10x12(from);
                // Rook moves to d1 (which is 'to')
                board.setPieceAt10x12(0x18, Piece.WHITE_ROOK);
                board.emptyPieceAt10x12(0x15);
            } else {
                // King from e8 to c8
                board.setPieceAt10x12(58, movedPiece);
//                        emptySquare10x12(from);
                // Rook moves to d8 (which is 'to')
                board.setPieceAt10x12(59, Piece.BLACK_ROOK);
                board.emptyPieceAt10x12(56);
            }
        }

        if (!move.isDoublePawnPush()) {
            board.setEnPassantSquare10x12(-1);
        }

        if (move.isCastle()) {
            if (movedPiece.isWhite()) {
                board.andCastlingRights(0b1100);
            } else {
                board.andCastlingRights(0b0011);
            }
        }

        board.advancePlayerToMove();

        if (AttackDetector.isKingInCheck(board, board.getPlayerToMove())) {
            undoMove(board, move);
        }
    }

    private static void doCastling(Board board, Move move, Piece movedPiece) {

    }

    public static void undoMove(Board board, Move move) {
        int from10x12 = move.getFrom10x12();
        int to10x12 = move.getTo10x12();
        Piece piece = board.getPieceAt10x12(to10x12);

        // p means previous
        Piece.Color pPlayerToMove = board.getPlayerToMove();
        Piece pPiece = move.capturedPiece();

        if (move.isCapture()) {
            board.setPieceAt10x12(to10x12, pPiece);
            board.setPieceAt10x12(from10x12, piece);

            if (move.isEnPassantCapture()) {
                switch (pPlayerToMove) {
                    case WHITE -> board.setPieceAt10x12(to10x12 - 10, Piece.BLACK_PAWN);
                    case BLACK -> board.setPieceAt10x12(to10x12 + 10, Piece.WHITE_PAWN);
                    default ->
                            throw new IllegalStateException("Previous playerToMove is neither WHITE nor BLACK: " + pPlayerToMove);
                }
            }
        } else if (move.isCastle()) {
            switch (pPlayerToMove) {
                case WHITE -> {
                    board.setPieceAt10x12(0x19, Piece.WHITE_KING);

                    if (move.isKingCastle()) {
                        board.emptyPieceAt10x12(0x1A);
                        board.emptyPieceAt10x12(0x1B);
                        board.setPieceAt10x12(0x1C, Piece.WHITE_ROOK);
                    } else {
                        board.setPieceAt10x12(0x15, Piece.WHITE_ROOK);
                        board.emptyPieceAt10x12(0x16);
                        board.emptyPieceAt10x12(0x17);
                        board.emptyPieceAt10x12(0x18);
                    }
                }
                case BLACK -> {
                    board.setPieceAt10x12(0x5F, Piece.BLACK_KING);

                    if (move.isKingCastle()) {
                        board.emptyPieceAt10x12(0x60);
                        board.emptyPieceAt10x12(0x61);
                        board.setPieceAt10x12(0x62, Piece.BLACK_ROOK);
                    } else {
                        board.setPieceAt10x12(0x5B, Piece.BLACK_ROOK);
                        board.emptyPieceAt10x12(0x5C);
                        board.emptyPieceAt10x12(0x5D);
                        board.emptyPieceAt10x12(0x5E);
                    }
                }
                default ->
                        throw new IllegalStateException("Previous playerToMove is neither WHITE nor BLACK: " + pPlayerToMove);
            }
        } else {
            board.emptyPieceAt10x12(to10x12);
            board.setPieceAt10x12(from10x12, piece);
        }

        if (move.isPromotion()) {
            board.setPieceAt10x12(from10x12, switch (pPlayerToMove) {
                case WHITE -> Piece.WHITE_PAWN;
                case BLACK -> Piece.BLACK_PAWN;
                default ->
                        throw new IllegalStateException("Previous playerToMove is neither WHITE nor BLACK: " + pPlayerToMove);
            });
        }

        board.setPlayerToMove(pPlayerToMove);
        board.setHalfmoveClock(move.halfmoveClock());
        board.setCastlingRights(move.castlingRights());
        board.setEnPassantSquare10x12(move.enPassantSquare10x12());

        if (pPlayerToMove.isBlack()) {
            board.decrementFullmoveNumber();
        }
    }

    // TODO make real Javadoc

    /**
     * blah blah blah
     * <p>
     * Before this function is called, the player to move has already been swapped, because the move has already been made
     *
     * @param board the board that the move was made on
     * @param move  the move that was just made
     */
    static void markEnPassantSquare(Board board, Move move) {
        int to = move.getTo10x12();
//
        int leftIndex10x12 = move.getTo10x12() + Direction.D10X12.W.toInt();
        Piece left = board.getPieceAt10x12(leftIndex10x12);
        boolean leftIsPawn = left.isPawn() && left.matchesColor(board.getPlayerToMove());

        int rightIndex10x12 = move.getTo10x12() + Direction.D10X12.E.toInt();
        Piece right = board.getPieceAt10x12(rightIndex10x12);
        boolean rightIsPawn = right.isPawn() && right.matchesColor(board.getPlayerToMove());

        if (leftIsPawn || rightIsPawn) {
            board.setEnPassantSquare10x12(switch (board.getPlayerToMove()) {
                case WHITE -> to + Direction.D10X12.N.toInt();
                case BLACK -> to + Direction.D10X12.S.toInt();
                default -> throw new IllegalStateException("PlayerToMove is neither WHITE nor BLACK");
            });

            board.setEnPassantSquare10x12(findEnPassantMove(leftIndex10x12, leftIsPawn, rightIndex10x12, rightIsPawn, board.getEnPassantSquare10x12()));
        } else {
            board.setEnPassantSquare10x12(-1);
        }
    }

    // TODO
    static int findEnPassantMove(int leftIndex10x12, boolean leftIsPawn, int rightIndex10x12, boolean rightIsPawn, int ep10x12) {
//        if (leftIsPawn && !wouldBeInCheckAfterMove(new Move(Util.convert10x12to8x8(leftIndex10x12), Util.convert10x12to8x8(ep10x12), Move.Flag.EN_PASSANT_CAPTURE_FLAG, ep10x12, castlingRights, halfmoveClock, board10x12.get(ep10x12)))) {
//            return ep10x12;
//        } else if (rightIsPawn && !wouldBeInCheckAfterMove(new Move(Util.convert10x12to8x8(rightIndex10x12), Util.convert10x12to8x8(ep10x12), Move.Flag.EN_PASSANT_CAPTURE_FLAG, ep10x12, castlingRights, halfmoveClock, board10x12.get(ep10x12)))) {
//            return ep10x12;
//        } else {
//            return -1;
//        }

        return -1;
    }

    static void removeEnPassantPawn(Board board, Move move) {
        int to10x12 = move.getTo10x12();
        int s10x12;

        if (board.getPlayerToMove().isWhite()) {
            s10x12 = to10x12 + Direction.D10X12.N.toInt();
        } else {
            s10x12 = to10x12 + Direction.D10X12.S.toInt();
        }

        board.emptyPieceAt10x12(s10x12);
    }
}
