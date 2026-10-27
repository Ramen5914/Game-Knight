package games.ramen.game_knight.engine.chess.helper;

import games.ramen.game_knight.engine.chess.Board;
import games.ramen.game_knight.engine.chess.type.Direction;
import games.ramen.game_knight.engine.chess.type.Move;
import games.ramen.game_knight.engine.chess.type.Piece;

public final class MoveApplier {
    public static void makeMove(Board board, Move move, boolean ignoreCheck) {
        if (move == null) {
            return;
        }

        if (!MoveValidator.isMoveValid(board, move)) {
            return;
        }

        int from10x12 = move.getFrom10x12();
        int to10x12 = move.getTo10x12();
        Piece movedPiece = board.getPieceAt10x12(from10x12);

        revokeCastlingRightsForRook(board, movedPiece, from10x12);
        revokeCastlingRightsForRook(board, move.capturedPiece(), to10x12);

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
        }

        if (!move.isDoublePawnPush()) {
            board.setEnPassantSquare10x12(-1);
        }

        if (move.isCastle() || movedPiece.isKing()) {
            if (movedPiece.isWhite()) {
                board.andCastlingRights(0b1100);
            } else {
                board.andCastlingRights(0b0011);
            }
        }

        if (!ignoreCheck) {
            if (AttackDetector.isKingInCheck(board, board.getPlayerToMove())) {
                undoMove(board, move, false);
                return;
            } else {
                board.advancePlayerToMove();
            }
        }
    }

    private static void revokeCastlingRightsForRook(Board board, Piece piece, int square10x12) {
        if (!piece.isRook()) {
            return;
        }

        switch (square10x12) {
            case 0x15 -> board.andCastlingRights(0b1101);
            case 0x1C -> board.andCastlingRights(0b1110);
            case 0x5B -> board.andCastlingRights(0b0111);
            case 0x62 -> board.andCastlingRights(0b1011);
        }
    }

    private static void doCastling(Board board, Move move, Piece movedPiece) {
        if (movedPiece.isWhite()) {
            if (move.isKingCastle()) {
                board.setPieceAt10x12(0x1B, movedPiece);
                board.setPieceAt10x12(0x1A, Piece.WHITE_ROOK);
                board.emptyPieceAt10x12(0x1C);
            } else {
                board.setPieceAt10x12(0x17, movedPiece);
                board.setPieceAt10x12(0x18, Piece.WHITE_ROOK);
                board.emptyPieceAt10x12(0x15);
            }
        } else if (move.isKingCastle()) {
            board.setPieceAt10x12(0x61, movedPiece);
            board.setPieceAt10x12(0x60, Piece.BLACK_ROOK);
            board.emptyPieceAt10x12(0x62);
        } else {
            board.setPieceAt10x12(0x5D, movedPiece);
            board.setPieceAt10x12(0x5E, Piece.BLACK_ROOK);
            board.emptyPieceAt10x12(0x5B);
        }
    }

    public static void undoMove(Board board, Move move) {
        undoMove(board, move, true);
    }

    static void undoMove(Board board, Move move, boolean playerAdvanced) {
        int from10x12 = move.getFrom10x12();
        int to10x12 = move.getTo10x12();
        Piece piece = board.getPieceAt10x12(to10x12);

        // p means previous
        Piece.Color pPlayerToMove = playerAdvanced
                ? board.getPlayerToMove().opposite()
                : board.getPlayerToMove();
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

        if (playerAdvanced && board.getPlayerToMove().isWhite()) {
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
        boolean leftIsPawn = left.isPawn() && left.matchesColor(board.getPlayerToMove().opposite());

        int rightIndex10x12 = move.getTo10x12() + Direction.D10X12.E.toInt();
        Piece right = board.getPieceAt10x12(rightIndex10x12);
        boolean rightIsPawn = right.isPawn() && right.matchesColor(board.getPlayerToMove().opposite());

        if (leftIsPawn || rightIsPawn) {
            int potentialEpSquare = switch (board.getPlayerToMove()) {
                case WHITE -> to + Direction.D10X12.S.toInt();
                case BLACK -> to + Direction.D10X12.N.toInt();
                default -> throw new IllegalStateException("PlayerToMove is neither WHITE nor BLACK");
            };

            board.setEnPassantSquare10x12(findEnPassantMove(board, leftIndex10x12, leftIsPawn, rightIndex10x12, rightIsPawn, potentialEpSquare));
        } else {
            board.setEnPassantSquare10x12(-1);
        }
    }

    static int findEnPassantMove(Board board, int leftIndex10x12, boolean leftIsPawn, int rightIndex10x12, boolean rightIsPawn, int ep10x12) {
        int ep8x8 = Util.convert10x12to8x8(ep10x12);
        Piece.Color previousPlayerToMove = board.getPlayerToMove();

        if (leftIsPawn) {
            Move epMove = new Move(Util.convert10x12to8x8(leftIndex10x12), ep8x8, Move.Flag.EN_PASSANT_CAPTURE_FLAG, ep10x12, board.getCastlingRights(), board.getHalfmoveClock(), board.getPieceAt10x12(ep10x12));
            if (isLegalEnPassantMove(board, epMove, previousPlayerToMove)) {
                return ep10x12;
            }
        }

        if (rightIsPawn) {
            Move epMove = new Move(Util.convert10x12to8x8(rightIndex10x12), ep8x8, Move.Flag.EN_PASSANT_CAPTURE_FLAG, ep10x12, board.getCastlingRights(), board.getHalfmoveClock(), board.getPieceAt10x12(ep10x12));
            if (isLegalEnPassantMove(board, epMove, previousPlayerToMove)) {
                return ep10x12;
            }
        }

        return -1;
    }

    private static boolean isLegalEnPassantMove(Board board, Move move, Piece.Color previousPlayerToMove) {
        board.advancePlayerToMove();
        board.setEnPassantSquare10x12(move.enPassantSquare10x12());
        MoveApplier.makeMove(board, move, true);

        boolean legal = !AttackDetector.isKingInCheck(board, board.getPlayerToMove());
        MoveApplier.undoMove(board, move, false);
        board.setPlayerToMove(previousPlayerToMove);
        return legal;
    }

    static void removeEnPassantPawn(Board board, Move move) {
        int to10x12 = move.getTo10x12();
        int s10x12;

        if (board.getPlayerToMove().isWhite()) {
            s10x12 = to10x12 + Direction.D10X12.S.toInt();
        } else {
            s10x12 = to10x12 + Direction.D10X12.N.toInt();
        }

        board.emptyPieceAt10x12(s10x12);
    }
}
