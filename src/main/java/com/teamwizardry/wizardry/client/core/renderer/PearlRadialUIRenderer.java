package com.teamwizardry.wizardry.client.core.renderer;

import com.teamwizardry.librarianlib.features.animator.Animator;
import com.teamwizardry.librarianlib.features.animator.Easing;
import com.teamwizardry.librarianlib.features.animator.animations.BasicAnimation;
import com.teamwizardry.librarianlib.features.helpers.ItemNBTHelper;
import com.teamwizardry.librarianlib.features.network.PacketHandler;
import com.teamwizardry.wizardry.api.ConfigValues;
import com.teamwizardry.wizardry.api.item.BaublesSupport;
import com.teamwizardry.wizardry.api.item.INacreProduct;
import com.teamwizardry.wizardry.common.item.pearlbelt.IPearlBelt;
import com.teamwizardry.wizardry.common.network.belt.PacketSetBeltScrollSlotServer;
import com.teamwizardry.wizardry.init.ModItems;
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
import net.minecraftforge.fml.common.gameevent.TickEvent;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;
import net.minecraftforge.items.IItemHandler;
import org.lwjgl.input.Keyboard;
import org.lwjgl.opengl.GL11;

import java.awt.*;
import java.util.ArrayList;
import java.util.List;

@SideOnly(Side.CLIENT)
@Mod.EventBusSubscriber(Side.CLIENT)
public class PearlRadialUIRenderer {

	private final static int SELECTOR_RADIUS = 40;
	private final static int SELECTOR_WIDTH = 25;
	private final static int SELECTOR_SHIFT = 5;
	private final static float SELECTOR_ALPHA = 0.7F;
	public static PearlRadialUIRenderer INSTANCE = new PearlRadialUIRenderer();
	private static boolean lastSneakTick = false;
	public final float[] slotRadii = new float[ConfigValues.pearlBeltInvSize];
	private final BasicAnimation[] slotAnimations = new BasicAnimation[ConfigValues.pearlBeltInvSize];
	private final Animator ANIMATOR = new Animator();

	@SubscribeEvent
	public static void clientTick(TickEvent.ClientTickEvent event) {
		EntityPlayer player = Minecraft.getMinecraft().player;
		if (player == null) return;

		if (player.isSneaking() && !lastSneakTick) {
			lastSneakTick = true;
		} else if (!player.isSneaking() && lastSneakTick) {
			lastSneakTick = false;

			ItemStack stack = player.getHeldItemMainhand();

			if (stack.getItem() == ModItems.PEARL_BELT) {
				int scrollSlot = ItemNBTHelper.getInt(stack, "scroll_slot", -1);
				if (scrollSlot >= 0) {

					//	PacketHandler.NETWORK.sendToServer(new PacketPearlHolderSubtract(player.inventory.getSlotFor(stack), scrollSlot));
				}

			} else if (stack.getItem() == ModItems.STAFF) {
				int scrollSlot = ItemNBTHelper.getInt(stack, "scroll_slot", -1);
				if (scrollSlot >= 0) {

					//	PacketHandler.NETWORK.sendToServer(new PacketStaffPearlSwap(player.inventory.getSlotFor(stack)));
				}
			}
		}
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

		if (Keyboard.isCreated() && event.getDwheel() != 0 && player.isSneaking()) {

			ItemStack stack = player.getHeldItemMainhand();

			if (stack.getItem() == ModItems.PEARL_BELT) {

				IPearlBelt belt = (IPearlBelt) stack.getItem();
				int rawCount = belt.getPearlCount(stack);
				int count = Math.max(rawCount - 1, 0);

				int scrollSlot = ItemNBTHelper.getInt(stack, "scroll_slot", -1);

				scrollSlot = getScrollSlot(event, count, scrollSlot);
				Minecraft.getMinecraft().player.sendChatMessage(scrollSlot + "");

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
	public static void renderHud(RenderGameOverlayEvent.Post event) {
		if (event.getType() == RenderGameOverlayEvent.ElementType.EXPERIENCE) {
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

			int numSegmentsPerArc = (int) Math.ceil(360d / pearls.size());
			float anglePerColor = (float) (2 * Math.PI / pearls.size());
			float anglePerSegment = anglePerColor / (numSegmentsPerArc);
			float angle = 0;

			Tessellator tess = Tessellator.getInstance();
			BufferBuilder bb = tess.getBuffer();
			for (int j = 0; j < pearls.size(); j++) {
				ItemStack pearl = pearls.get(j);
				if (!(pearl.getItem() instanceof INacreProduct)) continue;
				INacreProduct product = (INacreProduct) pearl.getItem();
				Function2<ItemStack, Integer, Integer> function = product.getItemColorFunction();
				if (function == null) continue;

				int colorInt = function.invoke(pearl, 0);
				Color color = new Color(colorInt);

				double innerRadius = SELECTOR_RADIUS - SELECTOR_WIDTH / 2.0;
				double outerRadius = SELECTOR_RADIUS + SELECTOR_WIDTH / 2.0 + (INSTANCE.slotRadii[j]);// + (scrollSlot == j ? SELECTOR_SHIFT : 0);

				GlStateManager.pushMatrix();
				GlStateManager.translate(width / 2.0, height / 2.0, 0);

				GlStateManager.enableBlend();
				GlStateManager.disableTexture2D();
				GlStateManager.shadeModel(GL11.GL_SMOOTH);

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

				Vec3d pearlOffset = normal.scale(70).subtract(8, 8, 0);

				RenderHelper.enableGUIStandardItemLighting();
				GlStateManager.enableRescaleNormal();
				GlStateManager.enableTexture2D();

				GlStateManager.translate(pearlOffset.x, pearlOffset.y, 0);

				Minecraft.getMinecraft().getRenderItem().renderItemAndEffectIntoGUI(pearl, 0, 0);
				Minecraft.getMinecraft().getRenderItem().renderItemOverlayIntoGUI(Minecraft.getMinecraft().fontRenderer, pearl, 0, 0, "");

				GlStateManager.translate(-pearlOffset.x, -pearlOffset.y, 0);

				GlStateManager.disableRescaleNormal();
				RenderHelper.disableStandardItemLighting();
				GlStateManager.disableBlend();
				GlStateManager.disableAlpha();

				GlStateManager.translate(-width / 2.0, -height / 2.0, 0);
				GlStateManager.popMatrix();

				angle += anglePerColor;
			}
		}
	}
}
