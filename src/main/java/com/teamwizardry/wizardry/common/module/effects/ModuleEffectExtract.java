package com.teamwizardry.wizardry.common.module.effects;

import com.teamwizardry.wizardry.api.spell.SpellData;
import com.teamwizardry.wizardry.api.spell.SpellRing;
import com.teamwizardry.wizardry.api.spell.annotation.RegisterModule;
import com.teamwizardry.wizardry.api.spell.attribute.AttributeRegistry;
import com.teamwizardry.wizardry.api.spell.module.IModuleEffect;
import com.teamwizardry.wizardry.api.spell.module.ModuleInstanceEffect;
import com.teamwizardry.wizardry.client.fx.LibParticles;
import com.teamwizardry.wizardry.init.ModSounds;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.item.EntityItem;
import net.minecraft.item.ItemStack;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.SoundCategory;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Vec3d;
import net.minecraft.world.World;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;
import net.minecraftforge.items.CapabilityItemHandler;
import net.minecraftforge.items.IItemHandler;
import org.jetbrains.annotations.NotNull;

import javax.annotation.Nonnull;

/**
 * Created by Demoniaque.
 */
@RegisterModule(ID = "effect_extract")
public class ModuleEffectExtract implements IModuleEffect {

	@Override
	public String[] compatibleModifiers() {
		return new String[]{"modifier_increase_potency", "modifier_extend_time"};
	}

	@Override
	public boolean run(@NotNull World world, ModuleInstanceEffect instance, @Nonnull SpellData spell, @Nonnull SpellRing spellRing) {
		Entity targetEntity = spell.getVictim(world);
		BlockPos targetPos = spell.getTargetPos();
		EnumFacing facing = spell.getFaceHit();

		double maxPotency = instance.getAttributeRanges().get(AttributeRegistry.POTENCY).max;
		double minPotency = instance.getAttributeRanges().get(AttributeRegistry.POTENCY).min;
		double potency = spellRing.getAttributeValue(world, AttributeRegistry.POTENCY, spell);
		double duration = spellRing.getAttributeValue(world, AttributeRegistry.DURATION, spell);

		if (targetEntity instanceof EntityLivingBase) {
			if (!world.isRemote) {
				if (!spellRing.taxCaster(world, spell, true)) return false;

				ItemStack held = ((EntityLivingBase) targetEntity).getHeldItemMainhand();

				EntityItem entityitem = new EntityItem(world, targetEntity.posX, targetEntity.posY, targetEntity.posZ, held.copy());
				entityitem.setPickupDelay((int) (duration));
				world.playSound(null, targetEntity.getPosition(), ModSounds.ELECTRIC_BLAST, SoundCategory.NEUTRAL, 1, 1);
				world.spawnEntity(entityitem);
				held.setCount(0);

				return true;
			}
		} else if (targetPos != null) {
			TileEntity tile = world.getTileEntity(targetPos);
			if (tile != null) {
				if (tile.hasCapability(CapabilityItemHandler.ITEM_HANDLER_CAPABILITY, facing)) {
					IItemHandler cap = tile.getCapability(CapabilityItemHandler.ITEM_HANDLER_CAPABILITY, facing);
					if (cap == null) return true;

					for (int i = 0; i < cap.getSlots(); i++) {
						ItemStack extracted = cap.extractItem(i, (int) potency, false);
						if (!extracted.isEmpty()) {
							if (!spellRing.taxCaster(world, spell, true)) return false;

							Vec3d pos = new Vec3d(targetPos.getX(), targetPos.getY(), targetPos.getZ()).add(0.5, 0.5, 0.5);
							if (facing != null)
								pos = pos.add(new Vec3d(facing.getDirectionVec()).scale(0.5));
							else
								pos = pos.add(0, 1, 0);

							EntityItem entityitem = new EntityItem(world, pos.x, pos.y, pos.z, extracted);
							entityitem.setPickupDelay((int) duration);
							world.spawnEntity(entityitem);

							return true;
						}
					}
				}
			}
		}

		return false;
	}

	@Override
	@SideOnly(Side.CLIENT)
	public void renderSpell(World world, ModuleInstanceEffect instance, @Nonnull SpellData
			spell, @Nonnull SpellRing spellRing) {
		Vec3d position = spell.getTarget(world);

		if (position == null) return;

		LibParticles.EFFECT_REGENERATE(world, position, instance.getPrimaryColor());
	}
}
