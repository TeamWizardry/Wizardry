package com.teamwizardry.wizardry.common.item.halos;

import com.teamwizardry.librarianlib.features.base.item.ItemModArmor;
import com.teamwizardry.wizardry.Wizardry;
import com.teamwizardry.wizardry.api.ConfigValues;
import com.teamwizardry.wizardry.api.capability.CapManager;
import com.teamwizardry.wizardry.api.item.halo.IHalo;
import net.minecraft.entity.Entity;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.inventory.EntityEquipmentSlot;
import net.minecraft.item.ItemStack;
import net.minecraft.util.EnumActionResult;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.EnumHand;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;

public class ItemFakeHaloHead extends ItemModArmor implements IHalo {

	public ItemFakeHaloHead() {
		super("halo_fake", ArmorMaterial.IRON, EntityEquipmentSlot.HEAD);
		setMaxStackSize(1);
	}

	@Override
	public EnumActionResult onItemUse(EntityPlayer player, World worldIn, BlockPos pos, EnumHand hand, EnumFacing facing, float hitX, float hitY, float hitZ) {

		//if (worldIn.getBlockState(pos).getBlock() == ModBlocks.HALO_INFUSER) {
		//	return EnumActionResult.SUCCESS;
		//}

		return super.onItemUse(player, worldIn, pos, hand, facing, hitX, hitY, hitZ);
	}

	@Override
	public void onUpdate(ItemStack stack, World worldIn, Entity entityIn, int itemSlot, boolean isSelected) {
		CapManager manager = new CapManager(entityIn).setManualSync(true);

		manager.setMaxMana(ConfigValues.crudeHaloBufferSize);
		manager.setMaxBurnout(ConfigValues.crudeHaloBufferSize);
		if (manager.getMana() > ConfigValues.crudeHaloBufferSize) manager.setMana(ConfigValues.crudeHaloBufferSize);
		if (manager.getBurnout() > ConfigValues.crudeHaloBufferSize)
			manager.setBurnout(ConfigValues.crudeHaloBufferSize);

		if (!manager.isBurnoutEmpty()) manager.removeBurnout(manager.getMaxBurnout() * ConfigValues.haloGenSpeed);

		if (manager.isSomethingChanged())
			manager.sync();
	}

	@Override
	public final String getArmorTexture(ItemStack stack, Entity entity, EntityEquipmentSlot slot, String type) {
		return new ResourceLocation(Wizardry.MODID, "textures/empty.png").toString();
	}

}
