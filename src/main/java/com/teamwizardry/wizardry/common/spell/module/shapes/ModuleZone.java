package com.teamwizardry.wizardry.common.spell.module.shapes;

import com.teamwizardry.wizardry.api.module.Module;
import com.teamwizardry.wizardry.api.module.attribute.Attribute;
import com.teamwizardry.wizardry.api.spell.ModuleType;
import com.teamwizardry.wizardry.api.spell.SpellEntity;
import com.teamwizardry.wizardry.api.trackerobject.SpellStack;
import com.teamwizardry.wizardry.api.trackerobject.SpellTracker;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.item.EntityItem;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.util.math.AxisAlignedBB;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Vec3i;

import java.util.ArrayList;
import java.util.List;

public class ModuleZone extends Module {
    public ModuleZone(ItemStack stack) {
		super(stack);
        attributes.addAttribute(Attribute.RADIUS);
        attributes.addAttribute(Attribute.DURATION);
    }

    @Override
    public ModuleType getType() {
        return ModuleType.SHAPE;
    }
    
    @Override
    public String getDescription()
    {
    	return "Casts the spell on all valid targets in a circular area.";
    }

    @Override
    public String getDisplayName() {
        return "Zone";
    }

    @Override
    public NBTTagCompound getModuleData() {
    	NBTTagCompound compound = super.getModuleData();
    	compound.setDouble(RADIUS, attributes.apply(Attribute.RADIUS, 1));
    	compound.setInteger(DURATION, (int) attributes.apply(Attribute.DURATION, 1));
    	compound.setDouble(MANA, attributes.apply(Attribute.MANA, 10));
    	compound.setDouble(BURNOUT, attributes.apply(Attribute.BURNOUT, 10));
        return compound;
    }

	@Override
	public boolean cast(EntityPlayer player, Entity caster, NBTTagCompound spell, SpellStack stack)
	{
		double radius = spell.getDouble(RADIUS);
		int duration = spell.getInteger(DURATION);

//        Circle3D circle = new Circle3D(new Vec3d(caster.posX, caster.posY, caster.posZ), radius, (int) (radius * 10));
//        for (Vec3d point : circle.getPoints()) {
//            SparkleFX fizz = Wizardry.proxy.spawnParticleSparkle(caster.worldObj, point);
//			fizz.setMaxAge(30);
//			fizz.setScale(3f);
//			fizz.setAlpha(0.5f);
//			fizz.setShrink();
//			fizz.setGrow();
//			fizz.setFadeOut();
//			fizz.setFadeIn();
//            fizz.setRandomSize();
//        }

		if (!(caster instanceof SpellEntity))
		{
			BlockPos pos = caster.getPosition();
			AxisAlignedBB axis = new AxisAlignedBB(pos.subtract(new Vec3i(radius, 0, radius)), pos.add(new Vec3i(radius, 1, radius)));
			List<Entity> entities = caster.worldObj.getEntitiesWithinAABB(EntityItem.class, axis);
			entities.addAll(caster.worldObj.getEntitiesWithinAABB(EntityLivingBase.class, axis));
			for (Entity entity : entities)
				if (entity.getDistanceSqToEntity(caster) <= radius * radius)
						stack.castEffects(entity);
		}
		else
		{
			BlockPos pos = caster.getPosition();
			List<BlockPos> blocks = new ArrayList<BlockPos>();
			for (int i = -(int) radius; i <= radius; i++)
			{
				for (int j = -(int) radius; j<= radius; j++)
				{
					if (i*i + j*j > radius * radius) continue;
					BlockPos block = pos.add(i, 0, j);
					if (!caster.worldObj.isAirBlock(block))
						blocks.add(block);
				}
			}
			for (BlockPos block : blocks)
			{
				SpellEntity entity = new SpellEntity(caster.worldObj, block.getX(), block.getY(), block.getZ());
				entity.rotationPitch = 90;
				stack.castEffects(entity);
			}
		}
		
		duration--;
		if (duration > 0)
		{
			spell.setInteger(DURATION, duration);
			SpellTracker.addSpell(player, caster, spell);
		}
		
		return false;
	}
}