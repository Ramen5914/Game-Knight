package com.r4men.game_night.engine.chess.type;

import com.r4men.game_night.engine.chess.helper.Util;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.network.codec.ByteBufCodecs;
import net.minecraft.network.codec.StreamCodec;
import net.neoforged.neoforge.network.codec.NeoForgeStreamCodecs;
import org.jetbrains.annotations.NotNull;

public record Move(int move, int enPassantSquare10x12, int castlingRights, int halfmoveClock,
                   @NotNull Piece capturedPiece) {
    public static final StreamCodec<FriendlyByteBuf, Move> STREAM_CODEC = StreamCodec.composite(
            ByteBufCodecs.INT, Move::move,
            ByteBufCodecs.INT, Move::enPassantSquare10x12,
            ByteBufCodecs.INT, Move::castlingRights,
            ByteBufCodecs.INT, Move::halfmoveClock,
            NeoForgeStreamCodecs.enumCodec(Piece.class), Move::capturedPiece,
            Move::new
    );

    public Move(int from8x8, int to8x8, Flag flag, int enPassantSquare10x12, int castlingRights, int halfmoveClock, Piece capturedPiece) {
        if (from8x8 >= 64 || from8x8 < 0) {
            throw new IllegalArgumentException("'from8x8' square must be in the range [0, 64). Received: " + from8x8);
        }

        if (to8x8 >= 64 || to8x8 < 0) {
            throw new IllegalArgumentException("'to8x8' square must be in the range [0, 64). Received: " + to8x8);
        }

        if (!((enPassantSquare10x12 >= 0x29 && enPassantSquare10x12 <= 0x30) || (enPassantSquare10x12 >= 0x47 && enPassantSquare10x12 <= 0x4E) || enPassantSquare10x12 == -1)) {
            throw new IllegalArgumentException("'enPassantSquare10x12' must be in [41,48] or [71,78], or -1. Received: " + enPassantSquare10x12);
        }

        this(
                ((flag.toInt() & 0b1111) << 12) | ((from8x8 & 0b111111) << 6) | (to8x8 & 0b111111),
                enPassantSquare10x12,
                castlingRights,
                halfmoveClock,
                capturedPiece != null ? capturedPiece : Piece.EMPTY
        );
    }

    @Override
    public @NotNull String toString() {
        return String.format(
                "0x%s -> 0x%s (%s)",
                String.format("%2s", Integer.toHexString(getFrom10x12()).replace(' ', '0')),
                String.format("%2s", Integer.toHexString(getTo10x12()).replace(' ', '0')),
                Flag.fromInt(getFlags()));
    }

    public int getFrom8x8() {
        return (move >> 6) & 0x3f;
    }

    public int getTo8x8() {
        return move & 0x3f;
    }

    public int getFrom10x12() {
        return Util.convert8x8to10x12(getFrom8x8());
    }

    public int getTo10x12() {
        return Util.convert8x8to10x12(getTo8x8());

    }

    public int getFlags() {
        return (move >> 12) & 0x0f;
    }

    public boolean isQuiet() {
        return move >> 12 == Flag.QUIET_MOVE_FLAG.toInt();
    }

    public boolean isDoublePawnPush() {
        return move >> 12 == Flag.DOUBLE_PAWN_PUSH_FLAG.toInt();
    }

    public boolean isCastle() {
        return ((move >> 12) & 0b1110) == 0b0010;
    }

    public boolean isKingCastle() {
        return move >> 12 == Flag.KING_CASTLE_FLAG.toInt();
    }

    public boolean isQueenCastle() {
        return move >> 12 == Flag.QUEEN_CASTLE_FLAG.toInt();
    }

    public boolean isCapture() {
        return ((move >> 12) & Flag.CAPTURES_FLAG.toInt()) == Flag.CAPTURES_FLAG.toInt();
    }

    public boolean isEnPassantCapture() {
        return move >> 12 == Flag.EN_PASSANT_CAPTURE_FLAG.toInt();
    }

    public boolean isPromotion() {
        return ((move >> 12) & 0b1000) == 0b1000;
    }

    public boolean isKnightPromotion() {
        return ((move >> 12) & (Flag.KNIGHT_PROMOTION_FLAG.toInt() | 0b1011)) == Flag.KNIGHT_PROMOTION_FLAG.toInt();
    }

    public boolean isBishopPromotion() {
        return ((move >> 12) & (Flag.BISHOP_PROMOTION_FLAG.toInt() | 0b1011)) == Flag.BISHOP_PROMOTION_FLAG.toInt();
    }

    public boolean isRookPromotion() {
        return ((move >> 12) & (Flag.ROOK_PROMOTION_FLAG.toInt() | 0b1011)) == Flag.ROOK_PROMOTION_FLAG.toInt();
    }

    public boolean isQueenPromotion() {
        return ((move >> 12) & (Flag.QUEEN_PROMOTION_FLAG.toInt() | 0b1011)) == Flag.QUEEN_PROMOTION_FLAG.toInt();
    }

    public boolean isKnightPromotionCapture() {
        return move >> 12 == Flag.KNIGHT_PROMOTION_CAPTURE_FLAG.toInt();
    }

    public boolean isBishopPromotionCapture() {
        return move >> 12 == Flag.BISHOP_PROMOTION_CAPTURE_FLAG.toInt();
    }

    public boolean isRookPromotionCapture() {
        return move >> 12 == Flag.ROOK_PROMOTION_CAPTURE_FLAG.toInt();
    }

    public boolean isQueenPromotionCapture() {
        return move >> 12 == Flag.QUEEN_PROMOTION_CAPTURE_FLAG.toInt();
    }

    public int getButterflyIndex() {
        return move & 0xffff;
    }

    @Override
    public boolean equals(Object obj) {
        if (this == obj) return true;
        if (obj == null || getClass() != obj.getClass()) return false;

        Move a = (Move) obj;

        return (move & 0xffff) == (a.move & 0xffff) && capturedPiece == a.capturedPiece && enPassantSquare10x12 == a.enPassantSquare10x12 && castlingRights == a.castlingRights && halfmoveClock == a.halfmoveClock;
    }

    @Override
    public int hashCode() {
        int result = Integer.hashCode(move & 0xffff);
        result = 31 * result + Integer.hashCode(enPassantSquare10x12);
        result = 31 * result + Integer.hashCode(castlingRights);
        result = 31 * result + Integer.hashCode(halfmoveClock);
        result = 31 * result + capturedPiece.hashCode();
        return result;
    }

    public enum Flag {
        QUIET_MOVE_FLAG(0b0000),
        DOUBLE_PAWN_PUSH_FLAG(0b0001),
        KING_CASTLE_FLAG(0b0010),
        QUEEN_CASTLE_FLAG(0b0011),
        CAPTURES_FLAG(0b0100),
        EN_PASSANT_CAPTURE_FLAG(0b0101),
        KNIGHT_PROMOTION_FLAG(0b1000),
        BISHOP_PROMOTION_FLAG(0b1001),
        ROOK_PROMOTION_FLAG(0b1010),
        QUEEN_PROMOTION_FLAG(0b1011),
        KNIGHT_PROMOTION_CAPTURE_FLAG(0b1100),
        BISHOP_PROMOTION_CAPTURE_FLAG(0b1101),
        ROOK_PROMOTION_CAPTURE_FLAG(0b1110),
        QUEEN_PROMOTION_CAPTURE_FLAG(0b1111);

        private final int flag;

        Flag(int flag) {
            this.flag = flag;
        }

        public static Flag fromInt(int flag) {
            if (flag >= 0 && flag <= 15) {
                for (Flag f : Flag.values()) {
                    if (f.flag == flag) {
                        return f;
                    }
                }
            }

            throw new IllegalArgumentException("Invalid flag value: " + flag);
        }

        public int toInt() {
            return flag;
        }
    }
}
