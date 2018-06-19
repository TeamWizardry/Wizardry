package com.teamwizardry.wizardry.common.entity.projectile;

import com.teamwizardry.wizardry.api.LightningGenerator;
import com.teamwizardry.wizardry.api.spell.SpellData;
import com.teamwizardry.wizardry.api.spell.SpellRing;
import com.teamwizardry.wizardry.api.spell.attribute.AttributeRange;
import com.teamwizardry.wizardry.api.spell.attribute.AttributeRegistry;
import com.teamwizardry.wizardry.api.util.PosUtils;
import com.teamwizardry.wizardry.api.util.RandUtil;
import com.teamwizardry.wizardry.api.util.RandUtilSeed;
import com.teamwizardry.wizardry.common.core.LightningTracker;
import com.teamwizardry.wizardry.init.ModSounds;
import net.minecraft.entity.Entity;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.network.datasync.DataParameter;
import net.minecraft.network.datasync.DataSerializers;
import net.minecraft.network.datasync.EntityDataManager;
import net.minecraft.util.SoundCategory;
import net.minecraft.util.math.AxisAlignedBB;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Vec3d;
import net.minecraft.world.World;

import javax.annotation.Nonnull;
import java.util.ArrayList;
import java.util.List;

public class EntityLightningProjectile extends EntitySpellProjectile {
	public static final DataParameter<NBTTagCompound> CHILD_RING = EntityDataManager.createKey(EntitySpellProjectile.class, DataSerializers.COMPOUND_TAG);

	public EntityLightningProjectile(World world) {
		super(world);
	}

	public EntityLightningProjectile(World world, SpellRing spellRing, SpellRing childRing, SpellData spellData, float dist, float speed, float gravity) {
		super(world, spellRing, spellData, dist, speed, gravity);
		setChildRing(childRing);
	}

	private SpellRing getChildRing() {
		NBTTagCompound compound = getDataManager().get(CHILD_RING);
		return SpellRing.deserializeRing(compound);
	}

	private void setChildRing(SpellRing ring) {
		getDataManager().set(CHILD_RING, ring.serializeNBT());
		getDataManager().setDirty(CHILD_RING);
	}

	@Override
	protected void goBoom(SpellRing spellRing, SpellData data) {
		SpellRing childRing = getChildRing();
		if (childRing == null || childRing.getModule() == null) {
			super.goBoom(spellRing, data);
			return;
		}

		double range = childRing.getAttributeValue(AttributeRegistry.RANGE, data);
		double potency = childRing.getAttributeValue(AttributeRegistry.POTENCY, data);
		double duration = childRing.getAttributeValue(AttributeRegistry.DURATION, data);
		AttributeRange potencyRange = childRing.getModule().getAttributeRanges().get(AttributeRegistry.POTENCY);
		Vec3d origin = data.getOriginWithFallback();
		Entity caster = data.getCaster();

		if (origin != null) {
			for (int i = 0; i < potency; i += ((int) potencyRange.min >> 2)) {
				RandUtilSeed random = new RandUtilSeed(RandUtil.nextLong(100, 100000));
				Vec3d dir = PosUtils.vecFromRotations(random.nextFloat(0, 180), random.nextFloat(0, 360));
				Vec3d pos = dir.scale(range).add(origin);
				LightningGenerator generator = new LightningGenerator(origin, pos, random);

				ArrayList<Vec3d> points = generator.generate();

				data.world.playSound(null, new BlockPos(pos), ModSounds.LIGHTNING, SoundCategory.NEUTRAL, 0.5f, RandUtil.nextFloat(1, 1.5f));
				for (Vec3d point : points) {
					List<Entity> entityList = world.getEntitiesWithinAABBExcludingEntity(caster, new AxisAlignedBB(new BlockPos(point)).contract(0.2, 0.2, 0.2));
					if (!entityList.isEmpty()) {
						for (Entity entity : entityList) {
							LightningTracker.INSTANCE.addEntity(origin, entity, caster, potency, duration);
						}
					}
				}
			}
		}

		super.goBoom(spellRing, data);
	}

	@Override
	public void readCustomNBT(@Nonnull NBTTagCompound compound) {
		super.readCustomNBT(compound);

		if (compound.hasKey("child_ring")) {
			setChildRing(SpellRing.deserializeRing(compound.getCompoundTag("child_ring")));
		}
	}

	@Override
	public void writeCustomNBT(@Nonnull NBTTagCompound compound) {
		super.writeCustomNBT(compound);

		// Stupid Wawla, refusing to fix their problems...
		// https://github.com/micdoodle8/Galacticraft/commit/543e6afad64e51b02252a07489d0832fb93faa8d
		// https://github.com/Darkhax-Minecraft/WAWLA/issues/75
		if (world.isRemote) return;

		compound.setTag("child_ring", getChildRing().serializeNBT());
	}
}
