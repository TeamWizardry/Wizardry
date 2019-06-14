package com.teamwizardry.wizardry.common.item;

import com.teamwizardry.librarianlib.features.base.item.ItemMod;
import com.teamwizardry.librarianlib.features.helpers.NBTHelper;
import com.teamwizardry.wizardry.api.entity.FairyData;
import com.teamwizardry.wizardry.common.entity.EntityFairy;
import com.teamwizardry.wizardry.common.tile.TileJar;
import com.teamwizardry.wizardry.init.ModSounds;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.ItemStack;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.EnumActionResult;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.EnumHand;
import net.minecraft.util.SoundCategory;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;
import org.jetbrains.annotations.NotNull;

public class ItemFairy extends ItemMod {

	public ItemFairy() {
		super("fairy_item");

		setMaxStackSize(1);
	}

	@NotNull
	@Override
	public EnumActionResult onItemUse(EntityPlayer player, World worldIn, BlockPos pos, EnumHand hand, EnumFacing facing, float hitX, float hitY, float hitZ) {
		ItemStack stack = player.getHeldItem(hand);
		if (stack.isEmpty()) return super.onItemUse(player, worldIn, pos, hand, facing, hitX, hitY, hitZ);
		if (worldIn.isRemote) return super.onItemUse(player, worldIn, pos, hand, facing, hitX, hitY, hitZ);

		FairyData object = FairyData.deserialize(NBTHelper.getCompound(stack, "fairy"));
		if (object == null) return super.onItemUse(player, worldIn, pos, hand, facing, hitX, hitY, hitZ);

		TileEntity tileEntity = worldIn.getTileEntity(pos);
		if (tileEntity instanceof TileJar) {
			TileJar jar = (TileJar) tileEntity;

			if (jar.fairy != null) return super.onItemUse(player, worldIn, pos, hand, facing, hitX, hitY, hitZ);

			jar.fairy = object;
			jar.markDirty();
			worldIn.checkLight(pos);

		} else {

			BlockPos offsetpos = pos.offset(facing);
			EntityFairy entity = new EntityFairy(worldIn, object);
			entity.setPosition(offsetpos.getX() + 0.5, offsetpos.getY() + 0.5, offsetpos.getZ() + 0.5);
			entity.originPos = pos;

			worldIn.spawnEntity(entity);
		}

		stack.shrink(1);
		worldIn.playSound(pos.getX(), pos.getY(), pos.getZ(), ModSounds.FAIRY, SoundCategory.BLOCKS, 1, 1, false);

		return super.onItemUse(player, worldIn, pos, hand, facing, hitX, hitY, hitZ);
	}
}
