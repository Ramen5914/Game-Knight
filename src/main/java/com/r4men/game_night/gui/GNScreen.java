package com.r4men.game_night.gui;

import net.minecraft.client.gui.screens.Screen;
import net.minecraft.client.gui.screens.inventory.MenuAccess;
import net.minecraft.network.chat.Component;
import net.minecraft.world.inventory.AbstractContainerMenu;
import org.jspecify.annotations.NonNull;

public abstract class GNScreen<T extends AbstractContainerMenu> extends Screen implements MenuAccess<T> {
    protected static final int BACKGROUND_TEXTURE_WIDTH = 256;
    protected static final int BACKGROUND_TEXTURE_HEIGHT = 256;
    protected static final int DEFAULT_IMAGE_WIDTH = 176;
    protected static final int DEFAULT_IMAGE_HEIGHT = 166;
    protected final int imageWidth;
    protected final int imageHeight;
    protected int titleLabelX;
    protected int titleLabelY;
    protected final T menu;
    protected int leftPos;
    protected int topPos;

    public GNScreen(T menu, Component title) {
        this(menu, title, DEFAULT_IMAGE_WIDTH, DEFAULT_IMAGE_HEIGHT);
    }

    protected GNScreen(T menu, Component title, int imageWidth, int imageHeight) {
        super(title);
        this.menu = menu;
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

    @Override
    public @NonNull T getMenu() {
        return this.menu;
    }
}
