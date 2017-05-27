package com.teamwizardry.wizardry.client.gui.worktable;

import com.teamwizardry.librarianlib.features.gui.EnumMouseButton;
import com.teamwizardry.librarianlib.features.gui.GuiComponent;
import com.teamwizardry.librarianlib.features.gui.components.ComponentVoid;
import com.teamwizardry.librarianlib.features.gui.mixin.DragMixin;
import com.teamwizardry.librarianlib.features.math.Vec2d;
import com.teamwizardry.librarianlib.features.math.interpolate.position.InterpBezier2D;
import com.teamwizardry.librarianlib.features.sprite.Sprite;
import com.teamwizardry.wizardry.api.spell.Module;
import com.teamwizardry.wizardry.lib.LibSprites;
import net.minecraft.client.renderer.GlStateManager;
import net.minecraft.client.renderer.Tessellator;
import net.minecraft.client.renderer.VertexBuffer;
import net.minecraft.client.renderer.vertex.DefaultVertexFormats;
import net.minecraft.util.text.TextFormatting;
import org.lwjgl.opengl.GL11;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

public class TableModule {

	private static final Sprite plate = LibSprites.Worktable.MODULE_DEFAULT;
	private static final Sprite plate_highlighted = LibSprites.Worktable.MODULE_DEFAULT_GLOW;

	public ComponentVoid component;

	public TableModule(WorktableGui table, Module module, boolean draggable) {
		ComponentVoid base = new ComponentVoid(0, 0, 16, 16);

		base.BUS.hook(GuiComponent.MouseDownEvent.class, (event) -> {
			if (event.getButton() == EnumMouseButton.LEFT) {
				if (!draggable && event.getComponent().getMouseOver()) {
					TableModule item = new TableModule(table, module, true);
					table.paper.add(item.component);

					DragMixin<ComponentVoid> drag = new DragMixin<>(item.component, vec2d -> vec2d);
					drag.setClickPos(new Vec2d(6, 6));
					drag.setMouseDown(event.getButton());
					event.cancel();
				}
			}
		});

		base.BUS.hook(DragMixin.DragPickupEvent.class, (event) -> {
			if (event.getButton() == EnumMouseButton.RIGHT) {
				event.getComponent().setData(Vec2d.class, "origin_pos", event.getComponent().getPos());
			}
		});

		base.BUS.hook(DragMixin.DragDropEvent.class, (event) -> {
			if (event.getButton() == EnumMouseButton.LEFT) {
				if (!table.paper.getMouseOver()) {
					UUID uuid = table.paperComponents.get(event.getComponent());
					for (UUID connectedUUID : table.componentLinks.get(event.getComponent())) {
						GuiComponent connectedComponent = table.paperComponents.inverse().get(connectedUUID);
						if (table.componentLinks.get(connectedComponent).contains(uuid)) {
							table.componentLinks.remove(connectedComponent, uuid);
						}
					}
					table.componentLinks.removeAll(event.getComponent());
					table.paperComponents.remove(event.getComponent());

					event.getComponent().invalidate();
				}

			} else if (event.getButton() == EnumMouseButton.RIGHT) {
				Vec2d position = null;
				if (event.getComponent().hasData(Vec2d.class, "origin_pos")) {
					position = (Vec2d) event.getComponent().getData(Vec2d.class, "origin_pos");
					event.getComponent().removeData(Vec2d.class, "origin_pos");
				}
				if (position != null) event.getComponent().setPos(position);

				for (GuiComponent component : table.paper.getChildren()) {
					if (component.getMouseOver()) {
						if (component == event.getComponent()) continue;
						table.componentLinks.put(event.getComponent(), table.paperComponents.get(component));
						return;
					}
				}
			}
		});

		base.BUS.hook(GuiComponent.PreDrawEvent.class, (event) -> {
			Vec2d position = event.getComponent().getPos();

			if (event.getComponent().hasData(Vec2d.class, "origin_pos")) {
				position = (Vec2d) event.getComponent().getData(Vec2d.class, "origin_pos");

				if (position != null) {
					GlStateManager.pushMatrix();
					GlStateManager.disableTexture2D();
					GlStateManager.color(0, 0, 0, 1);
					GlStateManager.translate(0, 0, 10);
					Tessellator tessellator = Tessellator.getInstance();
					VertexBuffer vb = tessellator.getBuffer();
					vb.begin(GL11.GL_LINES, DefaultVertexFormats.POSITION);
					Vec2d lastPos = null;
					for (Vec2d point : new InterpBezier2D(position.add(8, 8), event.getComponent().getParent().unTransformChildPos(event.getComponent(), event.getMousePos())).list(50)) {
						vb.pos(point.getX(), point.getY(), 0).endVertex();
						if (lastPos != null) vb.pos(lastPos.getX(), lastPos.getY(), 0).endVertex();
						lastPos = point;
					}
					tessellator.draw();

					GlStateManager.enableTexture2D();
					GlStateManager.popMatrix();
				}
			}

			if (position == null) position = event.getComponent().getPos();

			GlStateManager.pushMatrix();
			GlStateManager.color(1, 1, 1, 1);
			GlStateManager.translate(position.getXf(), position.getYf(), 100);
			plate.getTex().bind();
			plate.draw((int) event.getPartialTicks(), 0, 0);

			if (event.getComponent().getMouseOver()) {
				plate_highlighted.getTex().bind();
				plate_highlighted.draw((int) event.getPartialTicks(), 0, 0);
			}
			GlStateManager.popMatrix();

			for (UUID uuid : table.componentLinks.get(event.getComponent())) {
				GuiComponent component = table.paperComponents.inverse().get(uuid);
				if (component == null) continue;

				Vec2d toPos = component.getPos();

				GlStateManager.pushMatrix();
				GlStateManager.disableTexture2D();
				GlStateManager.color(0, 0, 0, 1);
				GlStateManager.translate(0, 0, 10);
				Tessellator tessellator = Tessellator.getInstance();
				VertexBuffer vb = tessellator.getBuffer();
				vb.begin(GL11.GL_LINES, DefaultVertexFormats.POSITION);
				Vec2d lastPos = null;
				for (Vec2d point : new InterpBezier2D(event.getComponent().getPos().add(8, 8), toPos.add(8, 8)).list(50)) {
					vb.pos(point.getX(), point.getY(), 0).endVertex();
					if (lastPos != null) vb.pos(lastPos.getX(), lastPos.getY(), 0).endVertex();
					lastPos = point;
				}
				tessellator.draw();

				GlStateManager.enableTexture2D();
				GlStateManager.popMatrix();
			}

			if (event.getComponent().getMouseOver() && !draggable) {
				List<String> txt = new ArrayList<>();
				txt.add(TextFormatting.GOLD + module.getReadableName());
				txt.add(TextFormatting.GRAY + module.getDescription());
				event.getComponent().setTooltip(txt);
			}
		});

		if (draggable)
			table.paperComponents.put(base, UUID.randomUUID());

		this.component = base;
	}
}
