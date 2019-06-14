package com.teamwizardry.wizardry.common.item;

import com.teamwizardry.librarianlib.features.base.item.ItemMod;
import com.teamwizardry.librarianlib.features.helpers.NBTHelper;
import com.teamwizardry.wizardry.api.capability.player.miscdata.IMiscCapability;
import com.teamwizardry.wizardry.api.capability.player.miscdata.MiscCapabilityProvider;
import com.teamwizardry.wizardry.api.entity.FairyData;
import com.teamwizardry.wizardry.common.entity.EntityFairy;
import com.teamwizardry.wizardry.init.ModSounds;
import net.minecraft.block.state.IBlockState;
import net.minecraft.client.resources.I18n;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.ItemStack;
import net.minecraft.util.*;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Vec3d;
import net.minecraft.util.text.TextComponentTranslation;
import net.minecraft.world.World;
import org.jetbrains.annotations.NotNull;

import java.util.UUID;

public class ItemFairyBell extends ItemMod {

	public ItemFairyBell() {
		super("fairy_bell");
	}

	@Override
	public boolean itemInteractionForEntity(ItemStack stack, EntityPlayer playerIn, EntityLivingBase target, EnumHand hand) {
		if (playerIn.world.isRemote || playerIn.isSneaking())
			return super.itemInteractionForEntity(stack, playerIn, target, hand);

		if (target instanceof EntityFairy) {

			EntityFairy fairy = (EntityFairy) target;
			FairyData fairyData = fairy.getDataFairy();

			if (fairyData == null) return super.itemInteractionForEntity(stack, playerIn, target, hand);

			if (fairyData.isDepressed) {
				IMiscCapability cap = MiscCapabilityProvider.getCap(playerIn);
				if (cap != null) {
					UUID selected = cap.getSelectedFairyUUID();
					if (selected != null && selected.equals(fairy.getUniqueID())) {
						cap.setSelectedFairy(null);
						playerIn.world.playSound(null, playerIn.getPosition(), ModSounds.TINY_BELL, SoundCategory.NEUTRAL, 1, 0.25f);

						playerIn.sendStatusMessage(new TextComponentTranslation("Fairy Deselected"), true);
					} else {
						cap.setSelectedFairy(fairy.getUniqueID());

						boolean movingMode = NBTHelper.getBoolean(stack, "moving_mode", true);

						if (!movingMode) {
							playerIn.world.playSound(null, playerIn.getPosition(), ModSounds.TINY_BELL, SoundCategory.NEUTRAL, 1, 0.75f);
						} else {
							playerIn.world.playSound(null, playerIn.getPosition(), ModSounds.TINY_BELL, SoundCategory.NEUTRAL, 1, 1.25f);
						}

						playerIn.sendStatusMessage(new TextComponentTranslation("Fairy Selected. Mode: " + (movingMode ? "fairy moving" : "fairy aiming")), true);

					}
					cap.dataChanged(playerIn);
				}
			}
		}

		return super.itemInteractionForEntity(stack, playerIn, target, hand);
	}

	@Override
	public String getItemStackDisplayName(ItemStack stack) {
		boolean movingMode = NBTHelper.getBoolean(stack, "moving_mode", true);

		if (movingMode) {
			return I18n.format("item.wizardry:fairy_bell_moving_mode");
		} else return I18n.format("item.wizardry:fairy_bell_aiming_mode");
	}

	@NotNull
	@Override
	public ActionResult<ItemStack> onItemRightClick(World worldIn, EntityPlayer playerIn, @NotNull EnumHand handIn) {
		ItemStack stack = playerIn.getHeldItem(handIn);
		if (playerIn.isSneaking() && !worldIn.isRemote) {
			NBTHelper.setBoolean(stack, "moving_mode", !NBTHelper.getBoolean(stack, "moving_mode", true));

			IMiscCapability cap = MiscCapabilityProvider.getCap(playerIn);
			if (cap == null) return super.onItemRightClick(worldIn, playerIn, handIn);

			cap.setSelectedFairy(null);
			cap.dataChanged(playerIn);

			return super.onItemRightClick(worldIn, playerIn, handIn);
		}

		return super.onItemRightClick(worldIn, playerIn, handIn);
	}

	@NotNull
	@Override
	public EnumActionResult onItemUse(EntityPlayer player, World worldIn, BlockPos pos, EnumHand hand, EnumFacing facing, float hitX, float hitY, float hitZ) {
		if (worldIn.isRemote || player.isSneaking())
			return super.onItemUse(player, worldIn, pos, hand, facing, hitX, hitY, hitZ);

		IMiscCapability cap = MiscCapabilityProvider.getCap(player);
		if (cap == null) return super.onItemUse(player, worldIn, pos, hand, facing, hitX, hitY, hitZ);

		EntityFairy entityFairy = cap.getSelectedFairyEntity(worldIn);
		if (entityFairy == null) return super.onItemUse(player, worldIn, pos, hand, facing, hitX, hitY, hitZ);

		cap.setSelectedFairy(null);
		cap.dataChanged(player);

		if (worldIn.isBlockLoaded(pos) && !worldIn.isAirBlock(pos)) {
			IBlockState state = worldIn.getBlockState(pos);
			if (state.getBlock().isCollidable()) {

				ItemStack stack = player.getHeldItem(hand);
				boolean movingMode = NBTHelper.getBoolean(stack, "moving_mode", true);

				if (entityFairy.originPos != null) {
					if (!movingMode) {

						entityFairy.setLookTarget(new Vec3d(pos).add(hitX, hitY, hitZ));

						player.world.playSound(null, player.getPosition(), ModSounds.TINY_BELL, SoundCategory.NEUTRAL, 1, 0.75f);

					} else {

						entityFairy.moveTo(pos.offset(facing));

						player.world.playSound(null, player.getPosition(), ModSounds.TINY_BELL, SoundCategory.NEUTRAL, 1, 1.25f);
					}
				}
			}
		}

		return super.onItemUse(player, worldIn, pos, hand, facing, hitX, hitY, hitZ);
	}
}
