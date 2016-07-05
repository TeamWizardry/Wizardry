package com.teamwizardry.wizardry.common.spell.module.effects;

import net.minecraft.block.Block;
import net.minecraft.block.state.IBlockState;
import net.minecraft.entity.Entity;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.ItemStack;
import net.minecraft.item.crafting.FurnaceRecipes;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.util.math.BlockPos;
import com.teamwizardry.wizardry.api.module.Module;
import com.teamwizardry.wizardry.api.module.attribute.Attribute;
import com.teamwizardry.wizardry.api.spell.ModuleType;

public class ModuleFlame extends Module {
    public ModuleFlame() {
        attributes.addAttribute(Attribute.DURATION);
    }

    @Override
    public ModuleType getType() {
        return ModuleType.EFFECT;
    }

    @Override
    public String getDescription() {
        return "Inflict fire damage every tick. Will smelt any block or item it touches.";
    }

    @Override
    public String getDisplayName() {
        return "Inflame";
    }

    @Override
    public NBTTagCompound getModuleData() {
        NBTTagCompound compound = super.getModuleData();
        compound.setInteger(DURATION, (int) attributes.apply(Attribute.DURATION, 1));
        compound.setDouble(MANA, attributes.apply(Attribute.MANA, 10));
        compound.setDouble(BURNOUT, attributes.apply(Attribute.BURNOUT, 10));
        return compound;
    }

	@Override
	public boolean cast(EntityPlayer player, Entity caster, NBTTagCompound spell)
	{
		BlockPos pos = caster.getPosition();
		IBlockState state = caster.worldObj.getBlockState(pos);
		Block block = state.getBlock();
		ItemStack stack = new ItemStack(block, 1, block.getMetaFromState(state));
		ItemStack result = FurnaceRecipes.instance().getSmeltingResult(stack);
		if (result != null)
		{
			Block smelted = Block.getBlockFromItem(result.getItem());
			if (smelted != null)
			{
				caster.worldObj.setBlockState(pos, smelted.getDefaultState());
				caster.worldObj.playEvent(2001, pos, Block.getStateId(smelted.getDefaultState()));
			}
		}
		return true;
	}
}