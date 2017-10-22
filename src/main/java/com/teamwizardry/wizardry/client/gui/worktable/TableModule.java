package com.teamwizardry.wizardry.client.gui.worktable;

import com.teamwizardry.librarianlib.core.client.ClientTickHandler;
import com.teamwizardry.librarianlib.features.gui.EnumMouseButton;
import com.teamwizardry.librarianlib.features.gui.GuiComponent;
import com.teamwizardry.librarianlib.features.gui.components.ComponentSprite;
import com.teamwizardry.librarianlib.features.gui.components.ComponentVoid;
import com.teamwizardry.librarianlib.features.gui.mixin.DragMixin;
import com.teamwizardry.librarianlib.features.gui.mixin.gl.GlMixin;
import com.teamwizardry.librarianlib.features.math.Vec2d;
import com.teamwizardry.librarianlib.features.math.interpolate.position.InterpBezier2D;
import com.teamwizardry.librarianlib.features.sprite.Sprite;
import com.teamwizardry.librarianlib.features.sprite.Texture;
import com.teamwizardry.wizardry.Wizardry;
import com.teamwizardry.wizardry.api.spell.module.Module;
import com.teamwizardry.wizardry.api.spell.module.ModuleRegistry;
import com.teamwizardry.wizardry.api.spell.module.ModuleType;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiScreen;
import net.minecraft.client.renderer.BufferBuilder;
import net.minecraft.client.renderer.GlStateManager;
import net.minecraft.client.renderer.Tessellator;
import net.minecraft.client.renderer.vertex.DefaultVertexFormats;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.math.Vec3d;
import net.minecraft.util.text.TextFormatting;
import org.lwjgl.opengl.GL11;

import java.awt.*;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;
import java.util.function.Function;

import static com.teamwizardry.wizardry.api.spell.module.ModuleType.MODIFIER;
import static org.lwjgl.opengl.GL11.GL_ONE_MINUS_SRC_ALPHA;
import static org.lwjgl.opengl.GL11.GL_SRC_ALPHA;

public class TableModule {

	private static final Texture SPRITE_SHEET = new Texture(new ResourceLocation(Wizardry.MODID, "textures/gui/worktable/sprite_sheet.png"));

	static final Sprite plate = SPRITE_SHEET.getSprite("module_default", 16, 16);
	private static final Sprite plate_highlighted = SPRITE_SHEET.getSprite("module_default_glow", 16, 16);
	private static final Sprite streak = new Sprite(new ResourceLocation(Wizardry.MODID, "textures/gui/worktable/streak.png"));
	public final Module module;
	public ComponentVoid component;
	private Sprite icon;
	private Vec2d prevPos;

	@SuppressWarnings({"unchecked", "rawtypes"})
	public TableModule(WorktableGui table, ComponentSprite parent, Module module, boolean draggable, boolean benign) {
		this.module = module;
		icon = new Sprite(new ResourceLocation(Wizardry.MODID, "textures/gui/worktable/icons/" + module.getID() + ".png"));

		ComponentVoid base = new ComponentVoid(0, 0, 16, 16);
		prevPos = base.getPos();
		if (draggable) GlMixin.INSTANCE.transform(base).setValue(new Vec3d(0, 0, 30));
		base.addTag(module);

		base.BUS.hook(GuiComponent.MouseDownEvent.class, (event) -> {
			if (event.getButton() == EnumMouseButton.LEFT) {
				if (!draggable && event.getComponent().getMouseOver()) {
					TableModule item = new TableModule(table, parent, module, true, false);
					table.paper.add(item.component);

					item.component.setPos(event.getComponent().getParent().unTransformChildPos(event.getComponent(), event.getMousePos()));
					item.component.addTag("dragging");
					DragMixin<ComponentVoid> drag = new DragMixin<>(item.component, vec2d -> vec2d);
					drag.setClickPos(new Vec2d(6, 6));
					drag.setMouseDown(event.getButton());
					event.cancel();
				}
			}
		});

		base.BUS.hook(DragMixin.DragPickupEvent.class, (event) -> {
			prevPos = event.getComponent().unTransform(Vec2d.ZERO, event.getComponent().getRoot());
			if (event.getButton() == EnumMouseButton.RIGHT) {
				event.getComponent().setData(Vec2d.class, "origin_pos", event.getComponent().getPos());
				event.getComponent().setZIndex(-1);
				event.getComponent().addTag("dragging");
			}
		});

		base.BUS.hook(DragMixin.DragDropEvent.class, (event) -> {
			event.getComponent().removeTag("dragging");

			if (prevPos.getXi() == event.getComponent().getPos().getX()
					&& prevPos.getYi() == event.getComponent().getPos().getYi()) {
				//Module lastModifier = ModuleRegistry.INSTANCE.getModule((String) event.getComponent().getData(String.class, "last_modifier_type"));
				//if (lastModifier != null && event.getComponent().hasData(Integer.class, lastModifier.getID())) {
				//	int x = (int) event.getComponent().getData(Integer.class, lastModifier.getID());
				//	if (event.getButton() == EnumMouseButton.LEFT) x++;
				//	else if (event.getButton() == EnumMouseButton.RIGHT) x--;
				//	if (x <= 0) event.getComponent().removeData(Integer.class, lastModifier.getID());
				//	else event.getComponent().setData(Integer.class, lastModifier.getID(), x);
				//}
				if (event.getComponent().getMouseOver() && draggable && !benign) {

					if (table.selectedcomponent == event.getComponent()) {
						table.selectedcomponent = null;
						table.whitelistedModifiers.refresh();
					} else {
						table.selectedcomponent = event.getComponent();
						table.whitelistedModifiers.refresh();
					}
				}
			}

			if (event.getButton() == EnumMouseButton.LEFT) {

				Vec2d plateSize = table.paper.getSize();
				Vec2d platePos = event.getComponent().getPos();
				boolean b = platePos.getX() >= 0 && platePos.getX() <= plateSize.getX() && platePos.getY() >= 0 && platePos.getY() <= plateSize.getY();

				// MODIFIER ADDING //
				{
					if (module.getModuleType() == MODIFIER) {
						GuiComponent<?> componentHovered = null;
						for (GuiComponent component : table.paperComponents.keySet()) {
							Module module2 = table.getModule(component);
							if (module2 == null) continue;
							if (!component.getMouseOverNoOcclusion()) continue;
							if (module2.getID().equals(module.getID())) continue;
							componentHovered = component;
						}
						if (componentHovered != null) {
							int i = componentHovered.hasData(Integer.class, module.getID()) ? componentHovered.getData(Integer.class, module.getID()) : 0;
							componentHovered.setData(Integer.class, module.getID(), ++i);
							componentHovered.setData(String.class, "last_modifier_type", module.getID());
						}

						table.paperComponents.remove(event.getComponent());

						event.getComponent().invalidate();
						event.cancel();
					}
				}
				// MODIFIER ADDING //

				if (!b) {
					if (module.getModuleType() != MODIFIER) {
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
			boolean anyDragging = false;
			for (GuiComponent<?> comp : table.paperComponents.keySet())
				if (comp.hasTag("dragging")) {
					anyDragging = true;
					break;
				}

			//---------// DRAW WIRE TO CURSOR //---------//
			{
				if (hasPos) {
					position = (Vec2d) event.getComponent().getData(Vec2d.class, "origin_pos");

					if (position != null) {
						Vec2d start = position.add(8, 8);
						Vec2d end = event.getComponent().getParent().unTransformChildPos(event.getComponent(), event.getMousePos());


						Module module1 = table.getModule(event.getComponent());
						if (module1 == null) return;

						drawWire(start, end, getColorForModule(module1.getModuleType()), Color.WHITE);
					}
				}

				if (position == null) position = event.getComponent().getPos();
			}
			//---------// DRAW WIRE TO CURSOR //---------//

			//---------// RENDER MODULE //---------//
			{
				float size = ((table.selectedcomponent == event.getComponent() || event.getComponent().getMouseOver()) && !hasPos) ? 24 : 16;
				float sizeIcon = ((table.selectedcomponent == event.getComponent() || event.getComponent().getMouseOver()) && !hasPos) ? 18 : 12;
				float posPlate = ((table.selectedcomponent == event.getComponent() || event.getComponent().getMouseOver()) && !hasPos) ? -4 : 0;
				float posIcon = ((table.selectedcomponent == event.getComponent() || event.getComponent().getMouseOver()) && !hasPos) ? -1.5f : 2;

				GlStateManager.pushMatrix();
				GlStateManager.color(1, 1, 1, 1);
				GlStateManager.translate(position.getXf(), position.getYf(), event.getComponent().getMouseOver() ? 150 : 5);
				GlStateManager.enableBlend();

				if (event.getComponent().getMouseOver() && !hasPos) {
					plate.getTex().bind();
					plate.draw((int) event.getPartialTicks(), posPlate, posPlate, size, size);

					if (!anyDragging) {
						plate_highlighted.getTex().bind();
						plate_highlighted.draw((int) event.getPartialTicks(), posPlate, posPlate, size, size);
					}
				} else {
					plate.getTex().bind();
					plate.draw((int) event.getPartialTicks(), posPlate, posPlate, size, size);
				}

				if (icon != null) {
					icon.getTex().bind();
					icon.draw((int) event.getPartialTicks(), posIcon, posIcon, sizeIcon, sizeIcon);
				}

				// RENDER MODIFIERS //
				if (draggable) {

					ArrayList<Module> modifiers = new ArrayList<>();
					for (Module modifier : ModuleRegistry.INSTANCE.getModules(ModuleType.MODIFIER)) {
						if (event.getComponent().hasData(Integer.class, modifier.getID())) {
							modifiers.add(modifier);
						}
					}

					double slice = 2 * Math.PI / modifiers.size();
					for (int i = 0; i < modifiers.size(); i++) {
						Module modifier = modifiers.get(i);
						double angle = slice * i + (ClientTickHandler.getTicks() / 20.0);// + event.getPartialTicks();
						float newX = (float) (5 + 30 * Math.cos(angle));
						float newY = (float) (5 + 30 * Math.sin(angle));
						float s = 12;
						Sprite modifierIcon = new Sprite(new ResourceLocation(Wizardry.MODID, "textures/gui/worktable/icons/" + modifier.getID() + ".png"));
						plate.getTex().bind();
						plate.draw((int) event.getPartialTicks(), newX, newY, s, s);
						modifierIcon.bind();
						modifierIcon.draw((int) event.getPartialTicks(), newX + 2, newY + 2, s - 4, s - 4);

						drawWire(
								new Vec2d(newX, newY).add(s / 2.0, s / 2.0),
								Vec2d.ZERO.add(8, 8),
								getColorForModule(ModuleType.MODIFIER),
								getColorForModule(table.getModule(event.getComponent()).getModuleType()));

						int x = (int) event.getComponent().getData(Integer.class, modifier.getID());
						Minecraft.getMinecraft().fontRenderer.drawString("x" + x, newX + s / 2 - Minecraft.getMinecraft().fontRenderer.getStringWidth("x" + x) / 2, newY + s, Color.LIGHT_GRAY.getRGB(), false);
						GlStateManager.color(1, 1, 1, 1);
					}
				}
				// RENDER MODIFIERS //

				GlStateManager.popMatrix();
			}
			//---------// RENDER MODULE //---------//

			//---------// RENDER LINKS BETWEEN MODULES //---------//
			{
				UUID linkedUuid = table.componentLinks.get(table.getUUID(event.getComponent()));

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

				drawWire(fromPos, toPos, getColorForModule(module2.getModuleType()), getColorForModule(module1.getModuleType()));
			}
			//---------// RENDER LINKS BETWEEN MODULES //---------//
		});

		base.getTooltip().func((Function<GuiComponent<ComponentVoid>, List<String>>) t -> {
			List<String> txt = new ArrayList<>();

			for (GuiComponent<?> comp : table.paperComponents.keySet()) if (comp.hasTag("dragging")) return txt;

			txt.add(TextFormatting.GOLD + module.getReadableName());
			if (GuiScreen.isShiftKeyDown())
				txt.add(TextFormatting.GRAY + module.getDescription());
			else txt.add(TextFormatting.GRAY + "<Sneak for info>");
			return txt;
		});

		if (draggable)
			table.paperComponents.put(base, UUID.randomUUID());

		this.component = base;
	}

	@SuppressWarnings("unused")
	public static void drawWire(Vec2d start, Vec2d end, Color primary, Color secondary) {
		GlStateManager.pushMatrix();
		GlStateManager.enableBlend();
		GlStateManager.disableCull();
		GlStateManager.blendFunc(GL_SRC_ALPHA, GL_ONE_MINUS_SRC_ALPHA);
		GlStateManager.color(1, 1, 1, 1);
		GlStateManager.translate(0, 0, -10);
		streak.getTex().bind();
		InterpBezier2D bezier = new InterpBezier2D(start, end);
		List<Vec2d> list = bezier.list(50);

		Vec2d pointerPos = null, behindPointer = null;
		float p = 0;
		for (int i = 0; i < list.size() - 1; i++) {
			float x = (float) (start.length() + ClientTickHandler.getTicks() + ClientTickHandler.getPartialTicks()) / 30f;
			if (i == (int) ((x - Math.floor(x)) * 50f)) {
				p = i / (list.size() - 1.0f);
			}
		}

		Tessellator tessellator = Tessellator.getInstance();
		BufferBuilder vb = tessellator.getBuffer();
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

	private static float lerp(float a, float b, float f) {
		return a + f * (b - a);
	}

	public static Color getColorForModule(ModuleType type) {
		switch (type) {
			case EVENT:
				return Color.PINK;
			case SHAPE:
				return Color.CYAN;
			case EFFECT:
				return Color.ORANGE;
			case MODIFIER:
				return Color.GREEN;
			default:
				return Color.BLACK;
		}
	}
}
