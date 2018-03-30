package com.teamwizardry.wizardry.common.item.halos;

import baubles.api.BaubleType;
import com.teamwizardry.librarianlib.features.base.item.ItemModBauble;
import com.teamwizardry.wizardry.api.ConfigValues;
import com.teamwizardry.wizardry.api.capability.CapManager;
import com.teamwizardry.wizardry.api.item.halo.IHalo;
import com.teamwizardry.wizardry.init.ModBlocks;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.ItemStack;
import net.minecraft.util.EnumActionResult;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.EnumHand;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;
import net.minecraftforge.fml.common.Optional;

import javax.annotation.Nonnull;

/**
 * Created by Demoniaque on 8/30/2016.
 */
@Optional.Interface(iface = "baubles.api.IBauble", modid = "baubles")
public class ItemFakeHaloBauble extends ItemModBauble implements IHalo {

	public ItemFakeHaloBauble() {
		super("halo_fake");
		setMaxStackSize(1);
	}

	@Override
	public EnumActionResult onItemUse(EntityPlayer player, World worldIn, BlockPos pos, EnumHand hand, EnumFacing facing, float hitX, float hitY, float hitZ) {

		if (worldIn.getBlockState(pos).getBlock() == ModBlocks.HALO_INFUSER) {
			return EnumActionResult.SUCCESS;
		}

		return super.onItemUse(player, worldIn, pos, hand, facing, hitX, hitY, hitZ);
	}

	@Override
	public void onWornTick(@Nonnull ItemStack stack, @Nonnull EntityLivingBase player) {
		CapManager manager = new CapManager(player).setManualSync(true);

		manager.setMaxMana(ConfigValues.crudeHaloBufferSize);
		manager.setMaxBurnout(ConfigValues.crudeHaloBufferSize);
		if (manager.getMana() > ConfigValues.crudeHaloBufferSize) manager.setMana(ConfigValues.crudeHaloBufferSize);
		if (manager.getBurnout() > ConfigValues.crudeHaloBufferSize)
			manager.setBurnout(ConfigValues.crudeHaloBufferSize);

		if (!manager.isBurnoutEmpty()) manager.removeBurnout(manager.getMaxBurnout() * ConfigValues.haloGenSpeed);

		if (manager.isSomethingChanged())
			manager.sync();
	}

	@Nonnull
	@Optional.Method(modid = "baubles")
	@Override
	public BaubleType getBaubleType(@Nonnull ItemStack itemStack) {
		return BaubleType.HEAD;
	}
}
