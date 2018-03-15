package com.teamwizardry.wizardry.common.entity.angel.zachriel;

import com.teamwizardry.librarianlib.features.math.interpolate.StaticInterp;
import com.teamwizardry.librarianlib.features.particle.ParticleBuilder;
import com.teamwizardry.librarianlib.features.particle.ParticleSpawner;
import com.teamwizardry.librarianlib.features.particle.functions.InterpFadeInOut;
import com.teamwizardry.librarianlib.features.saving.AbstractSaveHandler;
import com.teamwizardry.librarianlib.features.saving.SaveInPlace;
import com.teamwizardry.librarianlib.features.utilities.client.ClientRunnable;
import com.teamwizardry.wizardry.Wizardry;
import com.teamwizardry.wizardry.api.Constants;
import com.teamwizardry.wizardry.api.arena.Arena;
import com.teamwizardry.wizardry.api.arena.ArenaManager;
import com.teamwizardry.wizardry.api.util.RandUtil;
import com.teamwizardry.wizardry.common.entity.angel.EntityAngel;
import com.teamwizardry.wizardry.common.entity.angel.zachriel.nemez.NemezArenaTracker;
import com.teamwizardry.wizardry.common.entity.angel.zachriel.nemez.NemezEventHandler;
import com.teamwizardry.wizardry.init.ModItems;
import com.teamwizardry.wizardry.init.ModPotions;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.SharedMonsterAttributes;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.potion.PotionEffect;
import net.minecraft.util.DamageSource;
import net.minecraft.util.EntitySelectors;
import net.minecraft.util.EnumHand;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.math.AxisAlignedBB;
import net.minecraft.util.math.MathHelper;
import net.minecraft.util.math.Vec3d;
import net.minecraft.world.World;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;

import javax.annotation.Nonnull;
import java.awt.*;
import java.util.HashSet;
import java.util.List;
import java.util.UUID;

import static net.minecraftforge.common.util.Constants.NBT.TAG_COMPOUND;

/**
 * Created by Demoniaque.
 */
@SaveInPlace
public class EntityZachriel extends EntityAngel {

	public static final float burstDamage = 10;
	public static final float burstCorruptionScaling = 0.25F;
	private static int timeReverseTick = 200;
	public boolean saveTime = false;
	public boolean reverseTime = false;
	/**
	 * Number of times Zachriel has saved the arena
	 */
	public int numSaves = 0;
	/**
	 * Number of times Zachriel has loaded the last save
	 */
	public int numLoads = 0;
	/**
	 * List of health percentages when Zachriel will save the arena
	 */
	public float[] saveTimes = new float[]{5F / 6F, 1F / 2F, 1F / 6F};
	public int nextBurst = 0;
	public int nextBurstSave = -1;
	/**
	 * List of health percentages when Zachriel will load the last save
	 */
	public float[] loadTimes = new float[]{2F / 3F, 1F / 3F, 1F / getMaxHealth()};
	public int burstTimer = 0;
	public float[] burstLevels = new float[]{0.95F, 0.85F, 0.75F, 0.65F, 0.55F, 0.45F, 0.35F, 0.25F, 0.15F, 0.05F};
	public Arena arena = null;

	public NemezArenaTracker nemezDrive = new NemezArenaTracker();

	public EntityZachriel(World world) {
		super(world);
		setCustomNameTag("Zachriel");
		//setNoGravity(true);
		setSize(1.2F, 3.2F);
	}

	@Override
	protected void applyEntityAttributes() {
		super.applyEntityAttributes();
		getEntityAttribute(SharedMonsterAttributes.MAX_HEALTH).setBaseValue(300.0D);
	}

	@Override
	public boolean attackEntityFrom(@Nonnull DamageSource source, float amount) {
		if (arena == null) {
			HashSet<UUID> players = new HashSet<>();
			arena = new Arena(
					getEntityWorld().provider.getDimension(), getPosition(), 50, 50, getEntityId(), players);
			ArenaManager.INSTANCE.addArena(arena);
		}

		if (burstTimer > 0) return false;
		boolean result = super.attackEntityFrom(source, amount);
		if (source.equals(DamageSource.OUT_OF_WORLD)) // Special case for the /kill command
		{
			numLoads = loadTimes.length;
			return super.attackEntityFrom(source, amount);
		}

		if (!world.isRemote) {
			float healthPercent = this.getHealth() / this.getMaxHealth();
			if (numSaves < saveTimes.length && healthPercent < saveTimes[numSaves]) {
				numSaves++;
				nextBurstSave = nextBurst;
			}
			if (numLoads < loadTimes.length && healthPercent < loadTimes[numLoads]) {
				this.setHealth(this.getMaxHealth() * saveTimes[numLoads]);
				numLoads++;
				nextBurst = nextBurstSave;
				if (this.isDead)
					this.isDead = false;

				// run load code

				NemezEventHandler.reverseTime(this);
			}
			if (nextBurst < burstLevels.length && healthPercent < burstLevels[nextBurst]) {
				nextBurst++;
				burstTimer = 60;
			}
		}


		return result;

//		ZachTimeManager manager = null;
//		for (ZachTimeManager manager1 : ArenaManager.INSTANCE.zachHourGlasses) {
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
	public void onDeath(@Nonnull DamageSource source) {
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

			if (arena == null) {
				HashSet<UUID> players = new HashSet<>();
				arena = new Arena(
						getEntityWorld().provider.getDimension(), getPosition(), 50, 50, getEntityId(), players);
				ArenaManager.INSTANCE.addArena(arena);
			}


			for (EntityLivingBase targeted : arena.getVictims())
				nemezDrive.trackEntity(targeted);

			if (burstTimer > 0) {
				burstTimer--;
				if (burstTimer == 0)
					if (!world.isRemote)
						for (EntityPlayer player : world.playerEntities)
							if (EntitySelectors.CAN_AI_TARGET.apply(player) && getDistanceSq(player) < 32 * 32) {
								PotionEffect corruption = player.getActivePotionEffect(ModPotions.ZACH_CORRUPTION);
								float bonusDamage = corruption == null ? 0 : burstDamage * (corruption.getAmplifier() + 1);
								player.attackEntityFrom(DamageSource.causeMobDamage(this).setMagicDamage().setDamageBypassesArmor(), burstDamage + bonusDamage);
							}

				ClientRunnable.run(new ClientRunnable() {
					@Override
					@SideOnly(Side.CLIENT)
					public void runIfClient() {
						ParticleBuilder glitter = new ParticleBuilder(RandUtil.nextInt(30, 50));
						glitter.setRender(new ResourceLocation(Wizardry.MODID, Constants.MISC.SPARKLE_BLURRED));
						glitter.enableMotionCalculation();
						glitter.setCollision(burstTimer > 0);
						glitter.setCanBounce(burstTimer > 0);
						glitter.setAcceleration(new Vec3d(0, -0.035, 0));
						glitter.setColor(new Color(255, 0, 206));
						glitter.setAlphaFunction(new InterpFadeInOut(0.5F, 0F));
						ParticleSpawner.spawn(glitter, world, new StaticInterp<>(getPositionVector()), 1, 1, (i, build) -> {
							double theta = 2.0F * (float) Math.PI * RandUtil.nextFloat();
							double r;
							if (burstTimer > 0)
								r = (getEntityBoundingBox().maxX - getEntityBoundingBox().minX) / 2 * RandUtil.nextFloat(0.01F, 1);
							else r = 32 * RandUtil.nextFloat();
							double x = r * MathHelper.cos((float) theta);
							double y = (getEntityBoundingBox().maxY - getEntityBoundingBox().minY) * RandUtil.nextFloat();
							double z = r * MathHelper.sin((float) theta);
							build.setPositionOffset(new Vec3d(x, y, z));
							build.addMotion(new Vec3d(0, RandUtil.nextDouble(0.01, 0.15), 0));
						});
					}
				});
			}

			if (!world.isRemote) {
				List<EntityCorruptionArea> corruptionList = world.getEntitiesWithinAABB(EntityCorruptionArea.class, new AxisAlignedBB(getPosition()).grow(2));
				if (corruptionList.isEmpty()) {
					EntityCorruptionArea corruption = new EntityCorruptionArea(world, posX, posY, posZ);
					world.spawnEntity(corruption);
				}
			}
		}

		nemezDrive.endUpdate();
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
	public void writeCustomNBT(@Nonnull NBTTagCompound compound) {
		super.writeCustomNBT(compound);
		compound.setTag("save", AbstractSaveHandler.writeAutoNBT(this, true));
		compound.setTag("nemez", nemezDrive.serializeNBT());
	}

	@Override
	public void readCustomNBT(@Nonnull NBTTagCompound compound) {
		super.readCustomNBT(compound);
		AbstractSaveHandler.readAutoNBT(this, compound.getCompoundTag("save"), true);
		nemezDrive.deserializeNBT(compound.getTagList("nemez", TAG_COMPOUND));
	}
}
