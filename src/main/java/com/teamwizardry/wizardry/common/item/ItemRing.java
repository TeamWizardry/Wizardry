package com.teamwizardry.wizardry.common.item;

import com.teamwizardry.librarianlib.gui.GuiTickHandler;
import com.teamwizardry.wizardry.Wizardry;
import com.teamwizardry.wizardry.api.item.Colorable;
import net.minecraft.client.renderer.block.model.ModelResourceLocation;
import net.minecraft.client.renderer.color.IItemColor;
import net.minecraft.entity.Entity;
import net.minecraft.entity.item.EntityItem;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.util.math.MathHelper;
import net.minecraft.world.World;
import net.minecraftforge.client.model.ModelLoader;
import net.minecraftforge.fml.common.registry.GameRegistry;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;

/**
 * Created by Saad on 6/13/2016.
 */
public class ItemRing extends Item implements Colorable {

	public ItemRing() {
		setRegistryName("ring");
		setUnlocalizedName("ring");
		GameRegistry.register(this);
		setCreativeTab(Wizardry.tab);
	}

	@SideOnly(Side.CLIENT)
	public void initModel() {
		ModelResourceLocation full = new ModelResourceLocation(getRegistryName() + "_pearl", "inventory");
		ModelResourceLocation empty = new ModelResourceLocation(getRegistryName(), "inventory");
		ModelLoader.setCustomModelResourceLocation(this, 0, empty);
		ModelLoader.setCustomModelResourceLocation(this, 1, full);
	}

	@Override
	public void onUpdate(ItemStack stack, World worldIn, Entity entityIn, int itemSlot, boolean isSelected) {
		if (!worldIn.isRemote) return;

		colorableOnUpdate(stack);
	}

	@Override
	public boolean onEntityItemUpdate(EntityItem entityItem) {
		if (!entityItem.worldObj.isRemote) return false;

		colorableOnEntityItemUpdate(entityItem);

		return super.onEntityItemUpdate(entityItem);
	}

	@Override
	public boolean canItemEditBlocks() {
		return false;
	}

	@Override
	public boolean shouldCauseReequipAnimation(ItemStack oldS, ItemStack newS, boolean slotChanged) {
		return slotChanged;
	}

	@SideOnly(Side.CLIENT)
	public static class ColorHandler implements IItemColor {

		@Override
		public int getColorFromItemstack(ItemStack stack, int tintIndex) {
			int rand = 0;
			float saturation = 1f;
			NBTTagCompound compound = stack.getTagCompound();
			if (compound != null && compound.hasKey(TAG_RAND))
				rand = compound.getInteger(TAG_RAND);
			if (compound != null && compound.hasKey(TAG_PURITY))
				saturation = MathHelper.sin(compound.getInteger(TAG_PURITY) * (float) Math.PI * 0.5f / NACRE_PURITY_CONVERSION);

			return java.awt.Color.HSBtoRGB((rand + GuiTickHandler.ticksInGame) / (float) COLOR_CYCLE_LENGTH, saturation * 0.3f, 1f);
		}
	}
}
