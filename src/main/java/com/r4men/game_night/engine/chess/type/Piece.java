package com.r4men.game_night.engine.chess.type;

public class Piece {
    public static final Piece EMPTY = new Piece();
    public static final Piece OFF_BOARD = new Piece(PieceType.OFF_BOARD, Color.OFF_BOARD, 'X');

    public static final Piece WHITE_BISHOP = new Piece(PieceType.BISHOP, Color.WHITE, 'B');
    public static final Piece WHITE_KING = new Piece(PieceType.KING, Color.WHITE, 'K');
    public static final Piece WHITE_KNIGHT = new Piece(PieceType.KNIGHT, Color.WHITE, 'N');
    public static final Piece WHITE_PAWN = new Piece(PieceType.PAWN, Color.WHITE, 'P');
    public static final Piece WHITE_QUEEN = new Piece(PieceType.QUEEN, Color.WHITE, 'Q');
    public static final Piece WHITE_ROOK = new Piece(PieceType.ROOK, Color.WHITE, 'R');

    public static final Piece BLACK_BISHOP = new Piece(PieceType.BISHOP, Color.BLACK, 'b');
    public static final Piece BLACK_KING = new Piece(PieceType.KING, Color.BLACK, 'k');
    public static final Piece BLACK_KNIGHT = new Piece(PieceType.KNIGHT, Color.BLACK, 'n');
    public static final Piece BLACK_PAWN = new Piece(PieceType.PAWN, Color.BLACK, 'p');
    public static final Piece BLACK_QUEEN = new Piece(PieceType.QUEEN, Color.BLACK, 'q');
    public static final Piece BLACK_ROOK = new Piece(PieceType.ROOK, Color.BLACK, 'r');

    private final PieceType pieceType;
    private final Color color;
    private final char pieceChar;

    private Piece(PieceType pieceType, Color color, char pieceChar) {
        this.pieceType = pieceType;
        this.color = color;
        this.pieceChar = pieceChar;
    }

    private Piece() {
        this.pieceType = PieceType.EMPTY;
        this.color = Color.EMPTY;
        this.pieceChar = ' ';
    }

    public static Piece fromChar(char c) {
        return switch (c) {
            case 'B' -> WHITE_BISHOP;
            case 'K' -> WHITE_KING;
            case 'N' -> WHITE_KNIGHT;
            case 'P' -> WHITE_PAWN;
            case 'R' -> WHITE_ROOK;
            case 'Q' -> WHITE_QUEEN;
            case 'b' -> BLACK_BISHOP;
            case 'k' -> BLACK_KING;
            case 'n' -> BLACK_KNIGHT;
            case 'p' -> BLACK_PAWN;
            case 'r' -> BLACK_ROOK;
            case 'q' -> BLACK_QUEEN;
            case ' ' -> EMPTY;
            default -> throw new IllegalArgumentException("Illegal piece character " + c);
        };
    }

    @Override
    public String toString() {
        return String.valueOf(this.pieceChar);
    }

    public PieceType getPieceType() {
        return pieceType;
    }

    public Color getColor() {
        return color;
    }

    public char getPieceChar() {
        return pieceChar;
    }

    public boolean isEmpty() {
        return pieceType == PieceType.EMPTY;
    }

    public boolean isOffBoard() {
        return pieceType == PieceType.OFF_BOARD;
    }

    public boolean matchesColor(Color color) {
        return this.color == color;
    }

    public boolean isWhite() {
        return color == Color.WHITE;
    }

    public boolean isBlack() {
        return color == Color.BLACK;
    }

    public boolean isPawn() {
        return pieceType == PieceType.PAWN;
    }

    public boolean isKing() {
        return pieceType == PieceType.KING;
    }

    public boolean isKnight() {
        return pieceType == PieceType.KNIGHT;
    }

    public boolean isBishop() {
        return pieceType == PieceType.BISHOP;
    }

    public boolean isQueen() {
        return pieceType == PieceType.QUEEN;
    }

    public boolean isRook() {
        return pieceType == PieceType.ROOK;
    }

    public enum PieceType {
        BISHOP,
        KING,
        KNIGHT,
        PAWN,
        QUEEN,
        ROOK,
        OCC,
        EMPTY,
        OFF_BOARD
    }

    public enum Color {
        WHITE,
        BLACK,
        EMPTY,
        OFF_BOARD;

        public boolean isWhite() {
            return this == WHITE;
        }

        public boolean isBlack() {
            return this == BLACK;
        }

        public Color opposite() {
            return switch (this) {
                case WHITE -> BLACK;
                case BLACK -> WHITE;
                default -> throw new IllegalStateException("No opposite color for " + this);
            };
        }
    }
}
