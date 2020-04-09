package com.teamwizardry.wizardry.common.item;

import com.teamwizardry.librarianlib.features.base.item.IGlowingItem;
import com.teamwizardry.librarianlib.features.base.item.ItemMod;
import com.teamwizardry.wizardry.common.entity.EntityFairy;
import net.minecraft.client.renderer.block.model.IBakedModel;
import net.minecraft.enchantment.Enchantment;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.EnumRarity;
import net.minecraft.item.ItemStack;
import net.minecraft.util.EnumActionResult;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.EnumHand;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;
import org.jetbrains.annotations.NotNull;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;

public class ItemMagicWand extends ItemMod implements IGlowingItem {

	public ItemMagicWand() {
		super("magic_wand");
		setMaxStackSize(1);
	}

	@Override
	public EnumActionResult onItemUse(EntityPlayer player, World worldIn, BlockPos pos, EnumHand hand, EnumFacing facing, float hitX, float hitY, float hitZ) {
		if (worldIn.isRemote) return EnumActionResult.SUCCESS;

		EntityFairy entity = new EntityFairy(worldIn);
		entity.setPosition(pos.getX(), pos.getY() + 1, pos.getZ());
		worldIn.spawnEntity(entity);

		return EnumActionResult.SUCCESS;
	}

	@SideOnly(Side.CLIENT)
	@Override
	public int packedGlowCoords(@Nonnull ItemStack itemStack, @Nonnull IBakedModel iBakedModel) {
		return 0xf000f0;
	}

	@SideOnly(Side.CLIENT)
	@Nullable
	@Override
	public IBakedModel transformToGlow(@Nonnull ItemStack itemStack, @Nonnull IBakedModel iBakedModel) {
		return IGlowingItem.Helper.wrapperBake(iBakedModel, false, 1);
	}

	@SideOnly(Side.CLIENT)
	@Override
	public boolean shouldDisableLightingForGlow(@Nonnull ItemStack itemStack, @Nonnull IBakedModel iBakedModel) {
		return true;
	}

	@NotNull
	@Override
	public EnumRarity getRarity(ItemStack stack) {
		return EnumRarity.EPIC;
	}
}
