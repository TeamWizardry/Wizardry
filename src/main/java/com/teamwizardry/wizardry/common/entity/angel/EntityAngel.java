package com.teamwizardry.wizardry.common.entity.angel;

import com.teamwizardry.librarianlib.features.base.entity.LivingEntityMod;
import com.teamwizardry.wizardry.init.ModSounds;
import net.minecraft.entity.Entity;
import net.minecraft.entity.ai.EntityAIWatchClosest;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.potion.PotionEffect;
import net.minecraft.util.DamageSource;
import net.minecraft.world.World;

import javax.annotation.Nonnull;

public class EntityAngel extends LivingEntityMod {

	private boolean isBeingBattled = false;

	public EntityAngel(World world) {
		super(world);
	}

	@Override
	protected void entityInit() {
		super.entityInit();
		isImmuneToFire = true;
	}

	@Override
	protected void initEntityAI() {
		tasks.addTask(1, new EntityAIWatchClosest(this, EntityPlayer.class, 50.0F));
		applyEntityAI();
	}

	protected void applyEntityAI() {
	}

	@Override
	public void collideWithEntity(Entity entity) {
		if (!(entity instanceof EntityPlayer)) return;
		entity.fallDistance = 0;
		//PosUtils.boom(getEntityWorld(), this);
		playSound(ModSounds.BASS_BOOM, 1f, 1f);
	}

	@Override
	public void onUpdate() {
		super.onUpdate();
		if (world.isRemote) return;

		fallDistance = 0;

	}

	@Override
	public void dropLoot(boolean wasRecentlyHit, int lootingModifier, @Nonnull DamageSource source) {
		super.dropLoot(wasRecentlyHit, lootingModifier, source);
	}

	@Override
	public void addPotionEffect(@Nonnull PotionEffect potioneffectIn) {
	}

	@Override
	protected boolean canBeRidden(Entity entityIn) {
		return false;
	}

	public boolean isNonBoss() {
		return false;
	}

	@Override
	public void writeCustomNBT(@Nonnull NBTTagCompound compound) {
		super.writeCustomNBT(compound);
		compound.setBoolean("is_being_battled", isBeingBattled);
	}

	@Override
	public void readCustomNBT(@Nonnull NBTTagCompound compound) {
		super.readCustomNBT(compound);
		isBeingBattled = compound.getBoolean("is_being_battled");
	}

	public boolean isBeingBattled() {
		return isBeingBattled;
	}

	public void setBeingBattled(boolean beingBattled) {
		isBeingBattled = beingBattled;
	}
}
