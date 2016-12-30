package com.teamwizardry.wizardry.common.spell.module.shapes;

import com.teamwizardry.wizardry.api.Constants;
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
	public String getDescription() {
		return "Casts the spell on all valid targets in a circular area.";
	}

	@Override
	public String getDisplayName() {
		return "Zone";
	}

	@Override
	public NBTTagCompound getModuleData() {
		NBTTagCompound compound = super.getModuleData();
		compound.setDouble(Constants.Module.RADIUS, attributes.apply(Attribute.RADIUS, 1));
		compound.setInteger(Constants.Module.DURATION, (int) attributes.apply(Attribute.DURATION, 1));
		compound.setDouble(Constants.Module.MANA, attributes.apply(Attribute.MANA, 10));
		compound.setDouble(Constants.Module.BURNOUT, attributes.apply(Attribute.BURNOUT, 10));
		return compound;
	}

	@Override
	public boolean cast(EntityPlayer player, Entity caster, NBTTagCompound spell, SpellStack stack) {
		double radius = spell.getDouble(Constants.Module.RADIUS);
		int duration = spell.getInteger(Constants.Module.DURATION);

//        Circle3D circle = new Circle3D(new Vec3d(caster.posX, caster.posY, caster.posZ), radius, (int) (radius * 10));
//        for (Vec3d point : circle.getPoints()) {
//            SparkleFX fizz = Wizardry.proxy.spawnParticleSparkle(caster.world, point);
//			fizz.setMaxAge(30);
//			fizz.setScale(3f);
//			fizz.setAlpha(0.5f);
//			fizz.setShrink();
//			fizz.setGrow();
//			fizz.setFadeOut();
//			fizz.setFadeIn();
//            fizz.setRandomSize();
//        }

		if (caster instanceof SpellEntity) {
			BlockPos pos = caster.getPosition();
			List<BlockPos> blocks = new ArrayList<>();
			for (int i = -(int) radius; i <= radius; i++) {
				for (int j = -(int) radius; j <= radius; j++) {
					if (((i * i) + (j * j)) > (radius * radius)) continue;
					BlockPos block = pos.add(i, 0, j);
                    if (!caster.world.isAirBlock(block))
                        blocks.add(block);
				}
			}
			for (BlockPos block : blocks) {
                SpellEntity entity = new SpellEntity(caster.world, block.getX(), block.getY(), block.getZ());
                entity.rotationPitch = 90;
				stack.castEffects(entity);
			}
		} else {
			BlockPos pos = caster.getPosition();
			AxisAlignedBB axis = new AxisAlignedBB(pos.subtract(new Vec3i(radius, 0, radius)), pos.add(new Vec3i(radius, 1, radius)));
            List<Entity> entities = caster.world.getEntitiesWithinAABB(EntityItem.class, axis);
            entities.addAll(caster.world.getEntitiesWithinAABB(EntityLivingBase.class, axis));
            entities.stream().filter(entity -> entity.getDistanceSqToEntity(caster) <= (radius * radius)).forEach(stack::castEffects);
		}

		duration--;
		if (duration > 0) {
			spell.setInteger(Constants.Module.DURATION, duration);
			SpellTracker.addSpell(player, caster, spell);
		}

		return false;
	}
}
