package com.r4men.game_night.client.renderer;

import com.mojang.blaze3d.vertex.PoseStack;
import com.r4men.game_night.GameNight;
import com.r4men.game_night.block.entity.ChessBlockEntity;
import com.r4men.game_night.client.renderer.ChessBlockEntityRenderer.ChessRenderState;
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
        Supplier<ItemStack> whiteRook
) implements BlockEntityRenderer<ChessBlockEntity, ChessRenderState> {
    private static final float boardSize = 14f;
    private static final float boardThickness = 1f;
    private static final float squareCount = 8f;

    private static final float cornerOffset = ((16f - boardSize) / 2f)/16f;
    private static final float squareSize = boardSize/16f/squareCount;

    public static class ChessRenderState extends BlockEntityRenderState {
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

        public String fen = "";

        public Direction boardFacing = Direction.NORTH;
    }

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
                RenderHelper.memoizeStackModel(whiteRookModel)
        );
    }

    @Override
    public @NotNull ChessRenderState createRenderState() {
        return new ChessRenderState();
    }

    @Override
    public void extractRenderState(@NotNull ChessBlockEntity be, @NotNull ChessRenderState state, float partialTicks, @NotNull Vec3 cameraPosition, ModelFeatureRenderer.@Nullable CrumblingOverlay breakProgress) {
        BlockEntityRenderer.super.extractRenderState(be, state, partialTicks, cameraPosition, breakProgress);

        Level level = Minecraft.getInstance().level;

        int renderSeed = (int)(be.getBlockPos().asLong());
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

        state.fen = be.getSimplifiedFen();
        state.boardFacing = be.getFacing();
    }

    @Override
    public void submit(@NotNull ChessRenderState state, @NotNull PoseStack poseStack, @NotNull SubmitNodeCollector collector, @NotNull CameraRenderState cameraRenderState) {
        poseStack.pushPose();

        String[] boardState = state.fen.split("/");

        // Center of the a8 square (The -1f/128f value in the y section makes the pieces sit perfectly flush with the surface of the board)
        Vec3 a8 = new Vec3(cornerOffset + squareSize/2f + 7 * squareSize, 1f/16f + boardThickness/16f - 1f/128f, cornerOffset + squareSize/2f + 7 * squareSize);
        poseStack.translate(a8);
        poseStack.mulPose(new Matrix4f().rotateY((float) Math.toRadians(180d)));
        poseStack.scale(squareSize, squareSize, squareSize);

        Direction facing = state.boardFacing;
        switch (facing) {
            case EAST: poseStack.rotateAround(new Quaternionf().rotateY((float) Math.toRadians(-90d)), 3.5f, 0, 3.5f); break;
            case SOUTH: poseStack.rotateAround(new Quaternionf().rotateY((float) Math.toRadians(-180d)), 3.5f, 0, 3.5f); break;
            case WEST: poseStack.rotateAround(new Quaternionf().rotateY((float) Math.toRadians(-270d)), 3.5f, 0, 3.5f); break;
            default: break;
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

                renderPiece(poseStack, collector, state, piece);

                poseStack.popPose();
            }
        }

        poseStack.popPose();
    }

    private void renderPiece(@NotNull PoseStack poseStack, @NotNull SubmitNodeCollector collector, @NotNull ChessRenderState state, char piece) {
        ItemStackRenderState itemState = null;
        switch (piece) {
            case 'p': itemState = state.blackPawnItemState; break;
            case 'q': itemState = state.blackQueenItemState; break;
            case 'r': itemState = state.blackRookItemState; break;
            case 'b': itemState = state.blackBishopItemState; break;
            case 'n': itemState = state.blackKnightItemState; break;
            case 'k': itemState = state.blackKingItemState; break;
            case 'P': itemState = state.whitePawnItemState; break;
            case 'Q': itemState = state.whiteQueenItemState; break;
            case 'R': itemState = state.whiteRookItemState; break;
            case 'B': itemState = state.whiteBishopItemState; break;
            case 'N': itemState = state.whiteKnightItemState; break;
            case 'K': itemState = state.whiteKingItemState; break;
            default: break;
        }

        if (itemState != null) {
            itemState.submit(poseStack, collector, state.lightCoords, OverlayTexture.NO_OVERLAY, 0);
        }
    }
}
