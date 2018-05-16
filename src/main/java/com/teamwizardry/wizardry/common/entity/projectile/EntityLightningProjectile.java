package com.teamwizardry.wizardry.common.entity.projectile;

import java.util.ArrayList;
import java.util.List;

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
import net.minecraft.util.SoundCategory;
import net.minecraft.util.math.AxisAlignedBB;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Vec3d;
import net.minecraft.world.World;

public class EntityLightningProjectile extends EntitySpellProjectile
{
	public SpellRing childRing;
	
	public EntityLightningProjectile(World world)
	{
		super(world);
	}
	
	public EntityLightningProjectile(World world, SpellRing spellRing, SpellRing childRing, SpellData spellData, double dist, double speed, double gravity)
	{
		super(world, spellRing, spellData, dist, speed, gravity);
		this.childRing = childRing;
	}
	
	@Override
	protected void goBoom(SpellData data)
	{
		motionX = 0;
		motionY = 0;
		motionZ = 0;
		
		if (spellRing.getChildRing() != null)
			spellRing.getChildRing().runSpellRing(data);
		
		double range = childRing.getAttributeValue(AttributeRegistry.RANGE, data);
		double potency = childRing.getAttributeValue(AttributeRegistry.POTENCY, data);
		double duration = childRing.getAttributeValue(AttributeRegistry.DURATION, data);
		AttributeRange potencyRange = childRing.getModule().getAttributeRanges().get(AttributeRegistry.POTENCY);
		Vec3d origin = data.getOriginWithFallback();
		Entity caster = data.getCaster();
		
		if (origin != null)
		{
			for (int i = 0; i < potency; i += ((int) potencyRange.min >> 2))
			{
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
		
		setDead();
		world.removeEntity(this);
	}
}
