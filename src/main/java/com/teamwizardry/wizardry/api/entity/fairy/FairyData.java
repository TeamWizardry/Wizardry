package com.teamwizardry.wizardry.api.entity.fairy;

import com.teamwizardry.librarianlib.features.helpers.NBTHelper;
import com.teamwizardry.librarianlib.features.math.interpolate.StaticInterp;
import com.teamwizardry.librarianlib.features.math.interpolate.numeric.InterpFloatInOut;
import com.teamwizardry.librarianlib.features.particle.ParticleBuilder;
import com.teamwizardry.librarianlib.features.particle.ParticleSpawner;
import com.teamwizardry.wizardry.Wizardry;
import com.teamwizardry.wizardry.api.NBTConstants;
import com.teamwizardry.wizardry.api.capability.player.mana.CustomManaCapability;
import com.teamwizardry.wizardry.api.capability.player.mana.IManaCapability;
import com.teamwizardry.wizardry.api.capability.player.mana.ManaCapabilityProvider;
import com.teamwizardry.wizardry.api.spell.SpellRing;
import com.teamwizardry.wizardry.api.util.ColorUtils;
import com.teamwizardry.wizardry.api.util.RandUtil;
import com.teamwizardry.wizardry.client.fx.LibParticles;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.BufferBuilder;
import net.minecraft.client.renderer.GlStateManager;
import net.minecraft.client.renderer.Tessellator;
import net.minecraft.client.renderer.vertex.DefaultVertexFormats;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.nbt.NBTBase;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.nbt.NBTTagList;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.math.MathHelper;
import net.minecraft.util.math.Vec3d;
import net.minecraft.world.World;
import net.minecraftforge.common.capabilities.Capability;
import net.minecraftforge.common.capabilities.ICapabilityProvider;
import net.minecraftforge.common.util.Constants;
import net.minecraftforge.common.util.INBTSerializable;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;
import org.jetbrains.annotations.NotNull;
import org.lwjgl.opengl.GL11;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import java.awt.*;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

import static org.lwjgl.opengl.GL11.GL_ONE;
import static org.lwjgl.opengl.GL11.GL_SRC_ALPHA;

public class FairyData implements INBTSerializable<NBTTagCompound>, ICapabilityProvider {

	public boolean wasTamperedWith = false;

	public Color primaryColor = ColorUtils.generateRandomColor();

	public Color secondaryColor = ColorUtils.generateRandomColor();

	public int age = RandUtil.nextInt(100, 500);

	public boolean isDepressed = false;

	@Nonnull
	public final List<SpellRing> infusedSpell = new ArrayList<>();

	@Nullable
	public UUID owner = null;

	public final IManaCapability handler = new CustomManaCapability(1000, 1000, 0, 0);

	public FairyData() {
	}

	public FairyData(boolean wasTamperedWith, @NotNull Color primaryColor, @NotNull Color secondaryColor, int age, boolean isDepressed, @Nonnull List<SpellRing> infusedSpell, @Nullable UUID owner, @NotNull IManaCapability handler) {
		this.wasTamperedWith = wasTamperedWith;
		this.primaryColor = primaryColor;
		this.secondaryColor = secondaryColor;
		this.age = age;
		this.isDepressed = isDepressed;
		this.infusedSpell.clear();
		this.infusedSpell.addAll(infusedSpell);
		this.owner = owner;
		this.handler.deserializeNBT(handler.serializeNBT());
	}

	@Nullable
	public static FairyData deserialize(NBTTagCompound compound) {
		if (compound == null) return null;
		FairyData fairyData = new FairyData();
		fairyData.deserializeNBT(compound);
		return fairyData;
	}

	@SideOnly(Side.CLIENT)
	public void render(World world, Vec3d pos, Vec3d prevPos, float partialTicks) {
		if (!wasTamperedWith && !isDepressed) {
			LibParticles.FAIRY_HEAD(world, pos.add(0, 0.25, 0), primaryColor);

			ParticleBuilder glitter = new ParticleBuilder(age / 2);
			glitter.setRender(new ResourceLocation(Wizardry.MODID, NBTConstants.MISC.SPARKLE_BLURRED));
			glitter.setAlphaFunction(new InterpFloatInOut(0.2f, 1f));

			if (RandUtil.nextInt(3) == 0)
				ParticleSpawner.spawn(glitter, world, new StaticInterp<>(pos), 1, 0, (i, build) -> {
					build.setMotion(new Vec3d(RandUtil.nextDouble(-0.01, 0.01), RandUtil.nextDouble(-0.01, 0.01), RandUtil.nextDouble(-0.01, 0.01)));
					if (RandUtil.nextBoolean())
						build.setColor(primaryColor);
					else build.setColor(secondaryColor);
					if (isDepressed) {
						build.setCollision(true);
						build.enableMotionCalculation();
						build.setAcceleration(new Vec3d(0, -0.005, 0));
					}
				});
		} else {

			{
				GlStateManager.pushMatrix();

				EntityPlayer player = Minecraft.getMinecraft().player;
				if (player == null) return;

				double interpPosX = player.lastTickPosX + (player.posX - player.lastTickPosX) * partialTicks;
				double interpPosY = player.lastTickPosY + (player.posY - player.lastTickPosY) * partialTicks;
				double interpPosZ = player.lastTickPosZ + (player.posZ - player.lastTickPosZ) * partialTicks;

				GlStateManager.translate(-interpPosX, -interpPosY, -interpPosZ);

				GlStateManager.translate(pos.x, pos.y, pos.z);


				Minecraft.getMinecraft().entityRenderer.disableLightmap();
				GlStateManager.enableDepth();
				GlStateManager.disableCull();
				GlStateManager.enableAlpha();
				GlStateManager.enableBlend();
				GlStateManager.blendFunc(GL_SRC_ALPHA, GL_ONE);
				GlStateManager.tryBlendFuncSeparate(GlStateManager.SourceFactor.SRC_ALPHA, GlStateManager.DestFactor.ONE_MINUS_SRC_ALPHA, GlStateManager.SourceFactor.ONE, GlStateManager.DestFactor.ZERO);
				GlStateManager.color(1, 1, 1, 1);
				GlStateManager.disableTexture2D();
				GlStateManager.enableColorMaterial();
				GlStateManager.disableLighting();
				GlStateManager.shadeModel(GL11.GL_SMOOTH);

				int alphaFunc = GL11.glGetInteger(GL11.GL_ALPHA_TEST_FUNC);
				float alphaTest = GL11.glGetFloat(GL11.GL_ALPHA_TEST_REF);
				GlStateManager.alphaFunc(GL11.GL_ALWAYS, 1);

				GlStateManager.pushMatrix();
				GlStateManager.rotate(-Minecraft.getMinecraft().getRenderManager().playerViewY, 0.0F, 1.0F, 0.0F);
				GlStateManager.rotate((float) (Minecraft.getMinecraft().getRenderManager().options.thirdPersonView == 2 ? -1 : 1) * Minecraft.getMinecraft().getRenderManager().playerViewX, 1.0F, 0.0F, 0.0F);

				Tessellator tessellator = Tessellator.getInstance();
				BufferBuilder bb = tessellator.getBuffer();

				double radius = 0.1;
				Color color = primaryColor;

				bb.begin(GL11.GL_TRIANGLE_FAN, DefaultVertexFormats.POSITION_COLOR);
				for (int i = 0; i <= 50; i++) {
					double x = radius * MathHelper.cos((float) ((i / 50.0) * Math.PI * 2));

					double z = radius * MathHelper.sin((float) ((i / 50.0) * Math.PI * 2));

					bb.pos(x, z, 0).color(color.getRed(), color.getGreen(), color.getBlue(), 255).endVertex();
				}
				tessellator.draw();

				GlStateManager.popMatrix();


				bb.begin(GL11.GL_TRIANGLES, DefaultVertexFormats.POSITION_COLOR);

				Vec3d sub = pos.subtract(prevPos).scale(-1).add(0, 0.2, 0).scale(3);
				Vec3d posCross = sub.crossProduct(new Vec3d(0, 1, 0)).normalize();
				Vec3d posPerp = posCross.crossProduct(sub).normalize().scale(radius - 0.2);

				Vec3d vec1 = posPerp;
				Vec3d vec2 = posPerp.scale(-1);
				bb.pos(sub.x, sub.y, sub.z).color(color.getRed(), color.getGreen(), color.getBlue(), 255).endVertex();
				bb.pos(vec1.x, vec1.y, vec1.z).color(color.getRed(), color.getGreen(), color.getBlue(), 255).endVertex();
				bb.pos(vec2.x, vec2.y, vec2.z).color(color.getRed(), color.getGreen(), color.getBlue(), 255).endVertex();

				Vec3d vec3 = posCross.scale(radius);
				Vec3d vec4 = posCross.scale(-1).scale(radius);
				bb.pos(sub.x, sub.y, sub.z).color(color.getRed(), color.getGreen(), color.getBlue(), 255).endVertex();
				bb.pos(vec3.x, vec3.y, vec3.z).color(color.getRed(), color.getGreen(), color.getBlue(), 255).endVertex();
				bb.pos(vec4.x, vec4.y, vec4.z).color(color.getRed(), color.getGreen(), color.getBlue(), 255).endVertex();

				tessellator.draw();

				GlStateManager.pushMatrix();
				GlStateManager.rotate(-Minecraft.getMinecraft().getRenderManager().playerViewY, 0.0F, 1.0F, 0.0F);
				GlStateManager.rotate((float) (Minecraft.getMinecraft().getRenderManager().options.thirdPersonView == 2 ? -1 : 1) * Minecraft.getMinecraft().getRenderManager().playerViewX, 1.0F, 0.0F, 0.0F);


				int sections = 50;
				float epsilon = 2 * (float) Math.PI / sections;

				double innerRadius = radius;
				double outerRadius = radius + 0.01;
				Color inner = primaryColor;
				Color outer = new Color(primaryColor.getRed(), primaryColor.getGreen(), primaryColor.getBlue(), 0);
				bb.begin(GL11.GL_TRIANGLE_STRIP, DefaultVertexFormats.POSITION_COLOR);
				for (int i = 0; i <= sections; i++) {
					float angle = i * epsilon;
					float nextAngle = (i + 1) * epsilon;

					float x1 = (float) (MathHelper.cos(angle) * innerRadius);
					float y1 = (float) (MathHelper.sin(angle) * innerRadius);

					float x2 = (float) (MathHelper.cos(nextAngle) * outerRadius);
					float y2 = (float) (MathHelper.sin(nextAngle) * outerRadius);

					bb.pos(x1, y1, 0).color(inner.getRed(), inner.getGreen(), inner.getBlue(), inner.getAlpha()).endVertex();
					bb.pos(x2, y2, 0).color(outer.getRed(), outer.getGreen(), outer.getBlue(), outer.getAlpha()).endVertex();
				}
				tessellator.draw();

				radius = 0.2;
				color = secondaryColor;

				GlStateManager.translate(0, 0, 0.01);
				bb.begin(GL11.GL_TRIANGLE_FAN, DefaultVertexFormats.POSITION_COLOR);
				bb.pos(0, 0, 0).color(color.getRed(), color.getGreen(), color.getBlue(), 100).endVertex();
				for (int i = 0; i <= 50; i++) {
					double x = radius * MathHelper.cos((float) ((i / 50.0) * Math.PI * 2));

					double z = radius * MathHelper.sin((float) ((i / 50.0) * Math.PI * 2));

					bb.pos(x, z, 0).color(color.getRed(), color.getGreen(), color.getBlue(), 0).endVertex();
				}
				tessellator.draw();
				GlStateManager.translate(0, 0, -0.01);


				GlStateManager.alphaFunc(alphaFunc, alphaTest);
				GlStateManager.disableBlend();
				GlStateManager.enableAlpha();
				GlStateManager.enableTexture2D();
				GlStateManager.disableColorMaterial();

				Minecraft.getMinecraft().entityRenderer.enableLightmap();
				GlStateManager.enableDepth();
				GlStateManager.popMatrix();

				GlStateManager.popMatrix();
			}
		}
	}

	@Override
	public boolean hasCapability(@Nonnull Capability<?> capability, @Nullable EnumFacing facing) {
		return capability == ManaCapabilityProvider.manaCapability;
	}

	@Nullable
	@Override
	public <T> T getCapability(@Nonnull Capability<T> capability, @Nullable EnumFacing facing) {
		return capability == ManaCapabilityProvider.manaCapability ? (T) handler : null;
	}

	@Override
	public NBTTagCompound serializeNBT() {
		NBTTagCompound compound = new NBTTagCompound();

		compound.setBoolean("tampered_with", wasTamperedWith);
		compound.setInteger("primary_color", primaryColor.getRGB());
		compound.setInteger("secondary_color", secondaryColor.getRGB());
		compound.setInteger("age", age);
		compound.setBoolean("depressed", isDepressed);

		if (owner != null)
			NBTHelper.setUniqueId(compound, "owner_id", owner);

		NBTHelper.setCompoundTag(compound, "mana_capability", handler.serializeNBT());

		NBTTagList list = new NBTTagList();
		for (SpellRing chain : infusedSpell) {
			list.appendTag(chain.serializeNBT());
		}
		NBTHelper.setTagList(compound, "infused_spell", list);

		return compound;
	}

	@Override
	public void deserializeNBT(NBTTagCompound nbt) {
		if (nbt == null) return;

		wasTamperedWith = NBTHelper.getBoolean(nbt, "tampered_with", wasTamperedWith);

		isDepressed = NBTHelper.getBoolean(nbt, "depressed", isDepressed);

		primaryColor = new Color(NBTHelper.getInteger(nbt, "primary_color", 0xFFFFFF));

		secondaryColor = new Color(NBTHelper.getInteger(nbt, "secondary_color", 0xFFFFFF));

		age = NBTHelper.getInteger(nbt, "age", age);

		owner = NBTHelper.getUniqueId(nbt, "owner_id");

		if (nbt.hasKey("mana_capability"))
			handler.deserializeNBT(NBTHelper.getCompoundTag(nbt, "mana_capability"));

		infusedSpell.clear();
		NBTTagList list = NBTHelper.getTagList(nbt, "infused_spell", Constants.NBT.TAG_COMPOUND);
		if (list != null) {
			for (NBTBase base : list) {
				if (base instanceof NBTTagCompound) {
					NBTTagCompound compound = (NBTTagCompound) base;

					SpellRing ring = SpellRing.deserializeRing(compound);
					infusedSpell.add(ring);
				}
			}
		}
	}
}
