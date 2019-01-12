package com.teamwizardry.wizardry.client.core.renderer;

import com.teamwizardry.librarianlib.core.client.ClientTickHandler;
import com.teamwizardry.librarianlib.features.animator.Animator;
import com.teamwizardry.librarianlib.features.animator.Easing;
import com.teamwizardry.librarianlib.features.animator.animations.BasicAnimation;
import com.teamwizardry.librarianlib.features.helpers.ItemNBTHelper;
import com.teamwizardry.librarianlib.features.network.PacketHandler;
import com.teamwizardry.wizardry.api.ConfigValues;
import com.teamwizardry.wizardry.api.item.BaublesSupport;
import com.teamwizardry.wizardry.api.item.INacreProduct;
import com.teamwizardry.wizardry.api.util.RandUtil;
import com.teamwizardry.wizardry.common.item.pearlbelt.IPearlBelt;
import com.teamwizardry.wizardry.common.network.belt.PacketSetBeltScrollSlotServer;
import com.teamwizardry.wizardry.init.ModItems;
import com.teamwizardry.wizardry.init.ModKeybinds;
import kotlin.jvm.functions.Function2;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.ScaledResolution;
import net.minecraft.client.renderer.BufferBuilder;
import net.minecraft.client.renderer.GlStateManager;
import net.minecraft.client.renderer.RenderHelper;
import net.minecraft.client.renderer.Tessellator;
import net.minecraft.client.renderer.vertex.DefaultVertexFormats;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.ItemStack;
import net.minecraft.util.math.MathHelper;
import net.minecraft.util.math.Vec3d;
import net.minecraftforge.client.event.MouseEvent;
import net.minecraftforge.client.event.RenderGameOverlayEvent;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;
import net.minecraftforge.items.IItemHandler;
import org.lwjgl.input.Keyboard;
import org.lwjgl.opengl.GL11;

import java.awt.*;
import java.util.ArrayList;
import java.util.List;

@SuppressWarnings("Duplicates")
@SideOnly(Side.CLIENT)
@Mod.EventBusSubscriber(Side.CLIENT)
public class PearlRadialUIRenderer {

	private final static int SELECTOR_RADIUS = 90;
	private final static int SELECTOR_WIDTH = 50;
	public static PearlRadialUIRenderer INSTANCE = new PearlRadialUIRenderer();
	private final static int SELECTOR_SHIFT = 5;
	private final static float SELECTOR_ALPHA = 0.7F;
	public final float[] slotRadii = new float[ConfigValues.pearlBeltInvSize];
	private final BasicAnimation[] slotAnimations = new BasicAnimation[ConfigValues.pearlBeltInvSize];
	public double suckPearlHeartBeatRadius = 0.0; // Yes I know it's verbose, shut up.
	public BasicAnimation suckPearlHeartBeatAnim = null;
	public BasicAnimation radiusAnim = null;
	public double pearlGradientRadius = 0;
	public float color;
	public double pearlsCenterRadius = SELECTOR_RADIUS - SELECTOR_WIDTH / 2.0 - 0.5;

	private boolean wasEmpty = true;

	private final Animator ANIMATOR = new Animator();

	private PearlRadialUIRenderer() {
		this.color = RandUtil.nextFloat();
	}

	private static int getScrollSlot(MouseEvent event, int count, int scrollSlot) {
		if (event.getDwheel() < 0) {
			if (scrollSlot == count) scrollSlot = 0;
			else scrollSlot = MathHelper.clamp(scrollSlot + 1, 0, count);
		} else {
			if (scrollSlot == 0) scrollSlot = count;
			else scrollSlot = MathHelper.clamp(scrollSlot - 1, 0, count);
		}
		return scrollSlot;
	}

	@SubscribeEvent
	public static void onScroll(MouseEvent event) {
		EntityPlayer player = Minecraft.getMinecraft().player;
		if (player == null) return;

		if (Keyboard.isCreated() && ModKeybinds.pearlSwapping.isKeyDown() && event.getDwheel() != 0) {

			ItemStack stack = player.getHeldItemMainhand();

			if (stack.getItem() == ModItems.PEARL_BELT) {

				IPearlBelt belt = (IPearlBelt) stack.getItem();
				int rawCount = belt.getPearlCount(stack);
				int count = Math.max(rawCount - 1, 0);

				int scrollSlot = ItemNBTHelper.getInt(stack, "scroll_slot", -1);

				scrollSlot = getScrollSlot(event, count, scrollSlot);

				if (scrollSlot >= 0) {
					ItemNBTHelper.setInt(stack, "scroll_slot", scrollSlot);
					PacketHandler.NETWORK.sendToServer(new PacketSetBeltScrollSlotServer(player.inventory.getSlotFor(stack), scrollSlot));

					for (int i = 0; i < INSTANCE.slotAnimations.length; i++) {
						BasicAnimation animation = INSTANCE.slotAnimations[i];
						if (animation != null)
							INSTANCE.ANIMATOR.removeAnimations(animation);

						if (i == scrollSlot) continue;
						BasicAnimation<PearlRadialUIRenderer> newAnimation = new BasicAnimation<>(INSTANCE, "slotRadii[" + i + "]");
						newAnimation.setTo(0);
						newAnimation.setEasing(Easing.easeOutQuint);
						newAnimation.setDuration(20f);
						INSTANCE.ANIMATOR.add(newAnimation);

						INSTANCE.slotAnimations[i] = newAnimation;
					}

					BasicAnimation<PearlRadialUIRenderer> animation = new BasicAnimation<>(INSTANCE, "slotRadii[" + scrollSlot + "]");
					animation.setTo(SELECTOR_SHIFT * 10);
					animation.setEasing(Easing.easeOutQuint);
					animation.setDuration(20f);
					INSTANCE.ANIMATOR.add(animation);

					INSTANCE.slotAnimations[scrollSlot] = animation;

					event.setCanceled(true);
				}

			} else if (stack.getItem() == ModItems.STAFF) {
				ItemStack beltStack = BaublesSupport.getItem(player, ModItems.PEARL_BELT);
				if (beltStack.isEmpty()) return;

				IPearlBelt belt = (IPearlBelt) beltStack.getItem();

				int count = belt.getPearlCount(beltStack) - 1;
				if (count == 0) return;

				int scrollSlot = ItemNBTHelper.getInt(stack, "scroll_slot", -1);
				int lastSlot = scrollSlot;

				scrollSlot = getScrollSlot(event, count, scrollSlot);

				if ((count == 1 || lastSlot != scrollSlot) && scrollSlot >= 0) {
					ItemNBTHelper.setInt(stack, "scroll_slot", scrollSlot);

					event.setCanceled(true);
				}
			}
		}
	}

	@SubscribeEvent
	public static void renderHud(RenderGameOverlayEvent.Pre event) {
		//ModKeybinds.pearlSwapping.isKeyDown() &&
		if (event.getType() == RenderGameOverlayEvent.ElementType.CROSSHAIRS) {
			event.setCanceled(true);
			EntityPlayer player = Minecraft.getMinecraft().player;
			ItemStack stack = player.getHeldItemMainhand();

			IPearlBelt belt = null;
			if (stack.getItem() == ModItems.PEARL_BELT) {

				belt = (IPearlBelt) stack.getItem();

			} else if (stack.getItem() == ModItems.STAFF) {
				ItemStack beltStack = BaublesSupport.getItem(player, ModItems.PEARL_BELT);
				if (beltStack.isEmpty()) return;

				belt = (IPearlBelt) beltStack.getItem();
			}
			if (belt == null) return;

			IItemHandler handler = belt.getPearls(stack);
			if (handler == null) return;

			ScaledResolution resolution = event.getResolution();
			int width = resolution.getScaledWidth();
			int height = resolution.getScaledHeight();

			List<ItemStack> pearls = new ArrayList<>();
			for (int i = 0; i < handler.getSlots(); i++) {
				ItemStack pearl = handler.getStackInSlot(i);
				if (pearl.isEmpty()) continue;
				pearls.add(pearl);
			}

			Tessellator tess = Tessellator.getInstance();
			BufferBuilder bb = tess.getBuffer();

			// CIRCLE
			{
				GlStateManager.pushMatrix();
				int thing1 = GL11.glGetInteger(GL11.GL_ALPHA_TEST_FUNC);
				float thing2 = GL11.glGetFloat(GL11.GL_ALPHA_TEST_REF);

				GlStateManager.enableBlend();
				GlStateManager.shadeModel(GL11.GL_SMOOTH);
				GlStateManager.disableTexture2D();
				GlStateManager.disableCull();
				GlStateManager.alphaFunc(GL11.GL_ALWAYS, 1);
				GlStateManager.translate(width / 2.0, height / 2.0, 0);
				GlStateManager.glLineWidth(2);

				double vertexCount = 100.0;
				double x;
				double y;

				Color transitioningColor = new Color(Color.HSBtoRGB(INSTANCE.color, 0.25f, 1f));
				Color color = new Color(transitioningColor.getRed() / 255f, transitioningColor.getBlue() / 255f, transitioningColor.getGreen() / 255f, MathHelper.clamp(1f - (float) (INSTANCE.suckPearlHeartBeatRadius / INSTANCE.pearlsCenterRadius), 0f, 1f));
				bb.begin(GL11.GL_TRIANGLE_FAN, DefaultVertexFormats.POSITION_COLOR);
				bb.pos(0.0, 0.0, 0.0).color(color.getRed(), color.getGreen(), color.getBlue(), 0).endVertex();
				for (int i = 0; i < vertexCount; i++) {
					double angle = i * 2.0 * Math.PI / vertexCount;

					x = MathHelper.cos((float) angle) * INSTANCE.suckPearlHeartBeatRadius;
					y = MathHelper.sin((float) angle) * INSTANCE.suckPearlHeartBeatRadius;

					bb.pos(y, x, 0.0).color(color.getRed(), color.getGreen(), color.getBlue(), color.getAlpha()).endVertex();
				}
				x = MathHelper.cos((float) (2.0 * Math.PI)) * INSTANCE.suckPearlHeartBeatRadius;
				y = MathHelper.sin((float) (2.0 * Math.PI)) * INSTANCE.suckPearlHeartBeatRadius;
				bb.pos(y, x, 0.0).color(color.getRed(), color.getGreen(), color.getBlue(), color.getAlpha()).endVertex();

				tess.draw();

				color = new Color(transitioningColor.getRed() / 255f, transitioningColor.getBlue() / 255f, transitioningColor.getGreen() / 255f, 0.5f);
				bb.begin(GL11.GL_TRIANGLE_FAN, DefaultVertexFormats.POSITION_COLOR);
				bb.pos(0.0, 0.0, 0.0).color(color.getRed(), color.getGreen(), color.getBlue(), color.getAlpha()).endVertex();
				for (int i = 0; i < vertexCount; i++) {
					double angle = i * 2.0 * Math.PI / vertexCount;

					x = MathHelper.cos((float) angle) * INSTANCE.pearlsCenterRadius;
					y = MathHelper.sin((float) angle) * INSTANCE.pearlsCenterRadius;

					bb.pos(y, x, 0.0).color(color.getRed(), color.getGreen(), color.getBlue(), color.getAlpha()).endVertex();
				}
				x = MathHelper.cos((float) (2.0 * Math.PI)) * INSTANCE.pearlsCenterRadius;
				y = MathHelper.sin((float) (2.0 * Math.PI)) * INSTANCE.pearlsCenterRadius;
				bb.pos(y, x, 0.0).color(color.getRed(), color.getGreen(), color.getBlue(), color.getAlpha()).endVertex();

				tess.draw();

				// OUTER CIRCLE;
				color = Color.DARK_GRAY;
				bb.begin(GL11.GL_LINE_STRIP, DefaultVertexFormats.POSITION_COLOR);
				//	bb.pos(centerX, centerY, 0.0).color(color.getRed(), color.getGreen(), color.getBlue(), 255).endVertex();
				for (int i = 0; i < vertexCount; i++) {
					double angle = i * 2.0 * Math.PI / vertexCount;

					x = MathHelper.cos((float) angle) * INSTANCE.pearlsCenterRadius;
					y = MathHelper.sin((float) angle) * INSTANCE.pearlsCenterRadius;

					bb.pos(y, x, 0.0).color(color.getRed(), color.getGreen(), color.getBlue(), 255).endVertex();
				}
				x = MathHelper.cos((float) (2.0 * Math.PI)) * INSTANCE.pearlsCenterRadius;
				y = MathHelper.sin((float) (2.0 * Math.PI)) * INSTANCE.pearlsCenterRadius;
				bb.pos(y, x, 0.0).color(color.getRed(), color.getGreen(), color.getBlue(), 255).endVertex();

				tess.draw();

				GlStateManager.alphaFunc(thing1, thing2);
				GlStateManager.enableTexture2D();
				GlStateManager.popMatrix();
			}

			if (pearls.isEmpty()) {
				if (!INSTANCE.wasEmpty && (INSTANCE.radiusAnim == null || INSTANCE.radiusAnim.getFinished())) {
					INSTANCE.radiusAnim = new BasicAnimation<>(INSTANCE, "pearlsCenterRadius");
					INSTANCE.radiusAnim.setTo(100.0);
					INSTANCE.radiusAnim.setEasing(Easing.easeInOutQuart);
					INSTANCE.radiusAnim.setDuration(40f);
					INSTANCE.ANIMATOR.add(INSTANCE.radiusAnim);

					INSTANCE.wasEmpty = true;
				}

				if (INSTANCE.suckPearlHeartBeatAnim == null || INSTANCE.suckPearlHeartBeatAnim.getFinished()) {
					INSTANCE.suckPearlHeartBeatAnim = new BasicAnimation<>(INSTANCE, "suckPearlHeartBeatRadius");
					INSTANCE.suckPearlHeartBeatAnim.setTo(INSTANCE.pearlsCenterRadius);
					INSTANCE.suckPearlHeartBeatAnim.setFrom(0.0);
					INSTANCE.suckPearlHeartBeatAnim.setEasing(Easing.easeOutQuint);
					INSTANCE.suckPearlHeartBeatAnim.setDuration(100f);
					INSTANCE.ANIMATOR.add(INSTANCE.suckPearlHeartBeatAnim);

					BasicAnimation colorTransition = new BasicAnimation<>(INSTANCE, "color");
					colorTransition.setTo(INSTANCE.color + 0.25f > 1f ? 0.25f : INSTANCE.color + 0.25f);
					colorTransition.setEasing(Easing.linear);
					colorTransition.setDuration(100f);
					INSTANCE.ANIMATOR.add(colorTransition);
				}

				// EMPTY BELT TEXT
				{
					GlStateManager.pushMatrix();
					GlStateManager.enableTexture2D();
					GlStateManager.alphaFunc(GL11.GL_ALWAYS, 1);
					GlStateManager.enableBlend();
					GlStateManager.shadeModel(GL11.GL_SMOOTH);
					GlStateManager.translate(width / 2.0, height / 2.0, 0);

					String line = "Shift + Right Click to\nattach all pearls in your\ninventory to this belt";
					String[] split = line.split("\n");
					for (int i = 0; i < split.length; i++) {
						String text = split[i];
						Minecraft.getMinecraft().fontRenderer.drawString(
								text,
								(int) (-Minecraft.getMinecraft().fontRenderer.getStringWidth(text) / 2.0),
								(int) ((-Minecraft.getMinecraft().fontRenderer.FONT_HEIGHT + -Minecraft.getMinecraft().fontRenderer.FONT_HEIGHT / (split.length / 2.0)) + i * Minecraft.getMinecraft().fontRenderer.FONT_HEIGHT),
								0x000000);
					}

					float rot1 = MathHelper.sin(ClientTickHandler.getTicks() / 50f) * 5;
					GlStateManager.rotate(rot1, 0, 0, 1f * ClientTickHandler.getPartialTicks());
					for (int i = 0; i < split.length; i++) {
						String text = split[i];
						Minecraft.getMinecraft().fontRenderer.drawString(
								text,
								(int) (-Minecraft.getMinecraft().fontRenderer.getStringWidth(text) / 2.0),
								(int) ((-Minecraft.getMinecraft().fontRenderer.FONT_HEIGHT + -Minecraft.getMinecraft().fontRenderer.FONT_HEIGHT / (split.length / 2.0)) + i * Minecraft.getMinecraft().fontRenderer.FONT_HEIGHT),
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
								(int) ((-Minecraft.getMinecraft().fontRenderer.FONT_HEIGHT + -Minecraft.getMinecraft().fontRenderer.FONT_HEIGHT / (split.length / 2.0)) + i * Minecraft.getMinecraft().fontRenderer.FONT_HEIGHT),
								0x21000000);
					}
					GlStateManager.rotate(-rot2, 0, 0, 1f * ClientTickHandler.getPartialTicks());

					GlStateManager.popMatrix();
				}
				return;
			} else {
				if (INSTANCE.wasEmpty && (INSTANCE.radiusAnim == null || INSTANCE.radiusAnim.getFinished())) {
					INSTANCE.pearlGradientRadius = INSTANCE.pearlsCenterRadius;

					BasicAnimation radiusAnim = new BasicAnimation<>(INSTANCE, "pearlsCenterRadius");
					radiusAnim.setTo(SELECTOR_RADIUS - SELECTOR_WIDTH / 2.0);
					radiusAnim.setEasing(Easing.easeInOutQuart);
					radiusAnim.setDuration(30f);
					radiusAnim.setCompletion(() -> {

					});
					INSTANCE.ANIMATOR.add(radiusAnim);

					BasicAnimation gradientRadius = new BasicAnimation<>(INSTANCE, "pearlGradientRadius");
					gradientRadius.setTo(INSTANCE.pearlsCenterRadius + 20);
					gradientRadius.setFrom(INSTANCE.pearlsCenterRadius);
					gradientRadius.setEasing(Easing.easeOutQuart);
					gradientRadius.setDuration(20f);
					INSTANCE.ANIMATOR.add(gradientRadius);

					INSTANCE.wasEmpty = false;
				}
			}

			if (INSTANCE.radiusAnim == null || INSTANCE.radiusAnim.getFinished()) {
				GlStateManager.pushMatrix();
				GlStateManager.enableTexture2D();
				GlStateManager.alphaFunc(GL11.GL_ALWAYS, 1);
				GlStateManager.enableBlend();
				GlStateManager.shadeModel(GL11.GL_SMOOTH);
				GlStateManager.translate(width / 2.0, height / 2.0, 0);

				String line = "Scroll to select\na pearl.\n\nRight Click to pull\nthe pearl out.";
				String[] split = line.split("\n");
				for (int i = 0; i < split.length; i++) {
					String text = split[i];
					Minecraft.getMinecraft().fontRenderer.drawString(
							text,
							(int) (-Minecraft.getMinecraft().fontRenderer.getStringWidth(text) / 2.0),
							(int) ((-Minecraft.getMinecraft().fontRenderer.FONT_HEIGHT - Minecraft.getMinecraft().fontRenderer.FONT_HEIGHT + -Minecraft.getMinecraft().fontRenderer.FONT_HEIGHT / (split.length / 2.0)) + i * Minecraft.getMinecraft().fontRenderer.FONT_HEIGHT),
							0x000000);
				}

				float rot1 = MathHelper.sin(ClientTickHandler.getTicks() / 50f) * 5;
				GlStateManager.rotate(rot1, 0, 0, 1f * ClientTickHandler.getPartialTicks());
				for (int i = 0; i < split.length; i++) {
					String text = split[i];
					Minecraft.getMinecraft().fontRenderer.drawString(
							text,
							(int) (-Minecraft.getMinecraft().fontRenderer.getStringWidth(text) / 2.0),
							(int) ((-Minecraft.getMinecraft().fontRenderer.FONT_HEIGHT - Minecraft.getMinecraft().fontRenderer.FONT_HEIGHT + -Minecraft.getMinecraft().fontRenderer.FONT_HEIGHT / (split.length / 2.0)) + i * Minecraft.getMinecraft().fontRenderer.FONT_HEIGHT),
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
							(int) ((-Minecraft.getMinecraft().fontRenderer.FONT_HEIGHT - Minecraft.getMinecraft().fontRenderer.FONT_HEIGHT + -Minecraft.getMinecraft().fontRenderer.FONT_HEIGHT / (split.length / 2.0)) + i * Minecraft.getMinecraft().fontRenderer.FONT_HEIGHT),
							0x21000000);
				}
				GlStateManager.rotate(-rot2, 0, 0, 1f * ClientTickHandler.getPartialTicks());
				GlStateManager.popMatrix();


				int numSegmentsPerArc = (int) Math.ceil(360d / pearls.size());
				float anglePerColor = (float) (2 * Math.PI / pearls.size());
				float anglePerSegment = anglePerColor / (numSegmentsPerArc);
				float angle = 0;

				for (int j = 0; j < pearls.size(); j++) {
					ItemStack pearl = pearls.get(j);
					if (!(pearl.getItem() instanceof INacreProduct)) continue;
					INacreProduct product = (INacreProduct) pearl.getItem();
					Function2<ItemStack, Integer, Integer> function = product.getItemColorFunction();
					if (function == null) continue;

					int colorInt = function.invoke(pearl, 0);
					Color color = new Color(colorInt);

					double innerRadius = INSTANCE.pearlsCenterRadius + 0.5;
					double outerRadius = INSTANCE.pearlGradientRadius + (INSTANCE.slotRadii[j]);// + (scrollSlot == j ? SELECTOR_SHIFT : 0);

					GlStateManager.pushMatrix();
					int thing1 = GL11.glGetInteger(GL11.GL_ALPHA_TEST_FUNC);
					float thing2 = GL11.glGetFloat(GL11.GL_ALPHA_TEST_REF);

					GlStateManager.alphaFunc(GL11.GL_ALWAYS, 1);
					GlStateManager.enableBlend();
					GlStateManager.disableTexture2D();
					GlStateManager.shadeModel(GL11.GL_SMOOTH);

					GlStateManager.translate(width / 2.0, height / 2.0, 0);

					bb.begin(GL11.GL_QUADS, DefaultVertexFormats.POSITION_COLOR);
					for (int i = 0; i < numSegmentsPerArc; i++) {
						float currentAngle = i * anglePerSegment + angle;
						bb.pos(innerRadius * MathHelper.cos(currentAngle), innerRadius * MathHelper.sin(currentAngle), 0).color(color.getRed(), color.getGreen(), color.getBlue(), 255).endVertex();
						bb.pos(innerRadius * MathHelper.cos(currentAngle + anglePerSegment), innerRadius * MathHelper.sin(currentAngle + anglePerSegment), 0).color(color.getRed(), color.getGreen(), color.getBlue(), 255).endVertex();
						bb.pos(outerRadius * MathHelper.cos(currentAngle + anglePerSegment), outerRadius * MathHelper.sin(currentAngle + anglePerSegment), 0).color(color.getRed(), color.getGreen(), color.getBlue(), 0).endVertex();
						bb.pos(outerRadius * MathHelper.cos(currentAngle), outerRadius * MathHelper.sin(currentAngle), 0).color(color.getRed(), color.getGreen(), color.getBlue(), 0).endVertex();
					}
					tess.draw();

					float centerAngle = angle + anglePerColor / 2;
					Vec3d inner = new Vec3d(innerRadius * MathHelper.cos(centerAngle), innerRadius * MathHelper.sin(centerAngle), 0);
					Vec3d outer = new Vec3d(outerRadius * MathHelper.cos(centerAngle), outerRadius * MathHelper.sin(centerAngle), 0);

					Vec3d center = new Vec3d((inner.x + outer.x) / 2, (inner.y + outer.y) / 2, 0);
					Vec3d normal = center.normalize();

					Vec3d pearlOffset = normal.scale(INSTANCE.pearlsCenterRadius / 2).subtract(8, 8, 0);

					RenderHelper.enableGUIStandardItemLighting();
					GlStateManager.enableRescaleNormal();
					GlStateManager.enableTexture2D();

					GlStateManager.scale(2, 2, 2);
					GlStateManager.translate(pearlOffset.x, pearlOffset.y, 0);

					Minecraft.getMinecraft().getRenderItem().renderItemAndEffectIntoGUI(pearl, 0, 0);
					Minecraft.getMinecraft().getRenderItem().renderItemOverlayIntoGUI(Minecraft.getMinecraft().fontRenderer, pearl, 0, 0, "");

					GlStateManager.translate(-pearlOffset.x, -pearlOffset.y, 0);
					GlStateManager.scale(-2, -2, -2);

					GlStateManager.disableRescaleNormal();
					RenderHelper.disableStandardItemLighting();
					GlStateManager.disableBlend();
					GlStateManager.disableAlpha();
					GlStateManager.alphaFunc(thing1, thing2);

					GlStateManager.translate(-width / 2.0, -height / 2.0, 0);
					GlStateManager.popMatrix();

					angle += anglePerColor;
				}
			}
		}
	}
}
