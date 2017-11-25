package com.teamwizardry.wizardry.common.entity.angel.zachriel;

import java.util.HashSet;
import java.util.List;
import java.util.UUID;

import javax.annotation.Nonnull;

import org.jetbrains.annotations.NotNull;

import com.teamwizardry.librarianlib.features.saving.AbstractSaveHandler;
import com.teamwizardry.librarianlib.features.saving.SaveInPlace;
import com.teamwizardry.wizardry.api.arena.Arena;
import com.teamwizardry.wizardry.api.arena.ArenaManager;
import com.teamwizardry.wizardry.common.entity.angel.EntityAngel;
import com.teamwizardry.wizardry.init.ModItems;

import net.minecraft.entity.SharedMonsterAttributes;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.util.DamageSource;
import net.minecraft.util.EnumHand;
import net.minecraft.util.math.AxisAlignedBB;
import net.minecraft.util.text.TextComponentString;
import net.minecraft.world.World;

/**
 * Created by LordSaad.
 */
@SaveInPlace
public class EntityZachriel extends EntityAngel {

	public boolean saveTime = false;
	public boolean reverseTime = false;
	
	/**
	 * Number of times Zachriel has saved the arena
	 */
	public int numSaves = 0;
	/**
	 * List of health percentages when Zachriel will save the arena
	 */
	public float[] saveTimes = new float[]{ 5F/6F, 1F/2F, 1F/6F };
	/**
	 * Number of times Zachriel has loaded the last save
	 */
	public int numLoads = 0;
	/**
	 * List of health percentages when Zachriel will load the last save
	 */
	public float[] loadTimes = new float[]{ 2F/3F, 1F/3F, 1F/getMaxHealth() };

	public EntityZachriel(World world) {
		super(world);
		setCustomNameTag("Zachriel");
		setNoGravity(true);
		setSize(1.2F, 3.2F);
	}
	
	@Override
	protected void applyEntityAttributes()
	{
		super.applyEntityAttributes();
		getEntityAttribute(SharedMonsterAttributes.MAX_HEALTH).setBaseValue(300.0D);
	}

	@Override
	public boolean attackEntityFrom(DamageSource source, float amount) {
		boolean result = super.attackEntityFrom(source, amount);
		if (source.equals(DamageSource.OUT_OF_WORLD)) // Special case for the /kill command
		{
			numLoads = loadTimes.length;
			return super.attackEntityFrom(source, amount);
		}
		if (!world.isRemote)
		{
			float healthPercent = this.getHealth() / this.getMaxHealth();
			if (numSaves < saveTimes.length && healthPercent < saveTimes[numSaves])
			{
				numSaves++;
				source.getTrueSource().sendMessage(new TextComponentString("Save #" + numSaves));
				// run save code
			}
			if (numLoads < loadTimes.length && healthPercent < loadTimes[numLoads])
			{
				this.setHealth(this.getMaxHealth() * saveTimes[numLoads]);
				numLoads++;
				source.getTrueSource().sendMessage(new TextComponentString("Load #" + numLoads));
				if (this.isDead)
					this.isDead = false;
				// run load code
			}
		}
		
		return result;

//		ZachTimeManager manager = null;
//		for (ZachTimeManager manager1 : ArenaManager.INSTANCE.zachTimeManagers) {
//			if (manager1.getEntityZachriel().getEntityId() == getEntityId()) {
//				manager = manager1;
//			}
//		}
//
//		if (manager == null) return false;
//
//		ZachTimeManager finalManager = manager;
//		long lastRecordedBlockTime = System.currentTimeMillis();
//
//		Thread thread = new Thread(() -> {
//			ZachTimeManager.BasicPalette palette = finalManager.getPalette();
//
//			for (BlockPos pos : finalManager.getTrackedBlocks()) {
//				HashMap<Long, IBlockState> states = finalManager.getBlocksAtPos(pos, palette);
//				ArrayDeque<Long> dequeTime = new ArrayDeque<>(states.keySet());
//
//				while (!dequeTime.isEmpty()) {
//					if (System.currentTimeMillis() - lastRecordedBlockTime < dequeTime.peek() / 10.0) continue;
//
//					IBlockState state = states.get(dequeTime.pop());
//					world.setBlockState(pos, state);
//					world.playEvent(2001, pos, Block.getStateId(state));
//				}
//			}
//			finalManager.resetBlocks();
//		});
//		thread.start();
//
//		thread = new Thread(() -> {
//			for (Entity entity : finalManager.getTrackedEntities(world)) {
//				HashMap<Long, JsonObject> snapshots = finalManager.getEntitySnapshots(entity);
//				ArrayDeque<Long> dequeTime = new ArrayDeque<>(snapshots.keySet());
//
//				while (!dequeTime.isEmpty()) {
//					if (System.currentTimeMillis() - lastRecordedBlockTime < dequeTime.peek()) continue;
//
//					JsonObject snapshot = snapshots.get(dequeTime.pop());
//					finalManager.setEntityToSnapshot(snapshot, entity);
//				}
//			}
//			finalManager.resetEntities();
//		});
//		thread.start();
//
//		return false;
	}
	
	@Override
	public void onDeath(DamageSource source)
	{
		if (numLoads < loadTimes.length)
			return;
		super.onDeath(source);
	}

	@Override
	public void onUpdate() {
		this.motionY = 0;
		super.onUpdate();

		// BATTLE
		{
			//if (!isBeingBattled()) return;

			if (!world.isRemote)
			{
				List<EntityCorruptionArea> corruptionList = world.getEntitiesWithinAABB(EntityCorruptionArea.class, new AxisAlignedBB(getPosition()).grow(2));
				if (corruptionList.isEmpty())
				{
					EntityCorruptionArea corruption = new EntityCorruptionArea(world, posX, posY, posZ);
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
		entityDropItem(new ItemStack(ModItems.REAL_HALO), 0);
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
