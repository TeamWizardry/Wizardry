package com.teamwizardry.wizardry.common.spell;

import com.teamwizardry.librarianlib.common.util.RaycastUtils;
import com.teamwizardry.wizardry.api.Constants.Module;
import com.teamwizardry.wizardry.api.item.INacreColorable;
import com.teamwizardry.wizardry.api.spell.SpellEntity;
import com.teamwizardry.wizardry.api.trackerobject.SpellStack;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.ItemStack;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.RayTraceResult;
import net.minecraft.util.math.RayTraceResult.Type;
import net.minecraft.util.math.Vec3d;
import net.minecraft.world.World;

import java.awt.*;

public class ProjectileEntity extends SpellEntity {

	private final EntityPlayer player;
	private final SpellStack stack;
	private int ticker;
	private Color trailColor;

	public ProjectileEntity(World world, double posX, double posY, double posZ, SpellStack stack) {
		super(world, posX, posY, posZ, stack.spell);
		setSize(0.1F, 0.1F);
		isImmuneToFire = true;
		player = stack.player;
		this.stack = stack;

		if (stack.player.getHeldItemMainhand() != null) {
			ItemStack item = stack.player.getHeldItemMainhand();
			if (item.getItem() instanceof INacreColorable) {
				INacreColorable colorable = (INacreColorable) item.getItem();
				//trailColor = colorable.getColor(item);
			}
		}
	}

	@Override
	public float getEyeHeight() {
		return 0;
	}

	@Override
	public void onEntityUpdate() {
		super.onEntityUpdate();

		ticker++;
		for (int i = 0; i < 2; i++) {
			double theta = i * Math.toRadians((360.0 / 2) + ticker);
			Vec3d origin = new Vec3d(posX + (0.5 * StrictMath.cos(theta)), posY, posZ + (0.5 * StrictMath.sin(theta)));

			// TODO: Removed particle code (projectile trail)

			// TODO: Removed particle code (projectile dest)
		}

        RayTraceResult cast = RaycastUtils.raycast(world, getPositionVector(), new Vec3d(motionX, motionY, motionZ), Math.min(spell.getDouble(Module.SPEED), 1));

		if (cast != null) {
			if (cast.typeOfHit == Type.BLOCK) {
				BlockPos pos = cast.getBlockPos();
                SpellEntity entity = new SpellEntity(world, pos.getX(), pos.getY(), pos.getZ());
                stack.castEffects(entity);
				setDead();
			} else if ((cast.typeOfHit == Type.ENTITY) && (cast.entityHit != player)) {
				stack.castEffects(cast.entityHit);
				setDead();
			}
		}

		posX += motionX * 4;
		posY += motionY * 4;
		posZ += motionZ * 4;
		setPosition(posX, posY, posZ);
	}

	public void setDirection(float yaw, float pitch) {
		double speed = spell.getDouble(Module.SPEED) / 10;
		Vec3d dir = getVectorForRotation(pitch, yaw);
		setVelocity(dir.xCoord * speed, dir.yCoord * speed, dir.zCoord * speed);
	}
}
