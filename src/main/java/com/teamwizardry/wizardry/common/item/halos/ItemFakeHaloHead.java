package com.teamwizardry.wizardry.common.item.halos;

import com.teamwizardry.librarianlib.features.base.item.ItemModArmor;
import com.teamwizardry.wizardry.Wizardry;
import com.teamwizardry.wizardry.api.ConfigValues;
import com.teamwizardry.wizardry.api.capability.player.mana.ManaManager;
import com.teamwizardry.wizardry.api.item.halo.IHalo;
import com.teamwizardry.wizardry.init.ModBlocks;
import net.minecraft.client.util.ITooltipFlag;
import net.minecraft.entity.Entity;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.inventory.EntityEquipmentSlot;
import net.minecraft.item.EnumRarity;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.util.EnumActionResult;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.EnumHand;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import javax.annotation.Nonnull;
import java.util.List;

public class ItemFakeHaloHead extends ItemModArmor implements IHalo {

	public ItemFakeHaloHead() {
		super("halo_fake", ArmorMaterial.IRON, EntityEquipmentSlot.HEAD);
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
	public void onUpdate(ItemStack stack, World worldIn, Entity entityIn, int itemSlot, boolean isSelected) {
		if (worldIn.isRemote) return;

		try (ManaManager.CapManagerBuilder mgr = ManaManager.forObject(entityIn)) {
			mgr.setMaxMana(ConfigValues.crudeHaloBufferSize);
			mgr.setMaxBurnout(ConfigValues.crudeHaloBufferSize);
			mgr.removeBurnout(mgr.getMaxBurnout() * ConfigValues.haloGenSpeed * 2);
		}
	}

	@Override
	public final String getArmorTexture(ItemStack stack, Entity entity, EntityEquipmentSlot slot, String type) {
		return new ResourceLocation(Wizardry.MODID, "textures/empty.png").toString();
	}

	@Override
	public void addInformation(@NotNull ItemStack stack, @Nullable World world, @NotNull List<String> tooltip, @NotNull ITooltipFlag flag) {
		super.addInformation(stack, world, tooltip, flag);
		tooltip.addAll(getHaloTooltip(stack));
	}

	private boolean isInitialized; { isInitialized = true; }

	@NotNull
	@Override
	public EnumRarity getRarity(ItemStack stack) {
		return EnumRarity.UNCOMMON;
	}

	@Nonnull
	@Override
	public Item setMaxDamage(int maxDamageIn) {
		if (!isInitialized)
			return this;
		return super.setMaxDamage(maxDamageIn);
	}
}
