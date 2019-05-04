package com.teamwizardry.wizardry.client.core.renderer;

import com.teamwizardry.librarianlib.core.client.ClientTickHandler;
import com.teamwizardry.librarianlib.features.animator.Animator;
import com.teamwizardry.librarianlib.features.animator.Easing;
import com.teamwizardry.librarianlib.features.animator.animations.BasicAnimation;
import com.teamwizardry.librarianlib.features.animator.animations.ScheduledEventAnimation;
import com.teamwizardry.librarianlib.features.helpers.ItemNBTHelper;
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
import com.teamwizardry.wizardry.common.network.pearlswapping.PacketSetScrollSlotServer;
import com.teamwizardry.wizardry.init.ModKeybinds;
import kotlin.Pair;
import kotlin.jvm.functions.Function2;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.FontRenderer;
import net.minecraft.client.gui.ScaledResolution;
import net.minecraft.client.renderer.*;
import net.minecraft.client.renderer.block.model.BakedQuad;
import net.minecraft.client.renderer.block.model.IBakedModel;
import net.minecraft.client.renderer.block.model.ItemCameraTransforms;
import net.minecraft.client.renderer.texture.TextureAtlasSprite;
import net.minecraft.client.renderer.texture.TextureMap;
import net.minecraft.client.renderer.texture.TextureUtil;
import net.minecraft.client.renderer.vertex.DefaultVertexFormats;
import net.minecraft.client.renderer.vertex.VertexFormat;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.ItemStack;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.math.MathHelper;
import net.minecraft.util.math.Vec3d;
import net.minecraftforge.client.ForgeHooksClient;
import net.minecraftforge.client.event.MouseEvent;
import net.minecraftforge.client.event.RenderGameOverlayEvent;
import net.minecraftforge.client.model.pipeline.LightUtil;
import net.minecraftforge.client.model.pipeline.QuadGatheringTransformer;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;
import net.minecraftforge.items.IItemHandler;
import org.jetbrains.annotations.NotNull;
import org.lwjgl.input.Keyboard;
import org.lwjgl.opengl.GL11;

import javax.annotation.Nonnull;
import java.awt.*;
import java.util.ArrayList;
import java.util.List;

@SuppressWarnings("Duplicates")
@SideOnly(Side.CLIENT)
public class PearlRadialUIRenderer {

	public static final PearlRadialUIRenderer INSTANCE = new PearlRadialUIRenderer();

	/**
	 * Reimplement vanilla item so we can draw pearl stacks with opacity support.
	 */
	private static final ResourceLocation RES_ITEM_GLINT = new ResourceLocation("textures/misc/enchanted_item_glint.png");
	/**
	 * Reimplement vanilla item so we can draw pearl stacks with opacity support.
	 */
	private static final LightGatheringTransformer lightGatherer = new LightGatheringTransformer();

	/**
	 * Background base for spell component icons.
	 */
	private static final Sprite spritePlate = new Sprite(new ResourceLocation(Wizardry.MODID, "textures/gui/worktable/plate.png"));

	private static final int SELECTOR_RADIUS = 90;
	private static final int SELECTOR_WIDTH = 50;
	private static final int SELECTOR_SHIFT = 5;
	private static final float SELECTOR_ALPHA = 0.7F;

	@Nonnull
	private static final BasicAnimation[] slotAnimations = new BasicAnimation[ConfigValues.pearlBeltInvSize];
	@Nonnull
	private static final Animator ANIMATOR = new Animator();
	@Nonnull
	private static BasicAnimation<PearlRadialUIRenderer> centerRadiusAnim = new BasicAnimation<>(INSTANCE, "centerRadius");
	@Nonnull
	private static BasicAnimation<PearlRadialUIRenderer> heartBeatAnim = new BasicAnimation<>(INSTANCE, "heartBeatRadius");
	@Nonnull
	private static BasicAnimation<PearlRadialUIRenderer> parasolGradientAnim = new BasicAnimation<>(INSTANCE, "parasolGradientRadius");
	@Nonnull
	private static BasicAnimation<PearlRadialUIRenderer> colorAnim = new BasicAnimation<>(INSTANCE, "color");
	@Nonnull
	private static BasicAnimation<PearlRadialUIRenderer> itemExpansionAnim = new BasicAnimation<>(INSTANCE, "itemExpansion");

	@Nonnull
	private static Pair<Integer, List<ItemStack>> snapshotPearls = new Pair<>(0, new ArrayList<>());
	@Nonnull
	private static List<ItemStack> pearls = new ArrayList<>();
	@Nonnull
	private static Pair<Integer, List<ItemStack>> newPearls = new Pair<>(0, new ArrayList<>());

	private static boolean changing = false;
	private static boolean wasEmpty = true;
	private static boolean init = false;

	@Nonnull
	private static String centerText = "Shift + Right Click to\nattach all pearls in your\ninventory to this belt";

	@Nonnull
	public final float[] slotRadii = new float[ConfigValues.pearlBeltInvSize];

	public double centerRadius = 100.0;
	public double heartBeatRadius = 0.0;
	public double parasolGradientRadius = 0;

	public float color = RandUtil.nextFloat();
	public float itemExpansion = 0;

	private PearlRadialUIRenderer() {
		MinecraftForge.EVENT_BUS.register(this);
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

	/**
	 * Reimplement vanilla item so we can draw pearl stacks with opacity support.
	 */
	private static void renderModel(IBakedModel model, int color, ItemStack stack) {
		renderLitItem(Minecraft.getMinecraft().getRenderItem(), model, color, stack);
	}

	/**
	 * Reimplement vanilla item so we can draw pearl stacks with opacity support.
	 */
	private static int getColorMultiplier(ItemStack stack, int tintIndex) {
		if (tintIndex == -1 || stack.isEmpty()) return 0xFFFFFFFF;

		int colorMultiplier = Minecraft.getMinecraft().getItemColors().colorMultiplier(stack, tintIndex);

		if (EntityRenderer.anaglyphEnable) {
			colorMultiplier = TextureUtil.anaglyphColor(colorMultiplier);
		}

		// FUCK YOU
		//	// Always full opacity
		//	colorMultiplier |= 0xff << 24; // -16777216

		return colorMultiplier;
	}

	private static int genSnapshotHashcode(List<ItemStack> pearls) {
		int hashcode = 0;
		for (ItemStack stack : pearls) {
			List<SpellRing> rings = SpellUtils.getAllSpellRings(stack);
			for (SpellRing ring : rings) {
				hashcode += ring.hashCode();
			}
		}
		return hashcode;
	}

	private static void renderText(double width, double height, String string) {
		GlStateManager.pushMatrix();
		GlStateManager.enableTexture2D();
		GlStateManager.alphaFunc(GL11.GL_ALWAYS, 1);
		GlStateManager.enableBlend();
		GlStateManager.shadeModel(GL11.GL_SMOOTH);
		GlStateManager.translate(width / 2.0, height / 2.0, 0);

		String[] split = string.split("\n");
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
	}

	private static void renderQuads(BufferBuilder renderer, List<BakedQuad> quads, int color, ItemStack stack) {
		color &= 0xFF000000;
		boolean flag = !stack.isEmpty();
		int i = 0;

		for (int j = quads.size(); i < j; ++i) {
			BakedQuad bakedquad = quads.get(i);
			int k = color | 0xFFFFFF;

			if (flag && bakedquad.hasTintIndex()) {
				k = Minecraft.getMinecraft().getItemColors().colorMultiplier(stack, bakedquad.getTintIndex());

				if (EntityRenderer.anaglyphEnable) {
					k = TextureUtil.anaglyphColor(k);
				}

				k &= 0xFFFFFF;
				k |= color;
			}

			LightUtil.renderQuadColor(renderer, bakedquad, k);
		}
	}

	/**
	 * Reimplement vanilla item so we can draw pearl stacks with opacity support.
	 */
	private static void renderLitItem(RenderItem ri, IBakedModel model, int color, ItemStack stack) {
		List<BakedQuad> allquads = new ArrayList<>();

		for (EnumFacing enumfacing : EnumFacing.VALUES) {
			allquads.addAll(model.getQuads(null, enumfacing, 0));
		}

		allquads.addAll(model.getQuads(null, null, 0));

		if (allquads.isEmpty()) return;

		// Current list of consecutive quads with the same lighting
		List<BakedQuad> segment = new ArrayList<>();

		// Lighting of the current segment
		int segmentBlockLight = -1;
		int segmentSkyLight = -1;
		// Coloring of the current segment
		int segmentColorMultiplier = color;
		// If the current segment contains lighting data
		boolean hasLighting = false;

		// Tint index cache to avoid unnecessary IItemColor lookups
		int prevTintIndex = -1;

		for (int i = 0; i < allquads.size(); i++) {
			BakedQuad q = allquads.get(i);

			// Lighting of the current quad
			int bl = 0;
			int sl = 0;

			// Fail-fast on ITEM, as it cannot have light data
			if (q.getFormat() != DefaultVertexFormats.ITEM && q.getFormat().hasUvOffset(1)) {
				q.pipe(lightGatherer);
				if (lightGatherer.hasLighting()) {
					bl = lightGatherer.blockLight;
					sl = lightGatherer.skyLight;
				}
			}

			int colorMultiplier = segmentColorMultiplier;

			// If there is no color override, and this quad is tinted, we need to apply IItemColor
			if (color == 0xFFFFFFFF && q.hasTintIndex()) {
				int tintIndex = q.getTintIndex();

				if (prevTintIndex != tintIndex) {
					colorMultiplier = getColorMultiplier(stack, tintIndex);
				}
				prevTintIndex = tintIndex;
			} else {
				colorMultiplier = color;
				prevTintIndex = -1;
			}

			boolean lightingDirty = segmentBlockLight != bl || segmentSkyLight != sl;
			boolean colorDirty = hasLighting && segmentColorMultiplier != colorMultiplier;

			// If lighting or color data has changed, draw the segment and flush it
			if (lightingDirty || colorDirty) {
				if (i > 0) // Make sure this isn't the first quad being processed
				{
					drawSegment(color, stack, segment, segmentBlockLight, segmentSkyLight, segmentColorMultiplier, lightingDirty && (hasLighting || segment.size() < i), colorDirty);
				}
				segmentBlockLight = bl;
				segmentSkyLight = sl;
				segmentColorMultiplier = colorMultiplier;
				hasLighting = segmentBlockLight > 0 || segmentSkyLight > 0;
			}

			segment.add(q);
		}

		drawSegment(color, stack, segment, segmentBlockLight, segmentSkyLight, segmentColorMultiplier, hasLighting || segment.size() < allquads.size(), false);

		// Clean up render state if necessary
		if (hasLighting) {
			OpenGlHelper.setLightmapTextureCoords(OpenGlHelper.lightmapTexUnit, OpenGlHelper.lastBrightnessX, OpenGlHelper.lastBrightnessY);
			GL11.glMaterial(GL11.GL_FRONT_AND_BACK, GL11.GL_EMISSION, RenderHelper.setColorBuffer(0, 0, 0, 1));
		}
	}

	/**
	 * Reimplement vanilla item so we can draw pearl stacks with opacity support.
	 */
	private static void drawSegment(int baseColor, ItemStack stack, List<BakedQuad> segment, int bl, int sl, int tintColor, boolean updateLighting, boolean updateColor) {
		BufferBuilder bufferbuilder = Tessellator.getInstance().getBuffer();
		bufferbuilder.begin(GL11.GL_QUADS, DefaultVertexFormats.ITEM);

		float lastBl = OpenGlHelper.lastBrightnessX;
		float lastSl = OpenGlHelper.lastBrightnessY;

		if (updateLighting || updateColor) {
			float emissive = Math.max(bl, sl) / 240f;

			float r = (tintColor >>> 16 & 0xff) / 255f;
			float g = (tintColor >>> 8 & 0xff) / 255f;
			float b = (tintColor & 0xff) / 255f;

			GL11.glMaterial(GL11.GL_FRONT_AND_BACK, GL11.GL_EMISSION, RenderHelper.setColorBuffer(emissive * r, emissive * g, emissive * b, 1));

			if (updateLighting) {
				OpenGlHelper.setLightmapTextureCoords(OpenGlHelper.lightmapTexUnit, Math.max(bl, lastBl), Math.max(sl, lastSl));
			}
		}

		renderQuads(bufferbuilder, segment, baseColor, stack);
		Tessellator.getInstance().draw();

		// Preserve this as it represents the "world" lighting
		OpenGlHelper.lastBrightnessX = lastBl;
		OpenGlHelper.lastBrightnessY = lastSl;

		segment.clear();
	}

	/**
	 * Reimplement vanilla item so we can draw pearl stacks with opacity support.
	 */
	private static void renderEffect(IBakedModel model) {
		GlStateManager.depthMask(false);
		GlStateManager.depthFunc(514);
		GlStateManager.disableLighting();
		GlStateManager.blendFunc(GlStateManager.SourceFactor.SRC_COLOR, GlStateManager.DestFactor.ONE);
		Minecraft.getMinecraft().getTextureManager().bindTexture(RES_ITEM_GLINT);
		GlStateManager.matrixMode(5890);
		GlStateManager.pushMatrix();
		GlStateManager.scale(8.0F, 8.0F, 8.0F);
		float f = (float) (Minecraft.getSystemTime() % 3000L) / 3000.0F / 8.0F;
		GlStateManager.translate(f, 0.0F, 0.0F);
		GlStateManager.rotate(-50.0F, 0.0F, 0.0F, 1.0F);
		renderModel(model, -8372020, ItemStack.EMPTY);
		GlStateManager.popMatrix();
		GlStateManager.pushMatrix();
		GlStateManager.scale(8.0F, 8.0F, 8.0F);
		float f1 = (float) (Minecraft.getSystemTime() % 4873L) / 4873.0F / 8.0F;
		GlStateManager.translate(-f1, 0.0F, 0.0F);
		GlStateManager.rotate(10.0F, 0.0F, 0.0F, 1.0F);
		renderModel(model, -8372020, ItemStack.EMPTY);
		GlStateManager.popMatrix();
		GlStateManager.matrixMode(5888);
		GlStateManager.blendFunc(GlStateManager.SourceFactor.SRC_ALPHA, GlStateManager.DestFactor.ONE_MINUS_SRC_ALPHA);
		GlStateManager.enableLighting();
		GlStateManager.depthFunc(515);
		GlStateManager.depthMask(true);
		Minecraft.getMinecraft().getTextureManager().bindTexture(TextureMap.LOCATION_BLOCKS_TEXTURE);
	}

	@SubscribeEvent
	public void onMouse(MouseEvent event) {
		if (!Keyboard.isCreated()) return;

		EntityPlayer player = Minecraft.getMinecraft().player;
		if (player == null) return;

		ItemStack heldItem = player.getHeldItemMainhand();

		if (event.getDwheel() != 0) {

			for (int i = 0; i < player.inventory.getSizeInventory(); i++) {
				ItemStack invStack = player.inventory.getStackInSlot(i);
				if (ItemStack.areItemStacksEqual(heldItem, invStack)) continue;
				if (ItemNBTHelper.getInt(invStack, "scroll_slot", -1) == -1) continue;

				ItemNBTHelper.setInt(invStack, "scroll_slot", -1);
				PacketHandler.NETWORK.sendToServer(new PacketSetScrollSlotServer(i, -1));
			}

			if (ModKeybinds.pearlSwapping.isKeyDown()) {

				ItemStack pearlStorageStack;
				IPearlStorageHolder pearlStorage;
				if (heldItem.getItem() instanceof IPearlStorageHolder) {
					pearlStorageStack = heldItem;
					pearlStorage = (IPearlStorageHolder) heldItem.getItem();
				} else if (heldItem.getItem() instanceof IPearlSwappable) {
					ItemStack storageStack = BaublesSupport.getItem(player, IPearlStorageHolder.class);
					if (!storageStack.isEmpty()) {
						pearlStorageStack = storageStack;
						pearlStorage = (IPearlStorageHolder) storageStack.getItem();
					} else return;
				} else return;

				IItemHandler handler = pearlStorage.getPearls(pearlStorageStack);
				if (handler == null) return;
				List<ItemStack> temp = new ArrayList<>();
				for (int i = 0; i < handler.getSlots(); i++) {
					ItemStack pearl = handler.getStackInSlot(i);
					if (pearl.isEmpty()) continue;
					temp.add(pearl);
				}
				newPearls = new Pair<>(genSnapshotHashcode(temp), temp);

				int rawCount = pearlStorage.getPearlCount(pearlStorageStack);
				int count = Math.max(rawCount - 1, 0);

				int scrollSlot = ItemNBTHelper.getInt(heldItem, "scroll_slot", -1);

				scrollSlot = getScrollSlot(event, count, scrollSlot);

				if (scrollSlot >= 0) {
					ItemNBTHelper.setInt(heldItem, "scroll_slot", scrollSlot);
					PacketHandler.NETWORK.sendToServer(new PacketSetScrollSlotServer(player.inventory.getSlotFor(heldItem), scrollSlot));

					for (int i = 0; i < slotAnimations.length; i++) {
						BasicAnimation animation = slotAnimations[i];
						if (animation != null)
							ANIMATOR.removeAnimations(animation);

						if (i == scrollSlot) continue;
						BasicAnimation<PearlRadialUIRenderer> newAnimation = new BasicAnimation<>(INSTANCE, "slotRadii[" + i + "]");
						newAnimation.setTo(0);
						newAnimation.setEasing(Easing.easeOutQuint);
						newAnimation.setDuration(20f);
						ANIMATOR.add(newAnimation);

						slotAnimations[i] = newAnimation;
					}

					BasicAnimation<PearlRadialUIRenderer> animation = new BasicAnimation<>(INSTANCE, "slotRadii[" + scrollSlot + "]");
					animation.setTo(SELECTOR_SHIFT * 10);
					animation.setEasing(Easing.easeOutQuint);
					animation.setDuration(20f);
					ANIMATOR.add(animation);

					slotAnimations[scrollSlot] = animation;

					event.setCanceled(true);
				}
			} else {
				for (int i = 0; i < slotAnimations.length; i++) {
					BasicAnimation animation = slotAnimations[i];
					if (animation != null)
						ANIMATOR.removeAnimations(animation);

					BasicAnimation<PearlRadialUIRenderer> newAnimation = new BasicAnimation<>(INSTANCE, "slotRadii[" + i + "]");
					newAnimation.setTo(0);
					newAnimation.setEasing(Easing.easeOutQuint);
					newAnimation.setDuration(20f);
					ANIMATOR.add(newAnimation);

					slotAnimations[i] = newAnimation;
				}
			}
		}
	}

	@SubscribeEvent
	public void renderHud(RenderGameOverlayEvent.Pre event) {
		if (!init) {
			ANIMATOR.add(centerRadiusAnim, colorAnim, heartBeatAnim, parasolGradientAnim, itemExpansionAnim);
			init = true;
			return;
		}
		if (event.getType() == RenderGameOverlayEvent.ElementType.CROSSHAIRS) {
			EntityPlayer player = Minecraft.getMinecraft().player;
			ItemStack heldItem = player.getHeldItemMainhand();

			ItemStack pearlStorageStack;
			IPearlStorageHolder pearlStorage;
			if (heldItem.getItem() instanceof IPearlStorageHolder) {
				pearlStorageStack = heldItem;
				pearlStorage = (IPearlStorageHolder) heldItem.getItem();
			} else if (heldItem.getItem() instanceof IPearlSwappable && ModKeybinds.pearlSwapping.isKeyDown()) {
				ItemStack storageStack = BaublesSupport.getItem(player, IPearlStorageHolder.class);
				if (!storageStack.isEmpty()) {
					pearlStorageStack = storageStack;
					pearlStorage = (IPearlStorageHolder) storageStack.getItem();
				} else return;
			} else return;

			IItemHandler handler = pearlStorage.getPearls(pearlStorageStack);
			if (handler == null) return;
			//	event.setCanceled(true);

			ScaledResolution resolution = event.getResolution();
			int width = resolution.getScaledWidth();
			int height = resolution.getScaledHeight();

			Tessellator tess = Tessellator.getInstance();
			BufferBuilder bb = tess.getBuffer();

			// ------------- CENTER CIRCLE ------------- //
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
				Color color = new Color(transitioningColor.getRed() / 255f, transitioningColor.getBlue() / 255f, transitioningColor.getGreen() / 255f, MathHelper.clamp(1f - (float) (INSTANCE.heartBeatRadius / INSTANCE.centerRadius), 0f, 1f));
				bb.begin(GL11.GL_TRIANGLE_FAN, DefaultVertexFormats.POSITION_COLOR);
				bb.pos(0.0, 0.0, 0.0).color(color.getRed(), color.getGreen(), color.getBlue(), 0).endVertex();
				for (int i = 0; i < vertexCount; i++) {
					double angle = i * 2.0 * Math.PI / vertexCount;

					x = MathHelper.cos((float) angle) * INSTANCE.heartBeatRadius;
					y = MathHelper.sin((float) angle) * INSTANCE.heartBeatRadius;

					bb.pos(y, x, 0.0).color(color.getRed(), color.getGreen(), color.getBlue(), color.getAlpha()).endVertex();
				}
				x = MathHelper.cos((float) (2.0 * Math.PI)) * INSTANCE.heartBeatRadius;
				y = MathHelper.sin((float) (2.0 * Math.PI)) * INSTANCE.heartBeatRadius;
				bb.pos(y, x, 0.0).color(color.getRed(), color.getGreen(), color.getBlue(), color.getAlpha()).endVertex();

				tess.draw();

				color = new Color(transitioningColor.getRed() / 255f, transitioningColor.getBlue() / 255f, transitioningColor.getGreen() / 255f, 0.5f);
				bb.begin(GL11.GL_TRIANGLE_FAN, DefaultVertexFormats.POSITION_COLOR);
				bb.pos(0.0, 0.0, 0.0).color(color.getRed(), color.getGreen(), color.getBlue(), color.getAlpha()).endVertex();
				for (int i = 0; i < vertexCount; i++) {
					double angle = i * 2.0 * Math.PI / vertexCount;

					x = MathHelper.cos((float) angle) * INSTANCE.centerRadius;
					y = MathHelper.sin((float) angle) * INSTANCE.centerRadius;

					bb.pos(y, x, 0.0).color(color.getRed(), color.getGreen(), color.getBlue(), color.getAlpha()).endVertex();
				}
				x = MathHelper.cos((float) (2.0 * Math.PI)) * INSTANCE.centerRadius;
				y = MathHelper.sin((float) (2.0 * Math.PI)) * INSTANCE.centerRadius;
				bb.pos(y, x, 0.0).color(color.getRed(), color.getGreen(), color.getBlue(), color.getAlpha()).endVertex();

				tess.draw();

				// OUTER CIRCLE;
				color = Color.DARK_GRAY;
				bb.begin(GL11.GL_LINE_STRIP, DefaultVertexFormats.POSITION_COLOR);
				//	bb.pos(centerX, centerY, 0.0).color(color.getRed(), color.getGreen(), color.getBlue(), 255).endVertex();
				for (int i = 0; i < vertexCount; i++) {
					double angle = i * 2.0 * Math.PI / vertexCount;

					x = MathHelper.cos((float) angle) * INSTANCE.centerRadius;
					y = MathHelper.sin((float) angle) * INSTANCE.centerRadius;

					bb.pos(y, x, 0.0).color(color.getRed(), color.getGreen(), color.getBlue(), 255).endVertex();
				}
				x = MathHelper.cos((float) (2.0 * Math.PI)) * INSTANCE.centerRadius;
				y = MathHelper.sin((float) (2.0 * Math.PI)) * INSTANCE.centerRadius;
				bb.pos(y, x, 0.0).color(color.getRed(), color.getGreen(), color.getBlue(), 255).endVertex();

				tess.draw();

				GlStateManager.alphaFunc(thing1, thing2);
				GlStateManager.enableTexture2D();
				GlStateManager.popMatrix();
			}
			renderText(width, height, centerText);
			// ------------- CENTER CIRCLE ------------- //


			// ------------- PEARL UPDATER ------------- //
			List<ItemStack> temp = new ArrayList<>();
			for (int i = 0; i < handler.getSlots(); i++) {
				ItemStack pearl = handler.getStackInSlot(i);
				if (pearl.isEmpty()) continue;
				temp.add(pearl);
			}
			newPearls = new Pair<>(genSnapshotHashcode(temp), temp);

			if (!changing)
				if (!newPearls.getFirst().equals(snapshotPearls.getFirst())) {
					if (newPearls.getSecond().isEmpty()) {
						pearls = snapshotPearls.getSecond();
						changing = true;

						final Pair<Integer, List<ItemStack>> tmp = newPearls;
						ScheduledEventAnimation timer = new ScheduledEventAnimation(16, () -> {
							snapshotPearls = tmp;
							pearls = tmp.getSecond();
							changing = false;
						});
						ANIMATOR.add(timer);

					} else if (snapshotPearls.getSecond().isEmpty()) {

						final Pair<Integer, List<ItemStack>> tmp = newPearls;
						pearls = tmp.getSecond();
						snapshotPearls = tmp;

					} else {
						final Pair<Integer, List<ItemStack>> tmp = newPearls;

						changing = true;

						ANIMATOR.add(new ScheduledEventAnimation(8, () -> pearls = tmp.getSecond()));

						ANIMATOR.add(new ScheduledEventAnimation(16, () -> {
							snapshotPearls = tmp;
							changing = false;
						}));
					}
				}
			// ------------- PEARL UPDATER ------------- //


			// ------------- ANIMATIONS ------------- //

			int scrollSlot = ItemNBTHelper.getInt(heldItem, "scroll_slot", -1);

			if (scrollSlot > -1 && !pearls.isEmpty()) {
				String name = pearls.get(scrollSlot).getDisplayName();
				FontRenderer renderer = Minecraft.getMinecraft().fontRenderer;
				List<String> list = renderer.listFormattedStringToWidth(name, 100);
				StringBuilder builder = new StringBuilder();
				for (String line : list) {
					builder.append("\n").append(line);
				}
				centerText = builder.toString();

			}

			if (colorAnim.getFinished()) {
				colorAnim = new BasicAnimation<>(INSTANCE, "color");
				colorAnim.setTo(INSTANCE.color + 0.25f > 1f ? 0.25f : INSTANCE.color + 0.25f);
				colorAnim.setEasing(Easing.linear);
				colorAnim.setDuration(100f);
				ANIMATOR.add(colorAnim);
			}

			if (centerRadiusAnim.getFinished() && parasolGradientAnim.getFinished())
				if (newPearls.getSecond().isEmpty()) {
					if (!wasEmpty) {
						centerText = "Shift + Right Click to\nattach all pearls in your\ninventory to this belt";

						centerRadiusAnim = new BasicAnimation<>(INSTANCE, "centerRadius");
						centerRadiusAnim.setTo(100.0);
						centerRadiusAnim.setEasing(Easing.easeInOutQuart);
						centerRadiusAnim.setDuration(16);
						ANIMATOR.add(centerRadiusAnim);

						parasolGradientAnim = new BasicAnimation<>(INSTANCE, "parasolGradientRadius");
						parasolGradientAnim.setTo(100.0);
						parasolGradientAnim.setEasing(Easing.easeOutQuart);
						parasolGradientAnim.setDuration(16);
						ANIMATOR.add(parasolGradientAnim);

						itemExpansionAnim = new BasicAnimation<>(INSTANCE, "itemExpansion");
						itemExpansionAnim.setTo(0);
						itemExpansionAnim.setEasing(Easing.easeOutQuart);
						itemExpansionAnim.setDuration(16);
						ANIMATOR.add(itemExpansionAnim);

						wasEmpty = true;
					}

					if (centerRadiusAnim.getFinished() && heartBeatAnim.getFinished()) {
						heartBeatAnim = new BasicAnimation<>(INSTANCE, "heartBeatRadius");
						heartBeatAnim.setTo(INSTANCE.centerRadius);
						heartBeatAnim.setFrom(0.0);
						heartBeatAnim.setEasing(Easing.easeOutQuint);
						heartBeatAnim.setDuration(100f);
						ANIMATOR.add(heartBeatAnim);
					}

				} else if (!snapshotPearls.getSecond().isEmpty() && changing) {

					parasolGradientAnim = new BasicAnimation<>(INSTANCE, "parasolGradientRadius");
					parasolGradientAnim.setTo(INSTANCE.centerRadius);
					parasolGradientAnim.setEasing(Easing.easeOutQuart);
					parasolGradientAnim.setCompletion(() -> {
						parasolGradientAnim = new BasicAnimation<>(INSTANCE, "parasolGradientRadius");
						parasolGradientAnim.setTo(120);
						parasolGradientAnim.setEasing(Easing.easeOutQuart);
						parasolGradientAnim.setDuration(8);
						ANIMATOR.add(parasolGradientAnim);
					});
					parasolGradientAnim.setDuration(8);
					ANIMATOR.add(parasolGradientAnim);

					itemExpansionAnim = new BasicAnimation<>(INSTANCE, "itemExpansion");
					itemExpansionAnim.setTo(0);
					itemExpansionAnim.setEasing(Easing.easeOutQuart);
					itemExpansionAnim.setDuration(8);
					itemExpansionAnim.setCompletion(() -> {
						itemExpansionAnim = new BasicAnimation<>(INSTANCE, "itemExpansion");
						itemExpansionAnim.setTo(1f);
						itemExpansionAnim.setEasing(Easing.easeOutQuart);
						itemExpansionAnim.setDuration(8);
						ANIMATOR.add(itemExpansionAnim);
					});
					ANIMATOR.add(itemExpansionAnim);

				} else if (wasEmpty) {
					centerText = ModKeybinds.pearlSwapping.getDisplayName() + " + Scroll to select\na pearl.\n\nRight Click to pull\nthe pearl out.";

					centerRadiusAnim = new BasicAnimation<>(INSTANCE, "centerRadius");
					centerRadiusAnim.setTo(SELECTOR_RADIUS - SELECTOR_WIDTH / 2.0);
					centerRadiusAnim.setEasing(Easing.easeOutQuart);
					centerRadiusAnim.setDuration(16);
					ANIMATOR.add(centerRadiusAnim);

					parasolGradientAnim = new BasicAnimation<>(INSTANCE, "parasolGradientRadius");
					parasolGradientAnim.setTo(INSTANCE.centerRadius + 20);
					parasolGradientAnim.setFrom(INSTANCE.centerRadius);
					parasolGradientAnim.setEasing(Easing.easeOutQuart);
					parasolGradientAnim.setDuration(16);
					ANIMATOR.add(parasolGradientAnim);

					itemExpansionAnim = new BasicAnimation<>(INSTANCE, "itemExpansion");
					itemExpansionAnim.setTo(1f);
					itemExpansionAnim.setEasing(Easing.easeOutQuart);
					itemExpansionAnim.setDuration(16);
					ANIMATOR.add(itemExpansionAnim);

					wasEmpty = false;
				}
			// ------------- ANIMATIONS ------------- //


			// ------------- PARASOL AND PEARL RENDERING ------------- //
			if (!pearls.isEmpty()) {

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

					double innerRadius = INSTANCE.centerRadius + 0.5;
					double outerRadius = INSTANCE.parasolGradientRadius + (INSTANCE.slotRadii[j]);// + (scrollSlot == j ? SELECTOR_SHIFT : 0);

					GlStateManager.pushMatrix();
					int thing1 = GL11.glGetInteger(GL11.GL_ALPHA_TEST_FUNC);
					float thing2 = GL11.glGetFloat(GL11.GL_ALPHA_TEST_REF);

					GlStateManager.enableAlpha();
					GlStateManager.alphaFunc(GL11.GL_ALWAYS, 1);
					GlStateManager.enableBlend();
					GlStateManager.disableTexture2D();
					GlStateManager.shadeModel(GL11.GL_SMOOTH);

					GlStateManager.translate(width / 2.0, height / 2.0, 0);

					bb.begin(GL11.GL_QUADS, DefaultVertexFormats.POSITION_COLOR);
					for (int i = 0; i < numSegmentsPerArc; i++) {
						float currentAngle = i * anglePerSegment + angle;
						bb.pos(innerRadius * MathHelper.cos(currentAngle), innerRadius * MathHelper.sin(currentAngle), 0).color(color.getRed(), color.getGreen(), color.getBlue(), (int) (INSTANCE.itemExpansion * 255)).endVertex();
						bb.pos(innerRadius * MathHelper.cos(currentAngle + anglePerSegment), innerRadius * MathHelper.sin(currentAngle + anglePerSegment), 0).color(color.getRed(), color.getGreen(), color.getBlue(), (int) (INSTANCE.itemExpansion * 255)).endVertex();
						bb.pos(outerRadius * MathHelper.cos(currentAngle + anglePerSegment), outerRadius * MathHelper.sin(currentAngle + anglePerSegment), 0).color(color.getRed(), color.getGreen(), color.getBlue(), 0).endVertex();
						bb.pos(outerRadius * MathHelper.cos(currentAngle), outerRadius * MathHelper.sin(currentAngle), 0).color(color.getRed(), color.getGreen(), color.getBlue(), 0).endVertex();
					}
					tess.draw();

					float centerAngle = angle + anglePerColor / 2;
					Vec3d inner = new Vec3d(innerRadius * MathHelper.cos(centerAngle), innerRadius * MathHelper.sin(centerAngle), 0);
					Vec3d outer = new Vec3d(outerRadius * MathHelper.cos(centerAngle), outerRadius * MathHelper.sin(centerAngle), 0);

					Vec3d center = new Vec3d((inner.x + outer.x) / 2, (inner.y + outer.y) / 2, 0);
					Vec3d normal = center.normalize();

					Vec3d pearlOffset = normal.scale(INSTANCE.centerRadius / 2).scale(INSTANCE.itemExpansion);

					{
						RenderHelper.enableGUIStandardItemLighting();
						GlStateManager.enableRescaleNormal();
						GlStateManager.enableTexture2D();

						GlStateManager.scale(2, 2, 2);
						GlStateManager.translate(pearlOffset.x - 8, pearlOffset.y - 8, 10);
						GlStateManager.color(1f, 1f, 1f, INSTANCE.itemExpansion);

						IBakedModel model = Minecraft.getMinecraft().getRenderItem().getItemModelWithOverrides(pearl, null, Minecraft.getMinecraft().player);

						GlStateManager.pushMatrix();
						Minecraft.getMinecraft().getTextureManager().bindTexture(TextureMap.LOCATION_BLOCKS_TEXTURE);
						Minecraft.getMinecraft().getTextureManager().getTexture(TextureMap.LOCATION_BLOCKS_TEXTURE).setBlurMipmap(false, false);
						GlStateManager.enableRescaleNormal();
						GlStateManager.enableAlpha();
						GlStateManager.alphaFunc(516, 0.1F);
						GlStateManager.enableBlend();
						GlStateManager.blendFunc(GlStateManager.SourceFactor.SRC_ALPHA, GlStateManager.DestFactor.ONE_MINUS_SRC_ALPHA);

						GlStateManager.translate(8.0F, 8.0F, 0.0F);
						GlStateManager.scale(1.0F, -1.0F, 1.0F);
						GlStateManager.scale(16.0F, 16.0F, 16.0F);

						if (model.isGui3d()) GlStateManager.enableLighting();
						else GlStateManager.disableLighting();

						ForgeHooksClient.handleCameraTransforms(model, ItemCameraTransforms.TransformType.GUI, false);

						if (!pearl.isEmpty()) {
							GlStateManager.pushMatrix();
							GlStateManager.translate(-0.5F, -0.5F, -0.5F);

							if (model.isBuiltInRenderer()) {
								GlStateManager.enableRescaleNormal();
								pearl.getItem().getTileEntityItemStackRenderer().renderByItem(pearl);
							} else {
								renderModel(model, 0xFFFFFF | (((int) (INSTANCE.itemExpansion * 255)) << 24), pearl);

								if (pearl.hasEffect()) {
									renderEffect(model);
								}
							}

							GlStateManager.popMatrix();
						}

						GlStateManager.scale(1, 1, 1);

						GlStateManager.disableAlpha();
						GlStateManager.disableRescaleNormal();
						GlStateManager.disableLighting();
						GlStateManager.popMatrix();

						GlStateManager.translate(-pearlOffset.x + 8, -pearlOffset.y + 8, 0);
						Minecraft.getMinecraft().getTextureManager().bindTexture(TextureMap.LOCATION_BLOCKS_TEXTURE);
						Minecraft.getMinecraft().getTextureManager().getTexture(TextureMap.LOCATION_BLOCKS_TEXTURE).restoreLastBlurMipmap();
						GlStateManager.disableRescaleNormal();
						RenderHelper.disableStandardItemLighting();
					}

					GlStateManager.color(1f, 1f, 1f, INSTANCE.itemExpansion);
					GlStateManager.scale(1, 1, 1);

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
						Vec3d chainOffset = chainNormal.scale((INSTANCE.centerRadius / 2.0) * INSTANCE.itemExpansion).add(chainNormal.scale(size).scale(INSTANCE.itemExpansion));

						List<SpellRing> allSpellRings = SpellUtils.getAllSpellRings(rings.get(i));
						for (int k = 0; k < allSpellRings.size(); k++) {
							SpellRing ring = allSpellRings.get(k);
							ModuleInstance module = ring.getModule();
							if (module == null) continue;
							Sprite moduleSprite = new Sprite(module.getIconLocation());

							Vec3d moduleVec = chainOffset.add(chainNormal.scale(size * 1.5).scale(k).scale(INSTANCE.itemExpansion));

							spritePlate.bind();
							spritePlate.draw(0, (float) moduleVec.x - (size / 2f), (float) moduleVec.y - (size / 2f), size, size);

							moduleSprite.bind();
							moduleSprite.draw(0, (float) moduleVec.x - ((size - 1) / 2f), (float) moduleVec.y - ((size - 1) / 2f), size - 1, size - 1);

							if (k + 1 < allSpellRings.size()) {
								SpellRing linksTo = allSpellRings.get(k + 1);
								ModuleInstance linksToModule = linksTo.getModule();
								if (linksToModule == null) continue;
								Vec3d linksToVec = chainOffset.add(chainNormal.scale(size * 1.5).scale(k + 1).scale(INSTANCE.itemExpansion));

								TableModule.drawWire(
										new Vec2d(moduleVec.x, moduleVec.y),
										new Vec2d(linksToVec.x, linksToVec.y),
										TableModule.getColorForModule(module.getModuleType()),
										TableModule.getColorForModule(linksToModule.getModuleType()));
							}
						}
					}


					GlStateManager.disableBlend();
					GlStateManager.disableAlpha();
					GlStateManager.alphaFunc(thing1, thing2);

					GlStateManager.translate(-width / 2.0, -height / 2.0, 0);
					GlStateManager.popMatrix();

					angle += anglePerColor;
				}
			}
			// ------------- PARASOL AND PEARL RENDERING ------------- //
		}
	}

	/**
	 * Reimplement vanilla item so we can draw pearl stacks with opacity support.
	 */
	private static class LightGatheringTransformer extends QuadGatheringTransformer {

		private static final VertexFormat FORMAT = new VertexFormat().addElement(DefaultVertexFormats.TEX_2F).addElement(DefaultVertexFormats.TEX_2S);

		int blockLight, skyLight;

		{
			setVertexFormat(FORMAT);
		}

		boolean hasLighting() {
			return dataLength[1] >= 2;
		}

		@Override
		protected void processQuad() {
			// Reset light data
			blockLight = 0;
			skyLight = 0;
			// Compute average light for all 4 vertices
			for (int i = 0; i < 4; i++) {
				blockLight += (int) ((quadData[1][i][0] * 0xFFFF) / 0x20);
				skyLight += (int) ((quadData[1][i][1] * 0xFFFF) / 0x20);
			}
			// Values must be multiplied by 16, divided by 4 for average => x4
			blockLight *= 4;
			skyLight *= 4;
		}

		// Dummy overrides

		@Override
		public void setQuadTint(int tint) {
		}

		@Override
		public void setQuadOrientation(@NotNull EnumFacing orientation) {
		}

		@Override
		public void setApplyDiffuseLighting(boolean diffuse) {
		}

		@Override
		public void setTexture(@NotNull TextureAtlasSprite texture) {
		}
	}
}
