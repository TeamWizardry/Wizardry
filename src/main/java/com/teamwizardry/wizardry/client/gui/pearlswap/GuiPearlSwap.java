package com.teamwizardry.wizardry.client.gui.pearlswap;

import com.teamwizardry.librarianlib.core.client.ClientTickHandler;
import com.teamwizardry.librarianlib.features.animator.Animator;
import com.teamwizardry.librarianlib.features.animator.Easing;
import com.teamwizardry.librarianlib.features.animator.animations.BasicAnimation;
import com.teamwizardry.librarianlib.features.animator.animations.ScheduledEventAnimation;
import com.teamwizardry.librarianlib.features.gui.GuiBase;
import com.teamwizardry.librarianlib.features.gui.component.GuiComponentEvents;
import com.teamwizardry.librarianlib.features.gui.components.ComponentVoid;
import com.teamwizardry.librarianlib.features.math.Vec2d;
import com.teamwizardry.librarianlib.features.network.PacketHandler;
import com.teamwizardry.librarianlib.features.sprite.Sprite;
import com.teamwizardry.wizardry.Wizardry;
import com.teamwizardry.wizardry.api.ConfigValues;
import com.teamwizardry.wizardry.api.item.BaublesSupport;
import com.teamwizardry.wizardry.api.item.INacreProduct;
import com.teamwizardry.wizardry.api.item.pearlswapping.IPearlStorageHolder;
import com.teamwizardry.wizardry.api.item.pearlswapping.IPearlSwappable;
import com.teamwizardry.wizardry.api.spell.SpellRing;
import com.teamwizardry.wizardry.api.spell.SpellUtils;
import com.teamwizardry.wizardry.api.spell.module.ModuleInstance;
import com.teamwizardry.wizardry.api.util.RandUtil;
import com.teamwizardry.wizardry.client.gui.worktable.TableModule;
import com.teamwizardry.wizardry.common.network.pearlswapping.PacketRemovePearlFromBelt;
import com.teamwizardry.wizardry.common.network.pearlswapping.PacketSuccPearlsToStorageHolder;
import com.teamwizardry.wizardry.common.network.pearlswapping.PacketSwapPearl;
import com.teamwizardry.wizardry.init.ModSounds;
import kotlin.jvm.functions.Function2;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.ScaledResolution;
import net.minecraft.client.renderer.BufferBuilder;
import net.minecraft.client.renderer.GlStateManager;
import net.minecraft.client.renderer.Tessellator;
import net.minecraft.client.renderer.vertex.DefaultVertexFormats;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.ItemStack;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.math.MathHelper;
import net.minecraft.util.math.Vec3d;
import net.minecraftforge.items.IItemHandler;
import org.lwjgl.input.Mouse;
import org.lwjgl.opengl.GL11;

import java.awt.*;
import java.io.IOException;
import java.util.List;

import static java.lang.Math.PI;

public class GuiPearlSwap extends GuiBase {

	/**
	 * Background base for spell component icons.
	 */
	private static final Sprite spritePlate = new Sprite(new ResourceLocation(Wizardry.MODID, "textures/gui/worktable/plate.png"));
	public final float[] slotRadii = new float[ConfigValues.pearlBeltInvSize];
	public final float[] itemDilaters = new float[ConfigValues.pearlBeltInvSize];
	private final int SELECTOR_RADIUS = 90;
	private final int SELECTOR_WIDTH = 50;
	private final int SELECTOR_SHIFT = 5;
	private final float SELECTOR_ALPHA = 0.7F;
	private final Animator ANIMATOR = new Animator();
	private final BasicAnimation[] slotRadiusAnimations = new BasicAnimation[ConfigValues.pearlBeltInvSize];
	private final BasicAnimation[] itemDilationAnimations = new BasicAnimation[ConfigValues.pearlBeltInvSize];
	public double heartBeatRadius = 0.0;
	public double parasolGradientRadius = 0;
	public float color = RandUtil.nextFloat();
	public ComponentVoid componentCentralCircle;
	public float lockTransition = 0f;
	private BasicAnimation<ComponentVoid> centerRadiusAnim;
	private BasicAnimation<GuiPearlSwap> heartBeatAnim = new BasicAnimation<>(this, "heartBeatRadius");
	private BasicAnimation<GuiPearlSwap> colorAnim = new BasicAnimation<>(this, "color");
	private String centerText;
	private boolean lock = false;

	public GuiPearlSwap() {
		super(100, 100);

		ItemStack pearlStorageStack = getStorageHolderStack();
		if (pearlStorageStack.isEmpty()) return;
		IPearlStorageHolder pearlStorage = (IPearlStorageHolder) pearlStorageStack.getItem();

		componentCentralCircle = new ComponentVoid(0, 0, 0, 0);
		getMainComponents().add(componentCentralCircle);

		double centerRad = pearlStorage.getPearlCount(pearlStorageStack) == 0 ? getGuiHeight() : getGuiHeight() * 0.75;
		centerRadiusAnim = (BasicAnimation<ComponentVoid>) new BasicAnimation<>(componentCentralCircle, "size.x").from(0).to(centerRad).ease(Easing.easeOutQuint).duration(20).addTo(ANIMATOR);

		int count = pearlStorage.getPearlCount(pearlStorageStack);
		if (count > 0) {
			new BasicAnimation<>(this, "parasolGradientRadius").to(centerRad * 1.5).ease(Easing.easeOutQuart).duration(15).addTo(ANIMATOR);
			dilateItems();
		}
		ANIMATOR.add(colorAnim, heartBeatAnim);

		updateText(pearlStorageStack, pearlStorage);

		componentCentralCircle.BUS.hook(GuiComponentEvents.ComponentTickEvent.class, event -> {
			if (lock) return;

			ItemStack stack = getStorageHolderStack();
			if (stack.isEmpty()) return;
			IPearlStorageHolder holder = (IPearlStorageHolder) stack.getItem();

			updateText(stack, holder);

			runAnimations(stack);

			double mouseX = Mouse.getEventX() - mc.displayWidth / 2.0;
			double mouseY = (Mouse.getEventY() - mc.displayHeight / 2.0) * -1;

			Vec2d vec = new Vec2d(mouseX, mouseY);
			if (vec.length() <= componentCentralCircle.getSize().getX() * new ScaledResolution(Minecraft.getMinecraft()).getScaleFactor()) {

				unSelectAll();
				return;
			}

			double relativeMX = (Mouse.getEventX() / (double) this.mc.displayWidth) / 0.5 - 1;
			double relativeMY = ((Mouse.getEventY() / (double) this.mc.displayHeight) / 0.5 - 1) * -1;
			if (mc.displayWidth > mc.displayHeight) {
				relativeMY *= this.mc.displayHeight / (double) this.mc.displayWidth;
			} else if (mc.displayHeight > mc.displayWidth) {
				relativeMX *= mc.displayWidth / (double) mc.displayHeight;
			}
			double pointerAngle = (Math.atan2(relativeMY, relativeMX) + (PI * 2)) % (PI * 2.0);

			int pearlCount = Math.max(holder.getPearlCount(stack), 0);
			if (pearlCount != 0) {

				double anglePerColor = 2.0 * PI / (double) pearlCount;
				int pointerIndex = (int) (pointerAngle / anglePerColor);

				select(pointerIndex);
			}
		});

		componentCentralCircle.BUS.hook(GuiComponentEvents.PreDrawEvent.class, event -> {
			Tessellator tess = Tessellator.getInstance();
			BufferBuilder bb = tess.getBuffer();

			ItemStack stack = getStorageHolderStack();
			if (stack.isEmpty()) return;
			IPearlStorageHolder holder = (IPearlStorageHolder) stack.getItem();

			renderCenteralCircle(tess, bb, event.component.getSize().getX());
			renderParasol(tess, bb, stack, holder, event.component.getSize().getX());

		});
	}

	@Override
	protected void mouseClicked(int x, int y, int mouseButton) throws IOException {
		super.mouseClicked(x, y, mouseButton);

		if (mouseButton != 0 || lock) return;

		EntityPlayer player = Minecraft.getMinecraft().player;
		ItemStack heldItem = player.getHeldItemMainhand();

		ItemStack stack = getStorageHolderStack();
		if (stack.isEmpty()) return;
		IPearlStorageHolder holder = (IPearlStorageHolder) stack.getItem();

		IItemHandler handler = holder.getPearls(stack);
		if (handler == null) return;


		double mouseX = Mouse.getEventX() - mc.displayWidth / 2.0;
		double mouseY = (Mouse.getEventY() - mc.displayHeight / 2.0) * -1;

		Vec2d vec = new Vec2d(mouseX, mouseY);
		if (vec.length() <= componentCentralCircle.getSize().getX() * new ScaledResolution(Minecraft.getMinecraft()).getScaleFactor()) {

			if (heldItem.getItem() instanceof IPearlStorageHolder) {
				succPearlsToBelt();
			}

			return;
		}

		double relativeMX = (Mouse.getEventX() / (double) this.mc.displayWidth) / 0.5 - 1;
		double relativeMY = ((Mouse.getEventY() / (double) this.mc.displayHeight) / 0.5 - 1) * -1;
		if (mc.displayWidth > mc.displayHeight) {
			relativeMY *= this.mc.displayHeight / (double) this.mc.displayWidth;
		} else if (mc.displayHeight > mc.displayWidth) {
			relativeMX *= mc.displayWidth / (double) mc.displayHeight;
		}
		double pointerAngle = (Math.atan2(relativeMY, relativeMX) + (PI * 2)) % (PI * 2.0);

		int pearlCount = Math.max(holder.getPearlCount(stack), 0);
		if (pearlCount != 0) {

			double anglePerColor = 2.0 * PI / (double) pearlCount;
			int pointerIndex = (int) (pointerAngle / anglePerColor);

			if (heldItem.getItem() instanceof IPearlStorageHolder) {
				popPearlFromBelt(pointerIndex);
			} else if (heldItem.getItem() instanceof IPearlSwappable) {
				swapPearl(pointerIndex);
			}
		}
	}

	private void updateText(ItemStack pearlStorageStack, IPearlStorageHolder pearlStorage) {
		int count = pearlStorage.getPearlCount(pearlStorageStack);
		if (count > 0) {

			IItemHandler itemHandler = pearlStorage.getPearls(pearlStorageStack);
			if (itemHandler != null)
				if (count >= itemHandler.getSlots()) {
					centerText = "Belt Full\n\nClick any of the\npearls to pop\nthem out of the belt";
				} else {
					centerText = "Click here to attach all\npearls in your inventory\nto this belt\n\nClick any of the\npearls to pop\nthem out of the belt";
				}
		} else {
			centerText = "Click to attach all\npearls in your inventory\nto this belt";
		}
	}

	public void lock() {
		lock = true;
		unSelectAll();
		new BasicAnimation<>(this, "lockTransition").to(1f).ease(Easing.easeOutQuint).duration(20).addTo(ANIMATOR);
	}

	public void unlock() {
		lock = false;
		new BasicAnimation<>(this, "lockTransition").to(0f).ease(Easing.easeOutQuint).duration(20).addTo(ANIMATOR);
	}

	public void update(int originalPearlCount, int newPearlCount, int index) {
		unlock();

		EntityPlayer player = Minecraft.getMinecraft().player;
		player.playSound(ModSounds.POP, 1f, 1f);

		if (originalPearlCount != newPearlCount) {
			if (originalPearlCount == 0) {
				dilateItems();
			} else {
				contractItems();

				if (newPearlCount != 0)
					ANIMATOR.add(new ScheduledEventAnimation(10, this::dilateItems));
			}
		} else if (newPearlCount != 0 && index != -1) {
			contractItem(index);
			ANIMATOR.add(new ScheduledEventAnimation(10, () -> dilateItem(index)));
		}

		double centerRad = newPearlCount == 0 ? getGuiHeight() : getGuiHeight() * 0.75;
		new BasicAnimation<>(this, "parasolGradientRadius").to(centerRad * 1.5).ease(Easing.easeOutQuart).duration(15).addTo(ANIMATOR);
		centerRadiusAnim = (BasicAnimation<ComponentVoid>) new BasicAnimation<>(componentCentralCircle, "size.x").to(centerRad).ease(Easing.easeOutQuint).duration(20).addTo(ANIMATOR);
	}

	private ItemStack getStorageHolderStack() {
		EntityPlayer player = Minecraft.getMinecraft().player;

		ItemStack heldItem = player.getHeldItemMainhand();

		if (heldItem.getItem() instanceof IPearlStorageHolder) {

			return heldItem;

		} else if (heldItem.getItem() instanceof IPearlSwappable) {

			return BaublesSupport.getItem(player, IPearlStorageHolder.class);
		} else return ItemStack.EMPTY;
	}

	private void popPearlFromBelt(int index) {
		PacketHandler.NETWORK.sendToServer(new PacketRemovePearlFromBelt(index));
		lock();
	}

	private void swapPearl(int index) {
		PacketHandler.NETWORK.sendToServer(new PacketSwapPearl(index));
		lock();
	}

	private void succPearlsToBelt() {
		PacketHandler.NETWORK.sendToServer(new PacketSuccPearlsToStorageHolder());
		lock();
	}

	private void unSelectAll() {
		for (int i = 0; i < slotRadiusAnimations.length; i++) unSelect(i);
	}

	private void unSelect(int i) {
		if (slotRadii[i] == 0f) return;

		if (slotRadiusAnimations[i] != null)
			slotRadiusAnimations[i] = null;

		slotRadiusAnimations[i] = (BasicAnimation) new BasicAnimation<>(this, "slotRadii[" + i + "]").to(0f).ease(Easing.easeOutQuint).duration(10).addTo(ANIMATOR);
	}

	private void select(int i) {
		for (int j = 0; j < slotRadiusAnimations.length; j++) {
			if (i != j) unSelect(j);
		}

		if (slotRadii[i] == 1f) return;

		if (slotRadiusAnimations[i] != null)
			slotRadiusAnimations[i] = null;

		slotRadiusAnimations[i] = (BasicAnimation) new BasicAnimation<>(this, "slotRadii[" + i + "]").to(1f).ease(Easing.easeOutQuint).duration(10).addTo(ANIMATOR);
	}

	private void dilateItem(int i) {
		if (itemDilationAnimations[i] != null) {
			itemDilationAnimations[i] = null;
		}
		itemDilationAnimations[i] = (BasicAnimation) new BasicAnimation<>(this, "itemDilaters[" + i + "]").from(0f).to(1f).ease(Easing.easeOutQuint).duration(20).addTo(ANIMATOR);
		unSelect(i);
	}

	private void dilateItems() {
		for (int i = 0; i < itemDilaters.length; i++) {
			if (itemDilationAnimations[i] != null) {
				itemDilationAnimations[i] = null;
			}
			itemDilationAnimations[i] = (BasicAnimation) new BasicAnimation<>(this, "itemDilaters[" + i + "]").to(1f).ease(Easing.easeOutQuint).start(1.5f * (i + 1)).duration(20).addTo(ANIMATOR);
			unSelect(i);
		}
	}

	private void contractItem(int i) {
		if (itemDilationAnimations[i] != null) {
			itemDilationAnimations[i] = null;
		}
		itemDilationAnimations[i] = (BasicAnimation) new BasicAnimation<>(this, "itemDilaters[" + i + "]").from(1f).to(0f).ease(Easing.easeOutQuint).duration(20).addTo(ANIMATOR);
		unSelect(i);
	}

	private void contractItems() {
		for (int i = 0; i < itemDilaters.length; i++) {
			if (itemDilationAnimations[i] != null) {
				itemDilationAnimations[i] = null;
			}
			itemDilationAnimations[i] = (BasicAnimation) new BasicAnimation<>(this, "itemDilaters[" + i + "]").to(0f).ease(Easing.easeOutQuint).duration(20).addTo(ANIMATOR);
			unSelect(i);
		}
	}

	@Override
	public boolean doesGuiPauseGame() {
		return false;
	}

	private void renderParasol(Tessellator tess, BufferBuilder bb, ItemStack holderStack, IPearlStorageHolder holder, double radius) {
		int pearlCount = holder.getPearlCount(holderStack);
		IItemHandler handler = holder.getPearls(holderStack);
		if (pearlCount != 0 && handler != null) {

			double innerRadius = radius + 0.5;
			int numSegmentsPerArc = (int) Math.ceil(360d / pearlCount);
			float anglePerColor = (float) (2 * PI / pearlCount);
			float anglePerSegment = anglePerColor / numSegmentsPerArc;
			float angle = 0;

			for (int j = 0; j < pearlCount; j++) {
				ItemStack pearl = handler.getStackInSlot(j);
				if (!(pearl.getItem() instanceof INacreProduct)) continue;
				INacreProduct product = (INacreProduct) pearl.getItem();
				Function2<ItemStack, Integer, Integer> function = product.getItemColorFunction();
				if (function == null) continue;

				int colorInt = function.invoke(pearl, 0);
				Color color = new Color(colorInt);

				double outerRadius = parasolGradientRadius + (slotRadii[j]);// + (scrollSlot == j ? SELECTOR_SHIFT : 0);

				GlStateManager.pushMatrix();
				int thing1 = GL11.glGetInteger(GL11.GL_ALPHA_TEST_FUNC);
				float thing2 = GL11.glGetFloat(GL11.GL_ALPHA_TEST_REF);

				GlStateManager.shadeModel(GL11.GL_SMOOTH);
				GlStateManager.alphaFunc(GL11.GL_ALWAYS, 1);
				GlStateManager.enableAlpha();
				GlStateManager.enableBlend();
				GlStateManager.disableTexture2D();

				GlStateManager.translate(getGuiWidth() / 2.0, getGuiHeight() / 2.0, 0);

				bb.begin(GL11.GL_QUADS, DefaultVertexFormats.POSITION_COLOR);
				for (int i = 0; i < numSegmentsPerArc; i++) {
					float currentAngle = i * anglePerSegment + angle;
					bb.pos(innerRadius * MathHelper.cos(currentAngle), innerRadius * MathHelper.sin(currentAngle), 0).color(color.getRed(), color.getGreen(), color.getBlue(), (int) (itemDilaters[j] * 255)).endVertex();
					bb.pos(innerRadius * MathHelper.cos(currentAngle + anglePerSegment), innerRadius * MathHelper.sin(currentAngle + anglePerSegment), 0).color(color.getRed(), color.getGreen(), color.getBlue(), (int) (itemDilaters[j] * 255)).endVertex();
					bb.pos(outerRadius * MathHelper.cos(currentAngle + anglePerSegment), outerRadius * MathHelper.sin(currentAngle + anglePerSegment), 0).color(color.getRed(), color.getGreen(), color.getBlue(), 0).endVertex();
					bb.pos(outerRadius * MathHelper.cos(currentAngle), outerRadius * MathHelper.sin(currentAngle), 0).color(color.getRed(), color.getGreen(), color.getBlue(), 0).endVertex();
				}
				tess.draw();

				float centerAngle = angle + anglePerColor / 2;
				Vec3d inner = new Vec3d(innerRadius * MathHelper.cos(centerAngle), innerRadius * MathHelper.sin(centerAngle), 0);
				Vec3d outer = new Vec3d(outerRadius * MathHelper.cos(centerAngle), outerRadius * MathHelper.sin(centerAngle), 0);

				Vec3d center = new Vec3d((inner.x + outer.x) / 2, (inner.y + outer.y) / 2, 0);
				Vec3d normal = center.normalize();

				Vec3d pearlOffset = normal.scale(radius / 2).scale(itemDilaters[j]);
				//	GlStateManager.translate(pearlOffset.x, pearlOffset.y, 0);

				//	RenderUtils.renderItemStackWithOpacity(pearl, itemExpansion, () -> {
				//		GlStateManager.translate(pearlOffset.x - 8, pearlOffset.y - 8, 0);
				//	});
				//	GlStateManager.translate(-pearlOffset.x + 8, -pearlOffset.y + 8, 0);

				GlStateManager.translate(0, 0, 20);
				GlStateManager.color(1f, 1f, 1f, itemDilaters[j]);
				GlStateManager.scale(2, 2, 2);

				GlStateManager.enableTexture2D();

				List<SpellRing> rings = SpellUtils.getSpellChains(pearl);
				float startAngle = angle + 0.25f;
				float endAngle = angle + anglePerColor - 0.25f;
				float anglePerRing = (endAngle - startAngle) / (rings.size() + 1);
				float size = 10;
				for (int i = 0; i < rings.size(); i++) {
					float currentAngle = startAngle + (i + 1) * anglePerRing;
					double x = MathHelper.cos(currentAngle);
					double y = MathHelper.sin(currentAngle);
					Vec3d chainNormal = new Vec3d(x, y, 0).normalize();
					Vec3d chainOffset = chainNormal.scale((radius / 2.0) * itemDilaters[j]).add(chainNormal.scale(size).scale(itemDilaters[j]));

					List<SpellRing> allSpellRings = SpellUtils.getAllSpellRings(rings.get(i));
					for (int k = 0; k < allSpellRings.size(); k++) {
						SpellRing ring = allSpellRings.get(k);
						ModuleInstance module = ring.getModule();
						if (module == null) continue;
						Sprite moduleSprite = new Sprite(module.getIconLocation());

						Vec3d moduleVec = chainOffset.add(chainNormal.scale(size * 1.5).scale(k).scale(itemDilaters[j]));

						spritePlate.bind();
						spritePlate.draw(0, (float) moduleVec.x - (size / 2f), (float) moduleVec.y - (size / 2f), size, size);

						moduleSprite.bind();
						moduleSprite.draw(0, (float) moduleVec.x - ((size - 1) / 2f), (float) moduleVec.y - ((size - 1) / 2f), size - 1, size - 1);

						if (k + 1 < allSpellRings.size()) {
							SpellRing linksTo = allSpellRings.get(k + 1);
							ModuleInstance linksToModule = linksTo.getModule();
							if (linksToModule == null) continue;
							Vec3d linksToVec = chainOffset.add(chainNormal.scale(size * 1.5).scale(k + 1).scale(itemDilaters[j]));

							TableModule.drawWire(
									new Vec2d(moduleVec.x, moduleVec.y),
									new Vec2d(linksToVec.x, linksToVec.y),
									TableModule.getColorForModule(module.getModuleType()),
									TableModule.getColorForModule(linksToModule.getModuleType()));
						}
					}
				}

				GlStateManager.alphaFunc(thing1, thing2);

				GlStateManager.translate(-getGuiWidth() / 2.0, -getGuiHeight() / 2.0, 0);
				GlStateManager.popMatrix();

				angle += anglePerColor;
			}
		}

	}

	private void runAnimations(ItemStack stack) {
		if (colorAnim.getFinished()) {
			colorAnim = new BasicAnimation<>(this, "color");
			colorAnim.setTo(color + 0.25f > 1f ? 0.25f : color + 0.25f);
			colorAnim.setEasing(Easing.linear);
			colorAnim.setDuration(100f);
			ANIMATOR.add(colorAnim);
		}

		if (centerRadiusAnim.getFinished() && heartBeatAnim.getFinished()) {
			heartBeatAnim = new BasicAnimation<>(this, "heartBeatRadius");
			heartBeatAnim.setTo(componentCentralCircle.getSize().getX());
			heartBeatAnim.setFrom(0.0);
			heartBeatAnim.setEasing(Easing.easeOutQuint);
			heartBeatAnim.setDuration(100f);
			ANIMATOR.add(heartBeatAnim);
		}

	}

	private void renderCenteralCircle(Tessellator tess, BufferBuilder bb, double radius) {
		GlStateManager.pushMatrix();
		int thing1 = GL11.glGetInteger(GL11.GL_ALPHA_TEST_FUNC);
		float thing2 = GL11.glGetFloat(GL11.GL_ALPHA_TEST_REF);

		GlStateManager.enableBlend();

		GlStateManager.disableTexture2D();
		GlStateManager.disableCull();
		GlStateManager.alphaFunc(GL11.GL_ALWAYS, 1);
		GlStateManager.translate(getGuiWidth() / 2.0, getGuiHeight() / 2.0, 0);
		GlStateManager.glLineWidth(2);

		double vertexCount = 100.0;
		double x;
		double y;

		Color transitioningColor = new Color(Color.HSBtoRGB(color, 0.25f, 1f));
		Color color = new Color(transitioningColor.getRed() / 255f, transitioningColor.getGreen() / 255f, transitioningColor.getBlue() / 255f, MathHelper.clamp(1f - (float) (heartBeatRadius / radius), 0f, 1f));

		bb.begin(GL11.GL_TRIANGLE_FAN, DefaultVertexFormats.POSITION_COLOR);
		bb.pos(0.0, 0.0, 0.0).color(color.getRed(), color.getGreen(), color.getBlue(), 0).endVertex();
		for (int i = 0; i < vertexCount; i++) {
			double angle = i * 2.0 * PI / vertexCount;

			x = MathHelper.cos((float) angle) * heartBeatRadius;
			y = MathHelper.sin((float) angle) * heartBeatRadius;

			bb.pos(y, x, 0.0).color(color.getRed(), color.getGreen(), color.getBlue(), color.getAlpha()).endVertex();
		}
		x = MathHelper.cos((float) (2.0 * PI)) * heartBeatRadius;
		y = MathHelper.sin((float) (2.0 * PI)) * heartBeatRadius;
		bb.pos(y, x, 0.0).color(color.getRed(), color.getGreen(), color.getBlue(), color.getAlpha()).endVertex();

		tess.draw();

		float[] hsv = new float[3];
		Color.RGBtoHSB(transitioningColor.getRed(), transitioningColor.getGreen(), transitioningColor.getBlue(), hsv);
		hsv[2] *= 1 - lockTransition;
		color = Color.getHSBColor(hsv[0], hsv[1], hsv[2]);
		color = new Color(color.getRed() / 255f, color.getGreen() / 255f, color.getBlue() / 255f, 0.5f);

		bb.begin(GL11.GL_TRIANGLE_FAN, DefaultVertexFormats.POSITION_COLOR);
		bb.pos(0.0, 0.0, 0.0).color(color.getRed(), color.getGreen(), color.getBlue(), color.getAlpha()).endVertex();
		for (int i = 0; i < vertexCount; i++) {
			double angle = i * 2.0 * PI / vertexCount;

			x = MathHelper.cos((float) angle) * radius;
			y = MathHelper.sin((float) angle) * radius;

			bb.pos(y, x, 0.0).color(color.getRed(), color.getGreen(), color.getBlue(), color.getAlpha()).endVertex();
		}
		x = MathHelper.cos((float) (2.0 * PI)) * radius;
		y = MathHelper.sin((float) (2.0 * PI)) * radius;
		bb.pos(y, x, 0.0).color(color.getRed(), color.getGreen(), color.getBlue(), color.getAlpha()).endVertex();

		tess.draw();

		// OUTER CIRCLE;
		color = Color.DARK_GRAY;
		bb.begin(GL11.GL_LINE_STRIP, DefaultVertexFormats.POSITION_COLOR);
		//	bb.pos(centerX, centerY, 0.0).color(color.getRed(), color.getGreen(), color.getBlue(), 255).endVertex();
		for (int i = 0; i < vertexCount; i++) {
			double angle = i * 2.0 * PI / vertexCount;

			x = MathHelper.cos((float) angle) * radius;
			y = MathHelper.sin((float) angle) * radius;

			bb.pos(y, x, 0.0).color(color.getRed(), color.getGreen(), color.getBlue(), 255).endVertex();
		}
		x = MathHelper.cos((float) (2.0 * PI)) * radius;
		y = MathHelper.sin((float) (2.0 * PI)) * radius;
		bb.pos(y, x, 0.0).color(color.getRed(), color.getGreen(), color.getBlue(), 255).endVertex();

		tess.draw();

		GlStateManager.alphaFunc(thing1, thing2);
		GlStateManager.translate(-getGuiWidth() / 2.0, -getGuiHeight() / 2.0, 0);
		GlStateManager.popMatrix();

		renderText(centerText);
	}

	private int getTextY(int lineCount, int lineIndex) {
		int fontH = Minecraft.getMinecraft().fontRenderer.FONT_HEIGHT;
		return (int) (-fontH * (lineCount / 2.0) + lineIndex * fontH);
	}

	private void renderText(String string) {
		GlStateManager.pushMatrix();
		GlStateManager.enableTexture2D();

		GlStateManager.translate(getGuiWidth() / 2.0, getGuiHeight() / 2.0, 0);

		String[] split = string.split("\n");
		for (int i = 0; i < split.length; i++) {
			String text = split[i];
			Minecraft.getMinecraft().fontRenderer.drawString(
					text,
					(int) (-Minecraft.getMinecraft().fontRenderer.getStringWidth(text) / 2.0),
					getTextY(split.length, i),
					0x000000);
		}

		float rot1 = MathHelper.sin(ClientTickHandler.getTicks() / 50f) * 5;
		GlStateManager.rotate(rot1, 0, 0, 1f * ClientTickHandler.getPartialTicks());
		for (int i = 0; i < split.length; i++) {
			String text = split[i];
			Minecraft.getMinecraft().fontRenderer.drawString(
					text,
					(int) (-Minecraft.getMinecraft().fontRenderer.getStringWidth(text) / 2.0),
					getTextY(split.length, i),
					0x2b000000);
		}
		GlStateManager.rotate(-rot1, 0, 0, 1f * ClientTickHandler.getPartialTicks());

		float rot2 = MathHelper.sin(ClientTickHandler.getTicks() / 20f) * 6;
		GlStateManager.rotate(rot2, 0, 0, 1f * ClientTickHandler.getPartialTicks());
		for (int i = 0; i < split.length; i++) {
			String text = split[i];
			Minecraft.getMinecraft().fontRenderer.drawString(
					text,
					(int) (-Minecraft.getMinecraft().fontRenderer.getStringWidth(text) / 2.0),
					getTextY(split.length, i),
					0x21000000);
		}
		GlStateManager.rotate(-rot2, 0, 0, 1f * ClientTickHandler.getPartialTicks());
		GlStateManager.translate(-getGuiWidth() / 2.0, -getGuiHeight() / 2.0, 0);

		GlStateManager.popMatrix();
	}
}
