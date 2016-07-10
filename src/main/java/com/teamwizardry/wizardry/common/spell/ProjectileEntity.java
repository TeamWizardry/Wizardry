package com.teamwizardry.wizardry.common.spell;

import java.util.List;
import javax.annotation.Nullable;
import net.minecraft.block.Block;
import net.minecraft.entity.Entity;
import net.minecraft.entity.item.EntityItem;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.nbt.NBTTagList;
import net.minecraft.util.EntitySelectors;
import net.minecraft.util.math.AxisAlignedBB;
import net.minecraft.util.math.RayTraceResult;
import net.minecraft.util.math.Vec3d;
import net.minecraft.world.World;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.common.util.Constants.NBT;
import com.google.common.base.Predicate;
import com.google.common.base.Predicates;
import com.teamwizardry.wizardry.api.module.Module;
import com.teamwizardry.wizardry.api.spell.SpellEntity;
import com.teamwizardry.wizardry.api.spell.event.SpellCastEvent;

public class ProjectileEntity extends SpellEntity
{
	private NBTTagList modules;
	private EntityPlayer player;
	private Entity source;
	
	@SuppressWarnings("unchecked")
	private static final Predicate<Entity> TARGETS = Predicates.and(new Predicate[] {EntitySelectors.NOT_SPECTATING, new Predicate<Entity>()
	{
		public boolean apply(@Nullable Entity apply)
		{
			return apply.canBeCollidedWith() || apply instanceof EntityItem;
		}
	}
	});
	
	public ProjectileEntity(World world, double posX, double posY, double posZ, EntityPlayer caster, Entity source, NBTTagCompound spell)
	{
		super(world, posX, posY, posZ, spell);
		this.isImmuneToFire = true;
		this.player = caster;
		this.source = source;
		modules = spell.getTagList(Module.MODULES, NBT.TAG_COMPOUND);
	}
	
	@Override
	public void onEntityUpdate()
	{
		super.onEntityUpdate();
		
		if(!worldObj.isAirBlock(getPosition()))
		{
			AxisAlignedBB axis = worldObj.getBlockState(getPosition()).getCollisionBoundingBox(worldObj, getPosition());
			if (axis != Block.NULL_AABB && axis.offset(getPosition()).isVecInside(new Vec3d(posX, posY, posZ)))
			{
				for (int i = 0; i < modules.tagCount(); i++)
				{
					SpellCastEvent event = new SpellCastEvent(modules.getCompoundTagAt(i), this, player);
					MinecraftForge.EVENT_BUS.post(event);
				}
				this.setDead();
			}
		}
		else
		{
			Vec3d loc = new Vec3d(posX, posY, posZ);
			Vec3d nextLoc = new Vec3d(posX + motionX, posY + motionY, posZ + motionZ);
			RayTraceResult trace = worldObj.rayTraceBlocks(loc, nextLoc, false, true, false);
			if (trace != null)
				loc = new Vec3d(trace.hitVec.xCoord, trace.hitVec.yCoord, trace.hitVec.zCoord);
			
			Entity entity = findEntityOnPath(nextLoc, loc);
			
			if (entity != null)
				trace = new RayTraceResult(entity);
			
			if (trace != null && trace.entityHit != null)
				if (trace.entityHit.equals(source))
					trace = null;
			
			if (trace != null)
			{
				for (int i = 0; i < modules.tagCount(); i++)
				{
					SpellCastEvent event = new SpellCastEvent(modules.getCompoundTagAt(i), trace.entityHit, player);
					MinecraftForge.EVENT_BUS.post(event);
				}
				this.setDead();
			}
		}
	}
	
	private Entity findEntityOnPath(Vec3d start, Vec3d end)
	{
		Entity entity = null;
		List<Entity> list = worldObj.getEntitiesInAABBexcluding(this, getEntityBoundingBox().addCoord(motionX, motionY, motionZ).expandXyz(1), TARGETS);
		double distance = 0;
		
		for (int i = 0; i < list.size(); i++)
		{
			Entity listEntity = list.get(i);
			
			if (listEntity != source)
			{
				AxisAlignedBB axis = listEntity.getEntityBoundingBox().expandXyz(0.3);
				RayTraceResult result = axis.calculateIntercept(start, end);
				
				if (result != null)
				{
					double testDistance = start.squareDistanceTo(result.hitVec);
					
					if (testDistance < distance || distance == 0)
					{
						entity = listEntity;
						distance = testDistance;
					}
				}
			}
		}
		return entity;
	}
	
	public void setDirection(float yaw, float pitch)
	{
		rotationYaw = yaw;
		rotationPitch = pitch;
		double speed = spell.getDouble(Module.SPEED);
		
		// Weird way that Minecraft does directions... This is how to convert (pitch, yaw) to (x, y, z)
		double x = Math.sin(-yaw);
		double y = Math.sin(-pitch);
		double z = Math.cos(yaw);
		
		this.setVelocity(x*speed, y*speed, z*speed);
	}
}
