package games.ramen.game_knight.engine.chess;

import games.ramen.game_knight.engine.chess.helper.*;
import games.ramen.game_knight.engine.chess.type.Move;
import games.ramen.game_knight.engine.chess.type.Piece;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
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

    public Piece getPieceAt8x8(int s8x8) {
        return board10x12.get(Util.convert8x8to10x12(s8x8));
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

    public void makeMove(int from8x8, int to8x8, @Nullable Piece.PieceType promotionPiece) {
        Move move = MoveParser.fromInts(this, from8x8, to8x8, promotionPiece);
        MoveApplier.makeMove(this, move, false);
    }

    public void makeMove(Move move) {
        MoveApplier.makeMove(this, move, false);
    }

    public void undoMove(Move move) {
        MoveApplier.undoMove(this, move);
    }

    public int generateLegalMoves() {
        return MoveGenerator.generateLegalMoves(new ArrayList<>(256), this);
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
}
