package com.teamwizardry.wizardry.common.entity.angel.zachriel;

import com.teamwizardry.librarianlib.features.base.entity.EntityMod;
import com.teamwizardry.librarianlib.features.utilities.client.ClientRunnable;
import com.teamwizardry.wizardry.init.ModPotions;
import net.minecraft.block.material.EnumPushReaction;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.potion.PotionEffect;
import net.minecraft.world.World;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;

public class EntityCorruptionProjectile extends EntityMod {

	public EntityCorruptionProjectile(World worldIn) {
		super(worldIn);
		setSize(1, 1);
	}

	@Override
	public void onCollideWithPlayer(EntityPlayer entity) {
		PotionEffect effect = entity.getActivePotionEffect(ModPotions.ZACH_CORRUPTION);
		if (effect == null)
			entity.addPotionEffect(new PotionEffect(ModPotions.ZACH_CORRUPTION, 100, 0, true, false));
		else
			entity.addPotionEffect(new PotionEffect(ModPotions.ZACH_CORRUPTION, 100, effect.getAmplifier() + 1, true, false));
	}

	@Override
	public void onUpdate() {
		super.onUpdate();
		if (this.onGround) {
			this.setDead();
			EntityCorruptionArea area = new EntityCorruptionArea(this.world, this.posX, this.posY, this.posZ);
			this.world.spawnEntity(area);
		}

		ClientRunnable.run(new ClientRunnable() {
			@Override
			@SideOnly(Side.CLIENT)
			public void runIfClient() {
			}
		});
	}

	@Override
	public EnumPushReaction getPushReaction() {
		return EnumPushReaction.IGNORE;
	}

	@Override
	protected void entityInit() {
	}
}
