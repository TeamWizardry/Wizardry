package com.teamwizardry.wizardry.common.spell.module.shapes;

import com.teamwizardry.librarianlib.client.fx.particle.ParticleBuilder;
import com.teamwizardry.librarianlib.client.fx.particle.ParticleSpawner;
import com.teamwizardry.librarianlib.common.util.RaycastUtils;
import com.teamwizardry.librarianlib.common.util.math.interpolate.position.InterpCircle;
import com.teamwizardry.librarianlib.common.util.math.interpolate.position.InterpLine;
import com.teamwizardry.wizardry.Wizardry;
import com.teamwizardry.wizardry.api.module.Module;
import com.teamwizardry.wizardry.api.module.attribute.Attribute;
import com.teamwizardry.wizardry.api.spell.IContinuousCast;
import com.teamwizardry.wizardry.api.spell.ModuleType;
import com.teamwizardry.wizardry.api.spell.SpellEntity;
import com.teamwizardry.wizardry.api.trackerobject.SpellStack;
import net.minecraft.entity.Entity;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.math.RayTraceResult;
import net.minecraft.util.math.Vec3d;

import java.awt.*;
import java.util.concurrent.ThreadLocalRandom;

public class ModuleBeam extends Module implements IContinuousCast
{

	public ModuleBeam(ItemStack stack)
	{
		super(stack);
		attributes.addAttribute(Attribute.DISTANCE);
		attributes.addAttribute(Attribute.SCATTER);
		attributes.addAttribute(Attribute.PROJ_COUNT);
		attributes.addAttribute(Attribute.PIERCE);
	}

	@Override
	public ModuleType getType()
	{
		return ModuleType.SHAPE;
	}

	@Override
	public String getDescription()
	{
		return "Casts a beam that strikes the first target in a raycast.";
	}

	@Override
	public String getDisplayName()
	{
		return "Beam";
	}

	@Override
	public NBTTagCompound getModuleData()
	{
		NBTTagCompound compound = super.getModuleData();
		compound.setDouble(DISTANCE, attributes.apply(Attribute.DISTANCE, 1));
		compound.setDouble(SCATTER, attributes.apply(Attribute.SCATTER, 0));
		compound.setInteger(PROJ_COUNT, (int) attributes.apply(Attribute.PROJ_COUNT, 1));
		compound.setInteger(PIERCE, (int) attributes.apply(Attribute.PIERCE, 0));
		return compound;
	}

	@Override
	public boolean cast(EntityPlayer player, Entity caster, NBTTagCompound spell, SpellStack stack)
	{
		double distance = spell.getDouble(DISTANCE);
		double pierce = spell.getInteger(PIERCE);
		RayTraceResult raycast = RaycastUtils.raycast(caster, distance);

		if (raycast == null) return false;

		// Beam particles
		ParticleBuilder glitter = new ParticleBuilder(20);
		glitter.setColor(new Color(1f, 1f, 1f, 0.1f));
		Vec3d cross = caster.getLook(1).crossProduct(new Vec3d(0, caster.getEyeHeight(), 0)).normalize().scale(caster.width / 2);
		Vec3d casterVec = new Vec3d(caster.posX + cross.xCoord, caster.posY + caster.getEyeHeight() + cross.yCoord, caster.posZ + cross.zCoord);
		Vec3d target = raycast.hitVec.subtract(casterVec).scale(-1);
		glitter.setPositionFunction(new InterpCircle(Vec3d.ZERO, target, 0.2f));
		glitter.disableMotion();
		glitter.setRender(new ResourceLocation(Wizardry.MODID, "particles/sparkle"));
		ParticleSpawner.spawn(glitter, player.getEntityWorld(), new InterpLine(raycast.hitVec, casterVec), (int) distance, 0, (aFloat, particleBuilder) -> {
			glitter.setScale(ThreadLocalRandom.current().nextFloat());
		});
		// Beam particles

		do
		{
			if (raycast.typeOfHit == RayTraceResult.Type.BLOCK)
			{
				Entity entity = new SpellEntity(caster.worldObj, raycast.getBlockPos().getX(), raycast.getBlockPos().getY(), raycast.getBlockPos().getZ());
				stack.castEffects(entity);
				return true;
			}
			else if (raycast.typeOfHit == RayTraceResult.Type.ENTITY)
			{
				stack.castEffects(raycast.entityHit);
				pierce--;
				raycast = RaycastUtils.raycast(raycast.entityHit.worldObj, raycast.entityHit.getPositionVector(), caster.getLookVec(), distance);
			}
			else return false;
		}
		while (pierce > 0);
		return true;
	}
}
