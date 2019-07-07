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
import net.minecraft.nbt.NBTBase;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.nbt.NBTTagList;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.math.Vec3d;
import net.minecraft.world.World;
import net.minecraftforge.common.capabilities.Capability;
import net.minecraftforge.common.capabilities.ICapabilityProvider;
import net.minecraftforge.common.util.Constants;
import net.minecraftforge.common.util.INBTSerializable;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;
import org.jetbrains.annotations.NotNull;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import java.awt.*;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

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
	public void render(World world, Vec3d pos, float partialTicks) {
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
		} else if (world.getTotalWorldTime() % 2 == 0) {
			float excitement = (float) (handler.getMana() / handler.getMaxMana()) * (isDepressed ? 0 : 1);

			Color color = primaryColor;
			ParticleBuilder glitter = new ParticleBuilder((int) (RandUtil.nextInt(3, 5) + (20 * (1 - excitement))));
			glitter.setColor(color);
			glitter.setRender(new ResourceLocation(Wizardry.MODID, NBTConstants.MISC.SPARKLE_BLURRED));
			glitter.setAlphaFunction(new InterpFloatInOut(0.2f, 1f));
			glitter.setScale(0.3f + (excitement * 3));
			if (RandUtil.nextBoolean())
				glitter.setColor(primaryColor);
			else glitter.setColor(secondaryColor);

			if (isDepressed) {
				glitter.enableMotionCalculation();
				glitter.setCollision(true);
				glitter.setAcceleration(new Vec3d(0, -0.002, 0));
			}

			ParticleSpawner.spawn(glitter, world, new StaticInterp<>(pos), 1);
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
