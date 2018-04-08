package com.teamwizardry.wizardry.client.gui.worktable2;

import com.teamwizardry.librarianlib.core.LibrarianLib;
import com.teamwizardry.librarianlib.core.client.ClientTickHandler;
import com.teamwizardry.librarianlib.features.animator.Easing;
import com.teamwizardry.librarianlib.features.animator.animations.BasicAnimation;
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
import com.teamwizardry.wizardry.api.spell.module.ModuleModifier;
import com.teamwizardry.wizardry.api.spell.module.ModuleRegistry;
import com.teamwizardry.wizardry.api.spell.module.ModuleType;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.FontRenderer;
import net.minecraft.client.gui.GuiScreen;
import net.minecraft.client.renderer.BufferBuilder;
import net.minecraft.client.renderer.GlStateManager;
import net.minecraft.client.renderer.Tessellator;
import net.minecraft.client.renderer.vertex.DefaultVertexFormats;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.math.MathHelper;
import net.minecraft.util.text.TextFormatting;
import org.jetbrains.annotations.NotNull;
import org.lwjgl.opengl.GL11;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import java.awt.*;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.UUID;
import java.util.function.Function;

import static com.teamwizardry.wizardry.client.gui.worktable2.WorktableGui.*;
import static org.lwjgl.opengl.GL11.GL_ONE_MINUS_SRC_ALPHA;
import static org.lwjgl.opengl.GL11.GL_SRC_ALPHA;

public class TableModule extends GuiComponent {

	@Nonnull
	private final WorktableGui worktable;
	@Nonnull
	private final Module module;
	private final boolean draggable;
	private final Sprite icon;
	private final boolean benign;
	@Nullable
	private TableModule linksTo = null;
	private boolean enableTooltip;
	/**
	 * ALWAYS from the context of null. Never to any other component.
	 */
	private Vec2d initialPos;

	public float radius = 16, textRadius = 10;

	//public float size = 16;
	//public final float originalSize = 16;

	public TableModule(@Nonnull WorktableGui worktable, @Nonnull Module module, boolean draggable, boolean benign) {
		super(0, 0, PLATE.getWidth(), PLATE.getHeight());
		this.worktable = worktable;
		this.module = module;
		this.draggable = draggable;
		icon = new Sprite(new ResourceLocation(Wizardry.MODID, "textures/gui/worktable/icons/" + module.getID() + ".png"));
		this.benign = enableTooltip = benign;

		initialPos = thisPosToOtherContext(null);

		ComponentVoid paper = worktable.paper;

		if (draggable) getTransform().setTranslateZ(30);

		if (!benign && draggable) {
			setData(UUID.class, "uuid", UUID.randomUUID());
		}

		if (!benign && !draggable)
			BUS.hook(GuiComponentEvents.MouseDownEvent.class, (event) -> {
				if (worktable.animationPlaying) return;
				if (event.getButton() == EnumMouseButton.LEFT && getMouseOver()) {
					TableModule item = new TableModule(this.worktable, this.module, true, false);
					item.setPos(paper.otherPosToThisContext(event.component, event.getMousePos()));
					DragMixin drag = new DragMixin(item, vec2d -> vec2d);
					drag.setDragOffset(new Vec2d(6, 6));
					drag.setMouseDown(event.getButton());
					paper.add(item);

					event.cancel();
				}
			});

		if (!benign)
			BUS.hook(DragMixin.DragPickupEvent.class, (event) -> {
				if (worktable.animationPlaying) return;
				if (!getMouseOver()) return;
				initialPos = event.component.thisPosToOtherContext(null);
				if (event.getButton() == EnumMouseButton.RIGHT) {
					event.component.addTag("connecting");
				}
			});

		if (!benign)
			BUS.hook(DragMixin.DragMoveEvent.class, (event) -> {
				if (worktable.animationPlaying || event.getButton() == EnumMouseButton.RIGHT) {
					// event.getPos returns the before-moving position. Setting it back to it's place.
					// This allows the component to stay where it is while also allowing us to draw a line
					// outside of it's box
					event.setNewPos(event.getPos());
				}
			});

		if (!benign)
			BUS.hook(DragMixin.DragDropEvent.class, (event) -> {
				if (worktable.animationPlaying) return;

				if (!event.component.hasTag("placed")) event.component.addTag("placed");

				Vec2d currentPos = event.component.thisPosToOtherContext(null);
				if (event.getButton() == EnumMouseButton.LEFT && initialPos.equals(currentPos)) {

					if (worktable.selectedModule == this) {
						worktable.selectedModule = null;

						Vec2d toSize = new Vec2d(20, 20);
						BasicAnimation<TableModule> animSize = new BasicAnimation<>(this, "size");
						animSize.setDuration(5);
						animSize.setEasing(Easing.easeOutCubic);
						animSize.setTo(toSize);
						add(animSize);

						BasicAnimation<TableModule> animPos = new BasicAnimation<>(this, "pos");
						animPos.setDuration(5);
						animPos.setEasing(Easing.easeOutCubic);
						animPos.setTo(getPos().add((getSize().sub(toSize)).mul(0.5f)));
						add(animPos);

						BasicAnimation<TableModule> animRadius = new BasicAnimation<>(this, "radius");
						animRadius.setDuration(20);
						animRadius.setEasing(Easing.easeOutCubic);
						animRadius.setTo(16);
						add(animRadius);

						BasicAnimation<TableModule> animText = new BasicAnimation<>(this, "textRadius");
						animText.setDuration(40);
						animText.setEasing(Easing.easeOutCubic);
						animText.setTo(30);
						add(animText);

					} else {
						if (worktable.selectedModule != null) {
							Vec2d toSize = new Vec2d(16, 16);
							BasicAnimation<TableModule> animSize = new BasicAnimation<>(worktable.selectedModule, "size");
							animSize.setDuration(5);
							animSize.setEasing(Easing.easeOutCubic);
							animSize.setTo(toSize);
							worktable.selectedModule.add(animSize);

							BasicAnimation<TableModule> animPos = new BasicAnimation<>(worktable.selectedModule, "pos");
							animPos.setDuration(5);
							animPos.setEasing(Easing.easeOutCubic);
							animPos.setTo(worktable.selectedModule.getPos().add((worktable.selectedModule.getSize().sub(toSize)).mul(0.5f)));
							worktable.selectedModule.add(animPos);

							BasicAnimation<TableModule> animRadius = new BasicAnimation<>(worktable.selectedModule, "radius");
							animRadius.setDuration(5);
							animRadius.setEasing(Easing.easeOutCubic);
							animRadius.setTo(10);
							worktable.selectedModule.add(animRadius);

							BasicAnimation<TableModule> animText2 = new BasicAnimation<>(worktable.selectedModule, "textRadius");
							animText2.setDuration(40);
							animText2.setEasing(Easing.easeOutCubic);
							animText2.setTo(5);
							worktable.selectedModule.add(animText2);

							BasicAnimation<TableModule> animText = new BasicAnimation<>(this, "textRadius");
							animText.setDuration(40);
							animText.setEasing(Easing.easeOutCubic);
							animText.setTo(0);
							add(animText);
						}

						worktable.selectedModule = this;

						Vec2d toSize = new Vec2d(24, 24);
						BasicAnimation<TableModule> animSize = new BasicAnimation<>(this, "size");
						animSize.setDuration(5);
						animSize.setEasing(Easing.easeOutCubic);
						animSize.setTo(toSize);
						add(animSize);

						BasicAnimation<TableModule> animPos = new BasicAnimation<>(this, "pos");
						animPos.setDuration(5);
						animPos.setEasing(Easing.easeOutCubic);
						animPos.setTo(getPos().add((getSize().sub(toSize)).mul(0.5f)));
						add(animPos);

						BasicAnimation<TableModule> animRadius = new BasicAnimation<>(this, "radius");
						animRadius.setDuration(20);
						animRadius.setEasing(Easing.easeOutCubic);
						animRadius.setTo(24);
						add(animRadius);

						BasicAnimation<TableModule> animText = new BasicAnimation<>(this, "textRadius");
						animText.setDuration(40);
						animText.setEasing(Easing.easeOutCubic);
						animText.setTo(40);
						add(animText);
					}

					worktable.modifiers.refresh();

					event.component.removeTag("connecting");
					return;
				}

				Vec2d plateSize = paper.getSize();
				Vec2d platePos = event.component.getPos();
				boolean isInsidePaper = platePos.getX() >= 0 && platePos.getX() <= plateSize.getX() && platePos.getY() >= 0 && platePos.getY() <= plateSize.getY();

				if (!isInsidePaper) {
					if (!event.component.hasTag("connecting")) {

						for (GuiComponent paperComponent : paper.getChildren()) {
							if (paperComponent == event.component) continue;

							if (!(paperComponent instanceof TableModule)) continue;
							TableModule linkTo = (TableModule) paperComponent;

							if (linkTo.getLinksTo() == this) {
								linkTo.setLinksTo(null);
							}
						}

						if (worktable.selectedModule == this) worktable.selectedModule = null;

						event.component.invalidate();

						if (event.component.hasTag("placed"))
							worktable.setToastMessage("", Color.GREEN);

						worktable.modifiers.refresh();
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
						if (!linkTo.draggable) continue;
						if (linkTo == this) continue;

						if (checkSafety(paper)) {
							if (getLinksTo() == linkTo) {
								event.component.removeTag("connecting");
								setLinksTo(null);
								worktable.setToastMessage("", Color.GREEN);
								return;
							} else if (isCompatibleWith()) {
								setLinksTo(linkTo);

								boolean linkedToSelf = false;
								if (linkTo.getLinksTo() == this) {
									linkedToSelf = true;
									linkTo.setLinksTo(null);
								}

								if (checkSafety(paper)) {
									worktable.setToastMessage("", Color.GREEN);
								} else {
									setLinksTo(null);

									if (linkedToSelf) {
										linkTo.setLinksTo(this);
									}

									worktable.setToastMessage("You can't create a loop!", Color.RED);
								}
							}
						} else {
							worktable.setToastMessage("There's a loop somewhere! A spell should start from somewhere and not make an infinite cycle.", Color.RED);
						}

						event.component.removeTag("connecting");
						return;
					}
				}

				event.component.removeTag("connecting");
			});

		if (!benign || enableTooltip)
			render.getTooltip().func((Function<GuiComponent, List<String>>) t -> {
				List<String> txt = new ArrayList<>();

				if (worktable.animationPlaying) return txt;
				if (t.hasTag("connecting")) return txt;

				txt.add(TextFormatting.GOLD + module.getReadableName());
				if (GuiScreen.isShiftKeyDown()) {
					txt.add(TextFormatting.GRAY + module.getDescription());
					if (module.getAttributeRanges().keySet().stream().anyMatch(attribute -> attribute.hasDetailedText()))
						if (GuiScreen.isCtrlKeyDown())
							module.getDetailedInfo().forEach(info -> txt.add(TextFormatting.GRAY + info));
						else txt.add(TextFormatting.GRAY + LibrarianLib.PROXY.translate("wizardry.misc.ctrl"));
				} else txt.add(TextFormatting.GRAY + LibrarianLib.PROXY.translate("wizardry.misc.sneak"));
				return txt;
			});

		if (!benign)
			BUS.hook(GuiComponentEvents.MouseInEvent.class, event -> {
				if (worktable.animationPlaying) return;
				if (worktable.selectedModule == this) return;
				Vec2d toSize = new Vec2d(20, 20);
				BasicAnimation<TableModule> animSize = new BasicAnimation<>(this, "size");
				animSize.setDuration(5);
				animSize.setEasing(Easing.easeOutCubic);
				animSize.setTo(toSize);
				add(animSize);

				BasicAnimation<TableModule> animPos = new BasicAnimation<>(this, "pos");
				animPos.setDuration(5);
				animPos.setEasing(Easing.easeOutCubic);
				animPos.setTo(getPos().add((getSize().sub(toSize)).mul(0.5f)));
				add(animPos);

				BasicAnimation<TableModule> animRadius = new BasicAnimation<>(this, "radius");
				animRadius.setDuration(20);
				animRadius.setEasing(Easing.easeOutCubic);
				animRadius.setTo(16);
				add(animRadius);

				BasicAnimation<TableModule> animText = new BasicAnimation<>(this, "textRadius");
				animText.setDuration(40);
				animText.setEasing(Easing.easeOutCubic);
				animText.setTo(30);
				add(animText);
			});

		if (!benign)
			BUS.hook(GuiComponentEvents.MouseOutEvent.class, event -> {
				if (worktable.animationPlaying) return;
				if (worktable.selectedModule == this) return;
				Vec2d toSize = new Vec2d(16, 16);
				BasicAnimation<TableModule> animSize = new BasicAnimation<>(this, "size");
				animSize.setDuration(5);
				animSize.setEasing(Easing.easeOutCubic);
				animSize.setTo(toSize);
				add(animSize);

				BasicAnimation<TableModule> animPos = new BasicAnimation<>(this, "pos");
				animPos.setDuration(5);
				animPos.setEasing(Easing.easeOutCubic);
				animPos.setTo(getPos().add((getSize().sub(toSize)).mul(0.5f)));
				add(animPos);

				BasicAnimation<TableModule> animRadius = new BasicAnimation<>(this, "radius");
				animRadius.setDuration(20);
				animRadius.setEasing(Easing.easeOutCubic);
				animRadius.setTo(10);
				add(animRadius);

				BasicAnimation<TableModule> animText = new BasicAnimation<>(this, "textRadius");
				animText.setDuration(40);
				animText.setEasing(Easing.easeOutCubic);
				animText.setTo(0);
				add(animText);

			});
	}

	public static void drawWire(Vec2d start, Vec2d end, Color primary, Color secondary) {
		GlStateManager.pushMatrix();
		GlStateManager.enableBlend();
		GlStateManager.disableCull();
		GlStateManager.blendFunc(GL_SRC_ALPHA, GL_ONE_MINUS_SRC_ALPHA);
		GlStateManager.color(1, 1, 1, 1);
		GlStateManager.translate(0, 0, -10);
		STREAK.bind();
		InterpBezier2D bezier = new InterpBezier2D(start, end);
		List<Vec2d> list = bezier.list(50);

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

		GlStateManager.translate(0, 0, 10);
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
		GlStateManager.color(1f, 1f, 1f, 1f);
		GlStateManager.enableAlpha();
		GlStateManager.enableTexture2D();

		Sprite plate;
		plate = worktable.selectedModule == this ? PLATE_HIGHLIGHTED : PLATE;
		Vec2d pos = Vec2d.ZERO;

		GlStateManager.translate(0, 0, -20);
		if (hasTag("connecting")) {
			drawWire(pos.add(getSize().getX() / 2.0, getSize().getY() / 2.0), mousePos, getColorForModule(module.getModuleType()), Color.WHITE);
		}
		if (linksTo != null) {
			Vec2d posContext = linksTo.thisPosToOtherContext(this);
			Vec2d posTo = new Vec2d(posContext.getX(), posContext.getY());
			drawWire(pos.add(getSize().getX() / 2.0, getSize().getY() / 2.0), posTo.add(getSize().getX() / 2.0, getSize().getY() / 2.0), getColorForModule(module.getModuleType()), getColorForModule(linksTo.getModule().getModuleType()));
		}

		GlStateManager.translate(0, 0, 20);

		if (worktable.selectedModule == this || (!benign && !worktable.animationPlaying && getMouseOver() && !hasTag("connecting"))) {
			GlStateManager.translate(0, 0, 80);
		}

		plate.bind();
		plate.draw(0, 0, 0, getSize().getXf(), getSize().getYf());

		float shrink = 4;

		icon.bind();
		icon.draw(0, shrink / 2.0f, shrink / 2.0f, getSize().getXf() - shrink, getSize().getYf() - shrink);

		HashMap<ModuleModifier, Integer> modifiers = new HashMap<>();
		List<ModuleModifier> modifierList = new ArrayList<>();
		for (Module module : ModuleRegistry.INSTANCE.getModules(ModuleType.MODIFIER)) {
			if (!(module instanceof ModuleModifier)) continue;
			if (!hasData(Integer.class, module.getID())) continue;

			modifiers.put((ModuleModifier) module, getData(Integer.class, module.getID()));
			modifierList.add((ModuleModifier) module);
		}

		int count = modifierList.size();
		for (int i = 0; i < count; i++) {

			ModuleModifier modifier = modifierList.get(i);

			Vec2d modSize = getSize().mul(0.75f);

			float angle = (float) (i * Math.PI * 2.0 / count);

			// RENDER PLATE
			{
				float x = (getSize().getXf() / 2f - modSize.getXf() / 2f) + MathHelper.cos(angle) * radius;
				float y = (getSize().getYf() / 2f - modSize.getYf() / 2f) + MathHelper.sin(angle) * radius;

				GlStateManager.pushMatrix();
				GlStateManager.translate(x, y, -10);

				plate.bind();
				plate.draw(0, 0, 0, modSize.getXf(), modSize.getYf());

				float modShrink = 4;

				Sprite modICon = new Sprite(new ResourceLocation(Wizardry.MODID, "textures/gui/worktable/icons/" + modifier.getID() + ".png"));
				modICon.bind();
				modICon.draw(0, modShrink / 2.0f, modShrink / 2.0f, modSize.getXf() - modShrink, modSize.getYf() - modShrink);

				GlStateManager.translate(-x, -y, 10);
				GlStateManager.popMatrix();
			}

			// RENDER TEXT
			{
				FontRenderer font = Minecraft.getMinecraft().fontRenderer;
				String txt = "x" + modifiers.get(modifier);
				float txtWidth = font.getStringWidth(txt);
				float txtHeight = font.FONT_HEIGHT;

				float x = (getSize().getXf() / 2f - txtWidth / 2f) + MathHelper.cos(angle) * textRadius;
				float y = (getSize().getYf() / 2f - txtHeight / 2f) + MathHelper.sin(angle) * textRadius;

				GlStateManager.pushMatrix();
				GlStateManager.translate(x, y, -15);

				font.drawString(txt, 0, 0, 0x000000);
				GlStateManager.color(1f, 1f, 1f, 1f);

				GlStateManager.translate(-x, -y, 15);
				GlStateManager.popMatrix();
			}
		}

		if (worktable.selectedModule == this || (!benign && !worktable.animationPlaying && getMouseOver() && !hasTag("connecting"))) {
			GlStateManager.translate(0, 0, -80);
		}
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

	/**
	 * safe means that there's at least one module
	 * that has a link to something but isn't being linked by something
	 * IE: A spell chain head.
	 */
	private boolean checkSafety(GuiComponent paper) {
		for (GuiComponent child : paper.getChildren()) {
			if (child == this) continue;
			if (!(child instanceof TableModule)) continue;
			TableModule childModule = (TableModule) child;

			boolean linkedFromSomewhere = false;
			if (childModule.getLinksTo() != null) {
				for (GuiComponent subChild : paper.getChildren()) {
					if (subChild == child) continue;
					if (!(subChild instanceof TableModule)) continue;
					TableModule subChildModule = (TableModule) subChild;

					if (childModule.getLinksTo() == subChildModule) continue;

					if (subChildModule.getLinksTo() == childModule) {
						linkedFromSomewhere = true;
						break;
					}
				}
			}

			if (!linkedFromSomewhere) {
				return true;
			}
		}
		return false;
	}

	private boolean isCompatibleWith() {
		String bold = TextFormatting.BOLD.toString();
		String reset = TextFormatting.RESET.toString();
		switch (getModule().getModuleType()) {
			case SHAPE:
				return true;
			case EVENT:
				return true;
			default: {
				worktable.setToastMessage("These pieces don't work together.\n\nAn " + bold + "Effect" + reset + " module cannot link to a module. They can only be linked from other modules.", Color.RED);
				return false;
			}
		}
	}

	public Sprite getIcon() {
		return icon;
	}

	public boolean isEnableTooltip() {
		return enableTooltip;
	}

	public void setEnableTooltip(boolean enableTooltip) {
		this.enableTooltip = enableTooltip;
	}
}
