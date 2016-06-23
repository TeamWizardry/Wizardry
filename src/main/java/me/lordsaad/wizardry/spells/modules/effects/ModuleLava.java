package me.lordsaad.wizardry.spells.modules.effects;

import me.lordsaad.wizardry.api.modules.Module;
import me.lordsaad.wizardry.spells.modules.ModuleType;
import net.minecraft.entity.Entity;
import net.minecraft.init.Blocks;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;

/**
 * Created by Saad on 6/21/2016.
 */
public class ModuleLava extends Module
{
	public ModuleLava(Module... modules)
	{
		this.modules = modules;
	}

	public void onCollideWithBlock(World world, BlockPos pos)
	{
		world.setBlockState(pos, Blocks.LAVA.getDefaultState());
	}

	public void onCollideWithEntity(World world, Entity entity)
	{
		world.setBlockState(entity.getPosition(), Blocks.LAVA.getDefaultState());
	}

	@Override
	public ModuleType getType()
	{
		return ModuleType.EFFECT;
	}

	@Override
	public NBTTagCompound getModuleData()
	{
		return null;
	}
}