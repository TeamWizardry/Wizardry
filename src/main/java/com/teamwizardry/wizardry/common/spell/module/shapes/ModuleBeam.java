package com.teamwizardry.wizardry.common.spell.module.shapes;

import java.util.concurrent.ThreadLocalRandom;

import com.teamwizardry.librarianlib.common.util.RaycastUtils;
import net.minecraft.entity.Entity;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.util.math.RayTraceResult;
import net.minecraft.util.math.Vec3d;
import com.teamwizardry.wizardry.Wizardry;
import com.teamwizardry.wizardry.api.module.Module;
import com.teamwizardry.wizardry.api.module.attribute.Attribute;
import com.teamwizardry.wizardry.api.spell.IContinuousCast;
import com.teamwizardry.wizardry.api.spell.ModuleType;
import com.teamwizardry.wizardry.api.spell.SpellEntity;
import com.teamwizardry.wizardry.api.trackerobject.SpellStack;
import com.teamwizardry.wizardry.client.fx.GlitterFactory;
import com.teamwizardry.wizardry.client.fx.particle.SparkleFX;
import com.teamwizardry.wizardry.client.fx.particle.trails.SparkleTrailHelix;

public class ModuleBeam extends Module implements IContinuousCast
{

	public ModuleBeam(ItemStack stack)
	{
		super(stack);
		attributes.addAttribute(Attribute.DISTANCE);
		attributes.addAttribute(Attribute.SCATTER);
		attributes.addAttribute(Attribute.PROJ_COUNT);
		attributes.addAttribute(Attribute.PIERCE);
	}

	@Override
	public ModuleType getType()
	{
		return ModuleType.SHAPE;
	}

	@Override
	public String getDescription()
	{
		return "Casts a beam that strikes the first target in a raycast.";
	}

	@Override
	public String getDisplayName()
	{
		return "Beam";
	}

	@Override
	public NBTTagCompound getModuleData()
	{
		NBTTagCompound compound = super.getModuleData();
		compound.setDouble(DISTANCE, attributes.apply(Attribute.DISTANCE, 1));
		compound.setDouble(SCATTER, attributes.apply(Attribute.SCATTER, 0));
		compound.setInteger(PROJ_COUNT, (int) attributes.apply(Attribute.PROJ_COUNT, 1));
		compound.setInteger(PIERCE, (int) attributes.apply(Attribute.PIERCE, 0));
		return compound;
	}

	@Override
	public boolean cast(EntityPlayer player, Entity caster, NBTTagCompound spell, SpellStack stack)
	{

		double distance = spell.getDouble(DISTANCE);
		double pierce = spell.getInteger(PIERCE);
		RayTraceResult raycast = RaycastUtils.INSTANCE.raycast(caster, distance);

		// Beam particles
		double slopeX, slopeY, slopeZ;
		Vec3d cross = caster.getLook(1).crossProduct(new Vec3d(0, caster.getEyeHeight(), 0)).normalize().scale(caster.width / 2);
		slopeX = (raycast.hitVec.xCoord - (caster.posX + cross.xCoord)) / distance;
		slopeY = (raycast.hitVec.yCoord - (caster.posY + caster.getEyeHeight() + cross.yCoord)) / distance;
		slopeZ = (raycast.hitVec.zCoord - (caster.posZ + cross.zCoord)) / distance;

		for (double i = 0; i < distance; i += distance / 100)
		{
			double x = slopeX * i + caster.posX + cross.xCoord;
			double y = slopeY * i + caster.posY + caster.getEyeHeight();
			double z = slopeZ * i + caster.posZ + cross.zCoord;

			double theta = Math.toRadians((360.0 / i));
			Vec3d origin = new Vec3d(x + 0.2 * Math.cos(theta), y, z + 0.2 * Math.sin(theta));
			Vec3d center = new Vec3d(x, y, z);

			SparkleFX fizz = GlitterFactory.getInstance().createSparkle(caster.worldObj, center, 10);
			fizz.setScale(0.5f);
			fizz.setAlpha(1f);
			fizz.setFadeOut();
			fizz.setShrink();
			fizz.setBlurred();

			if (ThreadLocalRandom.current().nextInt(10) == 0)
			{
				SparkleTrailHelix helix = Wizardry.proxy.spawnParticleSparkleTrailHelix(caster.worldObj, origin, center, 0.2, theta);
				helix.setFadeOut();
				helix.setRandomlyBlurred();
			}
		}
		// Beam particles

		do
		{
			if (raycast.typeOfHit == RayTraceResult.Type.BLOCK)
			{
				Entity entity = new SpellEntity(caster.worldObj, raycast.getBlockPos().getX(), raycast.getBlockPos().getY(), raycast.getBlockPos().getZ());
				stack.castEffects(entity);
				return true;
			}
			else if (raycast.typeOfHit == RayTraceResult.Type.ENTITY)
			{
				stack.castEffects(raycast.entityHit);
				pierce--;
				raycast = RaycastUtils.INSTANCE.raycast(raycast.entityHit.worldObj, raycast.entityHit.getPositionVector(), caster.getLookVec(), distance);
			}
			else return false;
		}
		while (pierce > 0);
		return true;
	}
}