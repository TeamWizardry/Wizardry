package com.teamwizardry.wizardry.common.module.effects;

import com.teamwizardry.wizardry.api.item.BaublesSupport;
import com.teamwizardry.wizardry.api.spell.SpellData;
import com.teamwizardry.wizardry.api.spell.SpellRing;
import com.teamwizardry.wizardry.api.spell.annotation.RegisterModule;
import com.teamwizardry.wizardry.api.spell.attribute.AttributeRegistry;
import com.teamwizardry.wizardry.api.spell.module.IModuleEffect;
import com.teamwizardry.wizardry.api.spell.module.ModuleInstanceEffect;
import com.teamwizardry.wizardry.api.util.RandUtil;
import com.teamwizardry.wizardry.client.fx.LibParticles;
import com.teamwizardry.wizardry.init.ModSounds;
import com.teamwizardry.wizardry.proxy.CommonProxy;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.item.EntityItem;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.ItemStack;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Vec3d;
import net.minecraft.world.ILockableContainer;
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

	private static boolean shouldContinue(Entity caster, EntityLivingBase target, ItemStack stack) {
		if (!(caster instanceof EntityLivingBase)) return true;

		ItemStack held = ((EntityLivingBase) caster).getHeldItemMainhand();

		return !target.getUniqueID().equals(caster.getUniqueID()) || !ItemStack.areItemStacksEqual(held, stack);
	}

	@Override
	public boolean run(@NotNull World world, ModuleInstanceEffect instance, @Nonnull SpellData spell, @Nonnull SpellRing spellRing) {
		Entity caster = spell.getCaster(world);
		Entity targetEntity = spell.getVictim(world);
		BlockPos targetPos = spell.getTargetPos();
		EnumFacing facing = spell.getFaceHit();

		if (caster == null) return true;

		double potency = spellRing.getAttributeValue(world, AttributeRegistry.POTENCY, spell);
		double maxPotency = spellRing.getModule() != null ? spellRing.getModule().getAttributeRanges().get(AttributeRegistry.POTENCY).max : 0;
		float powerLevel = (float) (potency / maxPotency);
		double duration = spellRing.getAttributeValue(world, AttributeRegistry.DURATION, spell);

		if (targetEntity instanceof EntityLivingBase) {
			if (!spellRing.taxCaster(world, spell, true)) return false;

			ItemStack stack = ItemStack.EMPTY;

			boolean success = false;

			if (!(targetEntity instanceof EntityPlayer)) {
				stack = ((EntityLivingBase) targetEntity).getHeldItemMainhand();
				if (stack.isEmpty()) {
					stack = ((EntityLivingBase) targetEntity).getHeldItemOffhand();
				}
			}
			if (!stack.isEmpty()) success = true;

			if (!success)
				if (powerLevel <= 0.25) {
					// back to front of inv
					if (targetEntity instanceof EntityPlayer) {
						for (int i = ((EntityPlayer) targetEntity).inventory.getSizeInventory() - 1; i > 0; i--) {
							ItemStack invStack = ((EntityPlayer) targetEntity).inventory.getStackInSlot(i);
							if (!invStack.isEmpty() && shouldContinue(caster, (EntityLivingBase) targetEntity, invStack)) {
								stack = invStack;
								success = true;
								break;
							}
						}
					}
				} else if (powerLevel <= 0.5) {
					// front to back of inv
					if (targetEntity instanceof EntityPlayer) {
						for (int i = 0; i < ((EntityPlayer) targetEntity).inventory.getSizeInventory(); i++) {
							ItemStack invStack = ((EntityPlayer) targetEntity).inventory.getStackInSlot(i);
							if (!invStack.isEmpty() && shouldContinue(caster, (EntityLivingBase) targetEntity, invStack)) {
								stack = invStack;
								success = true;
								break;
							}
						}
					}
				} else {
					//baubles
					if (powerLevel >= 0.75) {
						for (ItemStack bauble : BaublesSupport.getAllBaubles((EntityLivingBase) targetEntity)) {
							if (bauble.isEmpty()) continue;
							stack = bauble;
							success = true;
							break;
						}
					}

					// remove armor
					if (!success) {
						for (ItemStack armorStack : targetEntity.getArmorInventoryList()) {
							if (!armorStack.isEmpty()) {

								stack = armorStack;
								success = true;
								break;
							}
						}
					}

					// front to back
					if (!success) {
						if (targetEntity instanceof EntityPlayer) {
							for (int i = 0; i < ((EntityPlayer) targetEntity).inventory.getSizeInventory(); i++) {
								ItemStack invStack = ((EntityPlayer) targetEntity).inventory.getStackInSlot(i);
								if (!invStack.isEmpty() && shouldContinue(caster, (EntityLivingBase) targetEntity, invStack)) {
									stack = invStack;
									success = true;
									break;
								}
							}
						}
					}
				}

			if (!success || stack.isEmpty())
				return true;

			ItemStack left = stack.splitStack(Math.max((int) (stack.getMaxStackSize() * powerLevel), 1));

			EntityItem entityitem = new EntityItem(world, targetEntity.posX, targetEntity.posY, targetEntity.posZ, left);
			entityitem.setPickupDelay((int) (duration));
			world.spawnEntity(entityitem);
			world.playSound(null, targetEntity.getPosition(), ModSounds.ENCHANTED_WHASHOOSH, CommonProxy.SoundCategory_WizardryGeneral, 1, RandUtil.nextFloat(0.5f, 1.5f));


			return true;

		} else if (targetPos != null) {
			TileEntity tile = world.getTileEntity(targetPos);
			if (tile != null) {
				if (tile instanceof ILockableContainer && ((ILockableContainer) tile).isLocked()) return true;

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
							world.playSound(null, targetPos, ModSounds.ENCHANTED_WHASHOOSH, CommonProxy.SoundCategory_WizardryGeneral, 1, RandUtil.nextFloat(0.5f, 1.5f));

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
