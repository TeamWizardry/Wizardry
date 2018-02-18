package com.teamwizardry.wizardry.api.arena;

import com.teamwizardry.librarianlib.features.math.interpolate.position.InterpCircle;
import com.teamwizardry.librarianlib.features.particle.ParticleBuilder;
import com.teamwizardry.librarianlib.features.particle.ParticleSpawner;
import com.teamwizardry.librarianlib.features.particle.functions.InterpFadeInOut;
import com.teamwizardry.librarianlib.features.saving.AbstractSaveHandler;
import com.teamwizardry.librarianlib.features.saving.Savable;
import com.teamwizardry.librarianlib.features.saving.Save;
import com.teamwizardry.librarianlib.features.utilities.client.ClientRunnable;
import com.teamwizardry.wizardry.Wizardry;
import com.teamwizardry.wizardry.api.Constants;
import com.teamwizardry.wizardry.api.util.ColorUtils;
import com.teamwizardry.wizardry.api.util.RandUtil;
import com.teamwizardry.wizardry.client.fx.LibParticles;
import com.teamwizardry.wizardry.common.entity.angel.EntityAngel;
import com.teamwizardry.wizardry.common.entity.angel.zachriel.EntityZachriel;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.math.AxisAlignedBB;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Vec3d;
import net.minecraft.world.World;
import net.minecraftforge.common.DimensionManager;
import net.minecraftforge.common.util.INBTSerializable;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;

import java.awt.*;
import java.util.Set;
import java.util.UUID;

@Savable
public class Arena implements INBTSerializable<NBTTagCompound> {

	private transient World world;
	private transient EntityAngel boss;
	@Save
	private int worldID;
	@Save
	private BlockPos center;
	@Save
	private double radius;
	@Save
	private double height;
	@Save
	private int bossID;
	@Save
	private Set<UUID> players;
	@Save
	private boolean isActive = true;
	@Save
	private boolean hasEnded = false;
	@Save
	private long startTick = 0;

	public Arena(int worldID, BlockPos center, double radius, double height, int bossID, Set<UUID> players) {
		this.worldID = worldID;
		this.world = DimensionManager.getWorld(worldID);
		this.boss = (EntityAngel) world.getEntityByID(bossID);
		this.center = center;
		this.radius = radius;
		this.height = height;
		this.bossID = bossID;
		this.players = players;
	}

	public boolean sanityCheck() {
		for (double x = -radius; x < radius; x++)
			for (double z = -radius; z < radius; z++)
				for (int y = 0; y < height; y++) {
					BlockPos pos = center.add(x, y, z);
					if (pos.getDistance(center.getX(), center.getY(), center.getZ()) > radius) {
						LibParticles.STRUCTURE_FLAIR(world, new Vec3d(pos).addVector(0.5, 0.5, 0.5), Color.RED);
						return false;
					}
					if (!world.canBlockSeeSky(pos)) {
						LibParticles.STRUCTURE_FLAIR(world, new Vec3d(pos).addVector(0.5, 0.5, 0.5), Color.GREEN);
						return false;
					}
					if (!world.isAirBlock(pos)) {
						LibParticles.STRUCTURE_FLAIR(world, new Vec3d(pos).addVector(0.5, 0.5, 0.5), Color.BLUE);
						return false;
					}
				}
		return true;
	}

	public void dealWithStructureConflict() {

	}

	public void begin() {
		isActive = true;
		hasEnded = false;
		startTick = System.currentTimeMillis();
	}

	public void tick(long timeMillis) {

		if (timeMillis % 500 == 0)
			ClientRunnable.run(new ClientRunnable() {
				@Override
				@SideOnly(Side.CLIENT)
				public void runIfClient() {
					ParticleBuilder glitter = new ParticleBuilder(10);
					glitter.setRender(new ResourceLocation(Wizardry.MODID, Constants.MISC.SPARKLE_BLURRED));
					glitter.setAlphaFunction(new InterpFadeInOut(0.3f, 0.3f));
					glitter.setCollision(true);
					glitter.enableMotionCalculation();
					ParticleSpawner.spawn(glitter, getWorld(), new InterpCircle(new Vec3d(getCenter()).addVector(0.5, getHeight(), 0.5), new Vec3d(0, 1, 0), (float) getRadius(), 1, RandUtil.nextFloat()), 10, RandUtil.nextInt(10), (aFloat, particleBuilder) -> {
						particleBuilder.setColor(ColorUtils.changeColorAlpha(new Color(0x0097FF), RandUtil.nextInt(100, 255)));
						particleBuilder.setScale(RandUtil.nextFloat(0.5f, 1));
						particleBuilder.addMotion(new Vec3d(RandUtil.nextDouble(-0.01, 0.01),
								RandUtil.nextDouble(-0.01, 0.01),
								RandUtil.nextDouble(-0.01, 0.01)));
						particleBuilder.setLifetime(RandUtil.nextInt(30, 60));
					});

					glitter.disableMotionCalculation();
					ParticleSpawner.spawn(glitter, getWorld(), new InterpCircle(new Vec3d(getCenter()).addVector(0.5, 0, 0.5), new Vec3d(0, 1, 0), (float) getRadius(), 1, RandUtil.nextFloat()), 10, RandUtil.nextInt(10), (aFloat, particleBuilder) -> {
						particleBuilder.setColor(ColorUtils.changeColorAlpha(new Color(0x0097FF), RandUtil.nextInt(100, 255)));
						particleBuilder.setScale(RandUtil.nextFloat(0.5f, 1));
						particleBuilder.addMotion(new Vec3d(RandUtil.nextDouble(-0.01, 0.01),
								RandUtil.nextDouble(0.01, 0.02),
								RandUtil.nextDouble(-0.01, 0.01)));
						particleBuilder.setLifetime(RandUtil.nextInt(30, 60));
					});
				}
			});
	}

	public void end() {
		hasEnded = true;
		isActive = false;
	}

	public boolean isActive() {
		return isActive;
	}

	public void setActive(boolean active) {
		isActive = active;
	}

	public long getStartTick() {
		return startTick;
	}

	public boolean hasEnded() {
		return hasEnded;
	}

	public World getWorld() {
		return world;
	}

	public int getWorldID() {
		return worldID;
	}

	public BlockPos getCenter() {
		return center;
	}

	public double getRadius() {
		return radius;
	}

	public double getHeight() {
		return height;
	}

	public int getBossID() {
		return bossID;
	}

	public Set<UUID> getPlayers() {
		return players;
	}

	public Iterable<EntityLivingBase> getVictims() {
		return world.getEntitiesWithinAABB(EntityLivingBase.class,
				new AxisAlignedBB(center).grow(radius, 0, radius).expand(0, height, 0),
				(entity) -> entity != null && entity.isEntityAlive() &&
						entity.canBeHitWithPotion() && !(entity instanceof EntityZachriel) &&
						entity.getDistanceSq(center) < radius * radius);
	}

	public boolean isHasEnded() {
		return hasEnded;
	}

	public void setHasEnded(boolean hasEnded) {
		this.hasEnded = hasEnded;
	}

	@Override
	public NBTTagCompound serializeNBT() {
		NBTTagCompound compound = new NBTTagCompound();
		compound.setTag("save", AbstractSaveHandler.writeAutoNBT(this, true));
		return compound;
	}

	@Override
	public void deserializeNBT(NBTTagCompound nbt) {
		AbstractSaveHandler.readAutoNBT(this, nbt.getCompoundTag("save"), true);
	}

	public EntityAngel getBoss() {
		return boss;
	}
}
