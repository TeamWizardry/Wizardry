package com.teamwizardry.wizardry.client.gui.worktable;

import net.minecraft.client.renderer.GlStateManager;
import net.minecraft.client.renderer.Tessellator;
import net.minecraft.client.renderer.VertexBuffer;
import net.minecraft.client.renderer.vertex.DefaultVertexFormats;

import org.lwjgl.opengl.GL11;

import com.teamwizardry.librarianlib.api.gui.GuiComponent;
import com.teamwizardry.librarianlib.api.gui.Option;
import com.teamwizardry.librarianlib.math.Vec2;

public class ComponentModuleLine extends GuiComponent<ComponentModuleLine> {

	public final Option<ComponentModuleLine, Vec2> endPos = new Option<>(Vec2.ZERO);
	
	public ComponentModuleLine(int posX, int posY) {
		super(posX, posY);
	}

	@Override
	public void drawComponent(Vec2 mousePos, float partialTicks) {
		Vec2 end = endPos.getValue(this);
		
		GlStateManager.disableTexture2D();
		Tessellator tessellator = Tessellator.getInstance();
		VertexBuffer vb = tessellator.getBuffer();
		vb.begin(GL11.GL_LINES, DefaultVertexFormats.POSITION);
		vb.pos(pos.x, pos.y, 0).endVertex();
		vb.pos(end.x, end.y, 0).endVertex();
		tessellator.draw();
		
		GlStateManager.enableTexture2D();
	}

}
