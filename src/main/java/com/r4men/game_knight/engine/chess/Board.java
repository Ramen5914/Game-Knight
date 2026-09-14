package com.r4men.game_knight.engine.chess;

import com.r4men.game_knight.engine.chess.helper.*;
import com.r4men.game_knight.engine.chess.type.Move;
import com.r4men.game_knight.engine.chess.type.Piece;
import org.jetbrains.annotations.NotNull;
import oshi.util.tuples.Pair;

import java.util.ArrayList;
import java.util.List;

public class Board {
    private final List<Piece> board10x12;
    private final long[][] bitBoards;
    private long allOcc;

    private int enPassantSquare10x12;
    private Piece.Color playerToMove;
    private int halfmoveClock;
    private int fullmoveNumber;
    private int castlingRights;

    public Board() {
        board10x12 = new ArrayList<>(120);
        for (int y = 0; y < 12; y++) {
            for (int x = 0; x < 10; x++) {
                if (x == 0 || x == 9 || y < 2 || y > 9) {
                    board10x12.add(Piece.OFF_BOARD);
                } else {
                    board10x12.add(Piece.EMPTY);
                }
            }
        }

        bitBoards = new long[2][7];
        allOcc = 0L;
        enPassantSquare10x12 = -1;
        playerToMove = Piece.Color.WHITE;
        halfmoveClock = 0;
        fullmoveNumber = 1;
        castlingRights = 0b0000;
    }

    public Board(String fen) {
        this();
        FenCodec.load(this, fen);
    }

    public long getBitBoard(Piece.Color color, Piece.PieceType type) {
        if (color.ordinal() > 1 || type.ordinal() > 6) {
            throw new IllegalArgumentException("Trying to retrieve bitboard of invalid color or type: " + color + ", " + type);
        }

        return bitBoards[color.ordinal()][type.ordinal()];
    }

    public long getAllOcc() {
        return allOcc;
    }

    public int getEnPassantSquare10x12() {
        return enPassantSquare10x12;
    }

    public void setEnPassantSquare10x12(int enPassantSquare10x12) {
        this.enPassantSquare10x12 = enPassantSquare10x12;
    }

    public Piece.Color getPlayerToMove() {
        return playerToMove;
    }

    public void setPlayerToMove(Piece.Color playerToMove) {
        this.playerToMove = playerToMove;
    }

    public int getHalfmoveClock() {
        return halfmoveClock;
    }

    public void setHalfmoveClock(int halfmoveClock) {
        this.halfmoveClock = halfmoveClock;
    }

    public void incrementHalfmoveClock() {
        this.halfmoveClock++;
    }

    public int getFullmoveNumber() {
        return fullmoveNumber;
    }

    public void setFullmoveNumber(int fullmoveNumber) {
        this.fullmoveNumber = fullmoveNumber;
    }

    public void decrementFullmoveNumber() {
        this.fullmoveNumber--;
    }

    public int getCastlingRights() {
        return castlingRights;
    }

    public void setCastlingRights(int castlingRights) {
        this.castlingRights = castlingRights;
    }

    public void orCastlingRights(int orBits) {
        this.castlingRights |= orBits;
    }

    public void andCastlingRights(int andBits) {
        this.castlingRights &= andBits;
    }

    public Piece getPieceAt10x12(int s10x12) {
        return board10x12.get(s10x12);
    }

    public void setPieceAt10x12(int s10x12, Piece piece) {
        Piece previousPiece = board10x12.get(s10x12);
        if (previousPiece.isOffBoard()) {
            throw new IllegalArgumentException("Attempting to write piece to an off-board location");
        }

        int s8x8 = Util.convert10x12to8x8(s10x12);
        long bit = 1L << s8x8;

        if (!previousPiece.isEmpty()) {
            int color = previousPiece.getColor().ordinal();
            bitBoards[color][previousPiece.getPieceType().ordinal()] &= ~bit;
            bitBoards[color][Piece.PieceType.OCC.ordinal()] &= ~bit;
            allOcc &= ~bit;
        }

        board10x12.set(s10x12, piece);

        if (!piece.isEmpty()) {
            int color = piece.getColor().ordinal();
            bitBoards[color][piece.getPieceType().ordinal()] |= bit;
            bitBoards[color][Piece.PieceType.OCC.ordinal()] |= bit;
            allOcc |= bit;
        }
    }

    public void emptyPieceAt10x12(int s10x12) {
        setPieceAt10x12(s10x12, Piece.EMPTY);
    }

    public void advancePlayerToMove() {
        playerToMove = playerToMove.opposite();
        if (playerToMove.isWhite()) {
            fullmoveNumber++;
        }
    }

    public void makeMove(String uci) {
        Move move = MoveParser.fromUci(this, uci);
        MoveApplier.makeMove(this, move, false);
    }

    public void makeMove(int from8x8, int to8x8) {
        Move move = MoveParser.fromInts(this, from8x8, to8x8);
        MoveApplier.makeMove(this, move, false);
    }

    public void makeMove(Move move) {
        MoveApplier.makeMove(this, move, false);
    }

    public void undoMove(Move move) {
        MoveApplier.undoMove(this, move);
    }

    public Pair<List<Move>, Integer> generateLegalMoves() {
        return MoveGenerator.generateLegalMoves(this);
    }

    public String toFen() {
        return FenCodec.toFen(this);
    }

    public String toSimpleFen() {
        return FenCodec.toSimpleFen(this);
    }

    public @NotNull List<Integer> generate8x8MovesForPiece(int s8x8) {
        return MoveGenerator.generate8x8MovesForPiece(this, s8x8);
    }

//    public void makeMove(String move) {
//        if (move.matches("^([a-hA-H][1-8])([a-hA-H][1-8])([qrbn])?$")) {
//            Move m = createMoveFromString(move);
//            makeMove(m);
//        } else {
//            throw new IllegalArgumentException("Invalid move string: " + move);
//        }
//    }
//
//    public void makeMove(@Nullable Move move) {
//        makeMove(move, false);
//    }
//
//    public void makeMove(@Nullable Move move, boolean ignoreSelfCheck) {
//        if (move != null) {
//
//        }
//    }
//
//    public void undoMove(Move move) {
//        int from10x12 = move.getFrom10x12();
//        int to10x12 = move.getTo10x12();
//        Piece piece = board10x12.get(to10x12);
//
//        // p means previous
//        Piece.Color pPlayerToMove = playerToMove.opposite();
//        Piece pPiece = move.capturedPiece();
//
//        if (move.isCapture()) {
//            setSquare10x12(to10x12, pPiece);
//            setSquare10x12(from10x12, piece);
//
//            if (move.isEnPassantCapture()) {
//                switch (pPlayerToMove) {
//                    case WHITE -> setSquare10x12(to10x12 - 10, Piece.BLACK_PAWN);
//                    case BLACK -> setSquare10x12(to10x12 + 10, Piece.WHITE_PAWN);
//                    default ->
//                            throw new IllegalStateException("Previous playerToMove is neither WHITE nor BLACK: " + pPlayerToMove);
//                }
//            }
//        } else if (move.isCastle()) {
//            switch (pPlayerToMove) {
//                case WHITE -> {
//                    setSquare10x12(0x19, Piece.WHITE_KING);
//
//                    if (move.isKingCastle()) {
//                        emptySquare10x12(0x1A);
//                        emptySquare10x12(0x1B);
//                        setSquare10x12(0x1C, Piece.WHITE_ROOK);
//                    } else {
//                        setSquare10x12(0x15, Piece.WHITE_ROOK);
//                        emptySquare10x12(0x16);
//                        emptySquare10x12(0x17);
//                        emptySquare10x12(0x18);
//                    }
//                }
//                case BLACK -> {
//                    setSquare10x12(0x5F, Piece.BLACK_KING);
//
//                    if (move.isKingCastle()) {
//                        emptySquare10x12(0x60);
//                        emptySquare10x12(0x61);
//                        setSquare10x12(0x62, Piece.BLACK_ROOK);
//                    } else {
//                        setSquare10x12(0x5B, Piece.BLACK_ROOK);
//                        emptySquare10x12(0x5C);
//                        emptySquare10x12(0x5D);
//                        emptySquare10x12(0x5E);
//                    }
//                }
//                default ->
//                        throw new IllegalStateException("Previous playerToMove is neither WHITE nor BLACK: " + pPlayerToMove);
//            }
//        } else {
//            emptySquare10x12(to10x12);
//            setSquare10x12(from10x12, piece);
//        }
//
//        if (move.isPromotion()) {
//            setSquare10x12(from10x12, switch (pPlayerToMove) {
//                case WHITE -> Piece.WHITE_PAWN;
//                case BLACK -> Piece.BLACK_PAWN;
//                default ->
//                        throw new IllegalStateException("Previous playerToMove is neither WHITE nor BLACK: " + pPlayerToMove);
//            });
//        }
//
//        playerToMove = pPlayerToMove;
//        halfmoveClock = move.halfmoveClock();
//        castlingRights = move.castlingRights();
//        enPassantSquare10x12 = move.enPassantSquare10x12();
//        if (playerToMove.isBlack()) {
//            fullmoveNumber--;
//        }
//    }
//
//    private void setSquare10x12(int square10x12, Piece piece) {
//        int square8x8 = Util.convert10x12to8x8(square10x12);
//
//        Piece previousPiece = board10x12.get(square10x12);
//
//        long bit8x8 = 1L << square8x8;
//
//        if (piece.isEmpty()) {
//            if (!previousPiece.isEmpty()) {
//                board10x12.set(square10x12, Piece.EMPTY);
//
//                int color = previousPiece.getColor().ordinal();
//
//                bitBoards[color][previousPiece.getPieceType().ordinal()] &= ~bit8x8;
//                bitBoards[color][Piece.PieceType.OCC.ordinal()] &= ~bit8x8;
//                allOcc &= ~bit8x8;
//            }
//        } else {
//            board10x12.set(square10x12, piece);
//
//            bitBoards[piece.getColor().ordinal()][piece.getPieceType().ordinal()] |= bit8x8;
//            bitBoards[piece.getColor().ordinal()][Piece.PieceType.OCC.ordinal()] |= bit8x8;
//            allOcc |= bit8x8;
//        }
//    }
//
//    private void emptySquare10x12(int square) {
//        setSquare10x12(square, Piece.EMPTY);
//    }
//
//    private boolean wouldBeInCheckAfterMove(Move move) {
//        makeMove(move, true);
//        boolean toReturn = isKingInCheck(playerToMove.opposite());
//        undoMove(move);
//
//        return toReturn;
//    }
//
//    private boolean isKingInCheck(Piece.Color color) {
//        int kingSquare8x8 = Long.numberOfTrailingZeros(bitBoards[color.ordinal()][Piece.PieceType.KING.ordinal()]);
//
//        if (kingSquare8x8 == 64) {
//            throw new IllegalStateException("No king found on board for " + color);
//        }
//
//        return isSquareAttackedBy10x12(Util.convert8x8to10x12(kingSquare8x8), color.opposite());
//    }
//
//    private boolean isSquareAttackedBy10x12(int s10x12, Piece.Color attackingColor) {
//        long occBB = bitBoards[attackingColor.ordinal()][Piece.PieceType.OCC.ordinal()];
//
//        for (int s8x8 = 0; s8x8 < 64; s8x8++) {
//            if (((occBB >>> s8x8) & 0b1) == 1) {
//                int from10x12 = Util.convert8x8to10x12(s8x8);
//                Piece piece = board10x12.get(from10x12);
//
//                if (canPieceAttackSquare10x12(from10x12, s10x12, piece)) {
//                    return true;
//                }
//            }
//        }
//
//        return false;
//    }
//
//    private boolean isSquareUnderAttack10x12(int s10x12, Piece.Color defendingColor) {
//        return isSquareAttackedBy10x12(s10x12, defendingColor.opposite());
//    }
//
//    // TODO redo logic in this method
//    private boolean canPieceAttackSquare10x12(int from10x12, int to10x12, Piece piece) {
//        if (from10x12 == to10x12) {
//            return false;
//        }
//
//        int fromRank = from10x12 / 10;
//        int fromFile = from10x12 % 10;
//        int toRank = to10x12 / 10;
//        int toFile = to10x12 % 10;
//        int rankDiff = toRank - fromRank;
//        int fileDiff = toFile - fromFile;
//
//        Direction.D10X12 direction = Util.getD10X12FromSquares(from10x12, to10x12);
//
//        assert(direction != null);
//
//        switch (piece.getPieceType()) {
//            case PAWN:
//                int start = piece.getColor().isWhite() ? Direction.D10X12.N.toInt() : Direction.D10X12.S.toInt();
//
//                return to10x12 == start + Direction.D10X12.E.toInt() || to10x12 == start + Direction.D10X12.W.toInt();
//            case KNIGHT:
//                return isValidKnightMove(null, null, from10x12, to10x12);
//            case BISHOP:
//                return isPathClear10x12(from10x12, to10x12, direction) && Util.isRayFrom10x12(from10x12, to10x12, direction) && direction.isDiagonal();
//            case ROOK:
//                return ((rankDiff == 0 && fileDiff != 0) || (rankDiff != 0 && fileDiff == 0)) && isPathClear10x12(from10x12, to10x12, direction);
//            case QUEEN:
//                // Queen moves like rook or bishop
//                boolean isRookMove = (rankDiff == 0 && fileDiff != 0) || (rankDiff != 0 && fileDiff == 0);
//                boolean isBishopMove = Math.abs(rankDiff) == Math.abs(fileDiff) && rankDiff != 0;
//                return (isRookMove || isBishopMove) && isPathClear10x12(from10x12, to10x12, direction);
//            case KING:
//                // King moves one square in any direction
//                return Math.abs(rankDiff) <= 1 && Math.abs(fileDiff) <= 1 && (rankDiff != 0 || fileDiff != 0);
//            default:
//                return false;
//        }
//    }
//
//    private boolean isMoveValid(Move move) {
//        return isMoveValid(move, false);
//    }
//
//    private boolean isMoveValid(Move move, boolean ignoreSelfCheck) {
//        int from10x12 = move.getFrom10x12();
//        int to10x12 = move.getTo10x12();
//
//        Piece movedPiece = board10x12.get(from10x12);
//        Piece destinationPiece = board10x12.get(to10x12);
//
//        // From and to squares must be different
//        // Moved piece belongs to the player whose turn it is to move
//        // Destination square is not off the board
//        if (from10x12 == to10x12 || !movedPiece.matchesColor(playerToMove) || destinationPiece.isOffBoard()) {
//            return false;
//        }
//
//        if (ignoreSelfCheck) {
//            return isValidMovementForPiece(movedPiece, move);
//        } else {
//            return isValidMovementForPiece(movedPiece, move) && !wouldBeInCheckAfterMove(move);
//        }
//    }
//
//    private boolean isValidMovementForPiece(Piece movedPiece, Move move) {
//        int from10x12 = move.getFrom10x12();
//        int to10x12 = move.getTo10x12();
//
//        Piece destinationPiece = board10x12.get(to10x12);
//
//        if (move.isCapture() && destinationPiece.isEmpty() && !move.isEnPassantCapture()) {
//            return false;
//        } else if (!movedPiece.isPawn() && move.isPromotion()) {
//            return false;
//        } else if (!movedPiece.isKing() && destinationPiece.getColor() == playerToMove) {
//            return false;
//        }
//
////        int fromRank = from / 8;
////        int fromFile = from % 8;
////        int toRank = to / 8;
////        int toFile = to % 8;
////        int rankDiff = toRank - fromRank;
////        int fileDiff = toFile - fromFile;
//
//        return switch (movedPiece.getPieceType()) {
//            case PAWN -> isValidPawnMove(move, destinationPiece, from10x12, to10x12);
//            case KNIGHT -> isValidKnightMove(move, destinationPiece, from10x12, to10x12);
//            case BISHOP -> isValidBishopMove(move, destinationPiece, from10x12, to10x12);
//            case ROOK -> isValidRookMove(move, destinationPiece, from10x12, to10x12);
//            case QUEEN -> isValidQueenMove(move, destinationPiece, from10x12, to10x12);
//            case KING -> isValidKingMove(move, destinationPiece, from10x12, to10x12);
//            default -> false;
//        };
//    }
//
//    private boolean isValidPawnMove(Move move, Piece destinationPiece, int from10x12, int to10x12) {
//        Direction.D10X12 direction = playerToMove.isWhite() ? Direction.D10X12.N : Direction.D10X12.S;
//
//        if (move.isDoublePawnPush()) {
//            int validStartRank = playerToMove.isWhite() ? 2 : 7;
//            int fromRank = from10x12 / 10 - 1;
//
//            if (fromRank == validStartRank && to10x12 == from10x12 + direction.toInt() * 2) {
//                return isPathClear10x12(from10x12, to10x12, direction);
//            } else {
//                return false;
//            }
//        } else if (move.isEnPassantCapture()) {
//            // En passant: pawn must be one rank forward and one file diagonal to the en passant square
//            int oneForward = from10x12 + direction.toInt();
//            return to10x12 == oneForward + Direction.D10X12.E.toInt() && enPassantSquare10x12 == to10x12
//                    || to10x12 == oneForward + Direction.D10X12.W.toInt() && enPassantSquare10x12 == to10x12;
//        }
//
//        if (move.isPromotion()) {
//            int toRank = to10x12 / 10 - 1;
//            int promotionRank = playerToMove.isWhite() ? 8 : 1;
//
//            if (toRank != promotionRank) {
//                return false;
//            }
//
//            if (move.isCapture()) {
//                return to10x12 == from10x12 + direction.toInt() + Direction.D10X12.E.toInt() && !destinationPiece.isEmpty()
//                        || to10x12 == from10x12 + direction.toInt() + Direction.D10X12.W.toInt() && !destinationPiece.isEmpty();
//            } else {
//                return to10x12 == from10x12 + direction.toInt() && destinationPiece.isEmpty();
//            }
//        }
//
//        if (move.isCapture()) {
//            // Regular pawn capture (non-promotion)
//            return (to10x12 == from10x12 + direction.toInt() + Direction.D10X12.E.toInt() || to10x12 == from10x12 + direction.toInt() + Direction.D10X12.W.toInt())
//                    && !destinationPiece.isEmpty();
//        }
//
//        // Regular pawn move (quiet, non-promotion)
//        // Pawn can only move forward one square on a quiet move
//        return to10x12 == from10x12 + direction.toInt() && destinationPiece.isEmpty();
//    }
//
//    private boolean isValidKnightMove(Move move, Piece destinationPiece, int from10x12, int to10x12) {
//        int rankDiff = Util.getRankDistance10x12(from10x12, to10x12);
//        int fileDiff = Util.getFileDistance10x12(from10x12, to10x12);
//
//        return rankDiff > 0 && fileDiff > 0 && rankDiff + fileDiff == 3;
//    }
//
//    private boolean isValidBishopMove(Move move, Piece destinationPiece, int from10x12, int to10x12) {
//        Direction.D10X12 direction = Util.getD10X12FromSquares(from10x12, to10x12);
//
//        if (direction == Direction.D10X12.N || direction == Direction.D10X12.E || direction == Direction.D10X12.S || direction == Direction.D10X12.W) {
//            return false;
//        }
//
//        if (isPathClear10x12(from10x12, to10x12, direction)) {
//            if (move.isCapture() && destinationPiece.getColor() == playerToMove.opposite()) {
//                return true;
//            } else {
//                return destinationPiece.isEmpty();
//            }
//        } else {
//            return false;
//        }
//    }
//
//    private boolean isValidRookMove(Move move, Piece destinationPiece, int from10x12, int to10x12) {
//        Direction.D10X12 direction = Util.getD10X12FromSquares(from10x12, to10x12);
//
//        if (direction == Direction.D10X12.NE || direction == Direction.D10X12.NW || direction == Direction.D10X12.SE || direction == Direction.D10X12.SW) {
//            return false;
//        }
//
//        if (isPathClear10x12(from10x12, to10x12, direction)) {
//            if (move.isCapture() && destinationPiece.getColor() == playerToMove.opposite()) {
//                return true;
//            } else {
//                return destinationPiece.isEmpty();
//            }
//        } else {
//            return false;
//        }
//    }
//
//    private boolean isValidQueenMove(Move move, Piece destinationPiece, int from10x12, int to10x12) {
//        return isValidBishopMove(move, destinationPiece, from10x12, to10x12) || isValidRookMove(move, destinationPiece, from10x12, to10x12);
//    }
//
//    // TODO make sure this works
//    private boolean isValidKingMove(Move move, Piece destinationPiece, int from10x12, int to10x12) {
//        if (move.isQuiet()) {
//            return Util.getRayDistance10x12(from10x12, to10x12) == 1 && destinationPiece.isEmpty();
//        } else if (move.isCapture()) {
//            return Util.getRayDistance10x12(from10x12, to10x12) == 1 && destinationPiece.getColor() == playerToMove.opposite();
//        } else if (move.isKingCastle()) {
//            if (playerToMove.isWhite()) {
//                return from10x12 == 0x19 && to10x12 == 0x1C && isKingsideCastleLegal();
//            } else {
//                return from10x12 == 0x5F && to10x12 == 0x62 && isKingsideCastleLegal();
//            }
//        } else if (move.isQueenCastle()) {
//            if (playerToMove.isWhite()) {
//                return from10x12 == 0x19 && to10x12 == 0x15 && isQueensideCastleLegal();
//            } else {
//                return from10x12 == 0x5f && to10x12 == 0x5B && isQueensideCastleLegal();
//            }
//        }
//
//        return false;
//    }
//
//    private boolean isPathClear10x12(int from, int to, Direction.D10X12 direction) {
//        if (Util.isRayFrom10x12(from, to, direction)) {
//            int squareToCheck = from + direction.toInt();
//            while (squareToCheck != to) {
//                if (!board10x12.get(squareToCheck).isEmpty()) {
//                    return false;
//                }
//                squareToCheck += direction.toInt();
//            }
//
//            return true;
//        } else {
//            throw new IllegalArgumentException("Squares " + from + " and " + to + " are not aligned in direction " + direction);
//        }
//    }
//
//    private boolean isKingsideCastleLegal() {
//        if (playerToMove.isWhite()) {
//            return ((castlingRights & 0b1) == 1 && isPathClear10x12(0x19, 0x1C, Direction.D10X12.E) && (!isSquareUnderAttack10x12(0x19, playerToMove) && !isSquareUnderAttack10x12(0x1A, playerToMove) && !isSquareUnderAttack10x12(0x1B, playerToMove)));
//        } else {
//            // TODO implement black king side castling
//            return false;
//
////            if ((castlingRights & 0b0100) == 0) {
////                return false; // No kingside castling rights
////            }
////            // Squares e8, f8, g8 must be unoccupied
////            if (!(board10x12.get(60).isKing() && board10x12.get(61).isEmpty() && board10x12.get(62).isEmpty() &&
////                   board10x12.get(63).isRook())) {
////                return false;
////            }
////            // King must not be in check, and cannot move through check
////            // Check e8 (from), f8 (through), g8 (to)
////            return !isSquareUnderAttack(60, playerToMove) &&
////                   !isSquareUnderAttack(61, playerToMove) &&
////                   !isSquareUnderAttack(62, playerToMove);
//        }
//    }
//
//    private boolean isQueensideCastleLegal() {
//        if (playerToMove.isWhite()) {
//            return ((castlingRights >>> 1 & 0b1) == 1 && isPathClear10x12(0x19, 0x15, Direction.D10X12.W) && (!isSquareUnderAttack10x12(0x16, playerToMove) && !isSquareUnderAttack10x12(0x17, playerToMove) && !isSquareUnderAttack10x12(0x18, playerToMove) && !isSquareUnderAttack10x12(0x19, playerToMove)));
//        } else {
//            // TODO implement black queen side castling
//            return false;
//
////            if ((castlingRights & 0b0100) == 0) {
////                return false; // No kingside castling rights
////            }
////            // Squares e8, f8, g8 must be unoccupied
////            if (!(board10x12.get(60).isKing() && board10x12.get(61).isEmpty() && board10x12.get(62).isEmpty() &&
////                   board10x12.get(63).isRook())) {
////                return false;
////            }
////            // King must not be in check, and cannot move through check
////            // Check e8 (from), f8 (through), g8 (to)
////            return !isSquareUnderAttack(60, playerToMove) &&
////                   !isSquareUnderAttack(61, playerToMove) &&
////                   !isSquareUnderAttack(62, playerToMove);
//        }
//    }
//
//    public int generateLegalMoves(Move[] moveList) {
//        // Initialize all Move objects in the array
//        for (int i = 0; i < moveList.length; i++) {
//            if (moveList[i] == null) {
////                moveList[i] = new Move(0, 0, 0);
//            }
//        }
//
//        int moveCount = 0;
//
//        // Iterate through all squares
//        for (int from = 0; from < 64; from++) {
//            Piece piece = board10x12.get(from);
//
//            // Skip empty squares and opponent pieces
//            if (piece.isEmpty() || piece.getColor() != playerToMove) {
//                continue;
//            }
//
//            // Generate moves for this piece
//            moveCount = generateMovesForPiece(from, piece, moveList, moveCount);
//        }
//
//        return moveCount;
//    }
//
//    private int generateMovesForPiece(int from, Piece piece, Move[] moveList, int moveCount) {
//        switch (piece.getPieceType()) {
//            case PAWN:
//                moveCount = generatePawnMoves(from, moveList, moveCount);
//                break;
//            case KNIGHT:
//                moveCount = generateKnightMoves(from, moveList, moveCount);
//                break;
//            case BISHOP:
//                moveCount = generateBishopMoves(from, moveList, moveCount);
//                break;
//            case ROOK:
//                moveCount = generateRookMoves(from, moveList, moveCount);
//                break;
//            case QUEEN:
//                moveCount = generateQueenMoves(from, moveList, moveCount);
//                break;
//            case KING:
//                moveCount = generateKingMoves(from, moveList, moveCount);
//                break;
//            default:
//                break;
//        }
//        return moveCount;
//    }
//
//    private int generatePawnMoves(int from, Move[] moveList, int moveCount) {
//        Piece piece = board10x12.get(from);
//        int toRank = from / 8;
//        int toFile = from % 8;
//        int direction = piece.isWhite() ? 1 : -1;
//        int promotionRank = piece.isWhite() ? 7 : 0;
//
//        // Forward move
//        int forwardSquare = from + 8 * direction;
//        if (forwardSquare >= 0 && forwardSquare < 64 && board10x12.get(forwardSquare).isEmpty()) {
//            if (toRank + direction == promotionRank) {
//                // Promotion moves
//                for (Move.Flag promFlag : new Move.Flag[]{
//                        Move.Flag.KNIGHT_PROMOTION_FLAG,
//                        Move.Flag.BISHOP_PROMOTION_FLAG,
//                        Move.Flag.ROOK_PROMOTION_FLAG,
//                        Move.Flag.QUEEN_PROMOTION_FLAG
//                }) {
//                    Move move = new Move(from, forwardSquare, promFlag, enPassantSquare10x12, castlingRights, halfmoveClock, null);
//                    if (isMoveValid(move)) {
////                        moveList[moveCount++].copyFrom(move);
//                    }
//                }
//            } else {
//                // Regular forward move
//                Move move = new Move(from, forwardSquare, Move.Flag.QUIET_MOVE_FLAG, enPassantSquare10x12, castlingRights, halfmoveClock, null);
//                if (isMoveValid(move)) {
////                    moveList[moveCount++].copyFrom(move);
//                }
//            }
//
//            // Double pawn push
//            int startRank = piece.isWhite() ? 1 : 6;
//            if (toRank == startRank) {
//                int doubleSquare = from + 16 * direction;
//                if (board10x12.get(doubleSquare).isEmpty()) {
//                    Move move = new Move(from, doubleSquare, Move.Flag.DOUBLE_PAWN_PUSH_FLAG, enPassantSquare10x12, castlingRights, halfmoveClock, null);
//                    if (isMoveValid(move)) {
////                        moveList[moveCount++].copyFrom(move);
//                    }
//                }
//            }
//        }
//
//        // Diagonal captures
//        for (int fileDelta : new int[]{-1, 1}) {
//            int captureSquare = from + 8 * direction + fileDelta;
//            if (captureSquare >= 0 && captureSquare < 64) {
//                int captureFile = captureSquare % 8;
//                int currentFile = from % 8;
//                if (Math.abs(captureFile - currentFile) == 1) {
//                    Piece target = board10x12.get(captureSquare);
//
//                    if (toRank + direction == promotionRank) {
//                        // Promotion captures
//                        for (Move.Flag promFlag : new Move.Flag[]{
//                                Move.Flag.KNIGHT_PROMOTION_CAPTURE_FLAG,
//                                Move.Flag.BISHOP_PROMOTION_CAPTURE_FLAG,
//                                Move.Flag.ROOK_PROMOTION_CAPTURE_FLAG,
//                                Move.Flag.QUEEN_PROMOTION_CAPTURE_FLAG
//                        }) {
//                            Move move = new Move(from, captureSquare, promFlag, enPassantSquare10x12, castlingRights, halfmoveClock, target);
//                            if (isMoveValid(move)) {
////                                moveList[moveCount++].copyFrom(move);
//                            }
//                        }
//                    } else if (!target.isEmpty() && target.getColor() != playerToMove) {
//                        // Regular capture
//                        Move move = new Move(from, captureSquare, Move.Flag.CAPTURES_FLAG, enPassantSquare10x12, castlingRights, halfmoveClock, target);
//                        if (isMoveValid(move)) {
////                            moveList[moveCount++].copyFrom(move);
//                        }
//                    } else if (enPassantSquare10x12 == captureSquare) {
//                        // En passant
//                        Move move = new Move(from, captureSquare, Move.Flag.EN_PASSANT_CAPTURE_FLAG, enPassantSquare10x12, castlingRights, halfmoveClock, target);
//                        if (isMoveValid(move)) {
////                            moveList[moveCount++].copyFrom(move);
//                        }
//                    }
//                }
//            }
//        }
//
//        return moveCount;
//    }
//
//    private int generateKnightMoves(int from, Move[] moveList, int moveCount) {
//        int[][] knightOffsets = {
//                {-2, -1}, {-2, 1}, {-1, -2}, {-1, 2},
//                {1, -2}, {1, 2}, {2, -1}, {2, 1}
//        };
//
//        for (int[] offset : knightOffsets) {
//            int to = from + offset[0] * 8 + offset[1];
//            if (to >= 0 && to < 64) {
//                int fromFile = from % 8;
//                int toFile = to % 8;
//                // Check boundary wrapping
//                if (Math.abs(toFile - fromFile) <= 2) {
//                    Piece target = board10x12.get(to);
//                    if (target.isEmpty()) {
////                        Move move = new Move(from, to, Move.Flag.QUIET_MOVE_FLAG);
////                        if (isMoveValid(move)) {
////                            moveList[moveCount++].copyFrom(move);
////                        }
//                    } else if (target.getColor() != playerToMove) {
////                        Move move = new Move(from, to, Move.Flag.CAPTURES_FLAG);
////                        if (isMoveValid(move)) {
////                            moveList[moveCount++].copyFrom(move);
////                        }
//                    }
//                }
//            }
//        }
//
//        return moveCount;
//    }
//
//    private int generateBishopMoves(int from, Move[] moveList, int moveCount) {
//        return generateSlidingMoves(from, new int[][]{{-1, -1}, {-1, 1}, {1, -1}, {1, 1}}, moveList, moveCount);
//    }
//
//    private int generateRookMoves(int from, Move[] moveList, int moveCount) {
//        return generateSlidingMoves(from, new int[][]{{-1, 0}, {1, 0}, {0, -1}, {0, 1}}, moveList, moveCount);
//    }
//
//    private int generateQueenMoves(int from, Move[] moveList, int moveCount) {
//        int[][] directions = {{-1, -1}, {-1, 0}, {-1, 1}, {0, -1}, {0, 1}, {1, -1}, {1, 0}, {1, 1}};
//        return generateSlidingMoves(from, directions, moveList, moveCount);
//    }
//
//    private int generateSlidingMoves(int from, int[][] directions, Move[] moveList, int moveCount) {
//        int fromRank = from / 8;
//        int fromFile = from % 8;
//
//        for (int[] direction : directions) {
//            int rank = fromRank + direction[0];
//            int file = fromFile + direction[1];
//
//            while (rank >= 0 && rank < 8 && file >= 0 && file < 8) {
//                int to = rank * 8 + file;
//                Piece target = board10x12.get(to);
//
//                if (target.isEmpty()) {
////                    Move move = new Move(from, to, Move.Flag.QUIET_MOVE_FLAG);
////                    if (isMoveValid(move)) {
////                        moveList[moveCount++].copyFrom(move);
////                    }
//                } else if (target.getColor() != playerToMove) {
////                    Move move = new Move(from, to, Move.Flag.CAPTURES_FLAG);
////                    if (isMoveValid(move)) {
////                        moveList[moveCount++].copyFrom(move);
////                    }
//                    break; // Stop sliding after capture
//                } else {
//                    break; // Stop sliding at own piece
//                }
//
//                rank += direction[0];
//                file += direction[1];
//            }
//        }
//
//        return moveCount;
//    }
//
//    private int generateKingMoves(int from, Move[] moveList, int moveCount) {
//        int[][] kingOffsets = {
//                {-1, -1}, {-1, 0}, {-1, 1},
//                {0, -1}, {0, 1},
//                {1, -1}, {1, 0}, {1, 1}
//        };
//
//        // Regular king moves
//        for (int[] offset : kingOffsets) {
//            int to = from + offset[0] * 8 + offset[1];
//            if (to >= 0 && to < 64) {
//                int fromFile = from % 8;
//                int toFile = to % 8;
//                // Check boundary wrapping
//                if (Math.abs(toFile - fromFile) <= 1) {
//                    Piece target = board10x12.get(to);
//                    if (target.isEmpty()) {
////                        Move move = new Move(from, to, Move.Flag.QUIET_MOVE_FLAG);
////                        if (isMoveValid(move)) {
////                            moveList[moveCount++].copyFrom(move);
////                        }
//                    } else if (target.getColor() != playerToMove) {
////                        Move move = new Move(from, to, Move.Flag.CAPTURES_FLAG);
////                        if (isMoveValid(move)) {
////                            moveList[moveCount++].copyFrom(move);
////                        }
//                    }
//                }
//            }
//        }
//
//        // Castling moves
//        if (playerToMove.isWhite()) {
//            // Kingside castling - represent move as king from e1 (4) to rook's original square h1 (7)
//            if (from == 4) {
////                Move move = new Move(from, 7, Move.Flag.KING_CASTLE_FLAG);
////                if (isMoveValid(move)) {
////                    moveList[moveCount++].copyFrom(move);
////                }
//            }
//            // Queenside castling - represent move as king from e1 (4) to rook's original square a1 (0)
//            if (from == 4) {
////                Move move = new Move(from, 0, Move.Flag.QUEEN_CASTLE_FLAG);
////                if (isMoveValid(move)) {
////                    moveList[moveCount++].copyFrom(move);
////                }
//            }
//        } else {
//            // Black kingside castling - king from e8 (60) to rook's original square h8 (63)
//            if (from == 60) {
////                Move move = new Move(from, 63, Move.Flag.KING_CASTLE_FLAG);
////                if (isMoveValid(move)) {
////                    moveList[moveCount++].copyFrom(move);
////                }
//            }
//            // Black queenside castling - king from e8 (60) to rook's original square a8 (56)
//            if (from == 60) {
////                Move move = new Move(from, 56, Move.Flag.QUEEN_CASTLE_FLAG);
////                if (isMoveValid(move)) {
////                    moveList[moveCount++].copyFrom(move);
////                }
//            }
//        }
//
//        return moveCount;
//    }
//
//    public long perft(int depth) {
//        Move[] moveList = new Move[256];
//        int numMoves;
//        long nodes = 0;
//
//        if (depth == 0) {
//            return 1L;
//        }
//
//        numMoves = generateLegalMoves(moveList);
//        for (int i = 0; i < numMoves; i++) {
//            makeMove(moveList[i]);
//            nodes += perft(depth - 1);
//            undoMove(moveList[i]);
//        }
//
//        return nodes;
//    }
//
//    public long fastPerft(int depth) {
//        Move[] moveList = new Move[256];
//        long nodes = 0;
//
//        int numMoves = generateLegalMoves(moveList);
//
//        if (depth == 0) {
//            return 1L;
//        }
//
//        if (depth == 1) {
//            return numMoves;
//        }
//
//        for (int i = 0; i < numMoves; i++) {
//            makeMove(moveList[i]);
//            nodes += fastPerft(depth - 1);
//            undoMove(moveList[i]);
//        }
//
//        return nodes;
//    }
//
//    @Override
//    public String toString() {
//        StringBuilder sb = new StringBuilder();
//
//        sb.append("FEN:\n").append(toFen()).append("\n\n");
//        sb.append(get10x12BoardString()).append("\n");
//        sb.append(get8x8BoardString()).append("\n");
//
//        for (int color = 0; color < 2; color++) {
//            for (int piece = 0; piece < 7; piece++) {
//                sb.append(bitboardString(color, piece));
//
//                if (piece != 6) {
//                    sb.append("\n\n");
//                }
//            }
//
//            if (color != 1) {
//                sb.append("\n\n");
//            }
//        }
//
//        return sb.toString();
//    }
//
//    public String get10x12BoardString() {
//        StringBuilder sb = new StringBuilder();
//
//        sb.append("10x12 Board:\n");
//        sb.append("    X   A   B   C   D   E   F   G   H   X\n");
//        sb.append("  +---+---+---+---+---+---+---+---+---+---+\n");
//        for (int y = 11; y >= 0; y--) {
//            if (y > 9 || y < 2) {
//                sb.append("X | ");
//            } else {
//                sb.append(y - 1).append(" | ");
//            }
//
//            for (int x = 0; x < 10; x++) {
//                sb.append(board10x12.get(y * 10 + x)).append(" | ");
//            }
//
//            if (y > 9 || y < 2) {
//                sb.append("X").append("\n");
//            } else {
//                sb.append(y - 1).append("\n");
//            }
//
//            sb.append("  +---+---+---+---+---+---+---+---+---+---+").append("\n");
//        }
//        sb.append("    X   A   B   C   D   E   F   G   H   X\n");
//
//        return sb.toString();
//    }
//
//    public String get8x8BoardString() {
//        StringBuilder sb = new StringBuilder();
//
//        sb.append("8x8 Board:\n");
//        sb.append("    A   B   C   D   E   F   G   H\n");
//        sb.append("  +---+---+---+---+---+---+---+---+\n");
//        for (int y = 7; y >= 0; y--) {
//            sb.append(y + 1).append(" | ");
//
//            for (int x = 0; x < 8; x++) {
//                sb.append(board10x12.get(10 * (y + 2) + (x + 1))).append(" | ");
//            }
//
//            sb.append(y + 1).append("\n");
//
//            sb.append("  +---+---+---+---+---+---+---+---+").append("\n");
//        }
//        sb.append("    A   B   C   D   E   F   G   H\n");
//
//        return sb.toString();
//    }
//
//    public String bitboardString(int color, int piece) {
//        return bitboardString(Piece.Color.values()[color], Piece.PieceType.values()[piece]);
//    }
//
//    public String bitboardString(Piece.Color color, Piece.PieceType piece) {
//        StringBuilder sb = new StringBuilder();
//
//        sb.append(String.format("%s %s bitboard:\n", color.toString().toLowerCase(), piece.toString().toLowerCase()));
//
//        long bb = bitBoards[color.ordinal()][piece.ordinal()];
//        for (int y = 7; y >= 0; y--) {
//            long select = 0b11111111L << (y * 8);
//            long row = (bb & select) >>> (y * 8);
//
//            String corrected = new StringBuilder(String.format("%8s", Long.toBinaryString(row)).replace(' ', '0')).reverse().toString();
//
//            sb.append(corrected);
//
//            if (y != 0) {
//                sb.append("\n");
//            }
//        }
//
//        return sb.toString();
//    }
//
//    public String ascii8x8() {
//        StringBuilder sb = new StringBuilder();
//
//        sb.append("    A   B   C   D   E   F   G   H\n");
//        sb.append("  +---+---+---+---+---+---+---+---+\n");
//        for (int y = 7; y >= 0; y--) {
//            sb.append(y + 1).append(" | ");
//
//            for (int x = 0; x < 8; x++) {
//                sb.append(board10x12.get(10 * (y + 2) + (x + 1))).append(" | ");
//            }
//
//            sb.append(y + 1).append("\n");
//
//            sb.append("  +---+---+---+---+---+---+---+---+").append("\n");
//        }
//        sb.append("    A   B   C   D   E   F   G   H");
//
//        return sb.toString();
//    }
}
