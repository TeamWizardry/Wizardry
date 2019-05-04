package com.teamwizardry.wizardry.common.item;

import com.teamwizardry.librarianlib.features.base.item.ItemMod;
import com.teamwizardry.wizardry.api.capability.mana.CapManager;
import net.minecraft.creativetab.CreativeTabs;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.item.IItemPropertyGetter;
import net.minecraft.item.ItemStack;
import net.minecraft.util.NonNullList;
import net.minecraft.util.ResourceLocation;
import net.minecraft.world.World;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;
import org.jetbrains.annotations.NotNull;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;

/**
 * Created by Demoniaque on 6/21/2016.
 */
public class ItemLevitationOrb extends ItemMod {

	public ItemLevitationOrb() {
		super("levitation_orb");
		this.setMaxDamage(60);
		this.setMaxStackSize(1);

		this.addPropertyOverride(new ResourceLocation("fill"), new IItemPropertyGetter() {
			@SideOnly(Side.CLIENT)
			public float apply(@Nonnull ItemStack stack, @Nullable World worldIn, @Nullable EntityLivingBase entityIn) {
				return 1 - (float) stack.getItemDamage() / (float) stack.getMaxDamage();
			}
		});
	}

	@NotNull
	@Override
	public String getTranslationKey(@NotNull ItemStack stack) {
		float percentage = (int) (10 * CapManager.getMana(stack) / CapManager.getMaxMana(stack)) / 10f;
		return super.getTranslationKey(stack) + ".fill." + ((int) (percentage * 100));
	}

	@Override
	public void getSubItems(@Nullable CreativeTabs tab, @Nonnull NonNullList<ItemStack> subItems) {
		if (isInCreativeTab(tab)) {

			for (int i = 1; i < 10; i++) {
				ItemStack stack = new ItemStack(this);
				stack.setItemDamage((int) (stack.getMaxDamage() * (i / 10.0)));
				subItems.add(stack);
			}
		}
	}
}
