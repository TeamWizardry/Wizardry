package com.teamwizardry.wizardry.client.gui.worktable2;

import com.teamwizardry.librarianlib.core.LibrarianLib;
import com.teamwizardry.librarianlib.core.client.ClientTickHandler;
import com.teamwizardry.librarianlib.features.gui.EnumMouseButton;
import com.teamwizardry.librarianlib.features.gui.component.GuiComponent;
import com.teamwizardry.librarianlib.features.gui.component.GuiComponentEvents;
import com.teamwizardry.librarianlib.features.gui.components.ComponentVoid;
import com.teamwizardry.librarianlib.features.gui.mixin.DragMixin;
import com.teamwizardry.librarianlib.features.math.Vec2d;
import com.teamwizardry.librarianlib.features.math.interpolate.position.InterpBezier2D;
import com.teamwizardry.librarianlib.features.sprite.Sprite;
import com.teamwizardry.wizardry.Wizardry;
import com.teamwizardry.wizardry.api.spell.module.Module;
import com.teamwizardry.wizardry.api.spell.module.ModuleType;
import net.minecraft.client.gui.GuiScreen;
import net.minecraft.client.renderer.BufferBuilder;
import net.minecraft.client.renderer.GlStateManager;
import net.minecraft.client.renderer.Tessellator;
import net.minecraft.client.renderer.vertex.DefaultVertexFormats;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.text.TextFormatting;
import org.jetbrains.annotations.NotNull;
import org.lwjgl.opengl.GL11;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import java.awt.*;
import java.util.ArrayList;
import java.util.List;
import java.util.function.Function;

import static com.teamwizardry.wizardry.api.spell.module.ModuleType.EFFECT;
import static com.teamwizardry.wizardry.client.gui.worktable2.WorktableGui.*;
import static org.lwjgl.opengl.GL11.GL_ONE_MINUS_SRC_ALPHA;
import static org.lwjgl.opengl.GL11.GL_SRC_ALPHA;

public class TableModule extends GuiComponent {

	@Nullable
	public static TableModule selectedModule = null;
	@Nonnull
	private final WorktableGui worktable;
	@Nonnull
	private final Module module;
	private final boolean draggable;
	private final Sprite icon;
	@Nullable
	private TableModule linksTo = null;
	/**
	 * ALWAYS from the context of null. Never to any other component.
	 */
	private Vec2d initialPos = null;

	public TableModule(@Nonnull WorktableGui worktable, @Nonnull Module module, boolean draggable) {
		super(0, 0, PLATE.getWidth(), PLATE.getHeight());
		this.worktable = worktable;
		this.module = module;
		this.draggable = draggable;
		icon = new Sprite(new ResourceLocation(Wizardry.MODID, "textures/gui/worktable/icons/" + module.getID() + ".png"));

		initialPos = thisPosToOtherContext(null);

		ComponentVoid paper = worktable.paper;

		geometry.setComponentOccludesMouseOver(false);
		//geometry.setComponentPropagatesMouseOverToParent(false);

		if (draggable) getTransform().setTranslateZ(30);

		BUS.hook(GuiComponentEvents.MouseDownEvent.class, (event) -> {
			if (event.getButton() == EnumMouseButton.LEFT && getMouseOver() && !this.draggable) {
				TableModule item = new TableModule(this.worktable, this.module, true);
				item.setPos(paper.otherPosToThisContext(event.component, event.getMousePos()));
				DragMixin drag = new DragMixin(item, vec2d -> vec2d);
				drag.setDragOffset(new Vec2d(6, 6));
				drag.setMouseDown(event.getButton());
				paper.add(item);

				event.cancel();
			}
		});

		BUS.hook(DragMixin.DragPickupEvent.class, (event) -> {
			if (!getMouseOver()) return;
			initialPos = event.component.thisPosToOtherContext(null);
			if (event.getButton() == EnumMouseButton.RIGHT) {
				event.component.addTag("connecting");
			}
		});

		BUS.hook(DragMixin.DragMoveEvent.class, (event) -> {
			if (event.getButton() == EnumMouseButton.RIGHT) {
				// event.getPos returns the before-moving position. Setting it back to it's place.
				// This allows the component to stay where it is while also allowing us to draw a line
				// outside of it's box
				event.setNewPos(event.getPos());
			}
		});

		BUS.hook(DragMixin.DragDropEvent.class, (event) -> {
			Vec2d currentPos = event.component.thisPosToOtherContext(null);
			if (event.getButton() == EnumMouseButton.RIGHT && initialPos.equals(currentPos)) {
				selectedModule = (selectedModule == event.component) ? null : this;

				// TODO: refresh modifiers here

				event.component.removeTag("connecting");
				return;
			}

			if (!paper.geometry.getMouseOver()) {
				if (!event.component.hasTag("connecting")) {

					for (GuiComponent paperComponent : paper.getChildren()) {
						if (paperComponent == event.component) continue;

						if (!(paperComponent instanceof TableModule)) continue;
						TableModule linkTo = (TableModule) paperComponent;

						if (linkTo.getLinksTo() == this) {
							linkTo.setLinksTo(null);
						}
					}

					event.component.invalidate();

				}
				event.component.removeTag("connecting");
				return;
			}

			if (event.component.hasTag("connecting")) {
				for (GuiComponent paperComponent : paper.getChildren()) {
					if (paperComponent == event.component) continue;
					if (!paperComponent.geometry.getMouseOverNoOcclusion()) continue;

					if (!(paperComponent instanceof TableModule)) continue;
					TableModule linkTo = (TableModule) paperComponent;

					if (linkTo.getLinksTo() == null) {
						linkTo.setLinksTo(this);
					}

					if (getLinksTo() == linkTo) {
						event.component.removeTag("connecting");
						setLinksTo(null);
						return;
					} else {
						setLinksTo(linkTo);
					}

					event.component.removeTag("connecting");
					return;
				}
			}

			event.component.removeTag("connecting");
		});

		render.getTooltip().func((Function<GuiComponent, List<String>>) t -> {
			List<String> txt = new ArrayList<>();

			if (t.hasTag("connecting")) return txt;

			txt.add(TextFormatting.GOLD + module.getReadableName());
			if (GuiScreen.isShiftKeyDown())
				txt.add(TextFormatting.GRAY + module.getDescription());
			else txt.add(TextFormatting.GRAY + LibrarianLib.PROXY.translate("wizardry.misc.sneak"));
			return txt;
		});
	}

	public static void drawWire(Vec2d start, Vec2d end, Color primary, Color secondary) {
		GlStateManager.pushMatrix();
		GlStateManager.enableBlend();
		GlStateManager.disableCull();
		GlStateManager.blendFunc(GL_SRC_ALPHA, GL_ONE_MINUS_SRC_ALPHA);
		GlStateManager.color(1, 1, 1, 1);
		GlStateManager.translate(0, 0, -1);
		STREAK.bind();
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

	@Override
	public void drawComponent(@NotNull Vec2d mousePos, float partialTicks) {
		super.drawComponent(mousePos, partialTicks);

		Sprite plate;
		plate = selectedModule == this ? PLATE_HIGHLIGHTED : PLATE;
		Vec2d pos = Vec2d.ZERO;

		Vec2d size = new Vec2d(plate.getWidth(), plate.getHeight());
		if (selectedModule == this || (getMouseOver() && !hasTag("connecting"))) {
			size = size.add(6, 6);
			pos = pos.sub(3, 3);
		}

		if (hasTag("connecting")) {
			drawWire(pos.add(size.getX() / 2.0, size.getY() / 2.0), mousePos, getColorForModule(module.getModuleType()), Color.WHITE);
		} else if (linksTo != null) {
			Vec2d posTo = new Vec2d(linksTo.getPos().getX() - (linksTo.getSize().getXi() / 2.0), linksTo.getPos().getY() - (linksTo.getSize().getYi() / 2.0));
			drawWire(pos.add(size.getX() / 2.0, size.getY() / 2.0), posTo.add(size.getX() / 2.0, size.getY() / 2.0), getColorForModule(module.getModuleType()), getColorForModule(linksTo.getModule().getModuleType()));
		}

		plate.bind();
		plate.draw(0, pos.getXi(), pos.getYi(), size.getXi(), size.getYi());

		double shrink = 2;

		icon.bind();
		icon.draw(0,
				(float) ((plate.getWidth() / 2.0) - ((size.getX() - shrink) / 2.0)),
				(float) ((plate.getHeight() / 2.0) - ((size.getY() - shrink) / 2.0)),
				(float) (size.getX() - shrink),
				(float) (size.getYi() - shrink));
	}

	@Nullable
	public TableModule getLinksTo() {
		return linksTo;
	}

	public void setLinksTo(@Nullable TableModule linksTo) {
		this.linksTo = linksTo;
	}

	@Nonnull
	public WorktableGui getWorktable() {
		return worktable;
	}

	public boolean isDraggable() {
		return draggable;
	}

	@Nonnull
	public Module getModule() {
		return module;
	}

	private boolean isCompatibleWith(ModuleType type) {
		switch (getModule().getModuleType()) {
			case SHAPE:
				return true;
			case EVENT:
				return type == EFFECT;
			default:
				return false;
		}
	}

	public Sprite getIcon() {
		return icon;
	}
}
