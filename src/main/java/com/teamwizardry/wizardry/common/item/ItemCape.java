package com.teamwizardry.wizardry.common.item;

import com.teamwizardry.librarianlib.common.base.item.ItemModArmor;
import com.teamwizardry.librarianlib.common.util.ItemNBTHelper;
import com.teamwizardry.wizardry.Wizardry;
import net.minecraft.client.Minecraft;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.inventory.EntityEquipmentSlot;
import net.minecraft.item.ItemStack;
import net.minecraft.util.ActionResult;
import net.minecraft.util.EnumActionResult;
import net.minecraft.util.EnumHand;
import net.minecraft.util.ResourceLocation;
import net.minecraft.world.World;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;
import org.jetbrains.annotations.NotNull;

import java.util.List;

/**
 * Created by Saad on 8/30/2016.
 */
public class ItemCape extends ItemModArmor {

	public ItemCape() {
		super("cape", ArmorMaterial.LEATHER, EntityEquipmentSlot.CHEST);
		setMaxStackSize(1);
		setCreativeTab(Wizardry.tab);
		setMaxDamage(0);
	}

	@Override
	public void onUpdate(ItemStack stack, World worldIn, Entity entityIn, int itemSlot, boolean isSelected) {
		if (!(entityIn instanceof EntityLivingBase)) return;

		ItemStack cape = ((EntityLivingBase) entityIn).getItemStackFromSlot(EntityEquipmentSlot.CHEST);
		if (cape == null) return;
		if (!(ItemStack.areItemStacksEqual(stack, cape))) return;

		int tick = ItemNBTHelper.getInt(stack, "tick", 0);
		if (tick < 1200) ItemNBTHelper.setInt(stack, "tick", tick + 1);
		else {
			ItemNBTHelper.setInt(stack, "tick", 0);

			int time = ItemNBTHelper.getInt(stack, "time", 0);
			int buffer = ItemNBTHelper.getInt(stack, "buffer", 0);
			int owner = ItemNBTHelper.getInt(stack, "owner", -1);
			int thief = ItemNBTHelper.getInt(stack, "thief", -1);

			if (owner != entityIn.getEntityId()) {
				Minecraft.getMinecraft().player.sendChatMessage("DIFFERENT OWNERS DETECTED");
				if (buffer >= 60) {
					ItemNBTHelper.setInt(stack, "owner", entityIn.getEntityId());
					ItemNBTHelper.setInt(stack, "time", (int) (time / 1.5));
					ItemNBTHelper.setInt(stack, "buffer", 0);
				} else {
					if (thief != entityIn.getEntityId()) {
						ItemNBTHelper.setInt(stack, "buffer", 0);
						ItemNBTHelper.setInt(stack, "thief", entityIn.getEntityId());
					} else ItemNBTHelper.setInt(stack, "buffer", buffer + 1);
				}
			} else ItemNBTHelper.setInt(stack, "time", time + 1);
			Minecraft.getMinecraft().player.sendChatMessage(time + " -- " + buffer + " -- " + owner + " -- " + thief);
		}
	}

	@Override
	@SideOnly(Side.CLIENT)
	public void addInformation(ItemStack stack, EntityPlayer player, List<String> tooltip, boolean advanced) {
		Entity owner = player.world.getEntityByID(ItemNBTHelper.getInt(stack, "owner", -1));
		Entity thief = player.world.getEntityByID(ItemNBTHelper.getInt(stack, "thief", -1));
		tooltip.add("time: " + ItemNBTHelper.getInt(stack, "time", 0));
		tooltip.add("buffer: " + ItemNBTHelper.getInt(stack, "buffer", 0));
		tooltip.add("owner: " + (owner == null ? "null" : owner.getName()));
		tooltip.add("thief: " + (thief == null ? "null" : thief.getName()));
	}

	@NotNull
	@Override
	public ActionResult<ItemStack> onItemRightClick(ItemStack itemStackIn, World worldIn, EntityPlayer playerIn, EnumHand hand) {
		ItemStack itemstack = playerIn.getItemStackFromSlot(EntityEquipmentSlot.CHEST);

		if (itemstack == null) {
			playerIn.setItemStackToSlot(EntityEquipmentSlot.CHEST, itemStackIn.copy());
			itemStackIn.stackSize = 0;
			return new ActionResult(EnumActionResult.SUCCESS, itemStackIn);
		} else {
			return new ActionResult(EnumActionResult.FAIL, itemStackIn);
		}
	}

	@Override
	public final String getArmorTexture(ItemStack stack, Entity entity, EntityEquipmentSlot slot, String type) {
		return new ResourceLocation(Wizardry.MODID, "textures/empty.png").toString();
	}
}
