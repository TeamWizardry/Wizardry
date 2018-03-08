package com.teamwizardry.wizardry.common.item.halos;

import baubles.api.BaubleType;
import com.teamwizardry.librarianlib.features.base.item.ItemModBauble;
import com.teamwizardry.wizardry.api.ConfigValues;
import com.teamwizardry.wizardry.api.capability.CapManager;
import com.teamwizardry.wizardry.api.item.IFakeHalo;
import com.teamwizardry.wizardry.api.item.IHalo;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.ItemStack;
import net.minecraft.util.ActionResult;
import net.minecraft.util.EnumHand;
import net.minecraft.world.World;
import net.minecraftforge.fml.common.Optional;
import org.jetbrains.annotations.NotNull;

import javax.annotation.Nonnull;

/**
 * Created by Saad on 8/30/2016.
 */
@Optional.Interface(iface = "baubles.api.IBauble", modid = "baubles")
public class ItemFakeHaloBauble extends ItemModBauble implements IFakeHalo, IHalo {

	public ItemFakeHaloBauble() {
		super("halo_fake");
		setMaxStackSize(1);
	}

	@NotNull
	@Override
	public ActionResult<ItemStack> onItemRightClick(@NotNull World world, @NotNull EntityPlayer player, @NotNull EnumHand hand) {
		return super.onItemRightClick(world, player, hand);
	}

	@Override
	public void onWornTick(@NotNull ItemStack stack, @NotNull EntityLivingBase player) {
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
	public BaubleType getBaubleType(@NotNull ItemStack itemStack) {
		return BaubleType.HEAD;
	}
}
