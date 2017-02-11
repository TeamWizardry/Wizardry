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
import java.util.UUID;

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
			UUID owner = ItemNBTHelper.getUUID(stack, "owner", true);
			UUID thief = ItemNBTHelper.getUUID(stack, "thief", true);

			if (owner == null) {
				ItemNBTHelper.setUUID(stack, "owner", entityIn.getUniqueID());
				owner = entityIn.getUniqueID();
			}
			if (!owner.equals(entityIn.getUniqueID())) {
				Minecraft.getMinecraft().player.sendChatMessage("DIFFERENT OWNERS DETECTED");
				if (buffer >= 30) {
					ItemNBTHelper.setUUID(stack, "owner", entityIn.getUniqueID());
					ItemNBTHelper.setInt(stack, "time", (int) (time / 1.5));
					ItemNBTHelper.setInt(stack, "buffer", 0);
				} else {
					if (thief != entityIn.getUniqueID()) {
						ItemNBTHelper.setInt(stack, "buffer", 0);
						ItemNBTHelper.setUUID(stack, "thief", entityIn.getUniqueID());
					} else ItemNBTHelper.setInt(stack, "buffer", buffer + 1);
				}
			} else ItemNBTHelper.setInt(stack, "time", time + 1);
			//Minecraft.getMinecraft().player.sendChatMessage(time + " -- " + buffer + " -- " + owner + " -- " + thief);
		}
	}

	@Override
	@SideOnly(Side.CLIENT)
	public void addInformation(ItemStack stack, EntityPlayer player, List<String> tooltip, boolean advanced) {
		Entity owner = player.world.getPlayerEntityByUUID(ItemNBTHelper.getUUID(stack, "owner", false));
		Entity thief = player.world.getPlayerEntityByUUID(ItemNBTHelper.getUUID(stack, "thief", false));
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
