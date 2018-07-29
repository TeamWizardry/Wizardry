package com.teamwizardry.wizardry.common.item;

import com.teamwizardry.librarianlib.core.LibrarianLib;
import com.teamwizardry.librarianlib.features.base.item.ItemMod;
import com.teamwizardry.librarianlib.features.helpers.ItemNBTHelper;
import com.teamwizardry.librarianlib.features.utilities.client.TooltipHelper;
import com.teamwizardry.wizardry.api.capability.mana.CapManager;
import com.teamwizardry.wizardry.common.block.fluid.ModFluids;
import com.teamwizardry.wizardry.common.core.DamageSourceMana;
import com.teamwizardry.wizardry.init.ModPotions;
import net.minecraft.block.BlockLiquid;
import net.minecraft.block.state.IBlockState;
import net.minecraft.client.Minecraft;
import net.minecraft.client.util.ITooltipFlag;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.EnumAction;
import net.minecraft.item.ItemStack;
import net.minecraft.potion.PotionEffect;
import net.minecraft.util.ActionResult;
import net.minecraft.util.EnumActionResult;
import net.minecraft.util.EnumHand;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.RayTraceResult;
import net.minecraft.world.World;
import net.minecraftforge.fluids.Fluid;
import net.minecraftforge.fluids.FluidRegistry;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import java.util.List;
import java.util.UUID;

/**
 * Created by Demoniaque.
 */
public class ItemSyringe extends ItemMod {

	public ItemSyringe() {
		super("syringe", "syringe", "syringe_mana", "syringe_steroid", "syringe_blood");
		setMaxStackSize(1);
	}

	@Nonnull
	@Override
	public ActionResult<ItemStack> onItemRightClick(World world, EntityPlayer player, @Nonnull EnumHand hand) {
		ItemStack stack = player.getHeldItem(hand);
		if (getItemUseAction(stack) == EnumAction.BOW) {
			if (world.isRemote && (Minecraft.getMinecraft().currentScreen != null)) {
				return new ActionResult<>(EnumActionResult.FAIL, stack);
			} else {
				player.setActiveHand(hand);
				return new ActionResult<>(EnumActionResult.PASS, stack);
			}
		} else return new ActionResult<>(EnumActionResult.FAIL, stack);
	}

	@Nonnull
	@Override
	public EnumAction getItemUseAction(ItemStack stack) {
		return stack.getItemDamage() != 3 ? EnumAction.BOW : EnumAction.NONE;
	}

	@Override
	public int getMaxItemUseDuration(ItemStack stack) {
		return 30;
	}

	@Override
	public void onUsingTick(ItemStack stack, EntityLivingBase player, int count) {
		if (!(player instanceof EntityPlayer)) return;
		if (player.world.isRemote) return;

		if (count <= 1) {
			player.swingArm(player.getActiveHand());
			((EntityPlayer) player).getCooldownTracker().setCooldown(this, stack.getItemDamage() == 1 ? 100 : 300);

			if (stack.getItemDamage() == 2) {
				player.addPotionEffect(new PotionEffect(ModPotions.STEROID, 500, 0, true, false));
				stack.setItemDamage(0);
			} else if (stack.getItemDamage() == 1) {
				CapManager.forObject(player)
						.addMana(CapManager.getMaxMana(player) / 1.5)
						.close();
				player.attackEntityFrom(DamageSourceMana.INSTANCE, 2);
				stack.setItemDamage(0);
			} else if (stack.getItemDamage() == 0) {

				RayTraceResult raytraceresult = this.rayTrace(player.world, (EntityPlayer) player, true);

				if (raytraceresult.typeOfHit == RayTraceResult.Type.BLOCK) {
					BlockPos blockpos = raytraceresult.getBlockPos();

					if (player.world.isBlockModifiable((EntityPlayer) player, blockpos)
							&& ((EntityPlayer) player).canPlayerEdit(blockpos.offset(raytraceresult.sideHit), raytraceresult.sideHit, stack)) {

						IBlockState iblockstate = player.world.getBlockState(blockpos);

						Fluid fluid = FluidRegistry.lookupFluidForBlock(iblockstate.getBlock());
						if (fluid != null && fluid == ModFluids.MANA && iblockstate.getValue(BlockLiquid.LEVEL) == 0) {
							stack.setItemDamage(1);
						}
					}
				}
			}
		}
	}

	@Override
	public boolean onLeftClickEntity(ItemStack stack, EntityPlayer player, Entity entity) {
		//if (stack.getItemDamage() == 0) {
		//	entity.attackEntityFrom(DamageSource.causePlayerDamage(player), 2);
		//	stack.setItemDamage(3);
		//	if (entity instanceof EntityPlayer)
		//		ItemNBTHelper.setUUID(stack, "uuid", entity.getUniqueID());
		//	else ItemNBTHelper.setString(stack, "entity", entity.getName());
		//}
		return false;
	}

	@Override
	public void addInformation(ItemStack stack, @Nullable World worldIn, List<String> tooltip, ITooltipFlag flagIn) {
		String desc = stack.getUnlocalizedName() + ".desc";
		String used = LibrarianLib.PROXY.canTranslate(desc) ? desc : desc + "0";
		if (LibrarianLib.PROXY.canTranslate(used)) {
			TooltipHelper.addToTooltip(tooltip, used);
			int i = 0;
			while (LibrarianLib.PROXY.canTranslate(desc + (++i)))
				TooltipHelper.addToTooltip(tooltip, desc + i);
		}

		if (stack.getItemDamage() == 3 && stack.hasTagCompound()) {
			UUID uuid = ItemNBTHelper.getUUID(stack, "uuid");
			String entity = ItemNBTHelper.getString(stack, "entity", null);
			if (uuid != null) {
				EntityPlayer player1 = null;
				if (worldIn != null) {
					player1 = worldIn.getPlayerEntityByUUID(uuid);
				}
				if (player1 != null) tooltip.add(player1.getName());
			}
			if (entity != null) {
				tooltip.add(entity);
			}
		}
	}
}
