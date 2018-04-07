package com.teamwizardry.wizardry.client.render.item;

import com.teamwizardry.librarianlib.core.LibrarianLib;
import com.teamwizardry.librarianlib.features.animator.Animator;
import com.teamwizardry.librarianlib.features.animator.Easing;
import com.teamwizardry.librarianlib.features.animator.animations.BasicAnimation;
import com.teamwizardry.librarianlib.features.helpers.ItemNBTHelper;
import com.teamwizardry.librarianlib.features.sprite.Sprite;
import com.teamwizardry.wizardry.Wizardry;
import com.teamwizardry.wizardry.api.Constants;
import com.teamwizardry.wizardry.api.spell.SpellUtils;
import com.teamwizardry.wizardry.api.spell.module.Module;
import com.teamwizardry.wizardry.api.util.RandUtil;
import com.teamwizardry.wizardry.client.gui.book.GuiBook;
import com.teamwizardry.wizardry.init.ModItems;
import net.minecraft.client.Minecraft;
import net.minecraft.client.entity.AbstractClientPlayer;
import net.minecraft.client.entity.EntityPlayerSP;
import net.minecraft.client.gui.FontRenderer;
import net.minecraft.client.model.ModelBook;
import net.minecraft.client.renderer.BufferBuilder;
import net.minecraft.client.renderer.GlStateManager;
import net.minecraft.client.renderer.RenderItem;
import net.minecraft.client.renderer.Tessellator;
import net.minecraft.client.renderer.vertex.DefaultVertexFormats;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagList;
import net.minecraft.util.EnumHand;
import net.minecraft.util.EnumHandSide;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.math.MathHelper;
import net.minecraft.util.text.TextFormatting;
import net.minecraftforge.client.event.RenderSpecificHandEvent;
import net.minecraftforge.fml.client.config.GuiUtils;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;
import org.apache.commons.lang3.StringUtils;
import org.lwjgl.opengl.GL11;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

/**
 * CODE COPIED FROM BOTANIA & HEAVILY HEAVILY HEAVILY HEAVILY MODIFIED
 * https://github.com/Vazkii/Botania/blob/master/src/main/java/vazkii/botania/client/core/handler/RenderLexicon.java
 * <p>
 * Please do not touch this code.
 * These are dangerous lands you are exploring
 * Take this code fairy with you
 * <p>
 * ~          .--.   _,
 * ~      .--;    \ /(_
 * ~     /    '.   |   '-._    . ' .
 * ~    |       \  \    ,-.)  -= * =-
 * ~     \ /\_   '. \((` .(    '/. '
 * ~      )\ /     \ )\  _/   _/
 * ~     /  \\    .-'   '--. /_\
 * ~    |    \\_.' ,        \/||
 * ~    \     \_.-';,_) _)'\ \||
 * ~     '.       /`\   (   '._/
 * ~       `\   .;  |  . '.
 * ~         ).'  )/|      \
 * ~         `    ` |  \|   |
 * ~                 \  |   |
 * ~                  '.|   |
 * ~                     \  '\__
 * ~                      `-._  '. _
 * ~                         \`;-.` `._
 * ~                          \ \ `'-._\
 * ~                           \ |
 * ~                            \ )
 * ~                             \_\
 */
@Mod.EventBusSubscriber(value = Side.CLIENT, modid = Wizardry.MODID)
@SideOnly(Side.CLIENT)
public class RenderCodex {

	private static final ModelBook model = new ModelBook();
	private static final ResourceLocation texture = new ResourceLocation(Wizardry.MODID, "textures/model/book.png");
	private static final Sprite arrowSprite = new Sprite(new ResourceLocation(Wizardry.MODID, "textures/gui/arrow.png"));
	public static RenderCodex INSTANCE = new RenderCodex();
	public Animator animator = new Animator();
	public float openingCooldownLeft = 0;
	public float tooltipCooldown = 40;
	public float idleXLeft = 0, idleYLeft = 0, idleZLeft = 0;
	public float openingCooldownRight = 0;
	public float pageFlipCooldownRight = 0;
	public float idleXRight = 0, idleYRight = 0, idleZRight = 0;
	private Set<String> animatingFields = new HashSet<>();

	private RenderCodex() {
	}

	@SubscribeEvent
	public static void renderItem(RenderSpecificHandEvent evt) {
		Minecraft mc = Minecraft.getMinecraft();
		if (mc.gameSettings.thirdPersonView != 0
				|| mc.player.getHeldItem(evt.getHand()).isEmpty()
				|| mc.player.getHeldItem(evt.getHand()).getItem() != ModItems.BOOK)
			return;
		evt.setCanceled(true);
		try {
			renderItemInFirstPerson(mc.player, evt.getHand(), evt.getSwingProgress(), evt.getItemStack(), evt.getEquipProgress());
		} catch (Throwable throwable) {
			Wizardry.logger.warn("Failed to render book in hand");
		}
	}

	private static void renderItemInFirstPerson(AbstractClientPlayer player, EnumHand hand, float swingProgress, ItemStack stack, float equipProgress) {
		// Cherry picked from ItemRenderer.renderItemInFirstPerson
		boolean flag = hand == EnumHand.MAIN_HAND;
		EnumHandSide enumhandside = flag ? player.getPrimaryHand() : player.getPrimaryHand().opposite();
		GlStateManager.pushMatrix();
		boolean flag1 = enumhandside == EnumHandSide.RIGHT;
		float f = -0.4F * MathHelper.sin(MathHelper.sqrt(swingProgress) * (float) Math.PI);
		float f1 = 0.2F * MathHelper.sin(MathHelper.sqrt(swingProgress) * ((float) Math.PI * 2F));
		float f2 = -0.2F * MathHelper.sin(swingProgress * (float) Math.PI);
		int i = flag1 ? 1 : -1;
		GlStateManager.translate(i * f, f1, f2);
		transformSideFirstPerson(enumhandside, equipProgress);
		transformFirstPerson(enumhandside, swingProgress);
		RenderCodex.INSTANCE.doRender(enumhandside, stack);
		GlStateManager.popMatrix();
	}

	// Copy - ItemRenderer.transformSideFirstPerson
	// Arg - Side, EquipProgress
	private static void transformSideFirstPerson(EnumHandSide p_187459_1_, float p_187459_2_) {
		int i = p_187459_1_ == EnumHandSide.RIGHT ? 1 : -1;
		GlStateManager.translate(i * 0.56F, -0.44F + p_187459_2_ * -0.8F, -0.72F);
	}

	// Copy with modification - ItemRenderer.transformFirstPerson
	// Arg - Side, SwingProgress
	private static void transformFirstPerson(EnumHandSide p_187453_1_, float p_187453_2_) {
		int i = p_187453_1_ == EnumHandSide.RIGHT ? 1 : -1;
		// Botania - added
		GlStateManager.translate(p_187453_1_ == EnumHandSide.RIGHT ? 0.2F : 0.52F, -0.125F, p_187453_1_ == EnumHandSide.RIGHT ? 0.6F : 0.25F);
		GlStateManager.rotate(p_187453_1_ == EnumHandSide.RIGHT ? 60F : 120F, 0F, 1F, 0F);
		GlStateManager.rotate(30F, 0F, 0F, -1F);
		// End add
		float f = MathHelper.sin(p_187453_2_ * p_187453_2_ * (float) Math.PI);
		GlStateManager.rotate(i * (45.0F + f * -20.0F), 0.0F, 1.0F, 0.0F);
		float f1 = MathHelper.sin(MathHelper.sqrt(p_187453_2_) * (float) Math.PI);
		GlStateManager.rotate(i * f1 * -20.0F, 0.0F, 0.0F, 1.0F);
		GlStateManager.rotate(f1 * -80.0F, 1.0F, 0.0F, 0.0F);
		GlStateManager.rotate(i * -45.0F, 0.0F, 1.0F, 0.0F);
	}

	private void doRender(EnumHandSide side, ItemStack stack) {
		Minecraft mc = Minecraft.getMinecraft();

		boolean isGuiOpen = mc.currentScreen instanceof GuiBook;
		boolean rightHand = side == EnumHandSide.RIGHT;
		boolean hasSpell = ItemNBTHelper.getBoolean(stack, "has_spell", false);

		final String openingTagRight = "openingCooldownRight";
		final String openingTagLeft = "openingCooldownLeft";

		final String openingTag = rightHand ? openingTagRight : openingTagLeft;
		if (isGuiOpen || hasSpell) {
			if (!animatingFields.contains(openingTag)) {
				animatingFields.add(openingTag);
				BasicAnimation<RenderCodex> anim = new BasicAnimation<>(INSTANCE, openingTag);
				anim.setTo(10);
				anim.setDuration(20);
				anim.setEasing(Easing.easeOutCubic);
				anim.setCompletion(() -> {
					if (animatingFields.contains(openingTag))
						animatingFields.remove(openingTag);
				});
				animator.add(anim);
			}

		} else {
			if (!animatingFields.contains(openingTag)) {
				animatingFields.add(openingTag);
				BasicAnimation<RenderCodex> anim = new BasicAnimation<>(INSTANCE, openingTag);
				anim.setTo(0);
				anim.setDuration(20);
				anim.setEasing(Easing.easeOutCubic);
				anim.setCompletion(() -> {
					if (animatingFields.contains(openingTag))
						animatingFields.remove(openingTag);
				});
				animator.add(anim);
			}
		}

		animateIdle("idleXRight");
		animateIdle("idleYRight");
		animateIdle("idleZRight");
		animateIdle("idleXLeft");
		animateIdle("idleYLeft");
		animateIdle("idleZLeft");

		GlStateManager.pushMatrix();
		GlStateManager.color(1F, 1F, 1F);

		double flip = rightHand ? 1 : -1;
		float openingCooldown = rightHand ? openingCooldownRight : openingCooldownLeft;
		float idleX = rightHand ? idleXRight : idleXLeft;
		float idleY = rightHand ? idleYRight : idleYLeft;
		float idleZ = rightHand ? idleZRight : idleZLeft;

		// X axis is Z axis
		// Z axis is X axis
		// Everything is reversed
		GlStateManager.translate(
				0.3F + 0.02F * openingCooldown + (openingCooldown / 50.0) + ((10 - openingCooldown) / 50.0F) + (rightHand ? -0.1F : 0),
				0.475F + 0.01F * openingCooldown + (!rightHand ? 0.02F : 0) + ((10 - openingCooldown) / 100.0F),
				-0.2F - (rightHand ? 0.035F : 0.01F) * openingCooldown + (flip * openingCooldown / 50.0) + (!rightHand ? ((openingCooldown / 10.0F) * 0.08F - 0.03F) : 0));

		// 87.5 rotates from facing the book's spine to the cover facing the player
		// Rotates around book spine
		GlStateManager.rotate((rightHand ? 87.5F : ((openingCooldown / 10.0F) * -20 + 90)) + openingCooldown * (rightHand ? 8 : 12) + (idleY * 5f), 0F, 1F, 0F);

		// Rotates around the CENTER of the book's spine horizontally.
		// IE: Transverse section of the book.
		// Point ur hand horizontally and slide it horizontally.
		// Will rotate the book backwards as it opens, adding a 3d effect
		GlStateManager.rotate(idleZ * 5f, 0F, 0F, 1F);

		mc.renderEngine.bindTexture(texture);
		model.render(null, 0F, 0F, pageFlipCooldownRight, openingCooldown / 12F, 0F, 1F / 16F);

		FontRenderer font = Minecraft.getMinecraft().fontRenderer;
		boolean prevFlag = font.getUnicodeFlag();

		GlStateManager.rotate(180F, 0F, 0F, 1F);

		if (openingCooldown < 3) {
			font.setUnicodeFlag(false);

			GlStateManager.pushMatrix();

			GlStateManager.disableLighting();

			GlStateManager.translate(-0.30F, -0.24F, -0.07F);
			GlStateManager.scale(0.003F, 0.003F, -0.003F);
			GlStateManager.translate(-2, 0, 0); // Yes. I'm nudging it. This is how horrible this gl state is.

			// Below logo
			GlStateManager.translate(0, 70 - font.FONT_HEIGHT, 0);

			String[] title = LibrarianLib.PROXY.translate("wizardry.book.title").split(" ");
			for (String aTitle : title) {
				GlStateManager.translate(0, font.FONT_HEIGHT, 0);

				String titleText = font.trimStringToWidth(aTitle, 80);
				int width = font.getStringWidth(titleText);
				int max = 80;
				double x = max / 2.0 - width / 2.0;
				GlStateManager.translate(x, 0, 0);
				font.drawString(titleText, 0, 0, 0x00FFFF);
				GlStateManager.translate(-x, 0, 0);
			}

			GlStateManager.translate(0, font.FONT_HEIGHT, 0);

			String subtitle = font.trimStringToWidth(LibrarianLib.PROXY.translate("wizardry.book.subtitle"), 80);
			double scale = 2.6;
			GlStateManager.translate((80F - font.getStringWidth(subtitle)) * scale, 0F, 0F);
			GlStateManager.scale(0.6F, 0.6F, 0.6F);
			font.drawString(TextFormatting.ITALIC + subtitle, 0, 0, 0x00FFFF);

			GlStateManager.enableLighting();
			GlStateManager.popMatrix();
		}

		// In both pages: 0,0 is the top left corner
		// You're welcome.
		if (hasSpell && openingCooldown > 6) {
			font.setUnicodeFlag(true);

			// LEFT PAGE
			{
				GlStateManager.pushMatrix();

				GlStateManager.rotate(60, 0, 1, 0);
				GlStateManager.translate(0, 0.1, 0);

				GlStateManager.translate(-0.30F, -0.24F, -0.07F);
				GlStateManager.scale(0.003F, 0.003F, -0.003F);

				GlStateManager.translate(-10, -30, -5);

				// Draw tooltip above page here
				if (rightHand) {
					GlStateManager.pushMatrix();
					GlStateManager.translate(0, -20, -30);
					drawTooltip(LibrarianLib.PROXY.translate("wizardry.book.shift_scroll"), font);
					GlStateManager.popMatrix();
				}

				String[] lines = getSpellStructureLines(stack);
				for (int i = 0; i < lines.length; i++)
					font.drawString(lines[i], 0, i * font.FONT_HEIGHT, 0x00);

				GlStateManager.popMatrix();
			}
			// RIGHT PAGE
			{
				GlStateManager.pushMatrix();

				GlStateManager.rotate(120, 0, 1, 0);
				GlStateManager.translate(0, 0.1, 0);

				GlStateManager.translate(-0.30F, -0.24F, -0.07F);
				GlStateManager.scale(0.003F, 0.003F, -0.003F);

				GlStateManager.translate(120, -25, 0);

				// Translate a little extra bit to fit the recipe cozily
				GlStateManager.translate(-5, -5, 0);

				// Draw tooltip above page here
				if (!rightHand)
					drawTooltip(LibrarianLib.PROXY.translate("wizardry.book.shift_scroll"), font);

				List<ItemStack> inventory = getSpellInventory(stack);
				int currentPage = ItemNBTHelper.getInt(stack, "page", 0);

				int row = 0;
				int column = 0;
				int pageNb = 0;
				for (int i = 0; i < inventory.size(); i++) {
					ItemStack recipeStack = inventory.get(i);
					if (recipeStack.isEmpty()) continue;

					if (pageNb >= currentPage) {

						GlStateManager.pushMatrix();
						GlStateManager.translate(column * 32, row * 16, -150);

						renderItemStack(recipeStack);

						GlStateManager.popMatrix();

						if (i != inventory.size() - 1 && column < 3) {
							GlStateManager.pushMatrix();
							GlStateManager.translate(32 + column * 32, row * 16 + 13, 0);

							GlStateManager.rotate(180, 0, 0, 1);
							GlStateManager.color(0, 1f, 1f, 1f);
							GlStateManager.disableLighting();

							arrowSprite.bind();
							arrowSprite.draw(0, 0, 0, 16, 8);

							GlStateManager.enableLighting();

							GlStateManager.popMatrix();
						}

					}

					if (++column >= 3) {
						column = 0;
						row++;
					}

					if (row >= 9) {
						row = 0;
						pageNb++;

						if (pageNb > currentPage) break;
					}

				}

				GlStateManager.popMatrix();
			}
		}

		font.setUnicodeFlag(prevFlag);
		GlStateManager.popMatrix();
	}

	public void drawTooltip(String text, FontRenderer font) {
		GlStateManager.pushMatrix();

		GlStateManager.translate(0, -25, 0);

		//GlStateManager.disableRescaleNormal();
		//RenderHelper.disableStandardItemLighting();
		GlStateManager.disableLighting();
		GlStateManager.disableDepth();

		boolean previousUnicode = font.getUnicodeFlag();
		font.setUnicodeFlag(false);

		int backgroundColor = 0xF0100010;
		//Color color = Color.decode("0xF0100010");
		//color = new Color(color.getRed(), color.getGreen(), color.getBlue(), (int) (tooltipCooldown / 40.0 * 255.0));
		//backgroundColor = color.getRGB();

		int tooltipTextWidth = font.getStringWidth(text);
		int tooltipHeight = 8;
		GuiUtils.drawGradientRect(0, 0 - 3, 0 - 4, tooltipTextWidth + 3, 0 - 3, backgroundColor, backgroundColor);
		GuiUtils.drawGradientRect(0, 0 - 3, tooltipHeight + 3, tooltipTextWidth + 3, tooltipHeight + 4, backgroundColor, backgroundColor);
		GuiUtils.drawGradientRect(0, 0 - 3, 0 - 3, tooltipTextWidth + 3, tooltipHeight + 3, backgroundColor, backgroundColor);
		GuiUtils.drawGradientRect(0, 0 - 4, 0 - 3, 0 - 3, tooltipHeight + 3, backgroundColor, backgroundColor);
		GuiUtils.drawGradientRect(0, tooltipTextWidth + 3, 0 - 3, tooltipTextWidth + 4, tooltipHeight + 3, backgroundColor, backgroundColor);
		final int borderColorStart = 0x505000FF;
		final int borderColorEnd = (borderColorStart & 0xFEFEFE) >> 1 | borderColorStart & 0xFF000000;
		GuiUtils.drawGradientRect(0, 0 - 3, 0 - 3 + 1, 0 - 3 + 1, tooltipHeight + 3 - 1, borderColorStart, borderColorEnd);
		GuiUtils.drawGradientRect(0, tooltipTextWidth + 2, 0 - 3 + 1, tooltipTextWidth + 3, tooltipHeight + 3 - 1, borderColorStart, borderColorEnd);
		GuiUtils.drawGradientRect(0, 0 - 3, 0 - 3, tooltipTextWidth + 3, 0 - 3 + 1, borderColorStart, borderColorStart);
		GuiUtils.drawGradientRect(0, 0 - 3, tooltipHeight + 2, tooltipTextWidth + 3, tooltipHeight + 3, borderColorEnd, borderColorEnd);

		font.drawStringWithShadow(text, 0, 0, 0xFFD700);
		font.setUnicodeFlag(previousUnicode);

		GlStateManager.enableDepth();
		GlStateManager.enableAlpha();
		GlStateManager.enableBlend();
		GlStateManager.disableRescaleNormal();
		GlStateManager.enableTexture2D();
		GlStateManager.tryBlendFuncSeparate(GlStateManager.SourceFactor.SRC_ALPHA, GlStateManager.DestFactor.ONE_MINUS_SRC_ALPHA, GlStateManager.SourceFactor.ONE, GlStateManager.DestFactor.ZERO);
		GlStateManager.shadeModel(GL11.GL_SMOOTH);
		GlStateManager.enableLighting();

		GlStateManager.popMatrix();
	}

	public void animateIdle(String tag) {
		if (!animatingFields.contains(tag) && RandUtil.nextBoolean()) {
			animatingFields.add(tag);
			BasicAnimation<RenderCodex> anim = new BasicAnimation<>(INSTANCE, tag);
			anim.setTo(RandUtil.nextFloat(0, 1));
			anim.setDuration(RandUtil.nextInt(80, 120));
			anim.setEasing(Easing.easeInOutSine);
			anim.setCompletion(() -> {
				if (animatingFields.contains(tag))
					animatingFields.remove(tag);
			});
			animator.add(anim);
		}
	}

	public String[] getSpellStructureLines(ItemStack stack) {
		NBTTagList moduleList = ItemNBTHelper.getList(stack, Constants.NBT.SPELL, net.minecraftforge.common.util.Constants.NBT.TAG_STRING);
		if (moduleList == null) return new String[0];

		List<List<Module>> spellModules = SpellUtils.deserializeModuleList(moduleList);
		spellModules = SpellUtils.getEssentialModules(spellModules);
		int page = ItemNBTHelper.getInt(stack, "page", 0);

		FontRenderer fr = Minecraft.getMinecraft().fontRenderer;

		int widthOfSpace = fr.getStringWidth(" ");
		StringBuilder builder = new StringBuilder("Spell Structure:\n");
		for (List<Module> spellModuleList : spellModules) {
			String margin = null;
			for (Module module : spellModuleList) {
				if (margin == null) {
					margin = " - ";
					builder.append(margin).append(module.getReadableName()).append("\n");
				} else {
					int realLength = fr.getStringWidth(margin);
					int nbOfSpace = MathHelper.clamp(realLength / widthOfSpace, 0, 17);
					margin = StringUtils.repeat(" ", nbOfSpace) + "|_ ";
					builder.append(margin).append(module.getReadableName()).append("\n");

					if (nbOfSpace >= 16) {
						builder.append("   ________________|")
								.append("\n");
						margin = "   ";
					}
				}
			}
		}

		String[] lines = builder.toString().split("\n");
		StringBuilder pageChunk = new StringBuilder();
		int count = 0;
		int currentPage = 0;
		for (String line : lines) {
			pageChunk.append(line).append("\n");

			if (++count >= 16) {
				count = 0;

				if (currentPage >= page) return pageChunk.toString().split("\n");
				pageChunk = new StringBuilder();
			}
		}

		return pageChunk.toString().split("\n");
	}

	public List<ItemStack> getSpellInventory(ItemStack stack) {
		if (!ItemNBTHelper.getBoolean(stack, "has_spell", false)) return new ArrayList<>();

		NBTTagList spellList = ItemNBTHelper.getList(stack, Constants.NBT.SPELL, net.minecraftforge.common.util.Constants.NBT.TAG_STRING);
		if (spellList == null) return new ArrayList<>();

		return SpellUtils.getSpellItems(SpellUtils.deserializeModuleList(spellList));
	}

	// Manually rendering itemstack overlays because mojang set all their states
	// to disabled depth which doesn't work for us
	public void renderItemStack(ItemStack stack) {
		GlStateManager.enableAlpha();
		GlStateManager.enableBlend();
		GlStateManager.enableRescaleNormal();

		GlStateManager.pushMatrix();

		RenderItem itemRender = Minecraft.getMinecraft().getRenderItem();
		itemRender.renderItemAndEffectIntoGUI(stack, 0, 0);

		GlStateManager.translate(0, 0, 150);

		FontRenderer font = stack.getItem().getFontRenderer(stack);
		if (font == null) font = Minecraft.getMinecraft().fontRenderer;

		if (font != null && stack.getCount() != 1) {
			GlStateManager.disableLighting();
			GlStateManager.disableBlend();
			font.drawStringWithShadow(stack.getCount() + "",
					17F - font.getStringWidth(stack.getCount() + ""),
					9F,
					0xFFFFFF);
			GlStateManager.enableDepth();
			GlStateManager.enableBlend();
		}

		if (stack.getItem().showDurabilityBar(stack)) {
			GlStateManager.disableLighting();
			GlStateManager.disableTexture2D();
			GlStateManager.disableAlpha();
			GlStateManager.disableBlend();
			Tessellator tessellator = Tessellator.getInstance();
			BufferBuilder bufferbuilder = tessellator.getBuffer();
			double health = stack.getItem().getDurabilityForDisplay(stack);
			int rgbfordisplay = stack.getItem().getRGBDurabilityForDisplay(stack);
			int i = Math.round(13.0F - (float) health * 13.0F);
			draw(bufferbuilder, 2, 13, 13, 2, 0, 0, 0, 255);
			draw(bufferbuilder, 2, 13, i, 1, rgbfordisplay >> 16 & 255, rgbfordisplay >> 8 & 255, rgbfordisplay & 255, 255);
			GlStateManager.enableBlend();
			GlStateManager.enableAlpha();
			GlStateManager.enableTexture2D();
			GlStateManager.enableLighting();
		}


		EntityPlayerSP entityplayersp = Minecraft.getMinecraft().player;
		float f3 = entityplayersp == null ? 0.0F : entityplayersp.getCooldownTracker().getCooldown(stack.getItem(), Minecraft.getMinecraft().getRenderPartialTicks());

		if (f3 > 0.0F) {
			GlStateManager.disableLighting();
			GlStateManager.disableTexture2D();
			Tessellator tessellator1 = Tessellator.getInstance();
			BufferBuilder bufferbuilder1 = tessellator1.getBuffer();
			this.draw(bufferbuilder1, 0, MathHelper.floor(16.0F * (1.0F - f3)), 16, MathHelper.ceil(16.0F * f3), 255, 255, 255, 127);
			GlStateManager.enableTexture2D();
			GlStateManager.enableLighting();
		}

		GlStateManager.popMatrix();

		GlStateManager.enableLighting();
		GlStateManager.enableAlpha();
		GlStateManager.enableBlend();
		GlStateManager.disableRescaleNormal();
	}

	// Copied from RenderItem because it was set to private
	// Fuck you Mojang
	private void draw(BufferBuilder renderer, int x, int y, int width, int height, int red, int green, int blue, int alpha) {
		renderer.begin(7, DefaultVertexFormats.POSITION_COLOR);
		renderer.pos((double) (x), (double) (y), 0.0D).color(red, green, blue, alpha).endVertex();
		renderer.pos((double) (x), (double) (y + height), 0.0D).color(red, green, blue, alpha).endVertex();
		renderer.pos((double) (x + width), (double) (y + height), 0.0D).color(red, green, blue, alpha).endVertex();
		renderer.pos((double) (x + width), (double) (y), 0.0D).color(red, green, blue, alpha).endVertex();
		Tessellator.getInstance().draw();
	}
}
