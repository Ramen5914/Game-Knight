package com.r4men.game_knight.client.renderer;

import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.blaze3d.vertex.VertexConsumer;
import net.minecraft.client.renderer.SubmitNodeCollector;
import org.jetbrains.annotations.NotNull;

public class DebugGrid implements SubmitNodeCollector.CustomGeometryRenderer {
    private final float x;
    private final float y;
    private final float z;
    private final float size;

    public DebugGrid(float x, float y, float z, float size) {
        this.x = x;
        this.y = y;
        this.z = z;
        this.size = size;
    }

    @Override
    public void render(PoseStack.@NotNull Pose pose, @NotNull VertexConsumer vc) {
        box(pose, vc, this.x, this.y, this.z, this.size);
    }

    public void box(PoseStack.Pose pose, VertexConsumer vc, float x, float y, float z, float size) {
        float offset = size / 2f;

        int r = (int) (z / 8f * 255f);
        int g = (int) (z / 8f * 255f);
        int b = (int) (z / 8f * 255f);

        quad(pose, vc, x - offset, y + offset, z - offset, x + offset, y + offset, z + offset, r, g, b, 255);
        quad(pose, vc, x - offset, y - offset, z - offset, x + offset, y - offset, z + offset, r, g, b, 255);
        quad(pose, vc, x - offset, y - offset, z + offset, x + offset, y + offset, z + offset, r, g, b, 255);
        quad(pose, vc, x - offset, y - offset, z - offset, x + offset, y + offset, z - offset, r, g, b, 255);
        quad(pose, vc, x + offset, y - offset, z - offset, x + offset, y + offset, z + offset, r, g, b, 255);
        quad(pose, vc, x - offset, y - offset, z - offset, x - offset, y + offset, z + offset, r, g, b, 255);
    }

    private void quad(PoseStack.Pose pose, VertexConsumer vc, float x1, float y1, float z1, float x2, float y2, float z2, int r, int g, int b, int a) {
        if (x1 == x2) { // YZ Plane (constant X) - use consistent winding: bottom-front, top-front, top-back, bottom-back
            vc.addVertex(pose, x1, y1, z1).setColor(r, g, b, a);
            vc.addVertex(pose, x1, y2, z1).setColor(r, g, b, a);
            vc.addVertex(pose, x1, y2, z2).setColor(r, g, b, a);
            vc.addVertex(pose, x1, y1, z2).setColor(r, g, b, a);
        } else if (y1 == y2) { // XZ Plane (constant Y) - consistent winding: minX,minZ -> minX,maxZ -> maxX,maxZ -> maxX,minZ
            vc.addVertex(pose, x1, y1, z1).setColor(r, g, b, a);
            vc.addVertex(pose, x1, y1, z2).setColor(r, g, b, a);
            vc.addVertex(pose, x2, y1, z2).setColor(r, g, b, a);
            vc.addVertex(pose, x2, y1, z1).setColor(r, g, b, a);
        } else if (z1 == z2) { // XY Plane (constant Z) - consistent winding: bottom-left, top-left, top-right, bottom-right
            vc.addVertex(pose, x1, y1, z1).setColor(r, g, b, a);
            vc.addVertex(pose, x1, y2, z1).setColor(r, g, b, a);
            vc.addVertex(pose, x2, y2, z1).setColor(r, g, b, a);
            vc.addVertex(pose, x2, y1, z1).setColor(r, g, b, a);
        }
    }
}
