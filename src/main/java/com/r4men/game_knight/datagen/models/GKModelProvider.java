package com.r4men.game_knight.datagen.models;

import com.r4men.game_knight.GameKnight;
import com.r4men.game_knight.block.GKBlocks;
import net.minecraft.client.data.models.BlockModelGenerators;
import net.minecraft.client.data.models.ItemModelGenerators;
import net.minecraft.client.data.models.ModelProvider;
import net.minecraft.client.data.models.model.ModelTemplate;
import net.minecraft.client.data.models.model.TextureMapping;
import net.minecraft.client.data.models.model.TextureSlot;
import net.minecraft.client.data.models.model.TexturedModel;
import net.minecraft.client.renderer.item.ClientItem;
import net.minecraft.client.renderer.item.CuboidItemModelWrapper;
import net.minecraft.data.PackOutput;

import java.util.List;
import java.util.Optional;

public class GKModelProvider extends ModelProvider {
    public static final TextureSlot BOARD = TextureSlot.create("board", TextureSlot.ALL);
    public static final ModelTemplate SQUARE_BOARD_TEMPLATE = new ModelTemplate(
            Optional.of(
                    GameKnight.id("block/square_board")
            ),
            Optional.empty(),
            BOARD
    );
    public static final TexturedModel.Provider SQUARE_BOARD_PROVIDER = TexturedModel.createDefault(
            // Block to texture mapping
            block -> new TextureMapping()
                    .put(BOARD, TextureMapping.getBlockTexture(block)),
            // The template to generate from
            SQUARE_BOARD_TEMPLATE
    );

    public GKModelProvider(PackOutput packOutput) {
        super(packOutput, GameKnight.MOD_ID);
    }

    private static void registerPiece(ItemModelGenerators itemModels, String name) {
        itemModels.itemModelOutput.register(
                GameKnight.id("chess/" + name),
                new ClientItem(
                        new CuboidItemModelWrapper.Unbaked(
                                GameKnight.id("chess/pieces/" + name),
                                Optional.empty(),
                                List.of()
                        ),
                        ClientItem.Properties.DEFAULT
                )
        );
    }

    @Override
    protected void registerModels(BlockModelGenerators blockModels, ItemModelGenerators itemModels) {
        blockModels.createHorizontallyRotatedBlock(GKBlocks.CHESS.get(), SQUARE_BOARD_PROVIDER);
        blockModels.createTrivialBlock(GKBlocks.MONOPOLY.get(), SQUARE_BOARD_PROVIDER);

        // Black pieces
        registerPiece(itemModels, "black_bishop");
        registerPiece(itemModels, "black_king");
        registerPiece(itemModels, "black_knight");
        registerPiece(itemModels, "black_pawn");
        registerPiece(itemModels, "black_queen");
        registerPiece(itemModels, "black_rook");

        registerPiece(itemModels, "black_bishop_cuboid");
        registerPiece(itemModels, "black_king_cuboid");
        registerPiece(itemModels, "black_knight_cuboid");
        registerPiece(itemModels, "black_pawn_cuboid");
        registerPiece(itemModels, "black_queen_cuboid");
        registerPiece(itemModels, "black_rook_cuboid");

        // White pieces
        registerPiece(itemModels, "white_bishop");
        registerPiece(itemModels, "white_king");
        registerPiece(itemModels, "white_knight");
        registerPiece(itemModels, "white_pawn");
        registerPiece(itemModels, "white_queen");
        registerPiece(itemModels, "white_rook");

        registerPiece(itemModels, "white_bishop_cuboid");
        registerPiece(itemModels, "white_king_cuboid");
        registerPiece(itemModels, "white_knight_cuboid");
        registerPiece(itemModels, "white_pawn_cuboid");
        registerPiece(itemModels, "white_queen_cuboid");
        registerPiece(itemModels, "white_rook_cuboid");
    }
}
