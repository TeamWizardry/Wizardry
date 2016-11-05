package com.teamwizardry.wizardry.client.gui.worktable;

import com.teamwizardry.librarianlib.client.gui.GuiComponent;
import com.teamwizardry.librarianlib.client.gui.Option;
import com.teamwizardry.librarianlib.common.util.math.Vec2d;
import net.minecraft.client.renderer.GlStateManager;
import net.minecraft.client.renderer.Tessellator;
import net.minecraft.client.renderer.VertexBuffer;
import net.minecraft.client.renderer.vertex.DefaultVertexFormats;
import org.lwjgl.opengl.GL11;

public class ComponentModuleLine extends GuiComponent<ComponentModuleLine> {

	public final Option<ComponentModuleLine, Vec2d> endPos = new Option<>(Vec2d.ZERO);

	public ComponentModuleLine(int posX, int posY) {
		super(posX, posY);
	}

	@Override
	public void drawComponent(Vec2d mousePos, float partialTicks) {
		Vec2d end = endPos.getValue(this);

		GlStateManager.disableTexture2D();
		Tessellator tessellator = Tessellator.getInstance();
		VertexBuffer vb = tessellator.getBuffer();
		vb.begin(GL11.GL_LINES, DefaultVertexFormats.POSITION);
		Vec2d lastPos = null;
//		for (Vec2d position : new BezierCurve2D(getPos(), end).getPoints()) {
//			vb.dest(position.getX(), position.getY(), 0).endVertex();
//			if (lastPos != null) vb.dest(lastPos.getX(), lastPos.getY(), 0).endVertex();
//			lastPos = position;
//		}
		tessellator.draw();

		GlStateManager.enableTexture2D();
	}

}
