package com.teamwizardry.wizardry.common.spell.module.shapes;

import java.util.ArrayList;
import java.util.List;

import com.teamwizardry.librarianlib.math.shapes.Circle3D;
import com.teamwizardry.wizardry.Wizardry;
import com.teamwizardry.wizardry.client.fx.particle.SparkleFX;
import com.teamwizardry.wizardry.client.fx.particle.trails.SparkleTrailHelix;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.item.EntityItem;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.nbt.NBTTagList;
import net.minecraft.util.math.AxisAlignedBB;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Vec3d;
import net.minecraft.util.math.Vec3i;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.common.util.Constants.NBT;
import com.teamwizardry.wizardry.api.module.Module;
import com.teamwizardry.wizardry.api.module.attribute.Attribute;
import com.teamwizardry.wizardry.api.spell.ModuleType;
import com.teamwizardry.wizardry.api.spell.SpellEntity;
import com.teamwizardry.wizardry.api.spell.event.SpellCastEvent;
import com.teamwizardry.wizardry.api.trackerobject.SpellTracker;

public class ModuleZone extends Module {
    public ModuleZone() {
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
	public boolean cast(EntityPlayer player, Entity caster, NBTTagCompound spell)
	{
		double radius = spell.getDouble(RADIUS);
		int duration = spell.getInteger(DURATION);
		NBTTagList modules = spell.getTagList(MODULES, NBT.TAG_COMPOUND);

        Circle3D circle = new Circle3D(new Vec3d(caster.posX, caster.posY, caster.posZ), radius, (int) (radius * 10));
        for (Vec3d point : circle.getPoints()) {
            SparkleFX fizz = Wizardry.proxy.spawnParticleSparkle(caster.worldObj, point.xCoord, point.yCoord, point.zCoord, 0.5f, 2f, 30, true);
            fizz.setRandomizedSizes(true);
        }

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
					for (int i = 0; i < modules.tagCount(); i++)
					{
						SpellCastEvent event = new SpellCastEvent(modules.getCompoundTagAt(i), entity, player);
						MinecraftForge.EVENT_BUS.post(event);
					}
				}
			}
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
				for (int i = 0; i < modules.tagCount(); i++)
				{
					SpellCastEvent event = new SpellCastEvent(modules.getCompoundTagAt(i), entity, player);
					MinecraftForge.EVENT_BUS.post(event);
				}
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