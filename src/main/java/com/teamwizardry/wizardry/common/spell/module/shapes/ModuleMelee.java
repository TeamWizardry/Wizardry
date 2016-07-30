package com.teamwizardry.wizardry.common.spell.module.shapes;

import net.minecraft.entity.Entity;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.nbt.NBTTagList;
import net.minecraft.util.math.RayTraceResult;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.common.util.Constants.NBT;
import com.teamwizardry.librarianlib.math.Raycast;
import com.teamwizardry.wizardry.api.module.Module;
import com.teamwizardry.wizardry.api.module.attribute.Attribute;
import com.teamwizardry.wizardry.api.spell.ModuleType;
import com.teamwizardry.wizardry.api.spell.SpellEntity;
import com.teamwizardry.wizardry.api.spell.event.SpellCastEvent;

public class ModuleMelee extends Module {
	public ModuleMelee(ItemStack stack) {
		super(stack);
	}

	@Override
    public ModuleType getType() {
        return ModuleType.SHAPE;
    }

    @Override
    public String getDescription()
    {
    	return "Casts the spell on the object you are looking at.";
    }
    
    @Override
    public NBTTagCompound getModuleData() {
    	NBTTagCompound compound = super.getModuleData();
    	compound.setDouble(MANA, attributes.apply(Attribute.MANA, 10));
    	compound.setDouble(BURNOUT, attributes.apply(Attribute.BURNOUT, 10));
        return compound;
    }

    @Override
    public String getDisplayName() {
        return "Melee";
    }

	@Override
	public boolean cast(EntityPlayer player, Entity caster, NBTTagCompound spell)
	{
		double distance = 3;
		RayTraceResult raycast = Raycast.cast(caster, distance);
		NBTTagList modules = spell.getTagList(MODULES, NBT.TAG_COMPOUND);
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
			return true;
		}
		return false;
	}
}