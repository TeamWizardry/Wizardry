package com.teamwizardry.wizardry.client.gui.worktable;

import com.teamwizardry.librarianlib.core.client.ClientTickHandler;
import com.teamwizardry.librarianlib.features.gui.EnumMouseButton;
import com.teamwizardry.librarianlib.features.gui.GuiComponent;
import com.teamwizardry.librarianlib.features.gui.components.ComponentVoid;
import com.teamwizardry.librarianlib.features.gui.mixin.DragMixin;
import com.teamwizardry.librarianlib.features.math.Vec2d;
import com.teamwizardry.librarianlib.features.math.interpolate.position.InterpBezier2D;
import com.teamwizardry.librarianlib.features.sprite.Sprite;
import com.teamwizardry.wizardry.Wizardry;
import com.teamwizardry.wizardry.api.spell.Module;
import com.teamwizardry.wizardry.api.spell.ModuleType;
import com.teamwizardry.wizardry.lib.LibSprites;
import net.minecraft.client.gui.GuiScreen;
import net.minecraft.client.renderer.GlStateManager;
import net.minecraft.client.renderer.Tessellator;
import net.minecraft.client.renderer.VertexBuffer;
import net.minecraft.client.renderer.vertex.DefaultVertexFormats;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.text.TextFormatting;
import org.lwjgl.opengl.GL11;

import java.awt.*;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;
import java.util.function.Function;

import static org.lwjgl.opengl.GL11.GL_ONE_MINUS_SRC_ALPHA;
import static org.lwjgl.opengl.GL11.GL_SRC_ALPHA;

public class TableModule {

	private static final Sprite plate = LibSprites.Worktable.MODULE_DEFAULT;
	private static final Sprite plate_highlighted = LibSprites.Worktable.MODULE_DEFAULT_GLOW;
	private static final Sprite streak = new Sprite(new ResourceLocation(Wizardry.MODID, "textures/gui/worktable/streak.png"));
	private static final Sprite dot = new Sprite(new ResourceLocation(Wizardry.MODID, "textures/gui/worktable/dot.png"));
	public final Module module;
	public ComponentVoid component;
	private Sprite icon;

	public TableModule(WorktableGui table, Module module, boolean draggable) {
		this.module = module;

		icon = new Sprite(new ResourceLocation(Wizardry.MODID, "textures/gui/worktable/icons/" + module.getID() + ".png"));

		ComponentVoid base = new ComponentVoid(0, 0, 16, 16);
		base.addTag(module);

		base.BUS.hook(GuiComponent.MouseDownEvent.class, (event) -> {
			if (event.getButton() == EnumMouseButton.LEFT) {
				if (!draggable && event.getComponent().getMouseOver()) {
					TableModule item = new TableModule(table, module, true);
					table.paper.add(item.component);

					item.component.setPos(event.getComponent().getParent().unTransformChildPos(event.getComponent(), event.getMousePos()));
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
				event.getComponent().setZIndex(-1);
			}
		});

		base.BUS.hook(DragMixin.DragDropEvent.class, (event) -> {
			if (event.getButton() == EnumMouseButton.LEFT) {
				Vec2d size = table.paper.getSize();
				Vec2d pos = event.getComponent().getPos();
				boolean b = pos.getX() >= 0 && pos.getX() <= size.getX() && pos.getY() >= 0 && pos.getY() <= size.getY();
				if (!b) {
					UUID uuid1 = table.getUUID(event.getComponent());
					if (table.componentLinks.containsKey(uuid1)) {
						table.componentLinks.remove(uuid1);
					}
					if (table.componentLinks.containsValue(uuid1)) {
						UUID uuid2 = table.componentLinks.get(uuid1);
						table.componentLinks.remove(uuid2, uuid1);
					}

					table.paperComponents.remove(event.getComponent());

					event.getComponent().invalidate();
					event.cancel();
				}

			} else if (event.getButton() == EnumMouseButton.RIGHT) {
				Vec2d position = null;
				if (event.getComponent().hasData(Vec2d.class, "origin_pos")) {
					position = (Vec2d) event.getComponent().getData(Vec2d.class, "origin_pos");
					event.getComponent().removeData(Vec2d.class, "origin_pos");
					event.getComponent().setZIndex(0);
				}
				if (position != null) event.getComponent().setPos(position);

				UUID uuid1 = table.getUUID(event.getComponent());

				for (GuiComponent component : table.paper.getChildren()) {
					if (component.getMouseOver()) {
						if (component == event.getComponent()) continue;
						UUID uuid2 = table.getUUID(component);

						if (table.componentLinks.containsKey(uuid1) && table.componentLinks.get(uuid1).equals(uuid2)) {
							table.componentLinks.remove(uuid1);
						}
						if (table.componentLinks.containsKey(uuid2) && table.componentLinks.get(uuid2).equals(uuid1)) {
							table.componentLinks.remove(uuid2);
						}

						if (table.componentLinks.containsValue(uuid2)) {
							for (GuiComponent component1 : table.paper.getChildren()) {
								UUID uuid3 = table.getUUID(component1);
								if (table.componentLinks.containsKey(uuid3) && table.componentLinks.get(uuid3).equals(uuid2)) {
									table.componentLinks.remove(uuid3);
									break;
								}
							}
						}

						if (table.componentLinks.containsKey(uuid2))
							table.componentLinks.remove(uuid2);
						if (table.componentLinks.containsKey(uuid1)) {
							table.componentLinks.remove(uuid1);
						}

						table.componentLinks.put(uuid1, uuid2);
						return;
					}
				}
			}
		});

		base.BUS.hook(GuiComponent.PreDrawEvent.class, (GuiComponent.PreDrawEvent event) -> {
			Vec2d position = event.getComponent().getPos();

			boolean hasPos = event.getComponent().hasData(Vec2d.class, "origin_pos");
			if (hasPos) {
				position = (Vec2d) event.getComponent().getData(Vec2d.class, "origin_pos");

				if (position != null) {
					Vec2d start = position.add(8, 8);
					Vec2d end = event.getComponent().getParent().unTransformChildPos(event.getComponent(), event.getMousePos());

					drawWire(start, end, Color.BLUE, Color.CYAN);
				}
			}

			if (position == null) position = event.getComponent().getPos();

			float size = (event.getComponent().getMouseOver() && !hasPos) ? 24 : 16;
			float sizeIcon = (event.getComponent().getMouseOver() && !hasPos) ? 18 : 12;
			float posPlate = (event.getComponent().getMouseOver() && !hasPos) ? -4 : 0;
			float posIcon = (event.getComponent().getMouseOver() && !hasPos) ? -1.5f : 2;

			GlStateManager.pushMatrix();
			GlStateManager.color(1, 1, 1, 1);
			GlStateManager.translate(position.getXf(), position.getYf(), event.getComponent().getMouseOver() ? 150 : 100);
			GlStateManager.enableBlend();
			plate.getTex().bind();
			plate.draw((int) event.getPartialTicks(), posPlate, posPlate, size, size);

			if (event.getComponent().getMouseOver() && !hasPos) {
				plate_highlighted.getTex().bind();
				plate_highlighted.draw((int) event.getPartialTicks(), posPlate, posPlate, size, size);
			}

			if (icon != null) {
				icon.getTex().bind();
				icon.draw((int) event.getPartialTicks(), posIcon, posIcon, sizeIcon, sizeIcon);
			}
			GlStateManager.popMatrix();

			UUID linkedUuid = table.componentLinks.get(table.getUUID(event.getComponent()));

			{
				GuiComponent component = table.getComponent(linkedUuid);
				if (component == null) return;
				if (linkedUuid == table.getUUID(event.getComponent())) return;

				Vec2d toPos = null;
				if (component.hasData(Vec2d.class, "origin_pos"))
					toPos = (Vec2d) component.getData(Vec2d.class, "origin_pos");
				if (toPos == null) toPos = component.getPos();
				toPos = toPos.add(8, 8);

				Vec2d fromPos = null;
				if (event.getComponent().hasData(Vec2d.class, "origin_pos"))
					fromPos = ((Vec2d) event.getComponent().getData(Vec2d.class, "origin_pos"));
				if (fromPos == null) fromPos = event.getComponent().getPos();
				fromPos = fromPos.add(8, 8);

				Module module1 = table.getModule(component);
				if (module1 == null) return;

				Module module2 = table.getModule(event.getComponent());
				if (module2 == null) return;

				drawWire(fromPos, toPos, getColorForModule(module1.getModuleType()), getColorForModule(module2.getModuleType()));
			}
		});

		base.getTooltip().func((Function<GuiComponent<ComponentVoid>, List<String>>) t -> {
			List<String> txt = new ArrayList<>();

			if (t.hasData(Vec2d.class, "origin_pos")) return txt;

			txt.add(TextFormatting.GOLD + module.getReadableName());
			if (GuiScreen.isShiftKeyDown())
				txt.add(TextFormatting.GRAY + module.getDescription());
			return txt;
		});

		if (draggable)
			table.paperComponents.put(base, UUID.randomUUID());

		this.component = base;
	}

	public void drawWire(Vec2d start, Vec2d end, Color primary, Color secondary) {
		GlStateManager.pushMatrix();
		GlStateManager.enableBlend();
		GlStateManager.disableCull();
		GlStateManager.blendFunc(GL_SRC_ALPHA, GL_ONE_MINUS_SRC_ALPHA);
		GlStateManager.color(1, 1, 1, 1);
		streak.getTex().bind();
		InterpBezier2D bezier = new InterpBezier2D(start, end);
		List<Vec2d> list = bezier.list(50);

		Vec2d pointerPos = null, behindPointer = null;
		float p = 0;
		for (int i = 0; i < list.size() - 1; i++) {
			float x = (float) (start.length() + ClientTickHandler.getTicks() + ClientTickHandler.getPartialTicks()) / 30f;
			if (i == (int) ((x - Math.floor(x)) * 50f)) {
				pointerPos = list.get(i);
				if (i > 0) behindPointer = list.get(i - 1);
				p = i / (list.size() - 1.0f);
			}
		}

		Tessellator tessellator = Tessellator.getInstance();
		VertexBuffer vb = tessellator.getBuffer();
		vb.begin(GL11.GL_QUADS, DefaultVertexFormats.POSITION_TEX_COLOR);
		Vec2d lastPoint = null;
		for (int i = 0; i < list.size() - 1; i++) {
			Vec2d point = list.get(i);
			if (lastPoint == null) {
				lastPoint = point;
				continue;
			}

			float dist = (i / (list.size() - 1.0f));

			float wire;
			if (dist < p) {
				float z = Math.abs(dist - p);
				wire = 256.0f / 27.0f * (z * z * z - z * z * z * z);
			} else {
				float z = Math.abs(dist - (p + 1f));
				wire = 256.0f / 27.0f * (z * z * z - z * z * z * z);
			}

			float r = lerp(primary.getRed(), secondary.getRed(), wire) / 255f;
			float g = lerp(primary.getGreen(), secondary.getGreen(), wire) / 255f;
			float b = lerp(primary.getBlue(), secondary.getBlue(), wire) / 255f;

			Vec2d normal = point.sub(lastPoint).normalize();
			Vec2d perp = new Vec2d(-normal.getYf(), normal.getXf()).mul((1.0f - 2.0f * Math.abs(dist - 0.5f) + 0.3f));
			Vec2d point1 = lastPoint.sub(normal.mul(0.5)).add(perp);
			Vec2d point2 = point.add(normal.mul(0.5)).add(perp);
			Vec2d point3 = point.add(normal.mul(0.5)).sub(perp);
			Vec2d point4 = lastPoint.sub(normal.mul(0.5)).sub(perp);

			vb.pos(point1.getXf(), point1.getYf(), 0).tex(0, 0).color(r, g, b, 1f).endVertex();
			vb.pos(point2.getXf(), point2.getYf(), 0).tex(0, 1).color(r, g, b, 1f).endVertex();
			vb.pos(point3.getXf(), point3.getYf(), 0).tex(1, 0).color(r, g, b, 1f).endVertex();
			vb.pos(point4.getXf(), point4.getYf(), 0).tex(1, 1).color(r, g, b, 1f).endVertex();

			lastPoint = point;
		}
		tessellator.draw();

		GlStateManager.enableTexture2D();
		GlStateManager.popMatrix();
	}

	private float lerp(float a, float b, float f) {
		return a + f * (b - a);
	}

	private Color getColorForModule(ModuleType type) {
		switch (type) {
			case EVENT:
				return Color.PINK;
			case SHAPE:
				return Color.CYAN;
			case EFFECT:
				return Color.ORANGE;
			case MODIFIER:
				return Color.GREEN;
		}

		return Color.BLACK;
	}
}
