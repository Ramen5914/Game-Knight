package com.r4men.game_knight.gui;

import net.minecraft.client.gui.screens.Screen;
import net.minecraft.network.chat.Component;

public abstract class GKScreen extends Screen {
    protected static final int BACKGROUND_TEXTURE_WIDTH = 256;
    protected static final int BACKGROUND_TEXTURE_HEIGHT = 256;
    protected static final int DEFAULT_IMAGE_WIDTH = 176;
    protected static final int DEFAULT_IMAGE_HEIGHT = 166;
    protected final int imageWidth;
    protected final int imageHeight;
    protected int titleLabelX;
    protected int titleLabelY;
    protected int leftPos;
    protected int topPos;

    public GKScreen(Component title) {
        this(title, DEFAULT_IMAGE_WIDTH, DEFAULT_IMAGE_HEIGHT);
    }

    protected GKScreen(Component title, int imageWidth, int imageHeight) {
        super(title);
        this.imageWidth = imageWidth;
        this.imageHeight = imageHeight;
        this.titleLabelX = 8;
        this.titleLabelY = 6;
    }

    @Override
    protected void init() {
        this.leftPos = (this.width - this.imageWidth) / 2;
        this.topPos = (this.height - this.imageHeight) / 2;
    }
}
