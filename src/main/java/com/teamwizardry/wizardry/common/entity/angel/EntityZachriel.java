package com.teamwizardry.wizardry.common.entity.angel;

import com.google.gson.JsonObject;
import com.teamwizardry.librarianlib.features.saving.AbstractSaveHandler;
import com.teamwizardry.librarianlib.features.saving.SaveInPlace;
import com.teamwizardry.wizardry.api.arena.Arena;
import com.teamwizardry.wizardry.api.arena.ArenaManager;
import com.teamwizardry.wizardry.api.arena.ZachTimeManager;
import com.teamwizardry.wizardry.common.entity.EntityZachrielCorruption;
import com.teamwizardry.wizardry.init.ModItems;
import net.minecraft.block.Block;
import net.minecraft.block.state.IBlockState;
import net.minecraft.entity.Entity;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.util.DamageSource;
import net.minecraft.util.EnumHand;
import net.minecraft.util.math.AxisAlignedBB;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;
import org.jetbrains.annotations.NotNull;

import javax.annotation.Nonnull;
import java.util.*;

/**
 * Created by LordSaad.
 */
@SaveInPlace
public class EntityZachriel extends EntityAngel {

	public boolean saveTime = false;
	public boolean reverseTime = false;

	public EntityZachriel(World worldIn) {
		super(worldIn);
		setCustomNameTag("Zachriel");
	}

	@Override
	public boolean attackEntityFrom(DamageSource source, float amount) {
		Thread thread = new Thread(() -> {
			ZachTimeManager manager = null;
			for (ZachTimeManager manager1 : ArenaManager.INSTANCE.zachTimeManagers) {
				if (manager1.getEntityZachriel().getEntityId() == getEntityId()) {
					manager = manager1;
				}
			}

			if (manager == null) return;

			ZachTimeManager.BasicPalette palette = manager.getPalette();
			long lastRecordedBlockTime = System.currentTimeMillis();

			for (BlockPos pos : manager.getTrackedBlocks()) {
				HashMap<Long, IBlockState> states = manager.getBlocksAtPos(pos, palette);
				ArrayDeque<Long> dequeTime = new ArrayDeque<>(states.keySet());

				while (!dequeTime.isEmpty()) {
					if (System.currentTimeMillis() - lastRecordedBlockTime < dequeTime.peek() / 10.0) continue;

					IBlockState state = states.get(dequeTime.pop());
					world.setBlockState(pos, state);
					world.playEvent(2001, pos, Block.getStateId(state));
				}
			}

			for (Entity entity : manager.getTrackedEntities(world)) {
				HashMap<Long, JsonObject> snapshots = manager.getEntitySnapshots(entity);
				ArrayDeque<Long> dequeTime = new ArrayDeque<>(snapshots.keySet());

				while (!dequeTime.isEmpty()) {
					if (System.currentTimeMillis() - lastRecordedBlockTime < dequeTime.peek() / 10.0) continue;

					JsonObject snapshot = snapshots.get(dequeTime.pop());
					manager.setEntityToSnapshot(snapshot, entity);
				}
			}
			manager.reset();
		});
		thread.start();

		return false;
	}

	@Override
	public void onUpdate() {
		super.onUpdate();

		fallDistance = 0;

		// BATTLE
		{
			//if (!isBeingBattled()) return;

			if (!world.isRemote) {
				List<Entity> entityList = world.getEntitiesWithinAABBExcludingEntity(this, new AxisAlignedBB(getPosition()).grow(2));
				boolean shouldSpawnMoreCorruption = true;
				if (!entityList.isEmpty())
					for (Entity entity : entityList) {
						if (entity instanceof EntityZachrielCorruption) {
							shouldSpawnMoreCorruption = false;
						}
					}
				if (shouldSpawnMoreCorruption) {
					EntityZachrielCorruption corruption = new EntityZachrielCorruption(world);
					corruption.setPosition(posX, posY, posZ);
					world.spawnEntity(corruption);
				}

			}
		}
	}

	@Override
	protected boolean processInteract(EntityPlayer player, EnumHand hand) {
		if (player.getHeldItemMainhand().getItem() == ModItems.MAGIC_WAND) {
			HashSet<UUID> players = new HashSet<>();
			players.add(player.getUniqueID());
			ArenaManager.INSTANCE.addArena(new Arena(player.world.provider.getDimension(), getPosition(), 10, 10, getEntityId(), players));
		}
		return super.processInteract(player, hand);
	}

	@Override
	public void dropLoot(boolean wasRecentlyHit, int lootingModifier, @Nonnull DamageSource source) {
		super.dropLoot(wasRecentlyHit, lootingModifier, source);
	}

	@Override
	public void writeCustomNBT(@NotNull NBTTagCompound compound) {
		super.writeCustomNBT(compound);
		compound.setTag("save", AbstractSaveHandler.writeAutoNBT(this, true));
	}

	@Override
	public void readCustomNBT(@NotNull NBTTagCompound compound) {
		super.readCustomNBT(compound);
		AbstractSaveHandler.readAutoNBT(this, compound.getCompoundTag("save"), true);
	}
}
