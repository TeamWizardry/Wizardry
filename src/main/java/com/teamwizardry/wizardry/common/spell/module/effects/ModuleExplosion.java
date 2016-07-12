package com.teamwizardry.wizardry.common.spell.module.effects;

import com.teamwizardry.wizardry.api.module.Module;
import com.teamwizardry.wizardry.api.module.attribute.Attribute;
import com.teamwizardry.wizardry.api.spell.ModuleType;
import net.minecraft.entity.Entity;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.Explosion;

import java.util.ArrayList;
import java.util.List;

public class ModuleExplosion extends Module {
    private static final String DAMAGE_TERRAIN = "Damage Terrain";

    private boolean damageTerrain;

    public ModuleExplosion() {
        attributes.addAttribute(Attribute.POWER);
    }

    @Override
    public ModuleType getType() {
        return ModuleType.EFFECT;
    }

    @Override
    public String getDescription() {
        return "Cause an explosion dealing blast damage. More increases the size and stiffness. x64 deals terrain damage.";
    }

    @Override
    public String getDisplayName() {
        return "Explode";
    }

    @Override
    public NBTTagCompound getModuleData() {
        NBTTagCompound compound = super.getModuleData();
        compound.setBoolean(DAMAGE_TERRAIN, damageTerrain);
        
        compound.setDouble(POWER, attributes.apply(Attribute.POWER, 1));
        compound.setDouble(MANA, attributes.apply(Attribute.MANA, 10));
        compound.setDouble(BURNOUT, attributes.apply(Attribute.BURNOUT, 10));
        return compound;
    }

    public ModuleExplosion setDamageTerrain(boolean canDamageTerrain) {
        damageTerrain = canDamageTerrain;
        return this;
    }

	@Override
	public boolean cast(EntityPlayer player, Entity caster, NBTTagCompound spell)
	{
		List<BlockPos> affectedPositions = new ArrayList<BlockPos>();
		float power = (float) spell.getDouble(POWER);
		if (spell.getBoolean(DAMAGE_TERRAIN))
		{
			for (int i = -(int) power; i <= power; i++)
				for (int j = -(int) power; j <= power; j++)
					for (int k = -(int) power; j <= power; j++)
						if (i*i + j*j + k*k < power*power)
							affectedPositions.add(caster.getPosition().add(i, j, k));
		}
		Explosion explosion = new Explosion(caster.worldObj, player, caster.posX, caster.posY, caster.posZ, power, affectedPositions);
		explosion.doExplosionA();
		explosion.doExplosionB(true);
		return true;
	}
}