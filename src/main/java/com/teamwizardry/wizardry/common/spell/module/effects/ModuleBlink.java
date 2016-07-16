package com.teamwizardry.wizardry.common.spell.module.effects;

import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.init.SoundEvents;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Vec3d;
import com.teamwizardry.wizardry.api.module.Module;
import com.teamwizardry.wizardry.api.module.attribute.Attribute;
import com.teamwizardry.wizardry.api.spell.ModuleType;

public class ModuleBlink extends Module
{
	public static final String COORD_SET = "Blink Coord Set";
	public static final String POS_X = "Blink X Coord";
	public static final String POS_Y = "Blink Y Coord";
	public static final String POS_Z = "Blink Z Coord";

	private boolean useCoord = false;
	private BlockPos pos = new BlockPos(0, 0, 0);

	public ModuleBlink()
	{
		attributes.addAttribute(Attribute.DISTANCE);
	}

	@Override
	public ModuleType getType()
	{
		return ModuleType.EFFECT;
	}

	@Override
	public String getDescription()
	{
		return "If no position is set, blink forward based on the power. Otherwise, teleport to the set location.";
	}

	@Override
	public String getDisplayName()
	{
		return "Blink";
	}

	@Override
	public NBTTagCompound getModuleData()
	{
		NBTTagCompound compound = super.getModuleData();
		compound.setBoolean(COORD_SET, useCoord);
		compound.setInteger(POS_X, pos.getX());
		compound.setInteger(POS_Y, pos.getY());
		compound.setInteger(POS_Z, pos.getZ());

		compound.setDouble(POWER, attributes.apply(Attribute.DISTANCE, 1));
		compound.setDouble(MANA, attributes.apply(Attribute.MANA, 10));
		compound.setDouble(BURNOUT, attributes.apply(Attribute.BURNOUT, 10));
		return compound;
	}

	public ModuleBlink setPos(BlockPos pos)
	{
		useCoord = true;
		this.pos = pos;
		return this;
	}

	@Override
	public boolean cast(EntityPlayer player, Entity caster, NBTTagCompound spell)
	{
		double power = spell.getDouble(POWER);

		Vec3d look = caster.getLook(1);
		double posX = look.xCoord * power;
		double posY = look.yCoord * power;
		double posZ = look.zCoord * power;

		if (!caster.worldObj.isRemote && caster instanceof EntityLivingBase)
		{
				net.minecraftforge.event.entity.living.EnderTeleportEvent event = new net.minecraftforge.event.entity.living.EnderTeleportEvent((EntityLivingBase) caster, caster.posX, caster.posY, caster.posZ, 0);
				if (net.minecraftforge.common.MinecraftForge.EVENT_BUS.post(event))
					return false;
		}
		caster.setPositionAndUpdate(caster.posX + posX, caster.posY + posY, caster.posZ + posZ);
		caster.worldObj.playSound(null, caster.prevPosX, caster.prevPosY, caster.prevPosZ, SoundEvents.ENTITY_ENDERMEN_TELEPORT, caster.getSoundCategory(), 1.0F, 1.0F);
		caster.playSound(SoundEvents.ENTITY_ENDERMEN_TELEPORT, 1.0F, 1.0F);

		return true;
	}
}