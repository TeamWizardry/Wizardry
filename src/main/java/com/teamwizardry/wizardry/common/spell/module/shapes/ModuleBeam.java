package com.teamwizardry.wizardry.common.spell.module.shapes;

import net.minecraft.entity.Entity;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.nbt.NBTTagList;
import net.minecraft.util.math.RayTraceResult;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.common.util.Constants.NBT;
import com.teamwizardry.librarianlib.math.Raycast;
import com.teamwizardry.wizardry.api.module.Module;
import com.teamwizardry.wizardry.api.module.attribute.Attribute;
import com.teamwizardry.wizardry.api.spell.IContinuousCast;
import com.teamwizardry.wizardry.api.spell.ModuleType;
import com.teamwizardry.wizardry.api.spell.SpellEntity;
import com.teamwizardry.wizardry.api.spell.event.SpellCastEvent;

public class ModuleBeam extends Module implements IContinuousCast {
    public ModuleBeam() {
        attributes.addAttribute(Attribute.DISTANCE);
        attributes.addAttribute(Attribute.SCATTER);
        attributes.addAttribute(Attribute.PROJ_COUNT);
        attributes.addAttribute(Attribute.PIERCE);
    }

    @Override
    public ModuleType getType() {
        return ModuleType.SHAPE;
    }
    
    @Override
    public String getDescription()
    {
    	return "Casts a beam that strikes the first target in a raycast.";
    }

    @Override
    public String getDisplayName() {
        return "Beam";
    }

    @Override
    public NBTTagCompound getModuleData() {
    	NBTTagCompound compound = super.getModuleData();
    	compound.setDouble(DISTANCE, attributes.apply(Attribute.DISTANCE, 1));
    	compound.setDouble(SCATTER, attributes.apply(Attribute.SCATTER, 0));
    	compound.setInteger(PROJ_COUNT, (int) attributes.apply(Attribute.PROJ_COUNT, 1));
    	compound.setInteger(PIERCE, (int) attributes.apply(Attribute.PIERCE, 0));
        return compound;
    }

	@Override
	public boolean cast(EntityPlayer player, Entity caster, NBTTagCompound spell)
	{
		double distance = spell.getDouble(DISTANCE);
		double pierce = spell.getInteger(PIERCE);
		RayTraceResult raycast = Raycast.cast(caster, distance);
		NBTTagList modules = spell.getTagList(MODULES, NBT.TAG_COMPOUND);
		do
		{
			if (raycast.typeOfHit == RayTraceResult.Type.BLOCK)
			{
				for (int i = 0; i < modules.tagCount(); i++)
				{
					Entity entity = new SpellEntity(caster.worldObj, raycast.getBlockPos().getX(), raycast.getBlockPos().getY(), raycast.getBlockPos().getZ());
					SpellCastEvent event = new SpellCastEvent(modules.getCompoundTagAt(i), entity, player);
					MinecraftForge.EVENT_BUS.post(event);
				}
				return true;
			}
			else if (raycast.typeOfHit == RayTraceResult.Type.ENTITY)
			{
				for (int i = 0; i < modules.tagCount(); i++)
				{
					SpellCastEvent event = new SpellCastEvent(modules.getCompoundTagAt(i), raycast.entityHit, player);
					MinecraftForge.EVENT_BUS.post(event);
				}
				pierce--;
				raycast = Raycast.cast(raycast.entityHit, caster.getLookVec(), distance);
			}
		}
		while (pierce > 0);
		return true;
	}
}