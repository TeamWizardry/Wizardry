package com.teamwizardry.wizardry.common.item;

import com.teamwizardry.librarianlib.features.base.item.ItemMod;
import com.teamwizardry.wizardry.api.capability.CapManager;
import com.teamwizardry.wizardry.api.capability.CustomWizardryCapability;
import com.teamwizardry.wizardry.api.capability.WizardryCapabilityProvider;
import com.teamwizardry.wizardry.api.item.IManaCell;
import com.teamwizardry.wizardry.common.block.fluid.ModFluids;
import com.teamwizardry.wizardry.init.ModItems;
import net.minecraft.block.state.IBlockState;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.item.EntityItem;
import net.minecraft.item.IItemPropertyGetter;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.util.ResourceLocation;
import net.minecraft.world.World;
import net.minecraftforge.common.capabilities.ICapabilityProvider;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;

/**
 * Created by Demoniaque on 6/21/2016.
 */
public class ItemOrb extends ItemMod implements IManaCell {

	public ItemOrb() {
		super("orb");

		this.addPropertyOverride(new ResourceLocation("fill"), new IItemPropertyGetter() {
			@SideOnly(Side.CLIENT)
			public float apply(@Nonnull ItemStack stack, @Nullable World worldIn, @Nullable EntityLivingBase entityIn) {
				CapManager manager = new CapManager(stack);
				double mana = manager.getMana();
				double maxMana = manager.getMaxMana();

				float percentage = (int) (10 * mana / maxMana) / 10f;
				return percentage;
			}
		});
	}

	@Nullable
	@Override
	public ICapabilityProvider initCapabilities(ItemStack stack, @Nullable NBTTagCompound nbt) {
		return new WizardryCapabilityProvider(new CustomWizardryCapability(100, 100, stack.getItemDamage() == 1 ? 100 : 100, 0));
	}

	@Override
	public boolean onEntityItemUpdate(EntityItem entityItem) {
		if (entityItem.getItem().getItemDamage() == 0) {
			IBlockState state = entityItem.world.getBlockState(entityItem.getPosition());
			if (state.getBlock() == ModFluids.NACRE.getActualBlock()) {
				ItemStack newStack = new ItemStack(ModItems.PEARL_NACRE, entityItem.getItem().getCount());
				entityItem.setItem(newStack);
				newStack.getItem().onEntityItemUpdate(entityItem);
			}
		}

		return super.onEntityItemUpdate(entityItem);
	}
}
