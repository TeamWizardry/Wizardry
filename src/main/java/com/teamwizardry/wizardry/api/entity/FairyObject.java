package com.teamwizardry.wizardry.api.entity;

import com.teamwizardry.librarianlib.features.math.interpolate.StaticInterp;
import com.teamwizardry.librarianlib.features.math.interpolate.numeric.InterpFloatInOut;
import com.teamwizardry.librarianlib.features.particle.ParticleBuilder;
import com.teamwizardry.librarianlib.features.particle.ParticleSpawner;
import com.teamwizardry.librarianlib.features.saving.AbstractSaveHandler;
import com.teamwizardry.librarianlib.features.saving.Savable;
import com.teamwizardry.librarianlib.features.saving.Save;
import com.teamwizardry.wizardry.Wizardry;
import com.teamwizardry.wizardry.api.Constants;
import com.teamwizardry.wizardry.api.capability.mana.CustomWizardryCapability;
import com.teamwizardry.wizardry.api.capability.mana.IWizardryCapability;
import com.teamwizardry.wizardry.api.capability.mana.WizardryCapabilityProvider;
import com.teamwizardry.wizardry.api.spell.SpellRing;
import com.teamwizardry.wizardry.api.util.RandUtil;
import com.teamwizardry.wizardry.client.fx.LibParticles;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.math.Vec3d;
import net.minecraft.world.World;
import net.minecraftforge.common.capabilities.Capability;
import net.minecraftforge.common.capabilities.ICapabilityProvider;
import net.minecraftforge.common.util.INBTSerializable;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;
import org.jetbrains.annotations.NotNull;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import java.awt.*;
import java.util.UUID;

@Savable
public class FairyObject implements INBTSerializable<NBTTagCompound>, ICapabilityProvider {

	@Save
	public boolean wasTamperedWith = false;

	@Save
	public Color primaryColor = Color.WHITE;

	@Save
	public Color secondaryColor = Color.WHITE;

	@Save
	public int age = 0;

	@Save
	public boolean isDepressed = false;

	@Save
	@Nullable
	public SpellRing infusedSpell = null;

	@Save
	@Nullable
	public UUID owner = null;

	@Save
	public IWizardryCapability handler = new CustomWizardryCapability(1000, 1000, 0, 0);

	public FairyObject() {
	}

	public FairyObject(boolean wasTamperedWith, @NotNull Color primaryColor, @NotNull Color secondaryColor, int age, boolean isDepressed, @Nullable SpellRing infusedSpell, UUID owner, @NotNull IWizardryCapability handler) {
		this.wasTamperedWith = wasTamperedWith;
		this.primaryColor = primaryColor;
		this.secondaryColor = secondaryColor;
		this.age = age;
		this.isDepressed = isDepressed;
		this.infusedSpell = infusedSpell;
		this.owner = owner;
		this.handler = handler;
	}

	@Nullable
	public static FairyObject deserialize(NBTTagCompound compound) {
		if (compound == null) return null;
		if (!compound.hasKey("save")) return null;
		FairyObject fairyObject = new FairyObject();
		fairyObject.deserializeNBT(compound);
		return fairyObject;
	}

	@SideOnly(Side.CLIENT)
	public void render(World world, Vec3d pos, float partialTicks) {
		if (!wasTamperedWith) {
			LibParticles.FAIRY_HEAD(world, pos.add(0, 0.25, 0), primaryColor);

			ParticleBuilder glitter = new ParticleBuilder(age);
			glitter.setRender(new ResourceLocation(Wizardry.MODID, Constants.MISC.SPARKLE_BLURRED));
			glitter.setAlphaFunction(new InterpFloatInOut(0.2f, 1f));

			if (RandUtil.nextInt(2) == 0)
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
			float excitement = (float) (handler.getMana() / handler.getMaxMana()) * (isDepressed ? 0 : 1);

			Color color = primaryColor;
			ParticleBuilder glitter = new ParticleBuilder((int) (RandUtil.nextInt(3, 5) + (10 * (1 - excitement))));
			glitter.setColor(color);
			glitter.setRender(new ResourceLocation(Wizardry.MODID, Constants.MISC.SPARKLE_BLURRED));
			glitter.setAlphaFunction(new InterpFloatInOut(0.2f, 1f));
			glitter.setScale(0.3f + (excitement * 3));
			if (RandUtil.nextBoolean())
				glitter.setColor(primaryColor);
			else glitter.setColor(secondaryColor);

			if (isDepressed) {
				glitter.enableMotionCalculation();
				glitter.setCollision(true);
				glitter.setAcceleration(new Vec3d(0, -0.005, 0));
			}

			ParticleSpawner.spawn(glitter, world, new StaticInterp<>(pos), 1);
		}
	}

	@Override
	public boolean hasCapability(@Nonnull Capability<?> capability, @Nullable EnumFacing facing) {
		return capability == WizardryCapabilityProvider.wizardryCapability;
	}

	@Nullable
	@Override
	public <T> T getCapability(@Nonnull Capability<T> capability, @Nullable EnumFacing facing) {
		return capability == WizardryCapabilityProvider.wizardryCapability ? (T) handler : null;
	}

	@Override
	public NBTTagCompound serializeNBT() {
		NBTTagCompound compound = new NBTTagCompound();
		compound.setTag("save", AbstractSaveHandler.writeAutoNBT(this, true));
		return compound;
	}

	@Override
	public void deserializeNBT(NBTTagCompound nbt) {
		if (nbt == null || !nbt.hasKey("save")) return;

		AbstractSaveHandler.readAutoNBT(this, nbt.getCompoundTag("save"), true);
	}
}
