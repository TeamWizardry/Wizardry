package com.teamwizardry.wizardry.common.item;

import com.teamwizardry.wizardry.api.capability.IWizardryCapability;
import com.teamwizardry.wizardry.api.capability.WizardryCapabilityProvider;
import kotlin.jvm.functions.Function1;
import net.minecraft.client.renderer.block.model.ModelResourceLocation;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.ItemStack;
import net.minecraft.world.World;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

/**
 * Created by Saad on 3/7/2016.
 */
public class ItemManaCake extends ItemWizardryFood {

	public ItemManaCake() {
		super("mana_cake", 0, 0.3F, false);
		setAlwaysEdible();
	}

	@Override
    protected void onFoodEaten(ItemStack stack, World worldIn, @NotNull EntityPlayer player) {
        super.onFoodEaten(stack, worldIn, player);
		IWizardryCapability cap = WizardryCapabilityProvider.get(player);
		if (cap.getMaxMana() >= (cap.getMana() + 300)) cap.setMana(cap.getMana() + 300, player);
		else cap.setMana(cap.getMaxMana(), player);
    }

    @Nullable
    @Override
    public Function1<ItemStack, ModelResourceLocation> getMeshDefinition() {
        return null;
    }
}
