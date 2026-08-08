package com.r4men.game_night.client.renderer;

import com.mojang.blaze3d.vertex.PoseStack;
import com.r4men.game_night.GameNight;
import com.r4men.game_night.block.entity.ChessBlockEntity;
import com.r4men.game_night.client.renderer.ChessBlockEntityRenderer.ChessBlockEntityRenderState;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.SubmitNodeCollector;
import net.minecraft.client.renderer.blockentity.BlockEntityRenderer;
import net.minecraft.client.renderer.blockentity.BlockEntityRendererProvider;
import net.minecraft.client.renderer.blockentity.state.BlockEntityRenderState;
import net.minecraft.client.renderer.feature.ModelFeatureRenderer;
import net.minecraft.client.renderer.item.ItemModelResolver;
import net.minecraft.client.renderer.item.ItemStackRenderState;
import net.minecraft.client.renderer.state.level.CameraRenderState;
import net.minecraft.client.renderer.texture.OverlayTexture;
import net.minecraft.core.Direction;
import net.minecraft.resources.Identifier;
import net.minecraft.world.item.ItemDisplayContext;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;
import net.minecraft.world.phys.Vec3;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import org.joml.Matrix4f;
import org.joml.Quaternionf;

import java.util.function.Supplier;

public record ChessBlockEntityRenderer(
        ItemModelResolver resolver,
        Supplier<ItemStack> blackBishop,
        Supplier<ItemStack> blackKing,
        Supplier<ItemStack> blackKnight,
        Supplier<ItemStack> blackPawn,
        Supplier<ItemStack> blackQueen,
        Supplier<ItemStack> blackRook,
        Supplier<ItemStack> whiteBishop,
        Supplier<ItemStack> whiteKing,
        Supplier<ItemStack> whiteKnight,
        Supplier<ItemStack> whitePawn,
        Supplier<ItemStack> whiteQueen,
        Supplier<ItemStack> whiteRook,
        Supplier<ItemStack> blackBishopCuboid,
        Supplier<ItemStack> blackKingCuboid,
        Supplier<ItemStack> blackKnightCuboid,
        Supplier<ItemStack> blackPawnCuboid,
        Supplier<ItemStack> blackQueenCuboid,
        Supplier<ItemStack> blackRookCuboid,
        Supplier<ItemStack> whiteBishopCuboid,
        Supplier<ItemStack> whiteKingCuboid,
        Supplier<ItemStack> whiteKnightCuboid,
        Supplier<ItemStack> whitePawnCuboid,
        Supplier<ItemStack> whiteQueenCuboid,
        Supplier<ItemStack> whiteRookCuboid
) implements BlockEntityRenderer<ChessBlockEntity, ChessBlockEntityRenderState> {
    private static final float boardSize = 14f;
    private static final float boardThickness = 1f;
    private static final float squareCount = 8f;

    private static final float cornerOffset = ((16f - boardSize) / 2f) / 16f;
    private static final float squareSize = boardSize / 16f / squareCount;

    public static ChessBlockEntityRenderer create(BlockEntityRendererProvider.Context context) {
        Identifier blackBishopModel = GameNight.getIdentifier("chess/black_bishop");
        Identifier blackKingModel = GameNight.getIdentifier("chess/black_king");
        Identifier blackKnightModel = GameNight.getIdentifier("chess/black_knight");
        Identifier blackPawnModel = GameNight.getIdentifier("chess/black_pawn");
        Identifier blackQueenModel = GameNight.getIdentifier("chess/black_queen");
        Identifier blackRookModel = GameNight.getIdentifier("chess/black_rook");
        Identifier whiteBishopModel = GameNight.getIdentifier("chess/white_bishop");
        Identifier whiteKingModel = GameNight.getIdentifier("chess/white_king");
        Identifier whiteKnightModel = GameNight.getIdentifier("chess/white_knight");
        Identifier whitePawnModel = GameNight.getIdentifier("chess/white_pawn");
        Identifier whiteQueenModel = GameNight.getIdentifier("chess/white_queen");
        Identifier whiteRookModel = GameNight.getIdentifier("chess/white_rook");

        Identifier blackBishopCuboidModel = GameNight.getIdentifier("chess/black_bishop_cuboid");
        Identifier blackKingCuboidModel = GameNight.getIdentifier("chess/black_king_cuboid");
        Identifier blackKnightCuboidModel = GameNight.getIdentifier("chess/black_knight_cuboid");
        Identifier blackPawnCuboidModel = GameNight.getIdentifier("chess/black_pawn_cuboid");
        Identifier blackQueenCuboidModel = GameNight.getIdentifier("chess/black_queen_cuboid");
        Identifier blackRookCuboidModel = GameNight.getIdentifier("chess/black_rook_cuboid");
        Identifier whiteBishopCuboidModel = GameNight.getIdentifier("chess/white_bishop_cuboid");
        Identifier whiteKingCuboidModel = GameNight.getIdentifier("chess/white_king_cuboid");
        Identifier whiteKnightCuboidModel = GameNight.getIdentifier("chess/white_knight_cuboid");
        Identifier whitePawnCuboidModel = GameNight.getIdentifier("chess/white_pawn_cuboid");
        Identifier whiteQueenCuboidModel = GameNight.getIdentifier("chess/white_queen_cuboid");
        Identifier whiteRookCuboidModel = GameNight.getIdentifier("chess/white_rook_cuboid");

        return new ChessBlockEntityRenderer(
                context.itemModelResolver(),
                RenderHelper.memoizeStackModel(blackBishopModel),
                RenderHelper.memoizeStackModel(blackKingModel),
                RenderHelper.memoizeStackModel(blackKnightModel),
                RenderHelper.memoizeStackModel(blackPawnModel),
                RenderHelper.memoizeStackModel(blackQueenModel),
                RenderHelper.memoizeStackModel(blackRookModel),
                RenderHelper.memoizeStackModel(whiteBishopModel),
                RenderHelper.memoizeStackModel(whiteKingModel),
                RenderHelper.memoizeStackModel(whiteKnightModel),
                RenderHelper.memoizeStackModel(whitePawnModel),
                RenderHelper.memoizeStackModel(whiteQueenModel),
                RenderHelper.memoizeStackModel(whiteRookModel),
                RenderHelper.memoizeStackModel(blackBishopCuboidModel),
                RenderHelper.memoizeStackModel(blackKingCuboidModel),
                RenderHelper.memoizeStackModel(blackKnightCuboidModel),
                RenderHelper.memoizeStackModel(blackPawnCuboidModel),
                RenderHelper.memoizeStackModel(blackQueenCuboidModel),
                RenderHelper.memoizeStackModel(blackRookCuboidModel),
                RenderHelper.memoizeStackModel(whiteBishopCuboidModel),
                RenderHelper.memoizeStackModel(whiteKingCuboidModel),
                RenderHelper.memoizeStackModel(whiteKnightCuboidModel),
                RenderHelper.memoizeStackModel(whitePawnCuboidModel),
                RenderHelper.memoizeStackModel(whiteQueenCuboidModel),
                RenderHelper.memoizeStackModel(whiteRookCuboidModel)
        );
    }

    @Override
    public @NotNull ChessBlockEntityRenderer.ChessBlockEntityRenderState createRenderState() {
        return new ChessBlockEntityRenderState();
    }

    @Override
    public void extractRenderState(@NotNull ChessBlockEntity be, @NotNull ChessBlockEntityRenderer.ChessBlockEntityRenderState state, float partialTicks, @NotNull Vec3 cameraPosition, ModelFeatureRenderer.@Nullable CrumblingOverlay breakProgress) {
        BlockEntityRenderer.super.extractRenderState(be, state, partialTicks, cameraPosition, breakProgress);

        Vec3 boardCenter = Vec3.atCenterOf(be.getBlockPos());
        double distanceSqr = cameraPosition.distanceToSqr(boardCenter);
        state.lod = selectLod(distanceSqr);

        Level level = Minecraft.getInstance().level;

        int renderSeed = (int) (be.getBlockPos().asLong());
        resolver.updateForTopItem(state.blackBishopItemState, this.blackBishop.get(), ItemDisplayContext.NONE, level, null, renderSeed);
        resolver.updateForTopItem(state.blackKingItemState, this.blackKing.get(), ItemDisplayContext.NONE, level, null, renderSeed);
        resolver.updateForTopItem(state.blackKnightItemState, this.blackKnight.get(), ItemDisplayContext.NONE, level, null, renderSeed);
        resolver.updateForTopItem(state.blackPawnItemState, this.blackPawn.get(), ItemDisplayContext.NONE, level, null, renderSeed);
        resolver.updateForTopItem(state.blackQueenItemState, this.blackQueen.get(), ItemDisplayContext.NONE, level, null, renderSeed);
        resolver.updateForTopItem(state.blackRookItemState, this.blackRook.get(), ItemDisplayContext.NONE, level, null, renderSeed);

        resolver.updateForTopItem(state.whiteBishopItemState, this.whiteBishop.get(), ItemDisplayContext.NONE, level, null, renderSeed);
        resolver.updateForTopItem(state.whiteKingItemState, this.whiteKing.get(), ItemDisplayContext.NONE, level, null, renderSeed);
        resolver.updateForTopItem(state.whiteKnightItemState, this.whiteKnight.get(), ItemDisplayContext.NONE, level, null, renderSeed);
        resolver.updateForTopItem(state.whitePawnItemState, this.whitePawn.get(), ItemDisplayContext.NONE, level, null, renderSeed);
        resolver.updateForTopItem(state.whiteQueenItemState, this.whiteQueen.get(), ItemDisplayContext.NONE, level, null, renderSeed);
        resolver.updateForTopItem(state.whiteRookItemState, this.whiteRook.get(), ItemDisplayContext.NONE, level, null, renderSeed);

        resolver.updateForTopItem(state.blackBishopCuboidItemState, this.blackBishopCuboid.get(), ItemDisplayContext.NONE, level, null, renderSeed);
        resolver.updateForTopItem(state.blackKingCuboidItemState, this.blackKingCuboid.get(), ItemDisplayContext.NONE, level, null, renderSeed);
        resolver.updateForTopItem(state.blackKnightCuboidItemState, this.blackKnightCuboid.get(), ItemDisplayContext.NONE, level, null, renderSeed);
        resolver.updateForTopItem(state.blackPawnCuboidItemState, this.blackPawnCuboid.get(), ItemDisplayContext.NONE, level, null, renderSeed);
        resolver.updateForTopItem(state.blackQueenCuboidItemState, this.blackQueenCuboid.get(), ItemDisplayContext.NONE, level, null, renderSeed);
        resolver.updateForTopItem(state.blackRookCuboidItemState, this.blackRookCuboid.get(), ItemDisplayContext.NONE, level, null, renderSeed);

        resolver.updateForTopItem(state.whiteBishopCuboidItemState, this.whiteBishopCuboid.get(), ItemDisplayContext.NONE, level, null, renderSeed);
        resolver.updateForTopItem(state.whiteKingCuboidItemState, this.whiteKingCuboid.get(), ItemDisplayContext.NONE, level, null, renderSeed);
        resolver.updateForTopItem(state.whiteKnightCuboidItemState, this.whiteKnightCuboid.get(), ItemDisplayContext.NONE, level, null, renderSeed);
        resolver.updateForTopItem(state.whitePawnCuboidItemState, this.whitePawnCuboid.get(), ItemDisplayContext.NONE, level, null, renderSeed);
        resolver.updateForTopItem(state.whiteQueenCuboidItemState, this.whiteQueenCuboid.get(), ItemDisplayContext.NONE, level, null, renderSeed);
        resolver.updateForTopItem(state.whiteRookCuboidItemState, this.whiteRookCuboid.get(), ItemDisplayContext.NONE, level, null, renderSeed);

        state.fen = be.getSimplifiedFen();
        state.boardFacing = be.getFacing();
    }

    private PieceLod selectLod(double distanceSqr) {
        if (distanceSqr <= 12) return PieceLod.FULL;
        if (distanceSqr <= 48) return PieceLod.CUBOID;
        else return PieceLod.EMPTY;
    }

    @Override
    public void submit(@NotNull ChessBlockEntityRenderer.ChessBlockEntityRenderState state, @NotNull PoseStack poseStack, @NotNull SubmitNodeCollector collector, @NotNull CameraRenderState cameraRenderState) {
        if (state.lod == PieceLod.EMPTY) return;

        poseStack.pushPose();

        String[] boardState = state.fen.split("/");

        // Center of the a8 square (The -1f/128f value in the y section makes the pieces sit perfectly flush with the surface of the board)
        Vec3 a8 = new Vec3(cornerOffset + squareSize / 2f + 7 * squareSize, 1f / 16f + boardThickness / 16f - 1f / 128f, cornerOffset + squareSize / 2f + 7 * squareSize);
        poseStack.translate(a8);
        poseStack.mulPose(new Matrix4f().rotateY((float) Math.toRadians(180d)));
        poseStack.scale(squareSize, squareSize, squareSize);

        Direction facing = state.boardFacing;
        switch (facing) {
            case EAST:
                poseStack.rotateAround(new Quaternionf().rotateY((float) Math.toRadians(-90d)), 3.5f, 0, 3.5f);
                break;
            case SOUTH:
                poseStack.rotateAround(new Quaternionf().rotateY((float) Math.toRadians(-180d)), 3.5f, 0, 3.5f);
                break;
            case WEST:
                poseStack.rotateAround(new Quaternionf().rotateY((float) Math.toRadians(-270d)), 3.5f, 0, 3.5f);
                break;
            default:
                break;
        }

        for (int i = 0; i < 8; i++) {
            String row = boardState[i];
            for (int j = 0; j < 8; j++) {
                char piece = row.charAt(j);
                poseStack.pushPose();
                poseStack.translate(j, 0, i);

                if (Character.isLowerCase(piece)) {
                    poseStack.mulPose(new Matrix4f().rotateY((float) Math.toRadians(180d)));
                }

                renderPiece(state.lod, poseStack, collector, state, piece);

                poseStack.popPose();
            }
        }

        poseStack.popPose();
    }

    private void renderPiece(PieceLod lod, @NotNull PoseStack poseStack, @NotNull SubmitNodeCollector collector, @NotNull ChessBlockEntityRenderer.ChessBlockEntityRenderState state, char piece) {
        ItemStackRenderState itemState = null;
        switch (lod) {
            case FULL -> {
                switch (piece) {
                    case 'p':
                        itemState = state.blackPawnItemState;
                        break;
                    case 'q':
                        itemState = state.blackQueenItemState;
                        break;
                    case 'r':
                        itemState = state.blackRookItemState;
                        break;
                    case 'b':
                        itemState = state.blackBishopItemState;
                        break;
                    case 'n':
                        itemState = state.blackKnightItemState;
                        break;
                    case 'k':
                        itemState = state.blackKingItemState;
                        break;
                    case 'P':
                        itemState = state.whitePawnItemState;
                        break;
                    case 'Q':
                        itemState = state.whiteQueenItemState;
                        break;
                    case 'R':
                        itemState = state.whiteRookItemState;
                        break;
                    case 'B':
                        itemState = state.whiteBishopItemState;
                        break;
                    case 'N':
                        itemState = state.whiteKnightItemState;
                        break;
                    case 'K':
                        itemState = state.whiteKingItemState;
                        break;
                    default:
                        break;
                }
            }
            case CUBOID -> {
                switch (piece) {
                    case 'p':
                        itemState = state.blackPawnCuboidItemState;
                        break;
                    case 'q':
                        itemState = state.blackQueenCuboidItemState;
                        break;
                    case 'r':
                        itemState = state.blackRookCuboidItemState;
                        break;
                    case 'b':
                        itemState = state.blackBishopCuboidItemState;
                        break;
                    case 'n':
                        itemState = state.blackKnightCuboidItemState;
                        break;
                    case 'k':
                        itemState = state.blackKingCuboidItemState;
                        break;
                    case 'P':
                        itemState = state.whitePawnCuboidItemState;
                        break;
                    case 'Q':
                        itemState = state.whiteQueenCuboidItemState;
                        break;
                    case 'R':
                        itemState = state.whiteRookCuboidItemState;
                        break;
                    case 'B':
                        itemState = state.whiteBishopCuboidItemState;
                        break;
                    case 'N':
                        itemState = state.whiteKnightCuboidItemState;
                        break;
                    case 'K':
                        itemState = state.whiteKingCuboidItemState;
                        break;
                    default:
                        break;
                }
            }
        }

        if (itemState != null) {
            itemState.submit(poseStack, collector, state.lightCoords, OverlayTexture.NO_OVERLAY, 0);
        }
    }

    @Override
    public int getViewDistance() {
        return BlockEntityRenderer.super.getViewDistance();
    }

    @Override
    public boolean shouldRender(@NotNull ChessBlockEntity blockEntity, @NotNull Vec3 cameraPosition) {
        return BlockEntityRenderer.super.shouldRender(blockEntity, cameraPosition);
    }

    public enum PieceLod {
        FULL,
        CUBOID,
        EMPTY
    }

    public static class ChessBlockEntityRenderState extends BlockEntityRenderState {
        public final ItemStackRenderState blackBishopItemState = new ItemStackRenderState();
        public final ItemStackRenderState blackKingItemState = new ItemStackRenderState();
        public final ItemStackRenderState blackKnightItemState = new ItemStackRenderState();
        public final ItemStackRenderState blackPawnItemState = new ItemStackRenderState();
        public final ItemStackRenderState blackQueenItemState = new ItemStackRenderState();
        public final ItemStackRenderState blackRookItemState = new ItemStackRenderState();
        public final ItemStackRenderState whiteBishopItemState = new ItemStackRenderState();
        public final ItemStackRenderState whiteKingItemState = new ItemStackRenderState();
        public final ItemStackRenderState whiteKnightItemState = new ItemStackRenderState();
        public final ItemStackRenderState whitePawnItemState = new ItemStackRenderState();
        public final ItemStackRenderState whiteQueenItemState = new ItemStackRenderState();
        public final ItemStackRenderState whiteRookItemState = new ItemStackRenderState();
        public final ItemStackRenderState blackBishopCuboidItemState = new ItemStackRenderState();
        public final ItemStackRenderState blackKingCuboidItemState = new ItemStackRenderState();
        public final ItemStackRenderState blackKnightCuboidItemState = new ItemStackRenderState();
        public final ItemStackRenderState blackPawnCuboidItemState = new ItemStackRenderState();
        public final ItemStackRenderState blackQueenCuboidItemState = new ItemStackRenderState();
        public final ItemStackRenderState blackRookCuboidItemState = new ItemStackRenderState();
        public final ItemStackRenderState whiteBishopCuboidItemState = new ItemStackRenderState();
        public final ItemStackRenderState whiteKingCuboidItemState = new ItemStackRenderState();
        public final ItemStackRenderState whiteKnightCuboidItemState = new ItemStackRenderState();
        public final ItemStackRenderState whitePawnCuboidItemState = new ItemStackRenderState();
        public final ItemStackRenderState whiteQueenCuboidItemState = new ItemStackRenderState();
        public final ItemStackRenderState whiteRookCuboidItemState = new ItemStackRenderState();
        public PieceLod lod = PieceLod.FULL;
        public String fen = "";

        public Direction boardFacing = Direction.NORTH;
    }
}
