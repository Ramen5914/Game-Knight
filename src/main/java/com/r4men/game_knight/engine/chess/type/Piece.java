package com.r4men.game_knight.engine.chess.type;

public enum Piece {
    EMPTY(PieceType.EMPTY, Color.EMPTY, ' '),
    OFF_BOARD(PieceType.OFF_BOARD, Color.OFF_BOARD, 'X'),
    WHITE_BISHOP(PieceType.BISHOP, Color.WHITE, 'B'),
    WHITE_KING(PieceType.KING, Color.WHITE, 'K'),
    WHITE_KNIGHT(PieceType.KNIGHT, Color.WHITE, 'N'),
    WHITE_PAWN(PieceType.PAWN, Color.WHITE, 'P'),
    WHITE_QUEEN(PieceType.QUEEN, Color.WHITE, 'Q'),
    WHITE_ROOK(PieceType.ROOK, Color.WHITE, 'R'),

    BLACK_BISHOP(PieceType.BISHOP, Color.BLACK, 'b'),
    BLACK_KING(PieceType.KING, Color.BLACK, 'k'),
    BLACK_KNIGHT(PieceType.KNIGHT, Color.BLACK, 'n'),
    BLACK_PAWN(PieceType.PAWN, Color.BLACK, 'p'),
    BLACK_QUEEN(PieceType.QUEEN, Color.BLACK, 'q'),
    BLACK_ROOK(PieceType.ROOK, Color.BLACK, 'r');

    private final PieceType pieceType;
    private final Color color;
    private final char pieceChar;

    Piece(PieceType pieceType, Color color, char pieceChar) {
        this.pieceType = pieceType;
        this.color = color;
        this.pieceChar = pieceChar;
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
            case 'X' -> OFF_BOARD;
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
