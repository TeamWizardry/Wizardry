package com.teamwizardry.wizardry.common.entity.angel;

import com.teamwizardry.wizardry.api.util.Utils;
import com.teamwizardry.wizardry.init.ModSounds;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityLiving;
import net.minecraft.entity.SharedMonsterAttributes;
import net.minecraft.entity.ai.EntityAIWatchClosest;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.potion.PotionEffect;
import net.minecraft.util.DamageSource;
import net.minecraft.world.World;
import org.jetbrains.annotations.NotNull;

import javax.annotation.Nonnull;

public class EntityAngel extends EntityLiving {

	public EntityAngel(World worldIn) {
		super(worldIn);
		setSize(0.6F, 1.8F);
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

	@Override
	protected void applyEntityAttributes() {
		super.applyEntityAttributes();
		getEntityAttribute(SharedMonsterAttributes.MAX_HEALTH).setBaseValue(50.0D);
	}

	protected void applyEntityAI() {
	}

	@Override
	public void collideWithEntity(Entity entity) {
		entity.fallDistance = 0;
		Utils.boom(getEntityWorld(), this);
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
	public void addPotionEffect(@NotNull PotionEffect potioneffectIn) {
	}

	@Override
	protected boolean canBeRidden(Entity entityIn) {
		return false;
	}

	public boolean isNonBoss() {
		return false;
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
