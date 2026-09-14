package com.r4men.game_knight.engine.chess.helper;

import com.r4men.game_knight.engine.chess.Board;
import com.r4men.game_knight.engine.chess.type.Piece;

public final class FenCodec {
    public static void load(Board board, String fen) {
        boolean isValid = fen.matches("^\\s*((?:[rnbqkpRNBQKP1-8]{1,8}/){7}[rnbqkpRNBQKP1-8]{1,8})\\s[wWbB]\\s(-|[KQkq]{1,4})\\s(-|[a-h][36])\\s(\\d+)\\s(\\d+)\\s*");

        if (isValid) {
            String[] fenParts = fen.split(" ");
            String[] fenPositionRows = fenParts[0].split("/");
            String playerToMove = fenParts[1];
            String castlingRights = fenParts[2];
            String enPassantSquare = fenParts[3];
            board.setHalfmoveClock(Integer.parseInt(fenParts[4]));
            board.setFullmoveNumber(Integer.parseInt(fenParts[5]));

            StringBuilder full = new StringBuilder();
            for (String fenRow : fenPositionRows) {
                StringBuilder row = new StringBuilder();
                for (char c : fenRow.toCharArray()) {
                    if (Character.isDigit(c)) {
                        int emptySquares = Character.getNumericValue(c);
                        row.repeat(' ', emptySquares);
                    } else {
                        row.append(c);
                    }
                }
                if (row.length() != 8) {
                    throw new IllegalArgumentException("Invalid fen string: " + fenRow);
                }

                full.append(row);
            }

            int i = 0;
            for (int y = 7; y >= 0; y--) {
                for (int x = 0; x < 8; x++) {
                    char c = full.charAt(i);

                    board.setPieceAt10x12(21 + (10 * y) + x, Piece.fromChar(c));

                    i++;
                }
            }

            board.setPlayerToMove(switch (playerToMove.toLowerCase()) {
                case "w" -> Piece.Color.WHITE;
                case "b" -> Piece.Color.BLACK;
                default -> throw new IllegalArgumentException("Invalid active color: " + playerToMove);
            });

            board.setCastlingRights(0b0000);
            for (char c : castlingRights.toCharArray()) {
                switch (c) {
                    case 'K' -> board.orCastlingRights(0b0001);
                    case 'Q' -> board.orCastlingRights(0b0010);
                    case 'k' -> board.orCastlingRights(0b0100);
                    case 'q' -> board.orCastlingRights(0b1000);
                    default -> {
                    }
                }
            }

            if (enPassantSquare.equals("-")) {
                board.setEnPassantSquare10x12(-1);
            } else {
                board.setEnPassantSquare10x12(Util.convertSquareTo10x12(enPassantSquare));
            }

            boolean hasWhiteKing = Util.hasOnlyOnePiece(board.getBitBoard(Piece.Color.WHITE, Piece.PieceType.KING));
            boolean hasBlackKing = Util.hasOnlyOnePiece(board.getBitBoard(Piece.Color.BLACK, Piece.PieceType.KING));

            if (!(hasWhiteKing && hasBlackKing)) {
                throw new IllegalArgumentException("Invalid FEN string: " + fen);
            }
        } else {
            throw new IllegalArgumentException("Invalid FEN string: " + fen);
        }
    }

    public static String toFen(Board board) {
        StringBuilder sb = new StringBuilder();

        for (int y = 7; y >= 0; y--) {
            int consecutiveSpaces = 0;
            for (int x = 0; x < 8; x++) {
                int s10x12 = 10 * (y + 2) + (x + 1);

                Piece piece = board.getPieceAt10x12(s10x12);

                if (piece.isEmpty()) {
                    consecutiveSpaces++;

                    if (x == 7) {
                        sb.append(consecutiveSpaces);
                    }
                } else {
                    if (consecutiveSpaces > 0) {
                        sb.append(consecutiveSpaces);
                    }
                    consecutiveSpaces = 0;
                    sb.append(piece.getPieceChar());
                }
            }

            if (y != 0) {
                sb.append('/');
            } else {
                sb.append(' ');
            }
        }

        if (board.getPlayerToMove().isWhite()) {
            sb.append("w ");
        } else {
            sb.append("b ");
        }

        StringBuilder castlingRights = new StringBuilder();
        for (int i = 0; i < 4; i++) {
            if (((board.getCastlingRights() >> i) & 0b1) == 1) {
                castlingRights.append(switch (i) {
                    case 0 -> 'K';
                    case 1 -> 'Q';
                    case 2 -> 'k';
                    case 3 -> 'q';
                    default -> "";
                });
            }
        }

        if (!castlingRights.isEmpty()) {
            sb.append(castlingRights).append(' ');
        } else {
            sb.append("- ");
        }

        if (board.getEnPassantSquare10x12() == -1) {
            sb.append("- ");
        } else {
            int s8x8 = Util.convert10x12to8x8(board.getEnPassantSquare10x12());
            int file = s8x8 % 8;
            int rank = s8x8 / 8 + 1;

            sb.append(switch (file) {
                case 0 -> "a" + rank;
                case 1 -> "b" + rank;
                case 2 -> "c" + rank;
                case 3 -> "d" + rank;
                case 4 -> "e" + rank;
                case 5 -> "f" + rank;
                case 6 -> "g" + rank;
                case 7 -> "h" + rank;
                default ->
                        throw new IllegalStateException("Invalid en passant square: " + board.getEnPassantSquare10x12());
            });

            sb.append(' ');
        }

        sb.append(board.getHalfmoveClock()).append(' ');
        sb.append(board.getFullmoveNumber());

        return sb.toString();
    }

    public static String toSimpleFen(Board board) {
        String pieceLayout = board.toFen().split(" ")[0];
        StringBuilder simpleFen = new StringBuilder();

        for (char c : pieceLayout.toCharArray()) {
            if (Character.isDigit(c)) {
                int i = Character.getNumericValue(c);
                simpleFen.repeat(" ", i);
            } else {
                simpleFen.append(c);
            }
        }

        return simpleFen.toString();
    }
}
