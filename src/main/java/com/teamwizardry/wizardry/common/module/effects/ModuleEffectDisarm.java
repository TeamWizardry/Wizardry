package com.teamwizardry.wizardry.common.module.effects;

import com.teamwizardry.wizardry.api.spell.*;
import com.teamwizardry.wizardry.api.spell.module.Module;
import com.teamwizardry.wizardry.api.spell.module.ModuleEffect;
import com.teamwizardry.wizardry.api.spell.module.RegisterModule;
import com.teamwizardry.wizardry.init.ModSounds;
import com.teamwizardry.wizardry.lib.LibParticles;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.item.EntityItem;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.ItemStack;
import net.minecraft.util.SoundCategory;
import net.minecraft.util.math.Vec3d;
import net.minecraft.world.World;

import javax.annotation.Nonnull;

import static com.teamwizardry.wizardry.api.spell.SpellData.DefaultKeys.ENTITY_HIT;
import static com.teamwizardry.wizardry.api.spell.SpellData.DefaultKeys.TARGET_HIT;

/**
 * Created by LordSaad.
 */
@RegisterModule
public class ModuleEffectDisarm extends ModuleEffect {

	@Nonnull
	@Override
	public String getID() {
		return "effect_disarm";
	}

	@Nonnull
	@Override
	public String getReadableName() {
		return "Disarm";
	}

	@Nonnull
	@Override
	public String getDescription() {
		return "Will drop the target's held item on the ground";
	}

	@Override
	public boolean run(@Nonnull SpellData spell) {
		Entity targetEntity = spell.getData(ENTITY_HIT);

		if (targetEntity instanceof EntityLivingBase) {
			if (!spell.world.isRemote) {
				if (!tax(this, spell)) return false;

				ItemStack held = ((EntityLivingBase) targetEntity).getHeldItemMainhand();

				if (targetEntity instanceof EntityPlayer) {
					for (int i = 9; i < ((EntityPlayer) targetEntity).inventory.getSizeInventory(); i++) {
						if (((EntityPlayer) targetEntity).inventory.getStackInSlot(i) == ItemStack.EMPTY) {
							((EntityPlayer) targetEntity).inventory.setInventorySlotContents(i, held.copy());
							held.setCount(0);
							return true;
						}
					}

					ItemStack copy = held.copy();
					held.setCount(0);
					EntityItem item = new EntityItem(spell.world, targetEntity.posX, targetEntity.posY + 1, targetEntity.posZ, copy);
					item.setPickupDelay(5);
					spell.world.playSound(null, targetEntity.getPosition(), ModSounds.ELECTRIC_BLAST, SoundCategory.NEUTRAL, 1, 1);
					return spell.world.spawnEntity(item);
				} else {
					ItemStack stack = held.copy();
					held.setCount(0);

					EntityItem item = new EntityItem(spell.world, targetEntity.posX, targetEntity.posY + 1, targetEntity.posZ, stack);
					item.setPickupDelay(5);
					spell.world.playSound(null, targetEntity.getPosition(), ModSounds.ELECTRIC_BLAST, SoundCategory.NEUTRAL, 1, 1);
					return spell.world.spawnEntity(item);
				}
			}
		}

		return false;
	}

	@Override
	public void runClient(@Nonnull SpellData spell) {
		World world = spell.world;
		Vec3d position = spell.getData(TARGET_HIT);

		if (position == null) return;

		LibParticles.EFFECT_REGENERATE(world, position, getPrimaryColor());
	}

	@Nonnull
	@Override
	public Module copy() {
		return cloneModule(new ModuleEffectDisarm());
	}
}
