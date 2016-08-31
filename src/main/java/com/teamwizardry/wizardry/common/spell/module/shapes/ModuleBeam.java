package com.teamwizardry.wizardry.common.spell.module.shapes;

import com.teamwizardry.librarianlib.common.util.RaycastUtils;
import com.teamwizardry.wizardry.api.module.Module;
import com.teamwizardry.wizardry.api.module.attribute.Attribute;
import com.teamwizardry.wizardry.api.spell.IContinuousCast;
import com.teamwizardry.wizardry.api.spell.ModuleType;
import com.teamwizardry.wizardry.api.spell.SpellEntity;
import com.teamwizardry.wizardry.api.trackerobject.SpellStack;
import com.teamwizardry.wizardry.lib.LibParticles;
import net.minecraft.entity.Entity;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.util.math.RayTraceResult;
import net.minecraft.util.math.Vec3d;

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
		RayTraceResult raycast = RaycastUtils.raycast(caster, distance);

		if (raycast == null) return false;

		// TODO: add light colors
		Vec3d cross = caster.getLook(1).crossProduct(new Vec3d(0, caster.getEyeHeight(), 0)).normalize().scale(caster.width / 2);
		Vec3d casterVec = new Vec3d(caster.posX + cross.xCoord, caster.posY + caster.getEyeHeight() + cross.yCoord, caster.posZ + cross.zCoord);
		LibParticles.MODULE_BEAM(player.worldObj, raycast.hitVec, casterVec, caster.getLook(1).scale(-1), (int) distance);

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
				raycast = RaycastUtils.raycast(raycast.entityHit.worldObj, raycast.entityHit.getPositionVector(), caster.getLookVec(), distance);
			}
			else return false;
		}
		while (pierce > 0);
		return true;
	}
}
