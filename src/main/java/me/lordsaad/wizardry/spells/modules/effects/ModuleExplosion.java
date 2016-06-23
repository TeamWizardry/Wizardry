package me.lordsaad.wizardry.spells.modules.effects;

import me.lordsaad.wizardry.api.modules.Module;
import me.lordsaad.wizardry.spells.modules.ModuleType;
import net.minecraft.nbt.NBTTagCompound;

public class ModuleExplosion extends Module
{
	private static final String DAMAGE_TERRAIN = "Damage Terrain";
	
	private boolean damageTerrain;
	private int power;
	
	public ModuleExplosion()
	{
		
	}
	
    @Override
    public ModuleType getType() {
        return ModuleType.EFFECT;
    }
    
	@Override
	public NBTTagCompound getModuleData()
	{
		NBTTagCompound compound = super.getModuleData();
		compound.setString(CLASS, this.getClass().getSimpleName());
		compound.setBoolean(DAMAGE_TERRAIN, damageTerrain);
		compound.setInteger(POWER, power);
		return compound;
	}
	
	public ModuleExplosion setDamageTerrain(boolean canDamageTerrain)
	{
		damageTerrain = canDamageTerrain;
		return this;
	}
	
	public ModuleExplosion setPower(int power)
	{
		this.power = power;
		return this;
	}
}