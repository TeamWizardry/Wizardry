package com.teamwizardry.wizardry.client.gui.book;

import com.teamwizardry.librarianlib.features.gui.GuiComponent;
import com.teamwizardry.librarianlib.features.gui.mixin.ScissorMixin;
import com.teamwizardry.librarianlib.features.math.Vec2d;
import com.teamwizardry.wizardry.api.block.IStructure;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.GlStateManager;
import net.minecraft.client.renderer.texture.TextureMap;
import org.lwjgl.opengl.GL11;

public class ComponentStructure extends GuiComponent<ComponentStructure> {

	private double rotateHorizontal = 0;
	private double rotateVertical = 0;
	private boolean dragging = false;
	private Vec2d prevPos = Vec2d.ZERO;

	public ComponentStructure(int x, int y, int width, int height, IStructure structure) {
		super(x, y, width, height);

		ScissorMixin.INSTANCE.scissor(this);

		BUS.hook(GuiComponent.MouseDragEvent.class, event -> {

			Vec2d untransform = event.getComponent().unTransform(event.getMousePos(), event.getComponent());
			Vec2d diff;
			if (dragging) diff = untransform.sub(prevPos).mul(1 / 5.0);
			else diff = event.getMousePos().mul(1 / 100.0);
			rotateHorizontal += diff.getX();
			rotateVertical += diff.getY();
			prevPos = untransform;
			dragging = true;
		});

		BUS.hook(GuiComponent.MouseUpEvent.class, event -> {
			prevPos = Vec2d.ZERO;
			dragging = false;
		});

		BUS.hook(GuiComponent.PostDrawEvent.class, event -> {

			GlStateManager.pushMatrix();
			GlStateManager.enableAlpha();
			GlStateManager.enableBlend();
			GlStateManager.matrixMode(GL11.GL_MODELVIEW);
			GlStateManager.shadeModel(GL11.GL_SMOOTH);
			GlStateManager.enableCull();
			GlStateManager.enableRescaleNormal();

			GlStateManager.translate(width / 2.0, (height / 2.0), 100);

			GlStateManager.rotate((float) (35 + rotateVertical), -1, 0, 0);
			GlStateManager.rotate((float) ((45 + rotateHorizontal)), 0, 1, 0);
			GlStateManager.scale(16, -16, 16);
			GlStateManager.translate(-structure.offsetToCenter().getX(), -structure.offsetToCenter().getY(), -structure.offsetToCenter().getZ());
			GlStateManager.translate(-0.5, -0.5, -0.5);

			Minecraft.getMinecraft().getTextureManager().bindTexture(TextureMap.LOCATION_BLOCKS_TEXTURE);
			structure.getStructure().draw();

			GlStateManager.popMatrix();
		});
	}

	@Override
	public void drawComponent(Vec2d mousePos, float partialTicks) {

	}
}
