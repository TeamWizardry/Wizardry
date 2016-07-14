package com.teamwizardry.wizardry.common.spell.module.shapes;

import java.util.List;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.item.EntityItem;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.nbt.NBTTagList;
import net.minecraft.util.math.AxisAlignedBB;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.MathHelper;
import net.minecraft.util.math.Vec3d;
import net.minecraft.util.math.Vec3i;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.common.util.Constants.NBT;
import com.teamwizardry.wizardry.api.module.Module;
import com.teamwizardry.wizardry.api.module.attribute.Attribute;
import com.teamwizardry.wizardry.api.spell.ModuleType;
import com.teamwizardry.wizardry.api.spell.SpellEntity;
import com.teamwizardry.wizardry.api.spell.event.SpellCastEvent;

public class ModuleCone extends Module
{
	public ModuleCone()
	{
		attributes.addAttribute(Attribute.DISTANCE);
		attributes.addAttribute(Attribute.SCATTER);
	}

	@Override
	public ModuleType getType()
	{
		return ModuleType.SHAPE;
	}

	@Override
	public String getDescription()
	{
		return "Casts the spell on all entities within a frontal cone.";
	}

	@Override
	public String getDisplayName()
	{
		return "Cone";
	}

	@Override
	public NBTTagCompound getModuleData()
	{
		NBTTagCompound compound = super.getModuleData();
		compound.setDouble(DISTANCE, attributes.apply(Attribute.DISTANCE, 1));
		compound.setDouble(SCATTER, attributes.apply(Attribute.SCATTER, 1));
		compound.setDouble(MANA, attributes.apply(Attribute.MANA, 10));
		compound.setDouble(BURNOUT, attributes.apply(Attribute.BURNOUT, 10));
		return compound;
	}

	@Override
	public boolean cast(EntityPlayer player, Entity caster, NBTTagCompound spell)
	{
		double radius = spell.getDouble(RADIUS);
		double scatter = Math.PI / 2 * MathHelper.clamp_double(spell.getDouble(SCATTER), 0, 1);
		NBTTagList modules = spell.getTagList(MODULES, NBT.TAG_COMPOUND);
		Vec3d look = caster.getLook(1);
		if (!(caster instanceof SpellEntity))
		{
			BlockPos pos = caster.getPosition();
			AxisAlignedBB axis = new AxisAlignedBB(pos.subtract(new Vec3i(radius, 0, radius)), pos.add(new Vec3i(radius, 1, radius)));
			List<Entity> entities = caster.worldObj.getEntitiesWithinAABB(EntityItem.class, axis);
			entities.addAll(caster.worldObj.getEntitiesWithinAABB(EntityLivingBase.class, axis));
			for (Entity entity : entities)
			{
				if (entity.getDistanceSqToEntity(caster) <= radius * radius)
				{
					double entityX = entity.posX - caster.posX;
					double entityZ = entity.posZ - caster.posZ;
					double lookX = look.xCoord;
					double lookZ = look.zCoord;
					double cos = (entityX * lookX + entityZ * lookZ) / Math.sqrt((entityX * entityX + entityZ * entityZ) * (lookX * lookX + lookZ * lookZ));
					double angle = Math.acos(Math.abs(cos));
					if (angle <= scatter)
					{
						for (int i = 0; i < modules.tagCount(); i++)
						{
							SpellCastEvent event = new SpellCastEvent(modules.getCompoundTagAt(i), entity, player);
							MinecraftForge.EVENT_BUS.post(event);
						}
					}
				}
			}
			return true;
		}
		else
		{
			BlockPos pos = caster.getPosition();
			for (int i = -(int) radius; i <= radius; i++)
			{
				for (int j = -(int) radius; j <= radius; j++)
				{
					if (i * i + j * j <= radius * radius && !caster.worldObj.isAirBlock(pos.add(i, 0, j)))
					{
						double xCoord = look.xCoord * i;
						double zCoord = look.zCoord * j;
						double lookSq = look.xCoord * look.xCoord + look.zCoord * look.zCoord;
						double posSq = i*i + j*j;
						double cos = (xCoord + zCoord) / Math.sqrt(lookSq * posSq);
//						double cos = (look.xCoord * i + look.zCoord * j) / Math.sqrt((look.xCoord * look.xCoord + look.zCoord * look.zCoord) * (i * i + j * j));
						double angle = Math.acos(Math.abs(cos));
						if (angle <= scatter)
						{
							SpellEntity entity = new SpellEntity(caster.worldObj, pos.getX() + i, pos.getY(), pos.getZ() + j);
							for (int k = 0; k < modules.tagCount(); k++)
							{
								SpellCastEvent event = new SpellCastEvent(modules.getCompoundTagAt(k), entity, player);
								MinecraftForge.EVENT_BUS.post(event);
							}
						}
					}
				}
			}
		}
		return false;
	}
}