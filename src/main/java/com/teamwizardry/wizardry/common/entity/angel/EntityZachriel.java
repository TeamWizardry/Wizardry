package com.teamwizardry.wizardry.common.entity.angel;

import com.teamwizardry.wizardry.api.arena.Arena;
import com.teamwizardry.wizardry.api.arena.ArenaManager;
import com.teamwizardry.wizardry.common.entity.EntityZachrielCorruption;
import com.teamwizardry.wizardry.init.ModItems;
import net.minecraft.entity.Entity;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.util.DamageSource;
import net.minecraft.util.EnumHand;
import net.minecraft.util.math.AxisAlignedBB;
import net.minecraft.world.World;

import javax.annotation.Nonnull;
import java.util.HashSet;
import java.util.List;
import java.util.UUID;

/**
 * Created by LordSaad.
 */
public class EntityZachriel extends EntityAngel {

	public EntityZachriel(World worldIn) {
		super(worldIn);
		setCustomNameTag("Zachriel");
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
	public void readEntityFromNBT(NBTTagCompound compound) {
		super.readEntityFromNBT(compound);
	}

	@Override
	public void writeEntityToNBT(NBTTagCompound compound) {
		super.writeEntityToNBT(compound);
	}
}
