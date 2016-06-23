package me.lordsaad.wizardry.spells.modules.effects;

import me.lordsaad.wizardry.api.modules.Module;
import me.lordsaad.wizardry.spells.modules.ModuleType;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.util.math.BlockPos;

public class ModuleBlink extends Module
{
	public static final String COORD_SET = "Blink Coord Set";
	public static final String POS_X = "Blink X Coord";
	public static final String POS_Y = "Blink Y Coord";
	public static final String POS_Z = "Blink Z Coord";
	
	private boolean useCoord = false;
	private BlockPos pos = new BlockPos(0, 0, 0);
	
	private int distance = 0;
	
	public ModuleBlink(Module... modules)
	{
		this.modules = modules;
	}
	
    @Override
    public ModuleType getType() {
        return ModuleType.EFFECT;
    }
    
	@Override
	public NBTTagCompound getModuleData()
	{
		NBTTagCompound compound = super.getModuleData();
		compound.setString(CLASS, "Blink");
		compound.setBoolean(COORD_SET, useCoord);
		compound.setInteger(POS_X, pos.getX());
		compound.setInteger(POS_Y, pos.getY());
		compound.setInteger(POS_Z, pos.getZ());
		compound.setInteger(POWER, distance);
		return compound;
	}
	
	public ModuleBlink setDistance(int power)
    {
    	distance = power;
    	return this;
    }
    
    public ModuleBlink setPos(BlockPos pos)
    {
    	useCoord = true;
    	this.pos = pos;
    	return this;
    }
}