package com.teamwizardry.wizardry.common.spell;

import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.nbt.NBTTagList;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.RayTraceResult;
import net.minecraft.util.math.Vec3d;
import net.minecraft.world.World;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.common.util.Constants.NBT;
import com.teamwizardry.librarianlib.math.Raycast;
import com.teamwizardry.wizardry.api.module.Module;
import com.teamwizardry.wizardry.api.spell.SpellEntity;
import com.teamwizardry.wizardry.api.spell.event.SpellCastEvent;

public class ProjectileEntity extends SpellEntity
{
	private NBTTagList modules;
	private EntityPlayer player;
	
	public ProjectileEntity(World world, double posX, double posY, double posZ, EntityPlayer caster, NBTTagCompound spell)
	{
		super(world, posX, posY, posZ, spell);
		this.setSize(0.1F, 0.1F);
		this.isImmuneToFire = true;
		this.player = caster;
		modules = spell.getTagList(Module.MODULES, NBT.TAG_COMPOUND);
	}
	
	@Override
	public float getEyeHeight()
	{
		return 0;
		
	}
	
	@Override
	public void onEntityUpdate()
	{	
		super.onEntityUpdate();
		RayTraceResult cast = Raycast.cast(this, new Vec3d(motionX, motionY, motionZ), Math.min(spell.getDouble(Module.SPEED), 1));
		
		if (cast != null)
		{
			if (cast.typeOfHit == RayTraceResult.Type.BLOCK)
			{
				for (int i = 0; i < modules.tagCount(); i++)
				{
					BlockPos pos = cast.getBlockPos();
					SpellEntity entity = new SpellEntity(worldObj, pos.getX(), pos.getY(), pos.getZ());
					SpellCastEvent event = new SpellCastEvent(modules.getCompoundTagAt(i), entity, player);
					MinecraftForge.EVENT_BUS.post(event);
				}
				this.setDead();
			}
			else if (cast.typeOfHit == RayTraceResult.Type.ENTITY && cast.entityHit != player)
			{
				for (int i = 0; i < modules.tagCount(); i++)
				{
					SpellCastEvent event = new SpellCastEvent(modules.getCompoundTagAt(i), cast.entityHit, player);
					MinecraftForge.EVENT_BUS.post(event);
				}
				this.setDead();
			}
		}
		
		posX += motionX;
		posY += motionY;
		posZ += motionZ;
		setPosition(posX, posY, posZ);
	}
	
	public void setDirection(float yaw, float pitch)
	{
		double speed = spell.getDouble(Module.SPEED) / 10;
		Vec3d dir = this.getVectorForRotation(pitch, yaw);
		this.setVelocity(dir.xCoord * speed, dir.yCoord * speed, dir.zCoord * speed);
	}
}
