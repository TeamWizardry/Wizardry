package com.teamwizardry.wizardry.api.trackerobject;

import com.teamwizardry.librarianlib.client.fx.particle.EnumMovementMode;
import com.teamwizardry.librarianlib.client.fx.particle.ParticleBuilder;
import com.teamwizardry.librarianlib.client.fx.particle.ParticleSpawner;
import com.teamwizardry.librarianlib.common.util.math.interpolate.StaticInterp;
import com.teamwizardry.librarianlib.common.util.math.interpolate.position.InterpHelix;
import com.teamwizardry.wizardry.Wizardry;
import com.teamwizardry.wizardry.api.util.PosUtils;
import com.teamwizardry.wizardry.init.ModItems;
import com.teamwizardry.wizardry.init.ModSounds;
import io.netty.util.internal.ThreadLocalRandom;
import net.minecraft.block.material.Material;
import net.minecraft.entity.item.EntityItem;
import net.minecraft.init.Blocks;
import net.minecraft.item.ItemStack;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.SoundCategory;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Vec3d;
import net.minecraft.world.World;

import java.awt.*;
import java.util.ArrayList;

/**
 * Created by Saad on 6/27/2016.
 */
public class DevilDustTracker {

	public static ArrayList<DevilDustTracker> expired = new ArrayList<>();

	private Vec3d pos;
	private World world;
	private int expiry = 0;
	private EntityItem redstone;
	private int stackSize = 1;
	private boolean consumed = false;

	public DevilDustTracker(EntityItem redstone, int expiry) {
		stackSize = redstone.getEntityItem().stackSize;
		this.redstone = redstone;
		world = redstone.worldObj;
		pos = new Vec3d(redstone.getPosition().getX() + 0.5, redstone.getPosition().getY(), redstone.getPosition().getZ() + 0.5);
		this.expiry = expiry;
	}

	public void tick() {
		if (redstone.isDead && !consumed) {
			if (!expired.contains(this)) expired.add(this);
			return;
		}

		if (!consumed) {
			stackSize = redstone.getEntityItem().stackSize;

			BlockPos fire = PosUtils.checkNeighbor(world, redstone.getPosition(), Blocks.FIRE);
			if (world.getBlockState(fire).getBlock() == Blocks.FIRE
					&& world.isMaterialInBB(redstone.getEntityBoundingBox().expand(0.1, 0.1, 0.1), Material.FIRE)) {

				pos = new Vec3d(fire.getX() + 0.5, fire.getY(), fire.getZ() + 0.5);
				redstone.setDead();
				consumed = true;
			}
		} else {
			if (expiry > 0) {
				expiry--;

				ParticleBuilder glitter = new ParticleBuilder(20);
				glitter.setColor(Color.RED);
				glitter.setMovementMode(EnumMovementMode.IN_DIRECTION);
				glitter.setRender(new ResourceLocation(Wizardry.MODID, "particles/sparkle"));
				glitter.setPositionFunction(new InterpHelix(new Vec3d(0, 0, 0), new Vec3d(0, 1.5, 0), 1.2f, 0, 4, 0));
				ParticleSpawner.spawn(glitter, world, new StaticInterp<>(pos), 1, 200);

				if (expiry % 5 == 0)
					world.playSound(null, pos.xCoord, pos.yCoord, pos.zCoord, ModSounds.FRYING_SIZZLE, SoundCategory.BLOCKS, 0.7F, (float) ThreadLocalRandom.current().nextDouble(0.8, 1.3));
			} else {
				EntityItem devilDust = new EntityItem(world, pos.xCoord, pos.yCoord, pos.zCoord, new ItemStack(ModItems.DEVIL_DUST, stackSize));
				devilDust.setPickupDelay(5);
				devilDust.motionY = 0.8;
				devilDust.forceSpawn = true;
				world.spawnEntityInWorld(devilDust);
				expired.add(this);
			}
		}
	}
}
