package com.teamwizardry.wizardry.common.item;

import com.teamwizardry.librarianlib.features.base.item.ItemMod;
import com.teamwizardry.wizardry.api.capability.player.miscdata.IMiscCapability;
import com.teamwizardry.wizardry.api.capability.player.miscdata.MiscCapabilityProvider;
import com.teamwizardry.wizardry.api.entity.FairyObject;
import com.teamwizardry.wizardry.common.entity.EntityFairy;
import com.teamwizardry.wizardry.init.ModSounds;
import net.minecraft.block.state.IBlockState;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.ItemStack;
import net.minecraft.util.EnumActionResult;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.EnumHand;
import net.minecraft.util.SoundCategory;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Vec3d;
import net.minecraft.world.World;
import org.jetbrains.annotations.NotNull;

public class ItemFairyBell extends ItemMod {

	public ItemFairyBell() {
		super("fairy_bell");
	}

	@Override
	public boolean itemInteractionForEntity(ItemStack stack, EntityPlayer playerIn, EntityLivingBase target, EnumHand hand) {
		if (playerIn.world.isRemote) return super.itemInteractionForEntity(stack, playerIn, target, hand);

		if (target instanceof EntityFairy) {

			EntityFairy fairy = (EntityFairy) target;
			FairyObject fairyObject = fairy.getDataFairy();

			if (fairyObject == null) return super.itemInteractionForEntity(stack, playerIn, target, hand);

			if (fairyObject.isDepressed) {
				IMiscCapability cap = MiscCapabilityProvider.getCap(playerIn);
				if (cap != null) {
					cap.setSelectedFairy(fairy.getUniqueID());
					cap.dataChanged(playerIn);
					playerIn.world.playSound(null, playerIn.getPosition(), ModSounds.SPARKLE, SoundCategory.NEUTRAL, 1, 1);
				}
			}
		}

		return super.itemInteractionForEntity(stack, playerIn, target, hand);
	}

	@NotNull
	@Override
	public EnumActionResult onItemUse(EntityPlayer player, World worldIn, BlockPos pos, EnumHand hand, EnumFacing facing, float hitX, float hitY, float hitZ) {
		if (worldIn.isRemote) return super.onItemUse(player, worldIn, pos, hand, facing, hitX, hitY, hitZ);

		IMiscCapability cap = MiscCapabilityProvider.getCap(player);
		if (cap == null) return super.onItemUse(player, worldIn, pos, hand, facing, hitX, hitY, hitZ);

		EntityFairy entityFairy = cap.getSelectedFairyEntity(worldIn);
		if (entityFairy == null) return super.onItemUse(player, worldIn, pos, hand, facing, hitX, hitY, hitZ);

		cap.setSelectedFairy(null);
		cap.dataChanged(player);

		if (worldIn.isBlockLoaded(pos) && !worldIn.isAirBlock(pos)) {
			IBlockState state = worldIn.getBlockState(pos);
			if (state.getBlock().isCollidable()) {

				if (entityFairy.originPos != null) {

					entityFairy.lookTargetPos = new Vec3d(pos).add(hitX, hitY, hitZ);

					player.world.playSound(null, player.getPosition(), ModSounds.FAIRY, SoundCategory.NEUTRAL, 1, 1);

				}
			}
		}

		return super.onItemUse(player, worldIn, pos, hand, facing, hitX, hitY, hitZ);
	}
}
