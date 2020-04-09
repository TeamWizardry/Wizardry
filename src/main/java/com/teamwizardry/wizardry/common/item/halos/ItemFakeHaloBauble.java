package com.teamwizardry.wizardry.common.item.halos;

import baubles.api.BaubleType;
import com.teamwizardry.librarianlib.features.base.item.ItemModBauble;
import com.teamwizardry.wizardry.api.ConfigValues;
import com.teamwizardry.wizardry.api.capability.player.mana.ManaManager;
import com.teamwizardry.wizardry.api.item.halo.IHalo;
import com.teamwizardry.wizardry.init.ModBlocks;
import net.minecraft.client.util.ITooltipFlag;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.EnumRarity;
import net.minecraft.item.ItemStack;
import net.minecraft.util.EnumActionResult;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.EnumHand;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;
import net.minecraftforge.fml.common.Optional;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import javax.annotation.Nonnull;
import java.util.ArrayList;
import java.util.List;

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

		if (worldIn.getBlockState(pos).getBlock() == ModBlocks.ALTAR_SACRAMENT) {
			return EnumActionResult.SUCCESS;
		}

		return super.onItemUse(player, worldIn, pos, hand, facing, hitX, hitY, hitZ);
	}

	@Override
	public void onWornTick(@Nonnull ItemStack stack, @Nonnull EntityLivingBase player) {
		if (player.world.isRemote) return;

		try (ManaManager.CapManagerBuilder mgr = ManaManager.forObject(player)) {
			mgr.setMaxMana(ConfigValues.crudeHaloBufferSize);
			mgr.setMaxBurnout(ConfigValues.crudeHaloBufferSize);
			mgr.removeBurnout(mgr.getMaxBurnout() * ConfigValues.haloGenSpeed * 2);
		}
	}

	@Nonnull
	@Optional.Method(modid = "baubles")
	@Override
	public BaubleType getBaubleType(@Nonnull ItemStack itemStack) {
		return BaubleType.HEAD;
	}

	@Override
	public void addInformation(@NotNull ItemStack stack, @Nullable World world, @NotNull List<String> tooltip, @NotNull ITooltipFlag flag) {
		super.addInformation(stack, world, tooltip, flag);
		tooltip.addAll(getHaloTooltip(stack));
	}

	@NotNull
	@Override
	public EnumRarity getRarity(ItemStack stack) {
		return EnumRarity.UNCOMMON;
	}
}
