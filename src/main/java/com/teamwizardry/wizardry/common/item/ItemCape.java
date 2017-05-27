package com.teamwizardry.wizardry.common.item;

import baubles.api.BaubleType;
import baubles.api.IBauble;
import com.teamwizardry.librarianlib.features.base.ModCreativeTab;
import com.teamwizardry.librarianlib.features.base.item.ItemModArmor;
import com.teamwizardry.librarianlib.features.helpers.ItemNBTHelper;
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

import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import java.util.List;
import java.util.UUID;

/**
 * Created by Saad on 8/30/2016.
 */
public class ItemCape extends ItemModArmor implements IBauble {

	public ItemCape() {
		super("cape", ArmorMaterial.LEATHER, EntityEquipmentSlot.CHEST);
		setMaxStackSize(1);
		setCreativeTab(Wizardry.tab);
		setMaxDamage(0);
	}

	@Override
	public void onCreated(ItemStack stack, World worldIn, EntityPlayer playerIn) {
		super.onCreated(stack, worldIn, playerIn);
		ItemNBTHelper.setUUID(stack, "uuid", UUID.randomUUID());
	}

	@Override
	public void onUpdate(ItemStack stack, World worldIn, Entity entityIn, int itemSlot, boolean isSelected) {
		if (!(entityIn instanceof EntityLivingBase)) return;

		ItemStack cape = ((EntityLivingBase) entityIn).getItemStackFromSlot(EntityEquipmentSlot.CHEST);
		if (!(ItemStack.areItemStacksEqual(stack, cape))) return;

		int tick = ItemNBTHelper.getInt(stack, "tick", 0);
		if (tick < 300) ItemNBTHelper.setInt(stack, "tick", tick + 1);
		else {
			ItemNBTHelper.setInt(stack, "tick", 0);

			int time = ItemNBTHelper.getInt(stack, "time", 0);
			int buffer = ItemNBTHelper.getInt(stack, "buffer", 0);
			UUID owner = ItemNBTHelper.getUUID(stack, "owner");
			UUID thief = ItemNBTHelper.getUUID(stack, "thief");

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
		if (ItemNBTHelper.verifyUUIDExistence(stack, "owner")) {
			UUID uuid = ItemNBTHelper.getUUID(stack, "owner");
			if (uuid != null) {
				Entity owner = player.world.getPlayerEntityByUUID(uuid);
				tooltip.add("owner: " + (owner == null ? "null" : owner.getName()));
			}
		}

		if (ItemNBTHelper.verifyUUIDExistence(stack, "thief")) {
			UUID uuid = ItemNBTHelper.getUUID(stack, "thief");
			if (uuid != null) {
				Entity thief = player.world.getPlayerEntityByUUID(uuid);
				tooltip.add("thief: " + (thief == null ? "null" : thief.getName()));
			}
		}
		tooltip.add("time: " + ItemNBTHelper.getInt(stack, "time", 0));
		tooltip.add("buffer: " + ItemNBTHelper.getInt(stack, "buffer", 0));
		tooltip.add("tick: " + ItemNBTHelper.getInt(stack, "tick", 0));
	}

	@Nonnull
	@Override
	public ActionResult<ItemStack> onItemRightClick(World world, EntityPlayer player, @Nonnull EnumHand hand) {
		ItemStack stack = player.getItemStackFromSlot(EntityEquipmentSlot.CHEST);

		player.setItemStackToSlot(EntityEquipmentSlot.CHEST, stack.copy());
		stack.setCount(0);
		return new ActionResult(EnumActionResult.SUCCESS, stack);
	}

	@Override
	public final String getArmorTexture(ItemStack stack, Entity entity, EntityEquipmentSlot slot, String type) {
		return new ResourceLocation(Wizardry.MODID, "textures/empty.png").toString();
	}

	@Nullable
	@Override
	public ModCreativeTab getCreativeTab() {
		return Wizardry.tab;
	}

	@Override
	public BaubleType getBaubleType(ItemStack itemStack) {
		return BaubleType.BODY;
	}
}
